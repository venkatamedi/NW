/////////////////////////////////////////////////////////////////////////
// LU Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.lu.MaskCustom.MASKING_LOGIC;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.*;
import java.math.*;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.shared.Globals;
import com.k2view.cdbms.shared.user.UserCode;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.cdbms.usercode.lu.MaskCustom.*;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;

import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.usercode.common.SharedLogic.*;
import static com.k2view.cdbms.usercode.lu.MaskCustom.Globals.*;

@SuppressWarnings({"unused", "DefaultAnnotationParam"})
public class Logic extends UserCode {


	@out(name = "value", type = String.class, desc = "")
	public static String getRandomDateFromDOB(String value, String format) throws Exception {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		LocalDate inputDateOfBirth = LocalDate.parse(value, formatter);
		
		int year = inputDateOfBirth.getYear();
		int month = inputDateOfBirth.getMonthValue();
		
		
		int maxDay = inputDateOfBirth.lengthOfMonth();
		int randomDay = ThreadLocalRandom.current().nextInt(1, maxDay + 1);
		LocalDate randomDateOfBirth = LocalDate.of(year, month, randomDay);
		return randomDateOfBirth.format(formatter);
	}

	
	
	
	
}
