/////////////////////////////////////////////////////////////////////////
// Project Web Services
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.lu.k2_ws.D2D;

import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.*;

import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.shared.user.WebServiceUserCode;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.cdbms.usercode.lu.k2_ws.*;
import com.k2view.fabric.api.endpoint.Endpoint.*;

import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.usercode.common.D2D.SharedLogic.fnCreateD2DTables;
import static com.k2view.cdbms.usercode.common.SharedLogic.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;

@SuppressWarnings({"unused", "DefaultAnnotationParam"})
public class Logic extends WebServiceUserCode {


	@webService(path = "", verb = {MethodType.POST}, version = "1", isRaw = false, isCustomPayload = false, produce = {Produce.XML, Produce.JSON}, elevatedPermission = true)
	public static String wsExecD2D(String luType, String source, String mig_SQL, String IID, Boolean cleanD2DTables, Boolean IGNOREMATCH) throws Exception {
		if (cleanD2DTables != null && cleanD2DTables) wsCreateD2DTables(false, true);
		
		fabric().execute("set sync force");
		
		if (IGNOREMATCH != null && !"".equals(IGNOREMATCH)) {
		    fabric().execute(String.format("set %s.IGNOREMATCH=%s",luType, IGNOREMATCH.toString()));
		}
		
		if (mig_SQL != null && !"".equals(mig_SQL)) {
		    String migID = fabric().fetch(String.format("Migrate %s from %s using ('%s') with ASYNC=true", luType, source, mig_SQL)).firstRow().get("Batch id").toString();
		    return String.format("Migration started successfully!, execution ID %s", migID);
		} else if (IID != null && !"".equals(IID)) {
		    fabric().execute("get ?.?", luType, IID);
		    return "IID synced successfully";
		} else {
		    return "No option selected!";
		}
	}


	@webService(path = "", verb = {MethodType.GET}, version = "1", isRaw = false, isCustomPayload = false, produce = {Produce.XML, Produce.JSON}, elevatedPermission = true)
	public static String wsCreateD2DTables(Boolean createD2DTables, Boolean cleanD2DTables) throws Exception {
		/*
		if (createD2DTables != null && createD2DTables) {
		    db("TDM").execute("CREATE TABLE if not exists D2D_FIELD_SUMMARY (EXECUTION_ID character varying(200),IID character varying(200),SOURCE_TABLE_NAME character varying(200),TARGET_TABLE_NAME character varying(200),CUSTOMIZED_KEY character varying(1000),COLUMN_NAME character varying(200),MATCH_RESULT character varying(50),TARGET_VALUE_SECURED character varying(50),SOURCE_COLUMN_VALUE character varying(1000),TARGET_COLUMN_VALUE character varying(1000),SOURCE_COLUMN_VALUE_TRANS character varying(1000),TARGET_COLUMN_VALUE_TRANS character varying(1000), bw_transformation_flow boolean, null_compare boolean, lookup boolean, default_value boolean, PRIMARY KEY(EXECUTION_ID,IID,SOURCE_TABLE_NAME,TARGET_TABLE_NAME,CUSTOMIZED_KEY,COLUMN_NAME))");
		    db("TDM").execute("CREATE TABLE if not exists D2D_ENTITY_SUMMARY (EXECUTION_ID character varying(200),IID character varying(200),NUMBER_OF_FIELDS_MATCH INTEGER,NUMBER_OF_FIELDS_MISMATCH INTEGER,NUMBER_OF_FIELDS_ONLY_IN_SOURCE INTEGER,NUMBER_OF_FIELDS_ONLY_IN_TARGET INTEGER,NUMBER_OF_FIELDS_UNSECURED_IN_TARGET INTEGER,NUMBER_OF_RECORDS_MATCH INTEGER,NUMBER_OF_RECORDS_MISMATCH INTEGER,NUMBER_OF_RECORDS_UNSECURED_IN_TARGET INTEGER,MATCH_RESULT character varying(200), PRIMARY KEY(EXECUTION_ID,IID))");
		    db("TDM").execute("CREATE TABLE if not exists D2D_RECORD_SUMMARY (EXECUTION_ID character varying(200),IID character varying(200),SOURCE_TABLE_NAME character varying(200),TARGET_TABLE_NAME character varying(200),CUSTOMIZED_KEY character varying(1000),NUMBER_OF_FIELDS_MATCH INTEGER,NUMBER_OF_FIELDS_MISMATCH INTEGER,NUMBER_OF_FIELDS_ONLY_IN_SOURCE INTEGER,NUMBER_OF_FIELDS_ONLY_IN_TARGET INTEGER,NUMBER_OF_FIELDS_UNSECURED_IN_TARGET INTEGER,MATCH_RESULT character varying(200), PRIMARY KEY(EXECUTION_ID,IID,SOURCE_TABLE_NAME,TARGET_TABLE_NAME,CUSTOMIZED_KEY))");
		    db("TDM").execute("CREATE TABLE if not exists D2D_TABLE_SUMMARY (EXECUTION_ID TEXT,IID TEXT,SOURCE_TABLE_NAME TEXT,TARGET_TABLE_NAME TEXT,NUMBER_OF_RECORDS_MATCH INTEGER,NUMBER_OF_RECORDS_MISMATCH INTEGER,NUMBER_OF_RECORDS_ONLY_IN_SOURCE INTEGER,NUMBER_OF_RECORDS_ONLY_IN_TARGET INTEGER,NUMBER_OF_RECORDS_UNSECURED_IN_TARGET INTEGER,MATCH_RESULT TEXT, PRIMARY KEY(EXECUTION_ID,IID,SOURCE_TABLE_NAME,TARGET_TABLE_NAME))");
		    db("TDM").execute("CREATE OR REPLACE VIEW public.d2d_execution_summary AS SELECT es.execution_id, count(es.iid) AS total_number_of_entities, ( SELECT count(*) AS total_number_of_matches FROM d2d_entity_summary m WHERE m.execution_id::text = es.execution_id::text AND m.match_result::text = 'Match'::text) AS total_number_of_matches, ( SELECT count(*) AS total_number_of_matches FROM d2d_entity_summary mm WHERE mm.execution_id::text = es.execution_id::text AND mm.match_result::text = 'Mismatch'::text) AS total_number_of_mismatches, (( SELECT count(*) AS total_number_of_matches FROM d2d_entity_summary m WHERE m.execution_id::text = es.execution_id::text AND m.match_result::text = 'Match'::text))::numeric / (count(es.iid)::numeric + 0.0000001) * 100::numeric AS match_rate FROM d2d_entity_summary es GROUP BY es.execution_id");
			db("TDM").execute("CREATE OR REPLACE VIEW public.d2d_execution_tables_summary AS SELECT d2d_table_summary.execution_id, d2d_table_summary.source_table_name, sum(d2d_table_summary.number_of_records_match) AS records_match, sum(d2d_table_summary.number_of_records_mismatch) AS records_mismatch, sum(d2d_table_summary.number_of_records_only_in_source) AS records_in_source, sum(d2d_table_summary.number_of_records_only_in_target) AS records_in_target, max(d2d_table_summary.match_result) AS match_result FROM d2d_table_summary GROUP BY d2d_table_summary.execution_id, d2d_table_summary.source_table_name");
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
		*/
		
		return fnCreateD2DTables(createD2DTables,cleanD2DTables);
	}


}
