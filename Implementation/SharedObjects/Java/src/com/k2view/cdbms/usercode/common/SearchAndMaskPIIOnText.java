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
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

@SuppressWarnings({"unused", "DefaultAnnotationParam","unchecked","deprecation"})
public class SearchAndMaskPIIOnText extends FabricAbstractActor {

	static private String INPUT_TEXT = "text";
	static private String INPUT_HINTS = "hints";
	static private String OUTPUT_NAME = "maskedText";

	//private List<Object> resultList = new ArrayList<>();
	//private Map<String, ArrayList<Object>> resultMap = new HashMap<>();
	private TreeMap<Integer, Map<String,Object>> resultMap;

	private JsonArray sortedJsonPatternObjectsArray = new JsonArray();

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

		String text = input.string(INPUT_TEXT);
		String hints = input.string(INPUT_HINTS);

		if(StringUtils.isBlank(text)){

			output.put(OUTPUT_NAME, text);
			return;
		}

		JsonObject obj = new JsonParser().parse(hints).getAsJsonObject();
		JsonArray arr = obj.getAsJsonArray("hints");

		List<JsonObject> jsonPatternObjects = new ArrayList<JsonObject>();

		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getAsJsonObject().get("ACTIVE").getAsBoolean() &&
				arr.get(i).getAsJsonObject().get("MATCH TYPE").getAsString().toUpperCase().matches("DATA|DATA_FUNCTION")) {
				jsonPatternObjects.add(arr.get(i).getAsJsonObject());
			}
		}


		Collections.sort(jsonPatternObjects, new Comparator<JsonObject>() {
			@Override
			public int compare(JsonObject a, JsonObject b) {
				if( (a.get("MATCH PROBABILITY").getAsDouble() >  b.get("MATCH PROBABILITY").getAsDouble()) || // If A's probability is bigger than B's
						(a.get("MATCH PROBABILITY").getAsDouble() == b.get("MATCH PROBABILITY").getAsDouble() && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN") && !b.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN")) ||	// if A is COLUMN and B is not
						(a.get("MATCH PROBABILITY").getAsDouble() == b.get("MATCH PROBABILITY").getAsDouble() && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_FUNCTION") && b.get("MATCH TYPE").getAsString().toUpperCase().matches("DATA|DATA_SAMPLE")) || // If their probability is the same but A's Data and B's Sample or function
						(a.get("MATCH PROBABILITY").getAsDouble() == b.get("MATCH PROBABILITY").getAsDouble() && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA") && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_SAMPLE"))){ // If their probability is the same but A's function and B's Sample
					return -1;
				} else if(
						(b.get("MATCH PROBABILITY").getAsDouble() >  a.get("MATCH PROBABILITY").getAsDouble()) || // If A's probability is bigger than B's
								(b.get("MATCH PROBABILITY").getAsDouble() == a.get("MATCH PROBABILITY").getAsDouble() && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN") && !a.get("MATCH TYPE").getAsString().equalsIgnoreCase("COLUMN")) ||	// if A is COLUMN and B is not
								(b.get("MATCH PROBABILITY").getAsDouble() == a.get("MATCH PROBABILITY").getAsDouble() && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_FUNCTION") && a.get("MATCH TYPE").getAsString().toUpperCase().matches("DATA|DATA_SAMPLE")) || // If their probability is the same but A's Data and B's Sample or function
								(b.get("MATCH PROBABILITY").getAsDouble() == a.get("MATCH PROBABILITY").getAsDouble() && b.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA") && a.get("MATCH TYPE").getAsString().equalsIgnoreCase("DATA_SAMPLE"))){ // If their probability is the same but A's function and B's Sample
					return 1;
				}
				return 0;
			}
		});

		for (int i = 0; i < jsonPatternObjects.size(); i++) {
			this.sortedJsonPatternObjectsArray.add(jsonPatternObjects.get(i));
		}

		List<String> attributeList = new ArrayList<String>();
		this.resultMap = new TreeMap<>();


		for (int i = 0; i < this.sortedJsonPatternObjectsArray.size(); i++) {
			String matchType = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH TYPE").getAsString();
			String patternType = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN TYPE").getAsString();
			String pattern = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("PATTERN").getAsString();
			String maskText = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK TEXT").getAsString();
			String maskFunction = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK FUNCTION").getAsString();
			String maskFuncParams = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MASK FUNCTION ARGS").getAsString();
			//Double matchProb = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("MATCH PROBABILITY").getAsDouble();
			Boolean patternActive = this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject().get("ACTIVE").getAsBoolean();

			if(matchType.equalsIgnoreCase("DATA")) {
				Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
				while (matcher.find()) {
					Map.Entry<Integer,Map<String,Object>> floor = this.resultMap.floorEntry(matcher.start());
					if (!this.resultMap.containsKey(matcher.start()) && (this.resultMap.isEmpty() || floor == null || Integer.parseInt(floor.getValue().get("end").toString()) < matcher.start())) {
						this.resultMap.put(matcher.start(), Util.map("key", matcher.group(), "start", matcher.start(), "end", matcher.end(), "value", this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject()));
					}
				}
			} else if (matchType.equalsIgnoreCase("DATA_FUNCTION")) {

				Matcher matcher = Pattern.compile("\\b\\S+\\b", Pattern.CASE_INSENSITIVE).matcher(text);
				while (matcher.find()) {
					Map.Entry<Integer,Map<String,Object>> floor = this.resultMap.floorEntry(matcher.start());
					//if((boolean)com.k2view.cdbms.usercode.common.DataMaskingDiscovery.SharedLogic.class.getDeclaredMethod(pattern, Object.class).invoke(null,matcher.group())) {
					//TODO: refactor to default response
					if((boolean) fabric().fetch(String.format("BROADWAY k2_ref.%s discover=? RESULT_STRUCTURE=ROW", pattern), matcher.group()).firstRow().get("value")) {
						if (!this.resultMap.containsKey(matcher.start()) && (this.resultMap.isEmpty() || floor == null || Integer.parseInt(floor.getValue().get("end").toString()) < matcher.start())) {
							this.resultMap.put(matcher.start(), Util.map("key", matcher.group(), "start", matcher.start(), "end", matcher.end(), "value", this.sortedJsonPatternObjectsArray.get(i).getAsJsonObject()));
						}
					}
				}
			}
		}

		int offset = 0;
		for(Map.Entry<Integer,Map<String,Object>> entry : this.resultMap.entrySet()){
			String valueToMask    = entry.getValue().get("key").toString();
			int start             = Integer.parseInt(entry.getValue().get("start").toString());
			int end               = Integer.parseInt(entry.getValue().get("end").toString());
			String matchType      = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("MATCH TYPE").getAsString();
			String patternType    = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("PATTERN TYPE").getAsString();
			String pattern        = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("PATTERN").getAsString();
			String maskText       = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("MASK TEXT").getAsString();
			String maskFunction   = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("MASK FUNCTION").getAsString();
			String maskFuncParams = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("MASK FUNCTION ARGS").getAsString();
			Boolean patternActive = ((JsonObject)entry.getValue().get("value")).getAsJsonObject().get("ACTIVE").getAsBoolean();

			String maskedValue = maskingValue(valueToMask,maskText,maskFunction,maskFuncParams,context);
			start += offset;
			end   += offset;
			offset += (maskedValue.length() - valueToMask.length());
			text = text.substring(0,start) + maskedValue + text.substring(end);
		}

		output.put(OUTPUT_NAME, text);
	}

	@Override
	public void close() throws Exception {
		//resultMap.clear();
	}

	//Function that is masking a given field based on
	private String maskingValue(String parameter, String maskText, String maskFunction, String maskFuncParams, Context context) throws Exception {
		String maskedValue = "";
		if (maskText.length() != 0)
			maskedValue = maskText;
		else {
			if (maskFunction.length() != 0) {
				if (maskFuncParams.length() != 0) {

					String[] params = maskFuncParams.split("(?:^|,)(?=(?:[^\']*(?:[\\\\][\'])*[^\']*(?<=[^\\\\])\'[^\']*(?:[\\\\][\'])*[^\']*(?<=[^\\\\])\')*[^\']*$)");
					Arrays.setAll(params, i -> params[i].toString().replaceAll("(?<=(?:^|[^\\\\]))\'",""));

					Map<String, Object> bi = new ObjectMapper().readValue(maskFuncParams, Map.class);
					bi.forEach((k, v) -> {
						if (v.toString().contains("<pattern>"))
							bi.put(k, v.toString().replace("<pattern>", "" + parameter));
					});

					//maskedValue = fabric().fetch(String.format("BROADWAY k2_ref.%s discover='%s'", pattern, parameter)).firstRow().get("value");
					maskedValue = "" + context.broadway().createFlow(maskFunction).act(Data.from(bi), context).fields().values().iterator().next();
					//maskedValue = "" + fabric().fetch(String.format("broadway k2_ws.%s %s",maskFunction,bi.entrySet().stream().map(es -> String.format("%s='%s'",es.getKey(),es.getValue())).collect(Collectors.joining(" ")))).firstRow().get("value");


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
		return maskedValue;
	}

}
		

