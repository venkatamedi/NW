
package com.k2view.cdbms.usercode.common;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.k2view.broadway.actors.masking.MaskingActors;
import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;

import com.k2view.broadway.exception.ActorException;
import com.k2view.broadway.model.Actor;
import com.k2view.broadway.model.Context;
import com.k2view.broadway.model.Data;
import com.k2view.fabric.common.Util;

import static com.k2view.cdbms.shared.user.UserCode.db;
import static com.k2view.cdbms.shared.user.UserCode.fabric;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static com.k2view.cdbms.shared.user.ProductFunctions.*;

import com.jayway.jsonpath.*;
import com.k2view.fabric.session.broadway.FabricAbstractActor;
import org.json.JSONArray;

import static com.k2view.cdbms.shared.user.UserCode.*;

import java.text.NumberFormat;

@SuppressWarnings({"unused", "DefaultAnnotationParam", "unchecked", "deprecation"})
public class SearchAndMaskPIIOnJson extends FabricAbstractActor {

    static private String INPUT_JSON = "json";
    static private String INPUT_HINTS = "hints";
    static private String INPUT_FIRST_MATCH = "firstMatchOnly";
    static private String INPUT_SHOW_PATH = "showPath";
    static private String OUTPUT_NAME = "maskedJson";
    static private String INPUT_LOG_MASK = "log";

    //private List<Object> resultList = new ArrayList<>();
    //private Map<String, ArrayList<Object>> resultMap = new HashMap<>();
    private Map<String, Object> resultMap;
    private Boolean firstMatchOnly;
    private Boolean showPath;
    private Boolean logMask;

    private JsonArray sortedJsonPatternObjectsArray;

    /**
     * Action performed by the actor.
     *
     * @param input   expected input is resource name (or relative or absolute path for resources)
     * @param output  provided output consists of document only
     * @param context
     * @throws Exception
     */
    @Override
    public void fabricAction(Data input, Data output, Context context) throws Exception {

        try {

            String json = input.string(INPUT_JSON);
            String hints = input.string(INPUT_HINTS);
            this.firstMatchOnly = input.bool(INPUT_FIRST_MATCH);
            this.showPath = input.bool(INPUT_SHOW_PATH);
            this.logMask = input.bool(INPUT_LOG_MASK);

            if (hints.length() != 0) {
                JsonObject obj = new JsonParser().parse(hints).getAsJsonObject();
                JsonArray arr = obj.getAsJsonArray("hints");

                List<JsonObject> jsonPatternObjects = new ArrayList<JsonObject>();

                for (int i = 0; i < arr.size(); i++) {
                    if (arr.get(i).getAsJsonObject().get("ACTIVE").getAsBoolean()) {
                        jsonPatternObjects.add(arr.get(i).getAsJsonObject());
                    }
                }


                Collections.sort(jsonPatternObjects, new Comparator<JsonObject>() {
                    @Override
                    public int compare(JsonObject a, JsonObject b) {
                        if ((a.get("MATCH PROBABILITY").getAsDouble() > b.get("MATCH PROBABILITY").getAsDouble()) || // If A's probability is bigger than B's
                                (a.get("MATCH PROBABILITY").getAsDouble() == b.get("MATCH PROBABILITY").getAsDouble() && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN") && !b.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN")) ||   // if A is COLUMN and B is not
                                (a.get("MATCH PROBABILITY").getAsDouble() == b.get("MATCH PROBABILITY").getAsDouble() && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA") && b.get("MATCH TYPE").getAsString().toUpperCase().matches("DATA_FUNCTION|DATA_SAMPLE")) || // If their probability is the same but A's Data and B's Sample or function
                                (a.get("MATCH PROBABILITY").getAsDouble() == b.get("MATCH PROBABILITY").getAsDouble() && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_FUNCTION") && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_SAMPLE"))) { // If their probability is the same but A's function and B's Sample
                            return -1;
                        } else if (
                                (b.get("MATCH PROBABILITY").getAsDouble() > a.get("MATCH PROBABILITY").getAsDouble()) || // If A's probability is bigger than B's
                                        (b.get("MATCH PROBABILITY").getAsDouble() == a.get("MATCH PROBABILITY").getAsDouble() && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN") && !a.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN")) ||   // if A is COLUMN and B is not
                                        (b.get("MATCH PROBABILITY").getAsDouble() == a.get("MATCH PROBABILITY").getAsDouble() && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA") && a.get("MATCH TYPE").getAsString().toUpperCase().matches("DATA_FUNCTION|DATA_SAMPLE")) || // If their probability is the same but A's Data and B's Sample or function
                                        (b.get("MATCH PROBABILITY").getAsDouble() == a.get("MATCH PROBABILITY").getAsDouble() && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_FUNCTION") && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_SAMPLE"))) { // If their probability is the same but A's function and B's Sample
                            return 1;
                        }
                        return 0;
                    }
                });

                this.sortedJsonPatternObjectsArray = new JsonArray();

                for (int i = 0; i < jsonPatternObjects.size(); i++) {
                    this.sortedJsonPatternObjectsArray.add(jsonPatternObjects.get(i));
                }

                List<String> attributeList = new ArrayList<String>();
                this.resultMap = new HashMap<>();

                JsonParser p = new JsonParser();

                //function that returns all pii fields discovered by hints and their path in the json
                check(p.parse(json), "", context);


                //this code is replacing json original values to masked one.
                String entryPath = "";
                String entryValue = "";

                for (Map.Entry entry : this.resultMap.entrySet()) {
                    json = updateJson(json, entry.getKey().toString(), entry.getValue());
                }
            }
            output.put(OUTPUT_NAME, json);

            // Close the Class objects
        } finally {
            this.sortedJsonPatternObjectsArray = null;
            this.resultMap = null;
        }
    }

    @Override
    public void close() throws Exception {
        //resultMap.clear();
    }

    //Using library com.jayway.jsonpath (https://github.com/json-path/JsonPath)
    //We need import the library into Fabric lib folder.
    private String updateJson(String json, String path, Object value) {
        DocumentContext doc = JsonPath.parse(json).
                set(Stream.of(path.split("[.]")).map(s -> (s.contains(" ")) ? String.format("['%s']", s) : s).collect(Collectors.joining(".")), value);
        return doc.jsonString();
    }

    //Function that check is field value is pii based on pattern
    private Boolean isPII(String parameter, String pattern, String patternType) {
        if (patternType.equalsIgnoreCase("COLUMN") || patternType.equalsIgnoreCase("DATA")) {
            return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(parameter).matches();
        } else if (patternType.equalsIgnoreCase("DATA_FUNCTION")) {
            try {
                // Note to allow validation in WS - the data function will need to be placed in shared ( under Discovery_DataFunctions)
                //return (boolean) com.k2view.cdbms.usercode.common.DataMaskingDiscovery.SharedLogic.class.getDeclaredMethod(pattern, Object.class).invoke(pattern, parameter);
                //TODO: refactor to default response
                return (boolean) fabric().fetch(String.format("BROADWAY k2_ref.%s discover='%s' RESULT_STRUCTURE=ROW", pattern, parameter)).firstRow().get("value");
            } catch (SQLException /*| IllegalAccessException | InvocationTargetException | NoSuchMethodException */ e) {
                log.error("Error :", e);
                return false;
            }
        } else if (patternType.equalsIgnoreCase("DATA_SAMPLE")) {
            return false;
        }
        return false;
    }

    //Function that is masking a given field based on
    private Object maskingValue(Object parameter, String maskText, String maskFunction, String maskFuncParams, Context context) throws Exception {
        Object maskedValue = null;
        if (maskText.length() != 0)
            maskedValue = maskText;
        else {
            if (maskFunction.length() != 0) {
                if (maskFuncParams.length() != 0) {

                    String[] params = maskFuncParams.split("(?:^|,)(?=(?:[^\']*(?:[\\\\][\'])*[^\']*(?<=[^\\\\])\'[^\']*(?:[\\\\][\'])*[^\']*(?<=[^\\\\])\')*[^\']*$)");
                    Arrays.setAll(params, i -> params[i].toString().replaceAll("(?<=(?:^|[^\\\\]))\'", ""));

                    Map<String, Object> bi = new ObjectMapper().readValue(maskFuncParams, Map.class);
                    bi.forEach((k, v) -> {
                        if (v.toString().contains("<pattern>"))
                            bi.put(k, v.toString().replace("<pattern>", "" + parameter));
                    });
                    /*Map<String, String> bi = new HashMap<>();
                    int idx = 0;
                    try (Db.Rows broadwayInputsRows = fabric().fetch("list bf LU_NAME=k2_ws FLOW=?", maskFunction)) {
                        for (Db.Row broadwayInputsRow : broadwayInputsRows) {
                            if (broadwayInputsRow.get("param").toString().equalsIgnoreCase("input"))
                                bi.put(broadwayInputsRow.get("name").toString(), (params[idx++].replace("<pattern>", "" + parameter)));
                        }
                    }*/

                    //maskedValue = "" + db("FabricRemote").fetch(String.format("broadway k2_ws.%s %s",maskFunction,bi.entrySet().stream().map(es -> String.format("%s='%s'",es.getKey(),es.getValue())).collect(Collectors.joining(",")))).firstRow().get("value");
                    //maskedValue = "" + db("FabricRemote").fetch(String.format("broadway k2_ws.%s %s",maskFunction,bi.entrySet().stream().map(es -> String.format("%s=?",es.getKey())).collect(Collectors.joining(","))),bi.values().toArray()).firstRow().get("value");
                    maskedValue = "" + context.broadway().createFlow(maskFunction).act(Data.from(bi), context).fields().values().iterator().next();
 
 
                   /*if (maskFunction.equals("fnRandomInList")) {
                                  params = new String[1];
                                  params[0] = maskFuncParams;
                   } else {
                                  params = maskFuncParams.split(",");
                   }

                   try {
                                  maskedValue = (String) com.k2view.cdbms.usercode.common.DataMaskingLibrary.SharedLogic.class.getDeclaredMethod(maskFunction, String.class).invoke(maskFunction, params);
                   } catch (NoSuchMethodException e) {
                                  e.printStackTrace();
                   } catch (IllegalAccessException e) {
                                  e.printStackTrace();
                   } catch (InvocationTargetException e) {
                                  e.printStackTrace();
                   }*/

                } else {
                    try {
                        maskedValue = (String) com.k2view.cdbms.usercode.common.DataMaskingLibrary.SharedLogic.class.getDeclaredMethod(maskFunction).invoke(maskFunction);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            } else
                maskedValue = parameter;
        }
        return (parameter instanceof Number && Pattern.compile("[0-9.-]+").matcher("" + maskedValue).matches()) ? NumberFormat.getInstance().parse("" + maskedValue) : maskedValue;
    }

    private void check(JsonElement jsonElement, String path, Context context) throws Exception {

        if (this.sortedJsonPatternObjectsArray.size() == 0) return;

        if (jsonElement.isJsonArray()) {
            int index = 0;

            boolean isPrimitiveArray = false;

            JSONArray maskedJsonArray = new JSONArray();

            for (JsonElement jsonElementArray : jsonElement.getAsJsonArray()) {
                if (!jsonElementArray.isJsonPrimitive()) {
                    check(jsonElementArray, isBlank(path) ? "" : path + String.format("[%d]", index++), context);

                } else {
                    isPrimitiveArray = true;
                    boolean foundMatchMask = false;
                    for (int i = 0; i < this.sortedJsonPatternObjectsArray.size(); i++) {
                        String matchType = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH TYPE").getAsString();
                        String pattern = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN").getAsString();
                        String patternType = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH TYPE").getAsString();
                        String maskText = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK TEXT").getAsString();
                        String maskFunction = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK FUNCTION").getAsString();
                        String maskFuncParams = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK FUNCTION ARGS").getAsString();
                        //Double matchProb = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH PROBABILITY").getAsDouble();
                        Boolean patternActive = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("ACTIVE").getAsBoolean();


                        if (isPII(jsonElementArray.getAsString(), pattern, matchType)) {
                            Object maskValue = maskingValue(jsonElementArray.getAsString(), maskText, maskFunction, maskFuncParams, context);
                            maskedJsonArray.put(maskValue);
                            foundMatchMask = true;

                            if (this.logMask)
                                log.info(String.format("\nUniversal Masking --------\n   Path: %s \n   Match Type: %s \n   Pattern Type: %s \n   Pattern: %s \n   Entry Value: %s \n   Masked Value: %s \n--------\n", String.format("$.%s", path), matchType, patternType, pattern, jsonElementArray.getAsString(), maskValue));

                            break;
                        }
                    }
                    if (!foundMatchMask) maskedJsonArray.put(jsonElementArray.getAsString());
                }
            }
            if (isPrimitiveArray) {
                this.resultMap.put(path, maskedJsonArray);
            }
        } else if (jsonElement.isJsonObject()) {
            Set<Map.Entry<String, JsonElement>> entrySet = jsonElement.getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String entryKey = entry.getKey();

                if (!entry.getValue().isJsonPrimitive()) {
                    //TODO: check why this is done, as JSON primitives include Object and Array
                    check(entry.getValue(), isBlank(path) ? entryKey : String.format("%s.%s", path, entryKey), context);
                } else {
                    Object entryValue = null;

                    //TODO: add implementation for all proper objects
                    if (entry.getValue().getAsJsonPrimitive().isNumber()) {
                        entryValue = entry.getValue().getAsNumber();
                    } else if (entry.getValue().getAsJsonPrimitive().isString()) {
                        entryValue = entry.getValue().getAsString();
                    } else {
                        //TODO: it is better to use proper JSON rather than do magic around strings
                        entryValue = entry.getValue().toString().replaceAll("^\"", "").replaceAll("\"$", "");
                    }

                    Boolean isPII = false;
                    Object maskValue;

                    for (int i = 0; i < this.sortedJsonPatternObjectsArray.size(); i++) {
                        String matchType = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH TYPE").getAsString();
                        String pattern = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN").getAsString();
                        String patternType = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN TYPE").getAsString();
                        String maskText = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK TEXT").getAsString();
                        String maskFunction = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK FUNCTION").getAsString();
                        String maskFuncParams = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK FUNCTION ARGS").getAsString();
                        //Double matchProb = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH PROBABILITY").getAsDouble();
                        Boolean patternActive = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("ACTIVE").getAsBoolean();

                        if (isPII((matchType.equalsIgnoreCase("COLUMN")) ? entryKey : "" + entryValue, pattern, matchType)) {
                            //log.info(String.format("\nBEFORE --------\n   Pattern Type: %s \n   Pattern: %s \n   EntryValue: %s \n--------\n",this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN TYPE").getAsString(),pattern,entryValue));
                            maskValue = maskingValue(entryValue, maskText, maskFunction, maskFuncParams, context);
                            //log.info(String.format("\nAFTER --------\n   Pattern Type: %s \n   Pattern: %s \n   EntryValue: %s \n   Mask: %s \n--------\n",this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN TYPE").getAsString(),pattern,entryValue,maskValue));

                            if (this.logMask)
                                log.info(String.format("\nUniversal Masking --------\n   Path: %s \n   Match Type: %s \n   Pattern Type: %s \n   Pattern: %s \n   Entry Value: %s \n   Masked Value: %s \n--------\n", String.format("$.%s", isBlank(path) ? entryKey : String.format("%s.%s", path, entryKey)), matchType, patternType, pattern, entryValue, maskValue));

                            this.resultMap.put(String.format("%s.%s", path, entryKey), maskValue);
                            isPII = true;
                        }
                        if (isPII) break;
                    }

                    check(entry.getValue(), isBlank(path) ? entryKey : String.format("%s.%s", path, entryKey), context);
                }
            }
        }
    }
}