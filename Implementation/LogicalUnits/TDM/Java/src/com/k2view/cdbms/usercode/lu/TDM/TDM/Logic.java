/////////////////////////////////////////////////////////////////////////
// LU Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.lu.TDM.TDM;

import com.k2view.cdbms.shared.Db;
import com.k2view.cdbms.shared.Utils;
import com.k2view.cdbms.shared.user.UserCode;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.desc;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.out;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.type;
import com.k2view.cdbms.usercode.lu.TDM.TdmTaskScheduler;
import com.k2view.cdbms.utils.K2TimestampWithTimeZone;
import com.k2view.fabric.common.Util;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.UserJob;
import static com.k2view.cdbms.usercode.common.SharedGlobals.TDMDB_SCHEMA;
import static com.k2view.cdbms.usercode.common.TDM.SharedLogic.*;
import static com.k2view.cdbms.usercode.common.TdmSharedUtils.SharedLogic.*;
import java.sql.*;
import java.math.*;
import java.io.*;
import java.util.Date;

import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.shared.Globals;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.usercode.common.SharedLogic.*;

@SuppressWarnings({"unused", "DefaultAnnotationParam", "unchecked"})
public class Logic extends UserCode {

	public static final String CASSANDRA_TEXT_TYPE = "TEXT";
	public static final String CASSANDRA_BIGINT_TYPE = "BIGINT";
	public static final String CASSANDRA_INT_TYPE = "INT";
	public static final String CASSANDRA_DOUBLE_TYPE = "DOUBLE";
	public static final String CASSANDRA_BLOB_TYPE = "BLOB";
	public static final String CASSANDRA_BOOL_TYPE = "BOOLEAN";
	public static final String COMMA_DEL = ",";
	public static final String PENDING = "pending";

	public static final String REF = "REF";
	public static final String TASKS = TDMDB_SCHEMA + ".TASKS";
	public static final String TDM = "TDM";
	public static final String DBCASSANDRA = "DB_CASSANDRA";
	public static final String DB_FABRIC = "fabric";
	public static final String TASK_REF_TABLES = TDMDB_SCHEMA + ".TASK_REF_TABLES";
	public static final String PRODUCT_LOGICAL_UNITS = TDMDB_SCHEMA + ".product_logical_units";
	public static final String TASK_REF_EXE_STATS = TDMDB_SCHEMA + ".TASK_REF_EXE_STATS";
	public static final String TASKS_LOGICAL_UNITS = TDMDB_SCHEMA + ".tasks_logical_units";
	public static final String RUNNING = "running";
	public static final String WAITING = "waiting";
	public static final String STOPPED = "stopped";
	public static final String RESUME = "resume";
	public static final String TASK_EXECUTION_LIST = TDMDB_SCHEMA + ".task_execution_list";
    public static final String FAILED = "failed";
    public  static final String COMPLETED = "completed";
	public static final String PAUSED = "paused";
	
	@out(name = "instanceId", type = String.class, desc = "")
	@out(name = "envName", type = String.class, desc = "")
	public static Object fnGetSplittedID(String entityID, String idType, String envID) throws Exception {
		Object[] res = {entityID, envID};

		if ("ENTITY".equals(idType)) {
			res = fnSplitUID(entityID);
			return res;
		} 

		return res;
		
		
		
	}


	@desc("check whether task of EXTRACT type has finished its migrate.\r\n" +
			"if yes - update TASK_EXECUTION_LIST with the status and migration information.\r\n" +
			"It will also clean redis for load tasks.")
	@type(UserJob)
	public static void fnCheckMigrateAndUpdateTDMDB() throws Exception {
		// TDM 5.1- fix the query- check the task_type instead of the fabric_execution_id, since a reference only task does not have the fabric_execution_id (= migrate id)
		String selectFromTaskExecutionListSql = "Select tel.fabric_execution_id, tel.task_id, tel.lu_id, tlu.lu_name, task_type," +
				"tel.process_id, tpost.process_name, tel.task_execution_id, tel.parent_lu_id from " + TDMDB_SCHEMA + ".task_execution_list tel\n" +
				"left join " + TDMDB_SCHEMA + ".tasks_logical_units tlu on tel.task_id = tlu.task_id And tel.lu_id = tlu.lu_id " +
				"left join " + TDMDB_SCHEMA + ".tasks_post_exe_process tpost On tel.task_id = tpost.task_id " +
				"and tel.process_id = tpost.process_id Where Lower(tel.execution_status) = 'running'";
		
		Db.Rows taskExecutionList = null;
		Db.Rows rows = null;
		Db.Rows refRunningLus = null;
		
		
		try {
			//log.info("fnCheckMigrateAndUpdateTDMDB- get running extract tasks");
			taskExecutionList = db(TDM).fetch(selectFromTaskExecutionListSql);
		
			for (Db.Row row : taskExecutionList) {
				//5.1 - Add Try - catch inside the loop to allow handling each iteration seprately
				// and allow continuation of the loop
				Date taskStartDate = new Date(0);
				Date taskEndDate = new Date();
				Integer num_of_processed_ref_tables = 0;
				Integer num_of_copied_ref_tables = 0;
				Integer num_of_failed_ref_tables = 0;
				Integer num_of_incomplete_ref_tables = 0;
				String taskID = "";
				String taskExecutionID = "";
				String updateTaskExecutionListSql = "UPDATE " + TDMDB_SCHEMA + ".task_execution_list SET execution_status = ?, num_of_processed_entities = ?, " +
					"num_of_copied_entities = ?, num_of_failed_entities = ?, end_execution_time = ?, " +
					"num_of_processed_ref_tables = ?, num_of_copied_ref_tables = ?, num_of_failed_ref_tables = ? " +
					"WHERE task_id = ? AND task_execution_id = ? and ((lu_id > 0 and lu_id = ?) or (process_id > 0 and process_id = ?) )";
				String status = COMPLETED;
				
				Long luID = 0L;
				Long parentLuID = 0L;
				Long processID = 0L;
				String luName = "";
				String taskType = "";
				try {
					// TDM 5.1- add a check if row[0] is null (will be null for reference only tasks)
					String batchID = null;
					
					if (row.get("fabric_execution_id") != null)
						batchID = "" + row.get("fabric_execution_id");
		
					taskID = "" + row.get("task_id");
					taskExecutionID = "" + row.get("task_execution_id");
					
					// TDM 6.0 - Since the extract tasks can include multi LUs, each extract task execution will have multi entries;
					// entry per LU, therefore we need to check the data of each LU seperately
					luID = (Long) row.get("lu_id");
					processID = (Long) row.get("process_id");
					luName = "" + row.get("lu_name");
					// TDM 7.0 - Since the this job also handles Load tasks, get the task_type
					taskType = "" + row.get("task_type");
					//log.info("Procssing LU_NAME: " + luName + ", Migrate ID: " + batchID);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
					if (row.get("parent_lu_id") != null) {
						parentLuID = (Long)row.get("parent_lu_id");
					}
					// TDM 5.1- Tali- Fix- get the selection_method from TASKS. If this selection_method is REF- do not call the migrate_summary command, 
					//but check the reference status instead
		
					String selectionMethod = "" + db(TDM).fetch("Select selection_method from " + TASKS + " where task_id=?", taskID).firstValue();
					
					Integer totNoOfRefTables = 0;
					// TDM 5.1- add the update of the reference tables fields
					//log.info("selectionMethod: " + selectionMethod);
					if (selectionMethod != null && selectionMethod.equals(REF)) {
						//log.info("fnCheckMigrateAndUpdateTDMDB- handle reference only task");
						Map<String, Object> refSummaryStatsBuf = fnGetReferenceSummaryData(taskExecutionID);
						//log.info("Getting refSummaryStats for luName: " + luName);
						HashMap <String, Object> refSummaryStats = (HashMap <String, Object>)refSummaryStatsBuf.get(luName);
						if (refSummaryStats != null) {
							totNoOfRefTables = (Integer) refSummaryStats.get("totNumOfTablesToProcess");
		
							//log.info("fnCheckMigrateAndUpdateTDMDB- totNoOfRefTables: " + totNoOfRefTables + " for task execution id: " + taskExecutionID);
							if (totNoOfRefTables > 0) // if the task has reference
							{
								num_of_incomplete_ref_tables = (Integer) refSummaryStats.get("numOfProcessingRefTables") + (Integer) refSummaryStats.get("numberOfNotStartedRefTables");
								//log.info("num of incomplete ref tables: " + num_of_incomplete_ref_tables);
		
								if (num_of_incomplete_ref_tables == 0) { // the processing of the reference tables is not completed yet
								
		
									// If the execution of the reference tables ended- get the summary information for the referenece tables
									Date refMinDate = new Date(0);
									Date refMaxDate = new Date(0);
									//log.info("minStartExecutionDate: " + refSummaryStats.get("minStartExecutionDate"));
									//log.info("maxEndExecutionDate: " + refSummaryStats.get("maxEndExecutionDate"));
									if (!"".equals(refSummaryStats.get("minStartExecutionDate"))) {
										refMinDate = (Date) refSummaryStats.get("minStartExecutionDate");
									}
									if (!"".equals(refSummaryStats.get("maxEndExecutionDate"))) {
										refMaxDate = (Date) refSummaryStats.get("maxEndExecutionDate");
									}
		
									num_of_processed_ref_tables = (Integer) refSummaryStats.get("numOfProcessedRefTables");
									num_of_copied_ref_tables = (Integer) refSummaryStats.get("numOfCopiedRefTables");
									num_of_failed_ref_tables = (Integer) refSummaryStats.get("numOfFailedRefTables");
									//log.info("num_of_copied_ref_tables: " + num_of_copied_ref_tables);
									if (num_of_copied_ref_tables == 0)
										status = FAILED;
		
									// TDM 5.1- change the start and end date parameters and add the parameters for the reference tables
									//log.info("Updating task status to failed as no reference table was copied");
									db(TDM).execute(updateTaskExecutionListSql, status, 0, 0, 0, //refMinDate.toString(), -- No need to update the start time
										refMaxDate.toString(), num_of_processed_ref_tables, num_of_copied_ref_tables, 
										num_of_failed_ref_tables, taskID, taskExecutionID, luID, processID);
								}
							} // if(totNoOfRefTables > 0)
						}//if (refSummaryStats != null)
						//Add reference tables to TASK_EXECUTION_ENTITIES
						//log.info("Calling fnTdmUpdateTaskExecutionEntities for reference only task");
						if(luID > 0){
							fnTdmUpdateTaskExecutionEntities(taskExecutionID, luID, luName);
						}else if(processID != null && processID > 0){
							String finalTaskExecutionID = taskExecutionID;
							Long finalProcessID = processID;
							Util.rte(()-> db(TDM).execute("UPDATE " + TDMDB_SCHEMA + ".task_execution_list SET execution_status=?, end_execution_time = (now() at time zone 'utc') WHERE task_execution_id=? and process_id=?", "completed", finalTaskExecutionID, finalProcessID));
						}
					}// if(selectionMethod != null && selectionMethod.equals(REF))
					else // the task contains entities (but can still have reference tables in addition to the entities)
					{
					
						Map<String, Object> batchStats = null;
						if (batchID != null && !"".equals(batchID)) {
							batchStats = fnBatchStats(batchID);
						}
						//log.info("fnCheckMigrateAndUpdateTDMDB - Returned output from fnRunBatchSummary: " + batchStats);
						
						if (batchStats != null) // response from migrate_summary command (which is run in wsMigrateStats ) should be returned with all levels (Node,DC,Cluster). if its returned without level DC - skip the update command and wait to next time the user job will be executed
						{
							status = getBatchStatus(batchStats.get("Status"));
		
							//log.info("fnCheckMigrateAndUpdateTDMDB- migration status for task execution id " + taskExecutionID + " is: " + status);
							// In case there are reference table to be handled, if the refernece job has finished
							String sqlrefSts = "select distinct lu_name from " + TDMDB_SCHEMA + ".task_ref_exe_stats es, " + TDMDB_SCHEMA + ".task_ref_tables rt where " +
								"lower(execution_status) in ('waiting', 'running', 'pending') and " +
								"task_Execution_id = ? and es.task_id = rt.task_id and es.task_ref_table_id = rt.task_ref_table_id " +
								"group by lu_name";
							
							refRunningLus = db(TDM).fetch(sqlrefSts, taskExecutionID);
							
							Boolean refStillRunning = false;
							for (Db.Row refRunningLu : refRunningLus) {
								if (luName.equals(refRunningLu.get("lu_name"))) {
									refStillRunning = true;
									break;
								}
							}
							
							//log.info("fnCheckMigrateAndUpdateTDMDB - luName: " + luName + ", refStillRunning: " + refStillRunning +
							//	", status: " + status);
							// Tali- fix defect- add the check that status is not "generate_iid_list"
							// Taha - check that all refernece tables were copied
							if (!status.equalsIgnoreCase(RUNNING) && !status.equalsIgnoreCase("generate_iid_list") 
									&& !status.equalsIgnoreCase("new") && !status.equalsIgnoreCase("WAITING_FOR_JOB")
									&& !refStillRunning) {
							
										
								// TDM 7.5 - if the task was paused or cancelled not from TDMGUI, fail the task
								if (PAUSED.equals(status) || STOPPED.equals(status)) {						
									log.error("The task execution ID: " + taskExecutionID + ", of LU: " + luName + "was " + status + " from outside TDM, failing it");
									status = FAILED;
								}
								String total = "" + batchStats.get("Total");
								String failed = "" + batchStats.get("Failed");
								
								// TDM 7.5 - if the total is zero for a root LU, then set the status of the task to failed
								if (Long.parseLong(total) == 0 && parentLuID == 0) {
									status = FAILED;
									log.error("No Instances were handled by Task");
									String insertSql = "insert into " + TDMDB_SCHEMA + ".TASK_EXE_ERROR_DETAILED (TASK_EXECUTION_ID,LU_NAME,ENTITY_ID,IID,TARGET_ENTITY_ID, " +
										"ERROR_CATEGORY, ERROR_MESSAGE) " +
										"VALUES (?, ?, ?, ?, ?, ?, ?)";
									db(TDM).execute(insertSql, taskExecutionID, luName, " ", " ", " ", "No Instances were handled by Task", "No Instances");
								}
								//If all the entities failed, then set the status of the task to failed
								if (failed.equals(total) && Long.parseLong(total) > 0) {
									status = FAILED;
								}
								
								//log.info("fnCheckMigrateAndUpdateTDMDB - total: " + total + ", failed: " + failed);
								String copied = "" + batchStats.get("Succeeded");//String.valueOf(Integer.parseInt(total) - Integer.parseInt(failed));
		
								String start_time = "" + batchStats.get("Start time");
								String end_time = "";
								if (!FAILED.equals(status)) {
										end_time = "" + batchStats.get("End time");
								}
		
								// TDM 5.1- add handle of reference tables
		
								//log.info("fnCheckMigrateAndUpdateTDMDB- start time of batch: " + start_time + " , end time of batch: " + end_time);
		
								taskStartDate = formatter.parse(start_time);
								if (!FAILED.equals(status)) {
									taskEndDate = formatter.parse(end_time);
								} 
		
								Map<String, Object> refSummaryStatsBuf = fnGetReferenceSummaryData(taskExecutionID);
								
								//for (Object map : refSummaryStatsBuf.values()) {
								//	HashMap <String, Object> refSummaryStats = (HashMap <String, Object>)map;
								num_of_processed_ref_tables = 0;
								num_of_copied_ref_tables = 0;
								num_of_failed_ref_tables = 0;
								num_of_incomplete_ref_tables = 0;
								
								//log.info("Getting refSummaryStats for luName: " + luName);
								HashMap <String, Object> refSummaryStats = (HashMap <String, Object>)refSummaryStatsBuf.get(luName);
								if (refSummaryStats != null) {
		
									totNoOfRefTables = (Integer) refSummaryStats.get("totNumOfTablesToProcess");
									
									//log.info("refSummaryStats - totNumOfTablesToProcess " + totNoOfRefTables + " for luName: " + luName);
		
									//log.info("fnCheckMigrateAndUpdateTDMDB- totNoOfRefTables: " + totNoOfRefTables + " for task execution id: " + taskExecutionID);
									if (totNoOfRefTables > 0) // if the task has reference
									{
										num_of_incomplete_ref_tables = (Integer) refSummaryStats.get("numOfProcessingRefTables") + (Integer) refSummaryStats.get("numberOfNotStartedRefTables");
		
										//log.info("num of incomplete ref tables: " + num_of_incomplete_ref_tables);
		
										if (num_of_incomplete_ref_tables > 0) // the processing of the reference tables is not completed yet
											continue;
		
										// If the execution of the reference tables ended- get the summary information for the refernece tables
		
										//log.info("fnCheckMigrateAndUpdateTDMDB - PARSING THE DATES OF REF TABLES");
										Date refMinDate = new Date(Long.MAX_VALUE);
										Date refMaxDate = new Date(Long.MIN_VALUE);
										if (!"".equals(refSummaryStats.get("minStartExecutionDate"))) {
											refMinDate = (Date) refSummaryStats.get("minStartExecutionDate");
										}
										if (!"".equals(refSummaryStats.get("maxEndExecutionDate"))) {
											refMaxDate = (Date) refSummaryStats.get("maxEndExecutionDate");
										}
		
										num_of_processed_ref_tables = (Integer) refSummaryStats.get("numOfProcessedRefTables");
										num_of_copied_ref_tables = (Integer) refSummaryStats.get("numOfCopiedRefTables");
										num_of_failed_ref_tables = (Integer) refSummaryStats.get("numOfFailedRefTables");
										//log.info("luName: " + luName + " - numOfProcessedRefTables: " + num_of_processed_ref_tables);
										//log.info("numOfCopiedRefTables: " + num_of_copied_ref_tables + ", numOfFailedRefTables: " + num_of_failed_ref_tables);
		
										// Check the refMinDate adn the refMaxDate against the start and end execution date of the migrate of entities
		
										if (taskStartDate.after(refMinDate))
											taskStartDate = refMinDate;
		
										if (taskEndDate.before(refMaxDate))
											taskEndDate = refMaxDate;
		
									} // end of if the task has reference tables
								}//if (refSummaryStats != null)
								
								// Taha - 16-Sep-19 - TDM5.6.0 - Add entries to TDM table TASK_EXECUTION_ENTITIES
								//log.info("fnCheckMigrateAndUpdateTDMDB - Calling fnTdmUpdateTaskExecutionEntities for LU_NAME: " + luName);
								//TDM 7.0, For Load tasks, the task_execution_entities table is populated by the BF, therefore handling only Extract tasks
								if ("extract".equalsIgnoreCase(taskType)) {
									fnTdmUpdateTaskExecutionEntities(taskExecutionID, luID, luName);
								}
								
								//TDM 6.1.1 - 20-may-20 - Insert the migrate errors to the TDM DB Error table
								//log.info("fnCheckMigrateAndUpdateTDMDB - Calling fnUpdateTaskErrorsDetails for LU_NAME: " + luName);
								//TDM 7.0, For Load tasks, the error details table is populated by the BF, therefore handling only Extract tasks
								if ("extract".equalsIgnoreCase(taskType) && !Util.isEmpty(luName)) {
									fnUpdateTaskErrorsDetails(taskExecutionID, luName, batchID);
								}
								//log.info("fnCheckMigrateAndUpdateTDMDB - finished updating TASK_EXECUTION_ENTITIES");
								
								// TDM 5.1- change the start and end date parameters and add the parameters for the reference tables
								//log.info("Updating task for luName: " + luName + " to status: " + status + " with:");
								//log.info("numOfProcessedRefTables: " + num_of_processed_ref_tables);
								//log.info("numOfCopiedRefTables: " + num_of_copied_ref_tables + ", numOfFailedRefTables: " + num_of_failed_ref_tables);
								
								
								db(TDM).execute(updateTaskExecutionListSql, new Object[]{status, total, copied, failed, //taskStartDate.toString(),  -- no need to set the start time
										taskEndDate.toString(), num_of_processed_ref_tables, num_of_copied_ref_tables, num_of_failed_ref_tables, 
										taskID, taskExecutionID, luID, processID});
								//log.info("fnCheckMigrateAndUpdateTDMDB - Updated the status");
							} // end of if status is not running
						}// end if if ( (batchStats).contains("\"Level\" : \"Cluster\"") )
					} // end of else (if the selection method is not 'REF')
				} catch (Exception e){
					log.error("Task Failed");
					log.error(e.getMessage(),e);
					status = FAILED;
					db(TDM).execute(updateTaskExecutionListSql, new Object[]{status, "0", "0", "0", //taskStartDate.toString(), -- No need to set the start time
							taskEndDate.toString(), num_of_processed_ref_tables, num_of_copied_ref_tables, num_of_failed_ref_tables, taskID, taskExecutionID, luID, processID});
					throw e;
				}
			} // end of for loop on the task_execution_list
		} finally {
			if (taskExecutionList != null){
				taskExecutionList.close();
			}
		}
		
		// Tali- 15-Oct-19- add the get into TDM for all completed tasks
		// In case of extract task with multi LUs, if it was stopped, the TDM LU will be updated only when all LUs are completed.
		String sqlCompletedTasks= "select distinct task_execution_id, upper(out.task_type) as task_type, selection_method, sync_mode from " + TDMDB_SCHEMA + ".task_execution_list out, " + TDMDB_SCHEMA + ".tasks t1 where  " + 
		"out.task_id = t1.task_id and out.synced_to_fabric = FALSE "+ 
		"and not exists (select 1 from " + TDMDB_SCHEMA + ".task_execution_list tbl, " + TDMDB_SCHEMA + ".tasks t where tbl.task_execution_id = out.task_execution_id " +  
		"and t.task_id = tbl.task_id " +
		"and tbl.execution_status not in ('completed','failed','killed') )";
		
		String taskExecutionId= "";
		String taskType="";
		String selectionMethod = "";
		String syncMode = "";
		
		try
		{
			rows = db(TDM).fetch(sqlCompletedTasks);
		
			for (Db.Row row:rows){
				// TALi- 25-Sep-19- TDM 5.5- add the clean of redis for completed load tasks
				if(row.get("task_execution_id") != null)
				{
					taskExecutionId = "" + row.get("task_execution_id");
					taskType = "" + row.get("task_type");
					selectionMethod = "" + row.get("selection_method");
					syncMode = "" + row.get("sync_mode");
					
					//log.info("fnCheckMigrateAndUpdateTDMDB - Loading task: " + taskExecutionId + " to TDM");
					int count = 0;
					int retries = 5;
					while(!Thread.currentThread().isInterrupted()) {
						try
						{
							// Get the task into the TDM LU
							fabric().execute("get TDM." + taskExecutionId);
							db(TDM).execute("update " + TDMDB_SCHEMA + ".task_execution_list set synced_to_fabric=TRUE where task_execution_id = ?", taskExecutionId );
							break;
						}catch(Exception e){
							if (e instanceof InterruptedException || e.getCause() instanceof InterruptedException) {
								throw e;
							}
							if (++count >= retries) {
								db(TDM).execute("update " + TDMDB_SCHEMA + ".task_execution_list set synced_to_fabric=TRUE where task_execution_id = ?", taskExecutionId );
								log.error("Failed to get task execution id: " + taskExecutionId + " into TDM LU. Updating the synced_to_fabric indicator of TDM DB. Error message: " + e.getMessage(),e);
								retries = 0;
								break;
							} else {
								log.warn("Failed to update to get the task execution id: " + taskExecutionId + " into TDM LU.  this is retry number: " + count);
								Thread.sleep(5000);
							}
						}
		
					}
		
					if (!"OFF".equalsIgnoreCase(syncMode) && !selectionMethod.equals(REF)) {
						// Refresh the LU Params Materialized View
						// For each LU Param view asssociated with this LU...
						db(TDM).fetch("" +
								"WITH business_entities AS ( " +
								"   SELECT DISTINCT 'lu_relations_' || be.be_name || '_' || tel.source_env_name as view_name  " +
								"   FROM            " + TDMDB_SCHEMA + ".task_execution_list tel  " +
								"              JOIN " + TDMDB_SCHEMA + ".tasks_logical_units tlu   ON (tel.task_id = tlu.task_id) " +
								"              JOIN " + TDMDB_SCHEMA + ".product_logical_units plu ON (tlu.lu_name = plu.lu_name) " +
								"              JOIN " + TDMDB_SCHEMA + ".business_entities be      ON (plu.be_id = be.be_id)  " +
								"   WHERE           tel.task_execution_id=? " +
								") " +
								"SELECT bes.view_name   " +
								"FROM business_entities bes   " +
								"  JOIN pg_matviews ON (bes.view_name = pg_matviews.matviewname)",taskExecutionId).each(r-> {
		
							// Refresh the view
							db(TDM).execute(String.format("REFRESH MATERIALIZED VIEW " + TDMDB_SCHEMA + ".\"%s\"",r.get("view_name")));
						});
					}
				}
			}
		}catch(Exception e)
		{
			log.error("Failed to clean redis for load task execution id: " + taskExecutionId + ". Error message: " + e.getMessage(),e);
			throw e;
			
		} finally {
			if (taskExecutionList != null) {
				taskExecutionList.close();
			}
			
			if (refRunningLus != null) {
				refRunningLus.close();
			}
			
			if (rows != null) {
				rows.close();
			}
		}
	}

	private static boolean isCommonlyUsedType(Object val) {
		// Cover all cases of Integer, Decimal, Double, Float etc...
		if (val instanceof Number) {
			return true;
		}
		if (val instanceof String) {
			return true;
		}
		return false;
	}

	@out(name = "result", type = Object.class, desc = "")
	public static Object typeCheck(Object val) throws Exception {
		try {
		
			if (val instanceof Utils.NullType || val == null) {
				return null;
			}
		
			// Check first for the commonly used types to save all other 'instanceof'.
			if (isCommonlyUsedType(val)) {
				//Class cls = val.getClass();
				//log.info("val instanceof " + cls.getName());
				if (val instanceof java.math.BigDecimal){
					return  ((BigDecimal) val).doubleValue();
				}else{
					return val;
				}
			}
		
			if (val instanceof K2TimestampWithTimeZone) {
				//log.info("val " + val + "instanceof K2TimestampWithTimeZone ");
				//return ((Date) val).getTime();
				/*return StringUtils.isBlank(TypeConversion.DATETIME_WITH_TIMEZONE_FORMAT) ? val.toString()
						:((K2TimestampWithTimeZone) val).formatWithTZ(TypeConversion.DATETIME_WITH_TIMEZONE_FORMAT);*/
				return ((Date) val).toString();
			}
		
			if (val instanceof java.sql.Timestamp) {
				//return ((Date) val).getTime();
				return ((Date) val).toString();
			}
		
			if (val instanceof java.sql.Date) {
				//return ((Date) val).getTime();
				/*return StringUtils.isBlank(DATE_FORMAT) ? val.toString()
						: new SimpleDateFormat(DATE_FORMAT).format((java.sql.Date) val);*/
				return ((Date) val).toString();
			}
		
			if (val instanceof java.sql.Time) {
				//return ((Date) val).getTime();
				/*return StringUtils.isBlank(TIME_FORMAT) ? val.toString()
						: new SimpleDateFormat(TIME_FORMAT).format((java.sql.Time) val);*/
				return ((Date) val).toString();
			}
		
			if (val instanceof java.util.Date) {
				//log.info("val " + val + " instanceof java.util.Date");
				//return ((Date) val).getTime();
				/*return StringUtils.isBlank(DATETIME_FORMAT) ? val.toString()
						: new SimpleDateFormat(DATETIME_FORMAT).format((java.util.Date) val);*/
				return ((Date) val).toString();
			}
		
			if (val instanceof Blob) {
				//return ((Blob) val).getBytes(1, (int) ((Blob) val).length());
				return ((ByteBuffer)val).array();
			}
		
			if (val instanceof Clob) {
				return Utils.clobToString(((Clob) val));
			}
		
			if (val instanceof ByteBuffer){
				return ((ByteBuffer)val).array();
			}
		
			return val;
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}


	@desc("This function will populate field ROOT_ENTITY_STATUS \r\n" +
			"in TASK_EXECUTION_LINK_ENTITIES table.")
	public static void fnEnrSetRootEntSts() throws Exception {
		// TDM 6.0  - 11-Sep-19 - New enrichment function to populate ROOT_ENTITY_STATUS in TASK_EXECUTION_LINK_ENTITIES
		String updateSql1 = "Update TASK_EXECUTION_LINK_ENTITIES set root_entity_status = 'failed' " +
			"where ROOT_LU_NAME||TARGET_ROOT_ENTITY_ID in " +
			"(select ROOT_LU_NAME||TARGET_ROOT_ENTITY_ID from TASK_EXECUTION_LINK_ENTITIES where Execution_Status <> 'completed')";
		/*"where Execution_Status = 'completed' and not exists " +
		"(select 1 from TDM.TASK_EXECUTION_LINK_ENTITIES t2 " +
		"where t2.TARGET_ROOT_ENTITY_ID = TASK_EXECUTION_LINK_ENTITIES.TARGET_ROOT_ENTITY_ID " +
		"and t2.ROOT_LU_NAME = TASK_EXECUTION_LINK_ENTITIES.ROOT_LU_NAME " +
		"and (t2.Execution_Status <> 'completed' or t2.Execution_Status is null))";
		*/
		String updateSql2 = "Update TASK_EXECUTION_LINK_ENTITIES set root_entity_status = 'completed' where root_entity_status is null";
		
		// Update status to 'failed' if all records, related to this root entity have a completed status
		ludb().execute(updateSql1);
		//Update remaining records to 'completed'
		ludb().execute(updateSql2);
	}


	@desc("This enrichment function updates TASK_EXECUTION_LIST table for ended tasks")
	public static void fnUpdateTaskSummaryTable() throws Exception {
		// TDM 6.0 - New Enrichment function
		String taskExecId = "" + fabric().fetch("SELECT iid(?)", TDM).firstValue();
			
		//String taskId = "" + fabric().fetch("select distinct task_id from task_execution_list").firstValue();
		//log.info("fnUpdateTaskSummaryTable - taskID: " + taskId);
		String executionStatus = "completed";
		String startExecTime = "";
		String endExecTime = "";
		int totProcessedRootEnt = 0;
		int totCopiedRootEnt = 0;
		int totFailedRootEnt = 0;
		int totProcessedRefTabs = 0;
		int totCopiedRefTabs = 0;
		int totFailedRefTabs = 0;
		String versionDateTime = "";
		String versionExpDate = "";
		String updateDate = "";
		
		totProcessedRootEnt = (int)fabric().fetch("select count(*) from (Select distinct be_root_entity_id, TARGET_ROOT_ENTITY_ID from TDM.TASK_EXECUTION_LINK_ENTITIES)").firstValue();
		totCopiedRootEnt = (int)fabric().fetch("select count(*) from (Select distinct be_root_entity_id, TARGET_ROOT_ENTITY_ID " +
							"from TDM.TASK_EXECUTION_LINK_ENTITIES where root_entity_status = 'completed' " +
							"EXCEPT Select be_root_entity_id, TARGET_ROOT_ENTITY_ID from TDM.TASK_EXECUTION_LINK_ENTITIES " +
							"where root_entity_status <> 'completed')").firstValue();
		totFailedRootEnt  = (int)fabric().fetch("select count(*) from (Select distinct be_root_entity_id, TARGET_ROOT_ENTITY_ID from TDM.TASK_EXECUTION_LINK_ENTITIES  where root_entity_status <> 'completed')").firstValue();
		
		totProcessedRefTabs = (int)fabric().fetch("select count(distinct entity_id) from TDM.task_execution_entities t where t.ID_Type = 'REFERENCE'").firstValue();
		totCopiedRefTabs = (int)fabric().fetch("select count(distinct entity_id) from TDM.task_execution_entities t where t.ID_Type = 'REFERENCE' and t.Execution_Status = 'completed' and not exists (select 1 from TDM.task_execution_entities t2 where t2.entity_id = t.entity_id  and ifNull(t2.Execution_Status, 'failed') <> 'completed')").firstValue();
		totFailedRefTabs = (int)fabric().fetch("select count(distinct entity_id) from TDM.task_execution_entities t where t.ID_Type = 'REFERENCE' and ifNull(t.Execution_Status, 'failed') <> 'completed'").firstValue();
		
		int totNumOfProcessedPostExecutions = (int) fabric().fetch("select count(*) from TDM.task_execution_list where task_execution_id = ? and process_id > 0", taskExecId).firstValue();
		int totNumOfSucceededPostExecutions = (int) fabric().fetch("select count(*) from TDM.task_execution_list where task_execution_id = ? and process_id > 0 and execution_status ='completed'", taskExecId).firstValue();
		int totNumOfFailedPostExecutions = (int) fabric().fetch("select count(*) from TDM.task_execution_list where task_execution_id = ? and process_id > 0 and execution_status !='completed'", taskExecId).firstValue();
		
		String rootTaskStatus = "" + fabric().fetch("select execution_status from TDM.task_execution_list where parent_lu_name = \"null\"").firstValue();
		
		if ((totCopiedRootEnt == 0 && totCopiedRefTabs == 0) || ("failed".equalsIgnoreCase(rootTaskStatus)) ) {
			executionStatus = "failed";
		}
		
		//TDM 7 - in case the task was stopped the summary status should be updated to stopped
		int numOfStoppedLUs = (int)fabric().fetch("select count(*) from TDM.task_execution_list where execution_status = 'stopped'").firstValue();
		
		if (numOfStoppedLUs > 0) {
			executionStatus = "stopped";
		}
		
		startExecTime = "" + fabric().fetch("select min(start_execution_time) from task_execution_list").firstValue();
		endExecTime = "" + fabric().fetch("select max(end_execution_time) from task_execution_list").firstValue();
		
		versionDateTime = "" + fabric().fetch("select max(version_datetime) from task_execution_list").firstValue();
		versionExpDate = "" + fabric().fetch("select max(version_expiration_date) from task_execution_list").firstValue();
		
		updateDate = Instant.now().toString();
		//log.info("Setting Dates - start_execution_time: " + startExecTime + ". end_execution_time: " + endExecTime +
		//		 ", version_datetime: <" + versionDateTime + ">, version_expiration_date: <" + versionExpDate + ">, update_date: " + updateDate);
		
		String sqlUpdateTaskSummaryTable = "update " + TDMDB_SCHEMA + ".task_execution_summary set execution_status = ?, " +
				"tot_num_of_processed_root_entities = ?, tot_num_of_copied_root_entities = ?, tot_num_of_failed_root_entities = ?, " +
				"tot_num_of_processed_ref_tables = ?, tot_num_of_copied_ref_tables = ?, tot_num_of_failed_ref_tables = ?, " +
				"tot_num_of_processed_post_executions = ?, tot_num_of_succeeded_post_executions = ?, tot_num_of_failed_post_executions = ?, update_date = ?";
		
		ArrayList<Object> paramList = new ArrayList<>();
		paramList.add(executionStatus);
		paramList.add(totProcessedRootEnt);
		paramList.add(totCopiedRootEnt);
		paramList.add(totFailedRootEnt);
		paramList.add(totProcessedRefTabs);
		paramList.add(totCopiedRefTabs);
		paramList.add(totFailedRefTabs);
		paramList.add(totNumOfProcessedPostExecutions);
		paramList.add(totNumOfSucceededPostExecutions);
		paramList.add(totNumOfFailedPostExecutions);
		paramList.add(updateDate);
		
		if (!"null".equals(startExecTime) && !"".equals(startExecTime)) {
			sqlUpdateTaskSummaryTable += ", start_execution_time = ?";
			paramList.add(startExecTime);
		}
		if (!"null".equals(endExecTime) && !"".equals(endExecTime)) {
			sqlUpdateTaskSummaryTable += ", end_execution_time = ?";
			paramList.add(endExecTime);
		}
		if (!"null".equals(versionDateTime) && !"".equals(versionDateTime)) {
			sqlUpdateTaskSummaryTable += ", version_datetime = ?";
			paramList.add(versionDateTime);
		}
		if (!"null".equals(versionExpDate) && !"".equals(versionExpDate)) {
			sqlUpdateTaskSummaryTable += ", version_expiration_date = ?";
			paramList.add(versionExpDate);
		}
		sqlUpdateTaskSummaryTable += " where task_execution_id = ?";
		paramList.add(taskExecId);
		Object[] params = paramList.toArray();
		
		db(TDM).execute(sqlUpdateTaskSummaryTable,params);
	}


	public static void tdmUpdateTaskExecutionEntities(String taskExecutionId, Long luId, String luName) throws Exception {
		// TALI- 5-May-20- add a select of selection_method  + fabric_Execution_uid columns. 
		//Remove the condition of fabric_execution_id is not null to support reference only task

		String taskExeListSql = "SELECT L.SOURCE_ENV_NAME, L.CREATION_DATE, L.START_EXECUTION_TIME, " +
						"L.END_EXECUTION_TIME, L.ENVIRONMENT_ID, T.VERSION_IND, T.TASK_TITLE, L.VERSION_DATETIME, T.SELECTION_METHOD, COALESCE(FABRIC_EXECUTION_ID, '') AS FABRIC_EXECUTION_ID " +
						"FROM " + TDMDB_SCHEMA + ".TASK_EXECUTION_LIST L, " + TDMDB_SCHEMA + ".TASKS T " +
						"WHERE TASK_EXECUTION_ID = ? AND LU_ID = ? AND L.TASK_ID = T.TASK_ID";
		
		String fabricExecID = "";
		String srcEnvName = "";
		String creationDate = "";
		String startExecDate = "";
		String endExecDate = "";
		String envID = "";
		String entityID = "";
		String targetEntityID = "";
		String execStatus = "";
		String idType = "ENTITY";
		String IID = "";
		String versionInd = "";
		String versionName = "";
		String verstionDateTime = "";
		// Add selectionMethod
		String selectionMethod = "";
		
		final String UIDLIST = "UIDList";
		
		String insertSql = "INSERT INTO " + TDMDB_SCHEMA + ".TASK_EXECUTION_ENTITIES(" +
				"TASK_EXECUTION_ID, LU_NAME, ENTITY_ID, TARGET_ENTITY_ID, ENV_ID, EXECUTION_STATUS, ID_TYPE, " +
				"FABRIC_EXECUTION_ID, IID, SOURCE_ENV";
		String insertBinding = "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
		
		Db.Row taskData = db(TDM).fetch(taskExeListSql, taskExecutionId, luId).firstRow();
		
		
		//log.info("tdmUpdateTaskExecutionEntities: TASK_EXECUTION_ID: " + TASK_EXECUTION_ID + ", LU_ID: " + LU_ID + ", LU_NAME: " + LU_NAME);
		if(!taskData.isEmpty()) {
			fabricExecID = "" + taskData.get("fabric_execution_id");
			srcEnvName = "" + taskData.get("source_env_name");
			creationDate = "" + taskData.get("creation_date");
			startExecDate = "" + taskData.get("start_execution_time");
			endExecDate = "" + taskData.get("end_execution_time");
			envID = "" + taskData.get("environment_id");
			versionInd = "" + taskData.get("version_ind");
			versionName = "" + taskData.get("task_title");
			verstionDateTime = "" + taskData.get("version_datetime");
		
			// Add selection method and fabric_execution_id
			selectionMethod = "" + taskData.get("selection_method");
						
			//log.info("creationDate: " + creationDate + ", startExecDate: " + startExecDate + ", endExecDate: " + endExecDate + ", SELECTION METHOD: " + selectionMethod);
			 
			if(!"null".equals(creationDate) && !"".equals(creationDate)) {
				insertSql += ", CREATION_DATE";
				creationDate = creationDate.substring(1);
				insertBinding += ", ?";
			} 
			
			if(!"null".equals(startExecDate) && !"".equals(startExecDate)) {
				insertSql += ", ENTITY_START_TIME";
				insertBinding += ", ?";
			}
			
			if(!"null".equals(endExecDate) && !"".equals(endExecDate)) {
				insertSql += ", ENTITY_END_TIME";
				insertBinding += ", ?";
			}
			
			if ("true".equals(versionInd)) {
				insertSql += ", VERSION_NAME,VERSION_DATETIME";
				insertBinding += ", ?, ?";
			}
			
			insertBinding += ")";
			insertSql += ") " + insertBinding;
			insertSql += " ON CONFLICT ON CONSTRAINT task_execution_entities_pkey Do update set execution_status = ?";
		
			// TALI- 5-May-20 - add a check of the sectionMethod. Do not get the list of IIDs for reference only task
		
			Map<String, Map> migrationList = new LinkedHashMap<String, Map>();

			if(!selectionMethod.equals("REF") && !fabricExecID.equals("")) {
				migrationList = (Map<String, Map>) fnGetIIDListForMigration(fabricExecID, null);
			}
			
			//log.info ("tdmUpdateTaskExecutionEntities - insertSql: " + insertSql);
			if (migrationList.containsKey("Copied entities per execution")) {
				LinkedHashMap<String, Object> m1 = (LinkedHashMap<String, Object>) migrationList.get("Copied entities per execution");
				
				if (m1.containsKey(UIDLIST)) {
					List<Object> copied_UID_list = (List<Object>) m1.get(UIDLIST);
					//log.info("Size of copied_UID_list: " + copied_UID_list.size());
					for (Object UID : copied_UID_list) {
						Map<Object, Object> innerCopiedUIDMap = (Map<java.lang.Object, java.lang.Object>) UID;
			
						for (Map.Entry<Object, Object> copiedUID : innerCopiedUIDMap.entrySet()) {
							targetEntityID = (String) copiedUID.getKey();
							IID = (String) copiedUID.getKey();
							entityID = (String) copiedUID.getValue();
							execStatus = COMPLETED;
							
							ArrayList<String> paramList = new ArrayList<>();
							
							paramList.add(taskExecutionId);
							paramList.add(luName);
							paramList.add(entityID);
							paramList.add(targetEntityID);
							paramList.add(envID);
							paramList.add(execStatus);
							paramList.add(idType);
							paramList.add(fabricExecID);
							paramList.add(IID);
							paramList.add(srcEnvName);
							
							//log.info("Inserting Copied: LU_NAME: " + LU_NAME + ", TASK_EXECUTION_ID: " + TASK_EXECUTION_ID + ", entityID: " + entityID);
							//In postgres, timestamp fields cannot be set to empty string,
							//therefore date fields should be insterted only if they have value		
							//log.info("Inserting: TASK_EXECUTION_ID: " + TASK_EXECUTION_ID + ", LU_NAME: " + LU_NAME + ", entityID: " + entityID);			
							if(!"null".equals(creationDate) && !"".equals(creationDate)) paramList.add(creationDate);
							if(!"null".equals(startExecDate) && !"".equals(startExecDate)) paramList.add(startExecDate);
							if(!"null".equals(endExecDate) && !"".equals(endExecDate)) paramList.add(endExecDate);
							if ("true".equals(versionInd)) {
								paramList.add(versionName);
								paramList.add(verstionDateTime);
							}
							
							//Adding additional parameter for execution_status, in case the insert failed on primary key constraint,
							//in that case only the status will be updated,such case can happen in case of cancel resume
							paramList.add(execStatus);
							
							Object[] params = paramList.toArray();
			
							//log.info ("insertSql - Copied Entities: " + insertSql);
							db(TDM).execute(insertSql, params);
						}
			
					}
				}
			}
			
			if (migrationList.containsKey("Failed entities per execution")) {
				LinkedHashMap<String, Object> m2 = (LinkedHashMap<String, Object>) migrationList.get("Failed entities per execution");
				if (m2.containsKey(UIDLIST)) {
					List<Object> failed_UID_list = (List<Object>) m2.get(UIDLIST);
					for (Object UID : failed_UID_list) {
						Map<Object, Object> innerFailedUIDMap = (Map<java.lang.Object, java.lang.Object>) UID;
			
						for (Map.Entry<Object, Object> failedUID : innerFailedUIDMap.entrySet()) {
							targetEntityID = (String) failedUID.getKey();
							IID = (String) failedUID.getKey();
							entityID = (String) failedUID.getValue();
							execStatus = FAILED;
							
							ArrayList<String> paramList = new ArrayList<>();
							
							paramList.add(taskExecutionId);
							paramList.add(luName);
							paramList.add(entityID);
							paramList.add(targetEntityID);
							paramList.add(envID);
							paramList.add(execStatus);
							paramList.add(idType);
							paramList.add(fabricExecID);
							paramList.add(IID);
							paramList.add(srcEnvName);
							
							//log.info("Inserting Failed: TASK_EXECUTION_ID: " + TASK_EXECUTION_ID + ", LU_NAME: " + LU_NAME + ", entityID: " + entityID);
							if(!"null".equals(creationDate) && !"".equals(creationDate)) paramList.add(creationDate);
							if(!"null".equals(startExecDate) && !"".equals(startExecDate)) paramList.add(startExecDate);
							if(!"null".equals(endExecDate) && !"".equals(endExecDate)) paramList.add(endExecDate);
							if ("true".equals(versionInd)) {
								paramList.add(versionName);
								paramList.add(verstionDateTime);
							}
							//Adding additional parameter for execution_status, in case the insert failed on primary key constraint,
							//in that case only the status will be updated,such case can happen in case of cancel resume
							paramList.add(execStatus);
							
							Object[] params = paramList.toArray();
			
							//log.info ("insertSql - Failed Entities: " + insertSql);
							db(TDM).execute(insertSql, params);
						}
			
					}
				}
			}
			
			//Add reference Entities to TASK_EXECUTION_ENTITIES table
			String refListSql = "SELECT REF_TABLE_NAME, EXECUTION_STATUS FROM " + TDMDB_SCHEMA + ".TASK_REF_EXE_STATS ES WHERE " +
					"TASK_EXECUTION_ID = ? AND TASK_REF_TABLE_ID IN (SELECT TASK_REF_TABLE_ID FROM TASK_REF_TABLES RT " +
						"WHERE RT.TASK_ID = ES.TASK_ID AND RT.TASK_REF_TABLE_ID = ES.TASK_REF_TABLE_ID AND RT.LU_NAME = ?)";
			
			idType = "REFERENCE";
			
			Db.Rows refList = db(TDM).fetch(refListSql, taskExecutionId, luName);
			
			for (Db.Row refTable : refList) {
				entityID = "" + refTable.get("ref_table_name");
				targetEntityID = entityID;
				execStatus = "" + refTable.get("execution_status");
				IID = entityID;
							
				ArrayList<String> paramList = new ArrayList<>();
							
				paramList.add(taskExecutionId);
				paramList.add(luName);
				paramList.add(entityID);
				paramList.add(targetEntityID);
				paramList.add(envID);
				paramList.add(execStatus);
				paramList.add(idType);
				paramList.add(fabricExecID);
				paramList.add(IID);
				paramList.add(srcEnvName);
				
				if(!"null".equals(creationDate) && !"".equals(creationDate)) paramList.add(creationDate);
				if(!"null".equals(startExecDate) && !"".equals(startExecDate)) paramList.add(startExecDate);
				if(!"null".equals(endExecDate) && !"".equals(endExecDate)) paramList.add(endExecDate);
				if ("true".equals(versionInd)) {
					paramList.add(versionName);
					paramList.add(verstionDateTime);
				}
				//Adding additional parameter for execution_status, in case the insert failed on primary key constraint,
				//in that case only the status will be updated,such case can happen in case of cancel resume
				paramList.add(execStatus);
				
				Object[] params = paramList.toArray();
				
				//log.info("Inserting Reference: TASK_EXECUTION_ID: " + TASK_EXECUTION_ID + ", LU_NAME: " + LU_NAME + ", entityID: " + entityID);
				//log.info ("insertSql - Reference Entity: " + insertSql);
				db(TDM).execute(insertSql, params);
			}
			
			if (refList != null) {
				refList.close();
			}
		}
	}


	@desc("This function runs the Fabric command migrate_summary and returns its output")
	@out(name = "migrateSummaryOutput", type = Map.class, desc = "")
	public static Map<String,Object> fnBatchStats(String i_migrateID) throws Exception {
		int retries = 0;
		Map<String, Object> response = null;
		
		
		while (response == null && retries < 4) {
			try {
				List<Map<String, Object>> stats = (List<Map<String, Object>>) fnBatchStatistics(i_migrateID, "S");
				for (Map<String, Object> tmp : stats) {
					if ("Cluster".equals(tmp.get("Level"))) {
						response = tmp;
						break;
					}
				}
				retries++;
			} catch (Exception e) {
				log.error("wsMigrateStats - Failed to get migrate info, with exception: " + e.getMessage());
				if (e.getMessage().toString().contains("Batch process is waiting to be taken by a job") && retries < 4) {
					response = null;
					retries++;
					Thread.sleep(1000);
				} else {
					log.error("wsMigrateStats - Check the command's afinity");
				    throw new Exception(e);
				}
			}
		}
			
		return response;
	}


	@desc("This function will load the errors of the migrate command to the error table task_exe_error_detailed")
	public static void fnUpdateTaskErrorsDetails(String i_taskExecId, String i_luName, String i_migrateId) throws Exception {
		//TDM 6.1.1 - New function to populate table task_exe_error_detailed with the errors from migrate command
		String insertSql = "insert into " + TDMDB_SCHEMA + ".TASK_EXE_ERROR_DETAILED (TASK_EXECUTION_ID,LU_NAME,ENTITY_ID,IID,TARGET_ENTITY_ID, " +
			"ERROR_CATEGORY, ERROR_MESSAGE) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		String getErrorlistSql = "";
		
		// if the cluster ID is define, add it to the name of the keyspace.
		String clusterID = "" + fabric().fetch("clusterid").firstValue();
				
		if (clusterID == null || clusterID.isEmpty()) {
			getErrorlistSql="select entityid, error from k2batchprocess.batchprocess_entities_errors where bid=?";
		} else {
			getErrorlistSql="select entityid, error from k2batchprocess_" + clusterID + ".batchprocess_entities_errors where bid=?";
		}
				
		Db.Rows errorList = db(DBCASSANDRA).fetch(getErrorlistSql, i_migrateId);
		
		for (Db.Row errorRec : errorList) {
			String entityId = "" + errorRec.get("entityid");
			String erroMsg = "" + errorRec.get("error");
			Object[] split_iid = fnSplitUID(entityId);
			String instanceId = "" + split_iid[0];
			
			db(TDM).execute(insertSql, i_taskExecId, i_luName, entityId, instanceId, instanceId, "Entity Failed", erroMsg);
		}
		
		
		if (errorList != null) {
				errorList.close();
			}
	}

	@desc("Execute TDM pending tasks and update the tasks status.")
	@type(UserJob)
	public static void tdmExecuteTask() throws Exception {
		TdmExecuteTask.fnTdmExecuteTask();
	}


	@desc("This function runs the Fabric command batch_summary and returns its output")
	@out(name = "fnRunBatchSummary", type = String.class, desc = "")
	public static String fnRunBatchSummary(String i_batchID) throws Exception {
		int retries = 0;
		StringBuilder outputString = new StringBuilder();
		String summaryOut = "";
				
		while ("".equals(summaryOut)) {
			try {
				Object batchSummary = getFabricResponse("batch_summary '" + i_batchID + "'");
				ArrayList batchSummaryList = (ArrayList) batchSummary;
				for (int i = 0; i < batchSummaryList.size(); i++) {
					outputString.append(batchSummaryList.get(i));
				}
				summaryOut = "" + outputString;
				//log.info("fnRunBatchSummary - summaryOut: " + summaryOut);
			} catch (Exception e) {
				log.error("fnRunBatchSummary - Failed to get migrate info, with exception: " + e.getMessage());
				if (e.getMessage().toString().contains("Batch process is waiting to be taken by a job") && retries < 4) {
					summaryOut = "";
					retries++;
					Thread.sleep(1000);
				} else {
					log.error("fnRunBatchSummary - Check the command's afinity");
				    throw new Exception(e);
				}
			}
		}
			
		return (summaryOut);
	}


	@type(UserJob)
	public static void tdmTaskScheduler() throws Exception {
		TdmTaskScheduler.fnTdmTaskScheduler();
	}

	private static String getBatchStatus(Object originalStatus) {
		if (originalStatus == null) {
			return null;
		}

		if (originalStatus.equals("IN_PROGRESS")) {
			return RUNNING;
		} else if(originalStatus.equals("DONE")) {
			return COMPLETED;
		} else if (originalStatus.equals("CANCELLED")) {
			return STOPPED;
		} else if (originalStatus.equals("PAUSED")) {
			return PAUSED;
		}

		return originalStatus.toString().toLowerCase();
	}

}
