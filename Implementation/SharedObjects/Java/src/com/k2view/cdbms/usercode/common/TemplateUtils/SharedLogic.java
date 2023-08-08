/////////////////////////////////////////////////////////////////////////
// Project Shared Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.common.TemplateUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.k2view.cdbms.lut.LUType;
import com.k2view.cdbms.lut.TablePopulation;
import com.k2view.cdbms.shared.Db;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.out;
import com.k2view.fabric.common.Util;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

import static com.k2view.cdbms.shared.user.UserCode.*;
import java.sql.*;
import java.math.*;
import java.io.*;
import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.shared.user.UserCode;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;
import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;

@SuppressWarnings({"unused", "DefaultAnnotationParam", "unchecked"})
public class SharedLogic {


	@out(name = "result", type = String.class, desc = "")
	public static String transform(String templateContent, Object data) throws Exception {
		Handlebars handlebars = new Handlebars();

		handlebars.registerHelper("indexPlus", new Helper<Integer>() {
			public Integer apply(Integer index, Options options) {
				return index + 1;
			}
		});


		handlebars.registerHelper("getTableName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("TARGET_TABLE_NAME");
			}
		});

		handlebars.registerHelper("getFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				//log.info(map.get("TARGET_FIELD_NAME"));
				return map.get("TARGET_FIELD_NAME");
			}
		});

		handlebars.registerHelper("getSourceTableName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("FABRIC_TABLE_NAME");
			}
		});

		handlebars.registerHelper("getSourceFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return (map.get("FABRIC_FIELD_NAME")).toUpperCase();
			}
		});

		handlebars.registerHelper("getSequenceName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("SEQUENCE_NAME");
			}
		});

		handlebars.registerHelper("getSequenceActorName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("SEQUENCE_NAME") + "_Actor";
			}
		});

		handlebars.registerHelper("getLUName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return getLuType().luName;
			}
		});

		handlebars.registerHelper("eq", ConditionalHelpers.eq);
		handlebars.registerHelper("neq", ConditionalHelpers.neq);


		Template template = handlebars.compileInline(templateContent);

		return template.apply(data);
	}

	@out(name = "res", type = Object.class, desc = "")
	public static Object buildTemplateData(String luName, String luTable, String targetDbInterface, String targetDbSchema, String targetDbTable, String tableIidFieldName, String sequenceName) throws Exception {
		//String luName = getLuType().luName;
		
		if (luName == null || Util.isEmpty(luName)) {
			luName = getLuType().luName;
		}
		
		
		List<String> luTableColumns = getLuTableColumns(luName, luTable);
		Object[] targetTableData = getDbTableColumns(targetDbInterface, targetDbSchema, targetDbTable);
		String seqIID;
		String seqName;
		
		
		luTableColumns.replaceAll(String::toUpperCase);
		
		Map<String, Object> map = new TreeMap<>();
		map.put("LU_TABLE", luTable);
		map.put("LU_TABLE_COLUMNS", luTableColumns);
		map.put("TARGET_INTERFACE", targetDbInterface);
		map.put("TARGET_SCHEMA", targetDbSchema);
		map.put("TARGET_TABLE", targetDbTable);
		map.put("TARGET_TABLE_COLUMNS", targetTableData[0]);
		map.put("TARGET_TABLE_PKS", targetTableData[1]);
		
		
		if ("".equals(tableIidFieldName)) {
			seqIID = "NO_ID";
			seqName = "";
		} else {
			seqIID = tableIidFieldName;
			seqName = sequenceName;
		}
		map.put("MAIN_TABLE_SEQ_ID", seqIID);
		map.put("MAIN_TABLE_SEQ_NAME", seqName);
		//log.info("buildTemplateData - LU_TABLE: " + luTable + ", MAIN_TABLE_SEQ_ID: " + seqIID);
		String cmd = "broadway " + luName + ".getTableSequenceMapping LU_NAME=" + luName + ", FABRIC_TABLE_NAME = '" + luTable + "', RESULT_STRUCTURE=ROW";
		//log.info("buildTemplateData - cmd: " + cmd);
		
		List<Object> tableSeq = (List<Object>)fabric().fetch(cmd).firstRow().get("value");
		//log.info("buildTemplateData - tableSeq: " + tableSeq);
		
		if (tableSeq != null) {
			Object[] tableSeqArr = tableSeq.toArray(new Object[tableSeq.size()]);
			if (tableSeqArr.length > 0) {
				map.put("TABLE_SEQ_DATA", tableSeqArr);
			} else {
				map.put("TABLE_SEQ_DATA", null);
			}
		} else {
			map.put("TABLE_SEQ_DATA", null);
		}
		
		return map;
	}

	@out(name = "res", type = List.class, desc = "")
	public static List<String> getLuTableColumns(String luName, String table) throws Exception {
		List<String> al = null;// = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}
		if(luType == null || !luType.ludbObjects.containsKey(table))
			return al;

		al = new ArrayList<>(luType.ludbObjects.get(table).getLudbObjectColumns().keySet());
		al.replaceAll(String::toLowerCase);
		return al;
	}

	@out(name = "res", type = List.class, desc = "")
	public static List<String> getLuTables(String luName) throws Exception {
		List<String> al = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}

		if(luType == null)
			return al;
		luType.ludbTables.forEach((s, s2) -> al.add(s));
		return al;
	}

	@out(name = "res", type = Object.class, desc = "")
	public static Object getLuTablesMappedByOrder(String luName, Boolean reverseInd) throws Exception {
		List<List<String>> buckets = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}

		if(luType == null)
			return "";

		List<TablePopulation> populations = luType.getPopulationCollection();
		// populations already ordered
		Set<String> tables = new HashSet<>();
		List<String> tmpBucket = new ArrayList<>();
		int currentOrder = 0;
		for (TablePopulation p : populations) {
			if (p.gettablePopulationOrder() > currentOrder) {
				if (!tmpBucket.isEmpty()) {
					buckets.add(tmpBucket);
					tmpBucket = new ArrayList<>();
				}
				currentOrder = p.gettablePopulationOrder();
			}

			//The table name in TablePopulation is kept in Upper case, to get the original name, loop over luType.ludbTables
			String originalTableName = p.getLudbObjectName();
			for (String tableName : luType.ludbTables.keySet()) {
				if (tableName.equalsIgnoreCase(p.getLudbObjectName())) {
					originalTableName = tableName;
					break;
				}
			}
			Db.Rows checkTable = fabric().fetch("broadway " + luType.luName + ".filterOutTDMTables tableName=', RESULT_STRUCTURE=ROW" +
					originalTableName + "', luName=" + luType.luName);
			String tableFiltered = "";
			if (checkTable != null && checkTable.firstValue() != null) {
				tableFiltered = "" + checkTable.firstValue();
			}

			if( !tables.contains(originalTableName)  && !Util.isEmpty(tableFiltered)) {
				tmpBucket.add(originalTableName);
				tables.add(originalTableName);
			}
		}
		// The last bucket
		if (!tmpBucket.isEmpty()) {
			buckets.add(tmpBucket);
		}
		if (reverseInd) {
			Collections.reverse(buckets);
		}

		return buckets;
		//return Json.get().toJson(buckets);
	}


	public static String[] getDBCollection(DatabaseMetaData md, String catalogSchema) throws Exception {
		String catalog = null;
		String schema = null;


		ResultSet schemas = md.getSchemas();
		while (schemas.next()) {
			//log.info("getDbTableColumns - Schema: " + schemas.getString("TABLE_SCHEM"));
			if (catalogSchema.equalsIgnoreCase(schemas.getString("TABLE_SCHEM"))) {
				schema = schemas.getString("TABLE_SCHEM");
				break;
			}
		}
		if (schema == null) {
			ResultSet catalogs = md.getCatalogs();
			while (catalogs.next()) {
				//log.info("getDbTableColumns - Catalog: " + catalogs.getString("TABLE_CAT"));
				if (catalogSchema.equalsIgnoreCase(catalogs.getString("TABLE_CAT"))) {
					catalog = catalogs.getString("TABLE_CAT");
					break;
				}
			}
		}

		return new String[]{catalog, schema};

	}

	@out(name = "columns", type = Object[].class, desc = "")
	@out(name = "pks", type = Object[].class, desc = "")
	public static Object[] getDbTableColumns(String dbInterfaceName, String catalogSchema, String table) throws Exception {
		ResultSet rs = null;
		String[] types = {"TABLE"};
		String targetTableName = table;

		try {
			DatabaseMetaData md = getConnection(dbInterfaceName).getMetaData();

			String[] dbSchemaType = getDBCollection(md, catalogSchema);
			String catalog = dbSchemaType[0];
			String schema = dbSchemaType[1];
			//log.info("getDbTableColumns - Catalog: " + catalog + ", Schema: " + schema);
			rs = md.getTables(catalog, schema, "%", types);

			while (rs.next()) {
				if (table.equalsIgnoreCase(rs.getString(3))) {
					targetTableName = rs.getString(3);
					//log.info("getDbTableColumns - tableName: " + targetTableName);
					break;
				}
			}

			rs = md.getColumns(catalog, schema, targetTableName, null);
			List<String> al = new ArrayList<>();
			while (rs.next()) {
				al.add(rs.getString("COLUMN_NAME"));
			}
			//result[0] = al;

			// get PKs
			rs = md.getPrimaryKeys(catalog, schema, targetTableName);
			List<String> al2 = new ArrayList<>();
			while (rs.next()) {
				al2.add(rs.getString("COLUMN_NAME"));
			}
			//result[1] = al2;
			return new Object[]{al,al2};
		} finally {
			if (rs != null)
				rs.close();
		}
	}


	@out(name = "res", type = List.class, desc = "")
	public static List<String> getDbTables(String dbInterfaceName, String catalogSchema) throws Exception {
		ResultSet rs = null;
		List<String> al = new ArrayList<>();
		try {
			DatabaseMetaData md = getConnection(dbInterfaceName).getMetaData();
			String[] dbSchemaType = getDBCollection(md, catalogSchema);
			String catalog = dbSchemaType[0];
			String schema = dbSchemaType[1];
			rs = md.getTables(catalog, schema, "%", null);
			while (rs.next()) {
				al.add(rs.getString(3));
			}
		} finally {
			if (rs != null)
				rs.close();
		}
		return al;
	}


	@out(name = "res", type = Object.class, desc = "")
	public static Object buildSeqTemplateData(String seqName, String cacheDBName, String redisOrDBName, String initiationScriptOrValue) throws Exception {
		Map<String, Object> map = new TreeMap<>();
		map.put("SEQUENCE_NAME", seqName);
		map.put("CACHE_DB_NAME", cacheDBName);
		map.put("SEQUENCE_REDIS_DB", redisOrDBName);
		map.put("INITIATE_VALUE_FLOW", initiationScriptOrValue);
		return map;
	}


	@out(name = "res", type = Object.class, desc = "")
	public static Object buildTransTemplateData(String transName, Object transColumns, Object transKeys) throws Exception {
		Map<String, Object> map = new TreeMap<>();
		map.put("TRANSLATION_NAME", transName);
		map.put("TRANS_COLUMNS", transColumns);
		map.put("TRANS_KEYS", transKeys);

		return map;
	}


	@out(name = "output", type = Map.class, desc = "")
	public static Map<String,Object> getTransDefaults(Object[] transDefinition, String luName, String transName) throws Exception {
		HashMap<String, Object> output = new HashMap<>();
		for (Object rec : transDefinition) {
			HashMap<String, String> map = (HashMap<String, String>) rec;
			if (transName.equals(map.get("translation_name")) &&
					("ALL".equals(map.get("owner_lu")) || luName.equals(map.get("owner_lu"))) ) {
				output.put(map.get("title"), map.get("default_value"));

			}
		}

		return output;
	}


}


