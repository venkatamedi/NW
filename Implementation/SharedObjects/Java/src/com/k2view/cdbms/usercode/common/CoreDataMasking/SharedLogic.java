package com.k2view.cdbms.usercode.common.CoreDataMasking;

import java.util.*;
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

import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.shared.user.UserCode.*;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@SuppressWarnings({"unused", "DefaultAnnotationParam","unchecked","deprecation"})
public class SharedLogic {


    @desc("This function will mask (obfuscate) a field based on the $iEncryption value.\nIt will add the value of the first number of the $iEncryption number to the ASCII value \nof the first character of the $iFieldValue and translate it back to its Char value.")
    @type(LudbFunction)
    @out(name = "oMaksedField", type = String.class, desc = "The obfuscated field")
    public static String k2s_MaskField(@desc("The value of the field on which to apply the masking") String iFieldValue, @desc("The encryption to be used to mask the value") String iEncryption) throws Exception {
        //Null check
        if(iFieldValue == null || iFieldValue.isEmpty()) return iFieldValue;

        int valueLength = iFieldValue.length();
        int encryptionLength = iEncryption.length();
        String newEncryption = iEncryption;

        // If the length of the iEncryption is smaller than the length of the iFieldValue,
        // Append the iEncryption to itself as many times as needed to match the length of iFieldValue.
        while(encryptionLength < valueLength){
            // Append iEncryption to itself.
            newEncryption = newEncryption + iEncryption;
            // Checks the length of the new iEncryption.
            encryptionLength = newEncryption.length();
        }

        String oMaksedField = "";
        newEncryption = newEncryption.substring(0,valueLength);

        //if iFieldValue is numeric
        // If it is, it will use the same encryption but on a cycle of 0-9
        // As an example, if $iFieldValue = 92 and $iEncryption = 86
        // The function will add 9 + 8 = 17 and will change it to 7
        // And 2 + 6 = 8 and will leave it as 8, so the final masked value is 78.
        if(iFieldValue.matches(getLuType().ludbGlobals.get("NUMBER_REGEX"))){
            int tempRes = 0;
            // Goes number by number in iFieldValue and adds it's equivalent iEncryption number.
            for(int i=0;i<valueLength;i++){
                tempRes = (Character.getNumericValue(iFieldValue.charAt(i)) +
                        Character.getNumericValue(newEncryption.charAt(i))) % 10;
                oMaksedField = oMaksedField + Integer.toString(tempRes);
                tempRes = 0;
            }
        }
        // In case iFieldValue is a non-numeric value.
        // Mask every alphanumeric charecter using the same logic.
        // First convert the alphanumeric charecter into ASCII, then add the equivalent value from iEncryption
        // Lastly, convert the new ASCII value back to Char.
        else{
            String maskedFieldValue = iFieldValue;
            char currentChar;
            int charecterASCII = 0;
            int maskedASCII = 0;
            char maskedChar;
            // Go charecter by charecter and check if it's alphanumeric,
            // If it is mask it, if not, leave it as is.
            for(int i = 0 ; i < valueLength ; i++){
                // Extract the current charecter from the iFieldValue argument.
                currentChar = iFieldValue.charAt(i);

                // If charecter is alphanumeric.
                if(Character.isDigit(currentChar) || Character.isLetter(currentChar)){

                    // Convert the current charecter into ASCII format
                    charecterASCII = (int)currentChar;

                    // Check the range of the alphanumeric charecter
                    if(Character.isDigit(currentChar)){ // If 0-9 (same as - "is_numeric($charecterValue)")

                        // Update the specific charecter in the value.
                        maskedASCII = (Character.getNumericValue(currentChar) + Character.getNumericValue(newEncryption.charAt(i))) % 10;
                        maskedChar = (Integer.toString(maskedASCII)).charAt(0);

                    } else if(Character.isUpperCase(currentChar)){ // If A-Z (upper case letter)
                        // Get the masked ASCII value.
                        maskedASCII = charecterASCII + Character.getNumericValue(newEncryption.charAt(i));
                        // Update the masked value.
                        maskedChar = (maskedASCII>90)?(char)(maskedASCII-26):(char)(maskedASCII);
                    } else { // if a-z (lower case letter)
                        // Get the masked ASCII value.
                        maskedASCII = charecterASCII + Character.getNumericValue(newEncryption.charAt(i));
                        maskedChar = (maskedASCII>122)?(char)(maskedASCII-26):(char)(maskedASCII);
                    }
                    // Update the masked value.
                    maskedFieldValue = maskedFieldValue.substring(0,i)+(char)maskedChar + maskedFieldValue.substring(i+1);
                }else{
                    maskedFieldValue = maskedFieldValue.substring(0,i)+ currentChar + maskedFieldValue.substring(i+1);
                }
            }
            // Update the return value.
            oMaksedField = maskedFieldValue;
        }
        return oMaksedField;
    }



}