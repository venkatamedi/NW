/////////////////////////////////////////////////////////////////////////
// Project Shared Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.common.D2D;

import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.*;

import com.k2view.broadway.actors.builtin.StringFormat;
import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.shared.user.UserCode;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
import com.k2view.fabric.common.io.basic.IoSimpleRow;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;
import org.json.JSONObject;

import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.shared.user.UserCode.*;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;

@SuppressWarnings({"unused", "DefaultAnnotationParam"})
public class SharedLogic {


    @out(name = "result", type = Object.class, desc = "")
    @out(name = "customizedKeyComparison", type = String.class, desc = "")
    public static Object fnD2DCompare(Object row, String luName, String Source_Transformation_Function_Name, String Target_Transformation_Function_Name, String customizedKeyComparison, String source_columns_to_Ignore_null, String target_columns_to_Ignore_null) throws Exception {
        Map<String, String> sourceMapTransformationFunction = getFunction2Column(Source_Transformation_Function_Name);
        Map<String, String> targetMapTransformationFunction = getFunction2Column(Target_Transformation_Function_Name);

        LUType luType = LUTypeFactoryImpl.getInstance().getTypeByName(luName);

        Map<String, String> sourceMap = new HashMap<>();
        Map<String, String> targetMap = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder();
        IoSimpleRow ioSimpleRow = (IoSimpleRow) row;

        for (Map.Entry<String, Object> entryRow : ioSimpleRow.entrySet()) {
            String columnName;
            Object columnValue = entryRow.getValue();
            String customFunctionName = null;

            if (entryRow.getKey().startsWith("source")) {
                columnName = entryRow.getKey().replace("source_", "");
                sourceMap.put(columnName + "_k2orig", columnValue != null ? columnValue.toString() : null);
                if (sourceMapTransformationFunction.containsKey(columnName)) {
                    customFunctionName = sourceMapTransformationFunction.get(columnName);
                    columnValue = getTransformedValue(customFunctionName, luType, columnValue);
                }
                sourceMap.put(columnName, columnValue == null ? null : columnValue.toString());
            } else if (entryRow.getKey().startsWith("target")) {
                columnName = entryRow.getKey().replace("target_", "");
                targetMap.put(columnName + "_k2orig", columnValue != null ? columnValue.toString() : null);
                if (targetMapTransformationFunction.containsKey(columnName)) {
                    customFunctionName = targetMapTransformationFunction.get(columnName);
                    columnValue = getTransformedValue(customFunctionName, luType, columnValue);
                }
                targetMap.put(columnName, columnValue == null ? null : columnValue.toString());
            }
        }

        List<String> tctin = new ArrayList<>();
        for (String column : target_columns_to_Ignore_null.split(",")) {
            tctin.add(column.toUpperCase());
        }

        List<String> sctin = new ArrayList<>();
        for (String column : source_columns_to_Ignore_null.split(",")) {
            sctin.add(column.toUpperCase());
        }

        Map<String, Map<String, String>> compareResult = new HashMap<>();
        sourceMap.forEach((key, value) -> {
            if (key.contains("_k2orig")) return;
            Map<String, String> columnResult = new HashMap<>();
            columnResult.put("column_name", key);
            columnResult.put("source_value", value);
            columnResult.put("target_value", targetMap.get(key));
            columnResult.put("source_column_orig_value", sourceMap.get(key + "_k2orig"));
            columnResult.put("target_column_orig_value", targetMap.get(key + "_k2orig"));
            Object targetValue = targetMap.get(key);
            if ((value == null && targetValue == null) || (value != null && value.equals(targetValue)) || (value != null && targetValue == null && tctin.contains(key)) || (value == null && targetValue != null && sctin.contains(key))) {
                columnResult.put("result", "Match");
            } else {
                columnResult.put("result", "Mismatch");
            }
            compareResult.put(key, columnResult);
        });

        JSONObject jsonObject = new JSONObject();
        for (String key : customizedKeyComparison.split(",")) {
            jsonObject.put(key, sourceMap.get(key.toUpperCase()));
        }

        return new Object[]{compareResult, "[" + jsonObject.toString().substring(1, jsonObject.toString().length() - 1) + "]"};
    }


    @out(name = "select", type = String.class, desc = "")
    public static String fnD2DSourceInTarget(String luName, String sourceTableName, String source_Transformation_Function_Name, String targetTableName, String target_Transformation_Function_Name, String customizedKeyComparison, String excludedColumnsNames) throws Exception {
        List<String> excludedColumnsNamesList = new ArrayList<>();
        for (String columnToIgnore : excludedColumnsNames.split(",")) {
            excludedColumnsNamesList.add(columnToIgnore.toUpperCase());
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder prefix = new StringBuilder();
        LUType luType = getLuType();
        Map<String, LudbColumn> columnsMap = luType.ludbObjects.get(sourceTableName).getLudbColumnMap();
        columnsMap.values().stream().filter(columnName -> !excludedColumnsNamesList.contains(columnName.getName().toUpperCase())).forEach(columnName -> {
            columns.append(prefix).append(" source." + columnName.getName() + " as source_" + columnName.getName() + ", target." + columnName.getName() + " as target_" + columnName.getName());
            if (prefix.length() == 0) prefix.append(",");
        });

        String select = "select %s from %s source, %s target on %s";
        StringBuilder stringBuilder = new StringBuilder();
        prefix.setLength(0);
        for (String customizedKey : customizedKeyComparison.split(",")) {
            stringBuilder.append(prefix + " source." + customizedKey + " = " + "target." + customizedKey);
            if (prefix.length() == 0) prefix.append(" and ");
        }

        return String.format(select, columns.toString(), sourceTableName, targetTableName, stringBuilder.toString());
    }


    @out(name = "select", type = String.class, desc = "")
    public static String fnD2DSourceNotInTarget(String luName, String sourceTableName, String targetTableName, String customizedKeyComparison, String searchIND) throws Exception {
        if ("T".equalsIgnoreCase(searchIND)) {
            String tmpSourceTable = sourceTableName;
            sourceTableName = targetTableName;
            targetTableName = tmpSourceTable;
        }

        String select = "select * from %s source where not exists (select 1 from %s target where %s)";
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder columns = new StringBuilder();
        String prefix = "";
        String prefix2 = "";
        for (String customizedKey : customizedKeyComparison.split(",")) {
            stringBuilder.append(prefix2 + " source." + customizedKey + " = " + "target." + customizedKey);
            columns.append(prefix + " source." + customizedKey);
            prefix = " , ";
            prefix2 = " and ";
        }

        return String.format(select, sourceTableName, targetTableName, stringBuilder.toString());
    }


    private static Map<String, String> getFunction2Column(String transformation_Function_Name) {
        Map<String, String> map = new HashMap<>();
        if ("".equals(transformation_Function_Name)) return map;
        for (String functionNdColumn : transformation_Function_Name.split("\\|")) {
            String[] functionNdColumnArr = functionNdColumn.split(",");
            map.put(functionNdColumnArr[0].toUpperCase(), functionNdColumnArr[1]);
        }

        return map;
    }

    private static Object getTransformedValue(String customFunctionName, LUType luType, Object columnValue) throws ReflectiveOperationException, InterruptedException, SQLException {
        String luName = getLuType().luName;
        if (customFunctionName != null) {
            Db.Row row = fabric().fetch(String.format("Broadway %s.%s i_value=?", luName, customFunctionName), columnValue).firstRow();
            columnValue = row.get("value");
            //columnValue = luType.invokeFunction(customFunctionName, null, new Object[]{columnValue});
        }
        return columnValue == null ? null : columnValue.toString();
    }


    @out(name = "ignoreMatchIND", type = Boolean.class, desc = "")
    public static Boolean fnD2DGetIgnoreMatchIND(String match) throws Exception {
        if ("Match".equals(match) && Boolean.parseBoolean(fabric().fetch(String.format("set %s.IGNOREMATCH", getLuType().luName)).firstValue().toString())) {
            return false;
        } else {
            return true;
        }
    }

    @out(name = "customizedKey", type = String.class, desc = "")
    public static String fnD2DGetCustomizedKey(Map<String, Object> rowMap, String customizedKey) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String[] cusKeyArr = customizedKey.split(",");
        for (String cusKey : cusKeyArr) {
            Object cusKeyVal = rowMap.get(cusKey.toUpperCase());
            jsonObject.put(cusKey, cusKeyVal.toString());
        }

        return "[" + jsonObject.toString().substring(1, jsonObject.toString().length() - 1) + "]";
    }


    @out(name = "result", type = Object.class, desc = "")
    public static Object fnD2DGetD2DConfiguration() throws Exception {
        return getTranslationsData("D2DConfiguration");
    }


	@out(name = "result", type = String.class, desc = "")
	public static String fnCreateD2DTables(Boolean createD2DTables, Boolean cleanD2DTables) throws Exception {
		if (createD2DTables != null && createD2DTables) {
		    db("TDM").execute("CREATE TABLE if not exists D2D_FIELD_SUMMARY (EXECUTION_ID character varying(200),IID character varying(200),SOURCE_TABLE_NAME character varying(200),TARGET_TABLE_NAME character varying(200),CUSTOMIZED_KEY character varying(1000),COLUMN_NAME character varying(200),MATCH_RESULT character varying(50),TARGET_VALUE_SECURED character varying(50),SOURCE_COLUMN_VALUE character varying(1000),TARGET_COLUMN_VALUE character varying(1000),SOURCE_COLUMN_VALUE_TRANS character varying(1000),TARGET_COLUMN_VALUE_TRANS character varying(1000), bw_transformation_flow boolean, null_compare boolean, lookup boolean, default_value boolean, PRIMARY KEY(EXECUTION_ID,IID,SOURCE_TABLE_NAME,TARGET_TABLE_NAME,CUSTOMIZED_KEY,COLUMN_NAME))");
		    db("TDM").execute("CREATE TABLE if not exists D2D_ENTITY_SUMMARY (EXECUTION_ID character varying(200),IID character varying(200),NUMBER_OF_FIELDS_MATCH INTEGER,NUMBER_OF_FIELDS_MISMATCH INTEGER,NUMBER_OF_FIELDS_ONLY_IN_SOURCE INTEGER,NUMBER_OF_FIELDS_ONLY_IN_TARGET INTEGER,NUMBER_OF_FIELDS_UNSECURED_IN_TARGET INTEGER,NUMBER_OF_RECORDS_MATCH INTEGER,NUMBER_OF_RECORDS_MISMATCH INTEGER,NUMBER_OF_RECORDS_UNSECURED_IN_TARGET INTEGER,MATCH_RESULT character varying(200), PRIMARY KEY(EXECUTION_ID,IID))");
		    db("TDM").execute("CREATE TABLE if not exists D2D_RECORD_SUMMARY (EXECUTION_ID character varying(200),IID character varying(200),SOURCE_TABLE_NAME character varying(200),TARGET_TABLE_NAME character varying(200),CUSTOMIZED_KEY character varying(1000),NUMBER_OF_FIELDS_MATCH INTEGER,NUMBER_OF_FIELDS_MISMATCH INTEGER,NUMBER_OF_FIELDS_ONLY_IN_SOURCE INTEGER,NUMBER_OF_FIELDS_ONLY_IN_TARGET INTEGER,NUMBER_OF_FIELDS_UNSECURED_IN_TARGET INTEGER,MATCH_RESULT character varying(200), PRIMARY KEY(EXECUTION_ID,IID,SOURCE_TABLE_NAME,TARGET_TABLE_NAME,CUSTOMIZED_KEY))");
		    db("TDM").execute("CREATE TABLE if not exists D2D_TABLE_SUMMARY (EXECUTION_ID TEXT,IID TEXT,SOURCE_TABLE_NAME TEXT,TARGET_TABLE_NAME TEXT,NUMBER_OF_RECORDS_MATCH INTEGER,NUMBER_OF_RECORDS_MISMATCH INTEGER,NUMBER_OF_RECORDS_ONLY_IN_SOURCE INTEGER,NUMBER_OF_RECORDS_ONLY_IN_TARGET INTEGER,NUMBER_OF_RECORDS_UNSECURED_IN_TARGET INTEGER,MATCH_RESULT TEXT, PRIMARY KEY(EXECUTION_ID,IID,SOURCE_TABLE_NAME,TARGET_TABLE_NAME))");
		    db("TDM").execute("CREATE OR REPLACE VIEW public.d2d_execution_summary AS SELECT es.execution_id, count(es.iid) AS total_number_of_entities, ( SELECT count(*) AS total_number_of_matches FROM d2d_entity_summary m WHERE m.execution_id::text = es.execution_id::text AND m.match_result::text = 'Match'::text) AS total_number_of_matches, ( SELECT count(*) AS total_number_of_matches FROM d2d_entity_summary mm WHERE mm.execution_id::text = es.execution_id::text AND mm.match_result::text = 'Mismatch'::text) AS total_number_of_mismatches, (( SELECT count(*) AS total_number_of_matches FROM d2d_entity_summary m WHERE m.execution_id::text = es.execution_id::text AND m.match_result::text = 'Match'::text))::numeric / (count(es.iid)::numeric + 0.0000001) * 100::numeric AS match_rate FROM d2d_entity_summary es GROUP BY es.execution_id");
			db("TDM").execute("CREATE OR REPLACE VIEW public.d2d_execution_tables_summary AS SELECT d2d_table_summary.execution_id, d2d_table_summary.source_table_name, sum(d2d_table_summary.number_of_records_match) AS records_match, sum(d2d_table_summary.number_of_records_mismatch) AS records_mismatch, sum(d2d_table_summary.number_of_records_only_in_source) AS records_in_source, sum(d2d_table_summary.number_of_records_only_in_target) AS records_in_target, max(d2d_table_summary.match_result) AS match_result FROM d2d_table_summary GROUP BY d2d_table_summary.execution_id, d2d_table_summary.source_table_name");
			//POC Report db("TDM").execute("CREATE OR REPLACE VIEW public.d2d_execution_logic_summary AS SELECT execution_id,source_table_name, table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column,table_total_failed_column, (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision as pass_rate , table_total_failed_column / table_total_columns::double precision * 100::double precision as fail_rate from ( select dts.execution_id, dts.source_table_name, SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records, (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name) as table_total_columns, (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column FROM d2d_table_summary dts group by dts.execution_id,dts.source_table_name ) as table_report");
			//POC Report db("TDM").execute("CREATE OR REPLACE VIEW d2d_execution_tables_to_columns_summary AS select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'None' as logic,     SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = false and bw_transformation_flow = false and lookup = false and null_compare = false) as table_total_columns,    (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = false and bw_transformation_flow = false and lookup = false and null_compare = false and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column     from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Default Value' as logic,    SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = true) as table_total_columns,    (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column     from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Null Compare' as logic,     SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and null_compare = true) as table_total_columns,     (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and null_compare = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column  from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Lookup Compare' as logic,   SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and lookup = true) as table_total_columns,   (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and lookup = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column    from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Transformation Compare' as logic,   SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and bw_transformation_flow = true) as table_total_columns,   (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and bw_transformation_flow = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column    from d2d_table_summary dts  group by execution_id,source_table_name ) as table_reportselect execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (   select dts.execution_id,dts.source_table_name ,'None' as logic,     SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = false and bw_transformation_flow = false and lookup = false and null_compare = false) as table_total_columns,    (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = false and bw_transformation_flow = false and lookup = false and null_compare = false and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column     from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Default Value' as logic,    SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = true) as table_total_columns,    (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and default_value = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column     from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Null Compare' as logic,     SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and null_compare = true) as table_total_columns,     (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and null_compare = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column  from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Lookup Compare' as logic,   SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and lookup = true) as table_total_columns,   (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and lookup = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column    from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report union select execution_id,source_table_name,logic,table_total_records,table_total_columns, (table_total_columns - table_total_failed_column) as table_total_pass_column, table_total_failed_column, case when table_total_columns = 0 then 0 else (table_total_columns - table_total_failed_column) / table_total_columns::double precision * 100::double precision END as pass_rate , case when table_total_columns = 0 then 0 else table_total_failed_column / table_total_columns::double precision * 100::double precision END as fail_rate from (    select dts.execution_id,dts.source_table_name ,'Transformation Compare' as logic,   SUM(dts.number_of_records_match) + SUM(dts.number_of_records_mismatch) + SUM(dts.number_of_records_only_in_source) /*+ SUM(dts.number_of_records_only_in_target)*/ as table_total_records,  (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and bw_transformation_flow = true) as table_total_columns,   (select count(distinct dfs.column_name) from d2d_field_summary dfs where dfs.execution_id = dts.execution_id and dfs.source_table_name = dts.source_table_name and bw_transformation_flow = true and dfs.match_result in ('Mismatch','Source Key Not Found In Target')) as table_total_failed_column    from d2d_table_summary dts  group by execution_id,source_table_name ) as table_report");
			return "D2D tables created successfully!";
		} else if (cleanD2DTables != null && cleanD2DTables) {
		    db("TDM").execute("Truncate TABLE D2D_FIELD_SUMMARY");
		    db("TDM").execute("Truncate TABLE D2D_ENTITY_SUMMARY");
		    db("TDM").execute("Truncate TABLE D2D_RECORD_SUMMARY");
		    db("TDM").execute("Truncate TABLE D2D_TABLE_SUMMARY");
		    return "D2D tables truncated successfully!";
		} else {
		    return "No option selected!";
		}
	}


}
