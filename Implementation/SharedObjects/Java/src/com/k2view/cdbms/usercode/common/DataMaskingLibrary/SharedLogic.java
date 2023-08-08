/////////////////////////////////////////////////////////////////////////
// Project Shared Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.common.DataMaskingLibrary;

import java.security.SecureRandom;
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
import com.k2view.fabric.common.Util;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.shared.user.UserCode.*;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;
import static com.k2view.cdbms.usercode.common.CoreDataMasking.SharedLogic.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;


@SuppressWarnings({"unused", "DefaultAnnotationParam","unchecked","deprecation"})
public class SharedLogic {


    @desc("This function generates an email with the format:<maskedFirstName>.<maskedLastName>@<originalDomain>\r\nMAke sure to run the masking of the first and last names before calling this function.")
    @type(LudbFunction)
    @out(name = "oMaskedEmail", type = String.class, desc = "The masked email address")
    public static String fnGenerateEmailFromName(@desc("The email address to mask") String iEmail, @desc("The masked first name") String iFirstName, @desc("The masked last name") String iLastName, @desc("Table containing the first and last names") String iNamesTable) throws Exception {
        final String EMAIL_SHTROUDL = "@";
        final String SQL_SELECT = "SELECT ";
        final String SQL_FROM = " FROM ";
        //validate that the iEmail input is not null and not empty.Else, If the iEmail is NULL then return NULL.
        if(iEmail!=null && !iEmail.isEmpty()){
    // Verify the email format
    if( iEmail.contains(EMAIL_SHTROUDL)){
        // Exploding the email address using in two chunks (left and right side of the @ symbol)
        String [] emailArray = iEmail.split(EMAIL_SHTROUDL);
        // Verify the email format
        if(emailArray.length == 2){
     String oMaskedEmail = "";

     // Getting the data from the table in input
     String fname = (String)DBSelectValue(DB_INTERFACE_NAME,SQL_SELECT+iFirstName+SQL_FROM+iNamesTable, null);
     String lname = (String)DBSelectValue(DB_INTERFACE_NAME,SQL_SELECT+iLastName+SQL_FROM+iNamesTable, null);

     //<maskedFirstName>.<maskedLastName>@<originalDomain>
     return fname + "." + lname + EMAIL_SHTROUDL + emailArray[1];
        }
    }
        }
        return null;
    }


    @desc("This function will generate a valid Credit Card Number based on a given length and prefix information.\nIt uses the Modulus 10 algorithm to determine the validity of the number.")
    @type(LudbFunction)
    @out(name = "oValidCreditCardNumber", type = String.class, desc = "A Random valid Credit Card Number")
    public static String fnGenerateRandomValidCreditCard(@desc("The Credit Card Length.") String iCreditCardLength, @desc("The Credit Card Prefix.") String iCreditCardPrefix) throws Exception {
        String oValidCreditCardNumber = null;
        // Create a new Credit Card Number.
        // The new card will be built with the following structure:
        // <prefix><random numbers until the size (including the prefix) is the request length -1><checksum>

        // Generate random numbers to complete the Credit Card to the requested size -1.
        String newCC = iCreditCardPrefix;
        int intCreditCardLength = Integer.parseInt(iCreditCardLength);

        // Keep adding more random numbers until you reach the right size.
        while(newCC.length() < intCreditCardLength - 1){
    // Append a random single number to the existing number.
    newCC = newCC + Integer.toString(fnGetRandomNumber(0,9));
        }

        // Define the parameter that would  hold the sum of the numbers (needed for the Modulus 10 Algorithm).
        int sumCCnumbers = 0;

        // Reverse the Credit Card Number so to make it easier to apply the Modulus 10 algorithm.
        String reversedCC =  new StringBuilder(newCC).reverse().toString();

        // Apply the Mudolus 10 Algorithm.
        //  For information on this algorithm please see: http://en.wikipedia.org/wiki/Luhn_algorithm.
        int oddNumberDouble = 0;
        for(int i = 1 ; i <= reversedCC.length() ; i++){

    // Check if it's an odd digit.
    if ( (i & 1) != 0 ){
        // If Yes, multiply it by 2.
        oddNumberDouble = java.lang.Character.getNumericValue(reversedCC.charAt(i-1)) * 2;
        // If the new value is larger than 9, subtract 9 from it.
        oddNumberDouble = (oddNumberDouble > 9)?oddNumberDouble - 9:oddNumberDouble;
        // Add the value to the overall Credit Card summation.
        sumCCnumbers += oddNumberDouble;
        // If the digit is Even.
    } else {//Even
        // Add the value of the even digit to the overall Credit Card summation.
        sumCCnumbers += java.lang.Character.getNumericValue(reversedCC.charAt(i-1));
    }
        }

        // Calcualte the checksum digit by substracting the units digit from 10.
        int checkSumDigit = 10 - (sumCCnumbers % 10);
        //In case checkSumDigit equals to 10 then change it to 0
        checkSumDigit  = (checkSumDigit==10)?0:checkSumDigit;
        // Append the Checksum Digit to the Credit Card Number to compelte it.
        newCC = newCC + Integer.toString(checkSumDigit);
        oValidCreditCardNumber = newCC;

        //Return the new valid Credit Card Number.
        return oValidCreditCardNumber;
    }


    @desc("This function will generate a random valid EIN number.")
    @type(LudbFunction)
    @out(name = "oMaskedEIN", type = String.class, desc = "The masked EIN")
    public static String fnGenerateRandomValidEIN(@desc("EIN to mask") String iEIN) throws Exception {
        final int PADDING_LENGTH = 7;
        // Prepare a list of all Campuses profix for the EIN.
        String [] campusPrefixArray = new String[]{"01","02","03","04","05","06","10","11","12","13","14","15","16","20","21","22","23","24","25","26","27","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","46","48","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","71","72","73","74","75","76","77","80","81","82","83","84","85","86","87","88","90","91","92","93","94","95","98","99"};
        // Generate a random Key value to extract a random prefix from the $campusPrefixArray array.
        int campusPrefixIndex = -1; int integerRandomVal = -1;
        String randomVal = null;
        int arrayLastIndex = campusPrefixArray.length - 1;

        //prevent generating random index value which is out of campusPrefixArray array
        do{
    campusPrefixIndex = fnGetRandomNumber(0,arrayLastIndex);
    randomVal = campusPrefixArray[campusPrefixIndex];
    integerRandomVal = Integer.parseInt(randomVal);
        }while(integerRandomVal > arrayLastIndex);


        String campusPrefix = campusPrefixArray[integerRandomVal];
        String oMaskedEIN = null;

        // Check to see if an hyphen exists in the EIN.
        // If it exists and its located after the first 2 digits:
        if((iEIN.replaceAll("[^\\d]", "")).length() == 9){
    String rand = Integer.toString(fnGetRandomNumber(0,9999999));
    //padd the random number with zeros on the left so eventually we will have a number of 7 digits
    rand = k2_pad(rand,"0",PADDING_LENGTH,false) ;
    // Prepare the masked value with the new prefix.
    oMaskedEIN = campusPrefix + "-" + rand ;
    // If EIN doesn't have a hyphen and is not 9 digits exactly, keep it as is.
        }else {
    // Initiate the Random Encryption argument.
    String randomEncryption = "";

    // For every charecter in the EIN:
    for(int i = 0 ; i < iEIN.length() ; i++){

        // If the current charecter equals -
        if(iEIN.charAt(i) == '-'){
     // Concatenate 0 to the Encrption arguent
     randomEncryption = randomEncryption + "0";

     // Else - The current charecter is not -
        } else {
     // Concatenate a rnadom number to the Encryption argument
     randomEncryption = randomEncryption + Integer.toString(fnGetRandomNumber(0,9));
        }
    }
    // Mask the EIN.
    oMaskedEIN = k2s_MaskField(iEIN,randomEncryption);
        }

        // Return the masked value.
        return oMaskedEIN;
    }


	@desc("This function will generate a masked SSN.\nIf the input parameter of $iSSN has 9 digits, the function will generate a valid masked SSN for it.\nIf the input parameter of $iSSN doesn't have 9 digits (which means it's not a vlid SSN) the function will create a random number in the same size of the $iSSN.")
	@type(LudbFunction)
	@out(name = "oSSN", type = String.class, desc = "A rendom generated valid SSN.")
	public static String fnGenerateRandomValidSSN(@desc("Social Security Number") String value) throws Exception {
		final String SEPARATOR = "";
		final int LAST_FOUR_SUBSTR_LENGTH = 4;
		final int GROUP_SUBSTR_LENGTH = 2;
		final int AREA_SUBSTR_LENGTH = 3;
		
		
		@SuppressWarnings("unchecked")
		Map <String, int[]> ssnSstatePrefixes = (Map <String, int[]>) getThreadGlobals("SSN:SSNstatePrefixes");
		@SuppressWarnings("unchecked")
		Map<String, Integer> subHighGroupMap = (Map<String, Integer>)getThreadGlobals("SSN:SSNsubHighGroupArray");
		int[] possibleGroups = (int[])getThreadGlobals("SSN:possibleGroups");
		String[] states = (String[])getThreadGlobals("SSN:States");
		
		// Checks to see if the set up parameters for the generation of SSN were already defined.
		if(subHighGroupMap==null){
		    // List of all available SSN High Groups updated to June 24th 2011 - (taken from http://www.ssa.gov/employer/ssnvhighgroup.htm)
		    String ssnHighGroup = "001 11,002 11,003 08,004 13,005 13,006 11,007 11,008 94,009 94,010 94,011 94,012 94,013 94,014 94,015 94,016 94,017 94,018 94,019 94,020 94,021 94,022 92,023 92,024 92,025 92,026 92,027 92,028 92,029 92,030 92,031 92,032 92,033 92,034 92,035 74,036 74,037 74,038 74,039 74,040 15,041 15,042 15,043 15,044 15,045 15,046 15,047 15,048 15,049 15,050 02,051 02,052 02,053 02,054 02,055 02,056 02,057 02,058 02,059 02,060 02,061 02,062 02,063 02,064 02,065 02,066 02,067 02,068 02,069 02,070 02,071 02,072 02,073 02,074 02,075 02,076 02,077 02,078 02,079 02,080 02,081 02,082 02,083 02,084 02,085 02,086 02,087 02,088 02,089 02,090 02,091 02,092 02,093 02,094 02,095 02,096 02,097 02,098 02,099 02,100 02,101 02,102 02,103 02,104 02,105 02,106 98,107 98,108 98,109 98,110 98,111 98,112 98,113 98,114 98,115 98,116 98,117 98,118 98,119 98,120 98,121 98,122 98,123 98,124 98,125 98,126 98,127 98,128 98,129 98,130 98,131 98,132 98,133 98,134 98,135 25,136 25,137 25,138 25,139 23,140 23,141 23,142 23,143 23,144 23,145 23,146 23,147 23,148 23,149 23,150 23,151 23,152 23,153 23,154 23,155 23,156 23,157 23,158 23,159 86,160 86,161 86,162 86,163 86,164 86,165 86,166 86,167 86,168 86,169 86,170 86,171 86,172 86,173 86,174 86,175 86,176 86,177 86,178 86,179 86,180 86,181 86,182 86,183 86,184 86,185 86,186 86,187 86,188 86,189 86,190 86,191 86,192 86,193 86,194 86,195 86,196 84,197 84,198 84,199 84,200 84,201 84,202 84,203 84,204 84,205 84,206 84,207 84,208 84,209 84,210 84,211 84,212 91,213 91,214 91,215 91,216 89,217 89,218 89,219 89,220 89,221 13,222 11,223 99,224 99,225 99,226 99,227 99,228 99,229 99,230 99,231 99,232 57,233 57,234 57,235 57,236 55,237 99,238 99,239 99,240 99,241 99,242 99,243 99,244 99,245 99,246 99,247 99,248 99,249 99,250 99,251 99,252 99,253 99,254 99,255 99,256 99,257 99,258 99,259 99,260 99,261 99,262 99,263 99,264 99,265 99,266 99,267 99,268 17,269 17,270 17,271 17,272 17,273 17,274 17,275 17,276 17,277 17,278 17,279 15,280 15,281 15,282 15,283 15,284 15,285 15,286 15,287 15,288 15,289 15,290 15,291 15,292 15,293 15,294 15,295 15,296 15,297 15,298 15,299 15,300 15,301 15,302 15,303 37,304 37,305 37,306 37,307 37,308 37,309 37,310 37,311 35,312 35,313 35,314 35,315 35,316 35,317 35,318 11,319 11,320 11,321 11,322 11,323 11,324 11,325 11,326 11,327 11,328 11,329 11,330 11,331 11,332 11,333 11,334 11,335 11,336 11,337 11,338 11,339 11,340 11,341 11,342 11,343 11,344 11,345 11,346 08,347 08,348 08,349 08,350 08,351 08,352 08,353 08,354 08,355 08,356 08,357 08,358 08,359 08,360 08,361 08,362 39,363 39,364 39,365 39,366 39,367 39,368 39,369 39,370 37,371 37,372 37,373 37,374 37,375 37,376 37,377 37,378 37,379 37,380 37,381 37,382 37,383 37,384 37,385 37,386 37,387 33,388 33,389 33,390 33,391 33,392 33,393 33,394 33,395 33,396 33,397 33,398 31,399 31,400 73,401 73,402 73,403 73,404 73,405 73,406 73,407 73,408 99,409 99,410 99,411 99,412 99,413 99,414 99,415 99,416 67,417 67,418 67,419 67,420 67,421 67,422 67,423 67,424 65,425 99,426 99,427 99,428 99,429 99,430 99,431 99,432 99,433 99,434 99,435 99,436 99,437 99,438 99,439 99,440 29,441 29,442 29,443 27,444 27,445 27,446 27,447 27,448 27,449 99,450 99,451 99,452 99,453 99,454 99,455 99,456 99,457 99,458 99,459 99,460 99,461 99,462 99,463 99,464 99,465 99,466 99,467 99,468 57,469 57,470 57,471 57,472 57,473 55,474 55,475 55,476 55,477 55,478 43,479 41,480 41,481 41,482 41,483 41,484 41,485 41,486 29,487 29,488 29,489 29,490 29,491 29,492 29,493 29,494 29,495 29,496 29,497 29,498 29,499 29,500 29,501 37,502 37,503 45,504 45,505 59,506 57,507 57,508 57,509 33,510 33,511 33,512 33,513 31,514 31,515 31,516 49,517 49,518 89,519 87,520 61,521 99,522 99,523 99,524 99,525 99,526 99,527 99,528 99,529 99,530 99,531 71,532 71,533 71,534 71,535 71,536 71,537 71,538 71,539 71,540 83,541 83,542 81,543 81,544 81,545 99,546 99,547 99,548 99,549 99,550 99,551 99,552 99,553 99,554 99,555 99,556 99,557 99,558 99,559 99,560 99,561 99,562 99,563 99,564 99,565 99,566 99,567 99,568 99,569 99,570 99,571 99,572 99,573 99,574 61,575 99,576 99,577 53,578 53,579 53,580 41,581 99,582 99,583 99,584 99,585 99,586 67,587 99,588 09,589 99,590 99,591 99,592 99,593 99,594 99,595 99,596 94,597 92,598 92,599 92,600 99,601 99,602 87,603 87,604 87,605 87,606 87,607 87,608 87,609 87,610 87,611 87,612 87,613 87,614 87,615 87,616 87,617 87,618 87,619 87,620 85,621 85,622 85,623 85,624 85,625 85,626 85,627 31,628 31,629 31,630 31,631 31,632 31,633 31,634 31,635 31,636 31,637 29,638 29,639 29,640 29,641 29,642 29,643 29,644 29,645 29,646 23,647 21,648 58,649 56,650 62,651 62,652 62,653 60,654 38,655 36,656 36,657 36,658 36,659 24,660 24,661 22,662 22,663 22,664 22,665 22,667 48,668 48,669 48,670 48,671 48,672 48,673 48,674 48,675 48,676 22,677 22,678 22,679 20,680 31,681 24,682 24,683 24,684 24,685 24,686 24,687 24,688 24,689 24,690 22,691 18,692 18,693 16,694 16,695 16,696 16,697 16,698 16,699 16,700 18,701 18,702 18,703 18,704 18,705 18,706 18,707 18,708 18,709 18,710 18,711 18,712 18,713 18,714 18,715 18,716 18,717 18,718 18,719 18,720 18,721 18,722 18,723 18,724 28,725 18,726 18,727 10,728 14,729 28,730 28,731 28,732 28,733 26,750 20,751 18,752 09,753 07,754 07,755 07,756 12,757 12,758 12,759 12,760 12,761 12,762 12,763 12,764 29,765 27,766 04,767 02,768 02,769 02,770 02,771 02,772 02,";
		    // Split on tab. This should give us an array of prefixes and group numbers, IE '203 82', '204 82', etc
		    String[] ssnHighGroupArray = ssnHighGroup.split(",");
		
		    subHighGroupMap = new java.util.HashMap<String,Integer>();
		    // Make array useful by splitting the prefix and group number
		    // We also convert the string to an int for easier handling later down the road
		    String [] ssnSubHighGroup = null;
		    for(String ssnHighGroupVal: ssnHighGroupArray){
		        // Checks if the current value is not an empty string.
		        if(!(ssnHighGroupVal.trim()).isEmpty()){
                    // If a valid entry, explode into area and and groups.
                    ssnSubHighGroup = ssnHighGroupVal.split(" ");
                    // add to the map the area as the key and the group as the value.
                    subHighGroupMap.put(ssnSubHighGroup[0].trim(), Integer.parseInt(ssnSubHighGroup[1].trim()));
		        }
		    }
		    //set the map of the LocalThread of the current thread
		    setThreadGlobals("SSN:SSNsubHighGroupArray",subHighGroupMap);
		}
		// Checks to see if the set up parameters for the generation of SSN were already defined.
		if(ssnSstatePrefixes == null){
		
		    ssnSstatePrefixes = new java.util.HashMap <String, int[]> ();
		    ssnSstatePrefixes.put("AK",new int[]{574});
		    ssnSstatePrefixes.put("AL",new int[]{416,417,418,419,420,421,422,423,424});
		    ssnSstatePrefixes.put("AR", new int[]{429,430,431,432,676,677,678,679});
		    ssnSstatePrefixes.put("AZ", new int[]{526,527,600,601,764,765});
		    ssnSstatePrefixes.put("CA", new int[]{545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,565,566,567,568,569,570,571,572,573,602,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626});
		    ssnSstatePrefixes.put("CO", new int[]{521,522,523,524,650,651,652,653});
		    ssnSstatePrefixes.put("CT", new int[]{40,41,42,43,44,45,46,47,48,49});
		    ssnSstatePrefixes.put("DC", new int[]{577,578,579});
		    ssnSstatePrefixes.put("DE", new int[]{221,222});
		    ssnSstatePrefixes.put("FL", new int[]{261,262,263,264,265,266,267,589,590,591,592,593,594,595,766,767,768,769,770,771,772});
		    ssnSstatePrefixes.put("GA", new int[]{252,253,254,255,256,257,258,259,260,667,668,669,670,671,672,673,674,675});
		    ssnSstatePrefixes.put("HI", new int[]{575,576,750,751});
		    ssnSstatePrefixes.put("IA", new int[]{478,479,480,481,482,483,484,485});
		    ssnSstatePrefixes.put("ID", new int[]{518,519});
		    ssnSstatePrefixes.put("IL", new int[]{318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336,337,338,339,340,341,342,343,344,345,346,347,348,349,350,351,352,353,354,355,356,357,358,359,360,361});
		    ssnSstatePrefixes.put("IN", new int[]{303,304,305,306,307,308,309,310,311,312,313,314,315,316,317});
		    ssnSstatePrefixes.put("KS", new int[]{509,510,511,512,513,514,515});
		    ssnSstatePrefixes.put("KY", new int[]{400,401,402,403,404,405,406,407});
		    ssnSstatePrefixes.put("LA", new int[]{433,434,435,436,437,438,439,659,660,661,662,663,664,665});
		    ssnSstatePrefixes.put("MA", new int[]{10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34});
		    ssnSstatePrefixes.put("MD", new int[]{212,213,214,215,216,217,218,219,220});
		    ssnSstatePrefixes.put("ME", new int[]{4,5,6,7});
		    ssnSstatePrefixes.put("MI", new int[]{362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386});
		    ssnSstatePrefixes.put("MN", new int[]{468,469,470,471,472,473,474,475,476,477});
		    ssnSstatePrefixes.put("MO", new int[]{486,487,488,489,490,491,492,493,494,495,496,497,498,499,500});
		    ssnSstatePrefixes.put("MS", new int[]{425,426,427,428,587});
		    ssnSstatePrefixes.put("MT", new int[]{516,517});
		    ssnSstatePrefixes.put("NC", new int[]{237,238,239,240,241,242,243,244,245,246,681,682,683,684,685,686,687,688,689,690});
		    ssnSstatePrefixes.put("ND", new int[]{501,502});
		    ssnSstatePrefixes.put("NE", new int[]{505,506,507,508});
		    ssnSstatePrefixes.put("NH", new int[]{1,2,3});
		    ssnSstatePrefixes.put("NJ", new int[]{135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158});
		    ssnSstatePrefixes.put("NM", new int[]{525,585,648,649});
		    ssnSstatePrefixes.put("NV", new int[]{530,680});
		    ssnSstatePrefixes.put("NY", new int[]{50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134});
		    ssnSstatePrefixes.put("OH", new int[]{268,269,270,271,272,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297,298,299,300,301,302});
		    ssnSstatePrefixes.put("OK", new int[]{440,441,442,443,444,445,446,447,448});
		    ssnSstatePrefixes.put("OR", new int[]{540,541,542,543,544});
		    ssnSstatePrefixes.put("PA", new int[]{159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211});
		    ssnSstatePrefixes.put("RI", new int[]{35,36,37,38,39});
		    ssnSstatePrefixes.put("SC", new int[]{247,248,249,250,251,654,655,656,657,658});
		    ssnSstatePrefixes.put("SD", new int[]{503,504});
		    ssnSstatePrefixes.put("TN", new int[]{408,409,410,411,412,413,414,415,756,757,758,759,760,761,762,763});
		    ssnSstatePrefixes.put("TX", new int[]{449,450,451,452,453,454,455,456,457,458,459,460,461,462,463,464,465,466,467,627,628,629,630,631,632,633,634,635,636,637,638,639,640,641,642,643,644,645});
		    ssnSstatePrefixes.put("UT", new int[]{528,529,646,647});
		    ssnSstatePrefixes.put("VA", new int[]{223,224,225,226,227,228,229,230,231,691,692,693,694,695,696,697,698,699});
		    ssnSstatePrefixes.put("VT", new int[]{8,9});
		    ssnSstatePrefixes.put("WA", new int[]{531,532,533,534,535,536,537,538,539});
		    ssnSstatePrefixes.put("WI", new int[]{387,388,389,390,391,392,393,394,395,396,397,398,399});
		    ssnSstatePrefixes.put("WV", new int[]{232,233,234,235,236});
		    ssnSstatePrefixes.put("WY", new int[]{520});
		
		    setThreadGlobals("SSN:SSNstatePrefixes",ssnSstatePrefixes);
		}
		// Checks to see if the set up parameters for the generation of SSN were already defined.
		if(states == null){
		    states = new String[]{"AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","HI","IA","ID","IL","IN","KS","KY","LA","MA","MD","ME","MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VA","VT","WA","WI","WV","WY"};
		    setThreadGlobals("SSN:States",states);
		}
		// Checks to see if the set up parameters for the generation of SSN were already defined.
		if(possibleGroups == null){
		    possibleGroups = new int[]{1,3,5,7,9,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,66,68,70,72,74,76,78,80,82,84,86,88,90,92,94,96,98,2,4,6,8,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39,41,43,45,47,49,51,53,55,57,59,61,63,65,67,69,71,73,75,77,79,81,83,85,87,89,91,93,95,97,99};
		    setThreadGlobals("SSN:possibleGroups",possibleGroups);
		}
		
		org.apache.commons.math3.random.MersenneTwister mt = new org.apache.commons.math3.random.MersenneTwister();
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
		final String zeroChar = "0";
		String paddingPrefix = null;
		
		String modifiedSSN = value.replaceAll("[^\\d]", "");
		
		String oSSN = null;
		// Check that the SSN as overall of 9 digits (excluding any non-digits charecters):
		if(modifiedSSN.length() == 9){
		
		    // Generate a random State.
		    int randIndex = mt.nextInt(states.length-1);
		
		    String state = states[randIndex];
		    // Generate area number
		    int[] selectedStatePrefixesArr = ssnSstatePrefixes.get(state);
		    randIndex = fnGetRandomNumber(0,selectedStatePrefixesArr.length-1);
		
		    // Generate area number
		    int area = selectedStatePrefixesArr[randIndex];
		    String areaStr = Integer.toString(area);
		
		    //pad groupStr with zeros on the left so eventually we will have a number of 3 digits
		    areaStr = k2_pad(areaStr,"0",AREA_SUBSTR_LENGTH,false) ;
		
		    int groupArea = subHighGroupMap.get(areaStr);
		    boolean isFound = false;
		    //Iterate over possible groups array and check if groupArea is found
		    for(int i=0;i<possibleGroups.length;i++){
		        if(possibleGroups[i] == groupArea){
		    isFound = true;
		    break;
		        }
		    }
		    if(isFound){
		        randIndex = mt.nextInt(groupArea);
		    }else{//In order to prevent index out of bounds
		        randIndex = 0;
		    }
		    // Generate group number
		    int group = possibleGroups[randIndex]; // Generate valid group number
		    String groupStr = Integer.toString(group);
		    // Generate last four
		    String lastFour = Integer.toString(mt.nextInt(9999 - 1) + 1);
		    //pad lastFour with zeros on the left so eventually we will have a number of 4 digits
		    lastFour = k2_pad(lastFour,"0",LAST_FOUR_SUBSTR_LENGTH,false) ;
		
		    //pad groupStr with zeros on the left so eventually we will have a number of 2 digits
		    groupStr = k2_pad(groupStr,"0",GROUP_SUBSTR_LENGTH,false) ;
		
		    StringBuilder sb = new StringBuilder();
		    // Build the random SSN.
		    sb.append(areaStr);
		    sb.append(SEPARATOR);
		    sb.append(groupStr);
		    sb.append(SEPARATOR);
		    sb.append(lastFour);
		
		    oSSN = sb.toString();
		    // If not, the SSN is not valid.
		} else {
		    // Get the size of SSN in numbers.
		    double ssnSize = java.lang.Math.pow(10.0,((double)value.length()) - 1.0);
		    // Initiate the Random Encryption argument.
		    String paddingStr = org.apache.commons.lang3.StringUtils.repeat(zeroChar, value.length());
		    String randomEncryption = Integer.toString(fnGetRandomNumber(1,(int)ssnSize)) + paddingStr;
		    // Mask the SSN.
		    oSSN = k2s_MaskField(value,randomEncryption);
		}
		
		return oSSN;
	}


    @desc("This function will return the City name based on the input Zip Code.\nThe function will translate the City name using the SAMPLE_POSTAL_CODES Global Container")
    @type(LudbFunction)
    @out(name = "oCity", type = String.class, desc = "The City name")
    public static String fnGetCityBasedOnZip(@desc("The Zip Code based on which to return the city name") String iZip) throws Exception {

        // Get the City name from the hcZipCodes Global Container.
        return (String)DBSelectValue(DB_INTERFACE_NAME, "SELECT city FROM SAMPLE_POSTAL_CODES WHERE postal_code="+'"'+iZip+'"', null);
    }


    @desc("This function will return a random value between iMin and iMax.")
    @type(LudbFunction)
    @out(name = "oRandomNumber", type = Integer.class, desc = "The new random value")
    public static Integer fnGetRandomNumber(@desc("The lowest value to return") Integer iMin, @desc("The highest value to return") Integer iMax) throws Exception {
        //return random number in the range [iMin,iMax]
        return new Random().nextInt(iMax - iMin + 1) + iMin;
    }


    @desc("This function will mask a value based on a list of reference data existing in K2View in a form of either a table (ivv,vc or gc) or a Translation.\r\nThe function will identify where the reference data is located based on the iObject input value and what is the key that holds the information based on the $iKey input value;\r\nThen it will randomly select a different masked value from the reference list per each iValueToMask input value provided \r\nand will linked between the two values so if the same $iValueToMask if provided the same masked value will be returned.\r\nNote: This function does not use Shared Memory to store the information.")
    @type(LudbFunction)
    @out(name = "oRandomMaskedValue", type = String.class, desc = "The masked value.")
    public static String fnGetRandomReferenceValue(@desc("The type of the K2 Object that will be used to reference the data ('Table' or 'Translation').") String iObjectType, @desc("The name of the object.") String iObject, @desc("The name of the key/field to use as reference in the iObject.") String iKey, @desc("The Value to be masked.") String iValueToMask) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, String> dmValuesMap = (Map<String, String>)getThreadGlobals("DM");

        //log.info("iObjectType: " + iObjectType);
        //log.info("iObject: " + iObject);
        //log.info("iKey: " + iKey);
        //log.info("iValueToMask: ", iValueToMask);

        String maskedValue = null;
        StringBuilder keySb = new StringBuilder();
        keySb.append(iObjectType);
        keySb.append(":");
        keySb.append(iObject);
        keySb.append(":");
        keySb.append(iKey);
        keySb.append(":");
        keySb.append(iValueToMask);
        String key = keySb.toString();

        //log.info("Inside fnGetRandomReferenceValue");
        //if global DM is set
        if(dmValuesMap!=null) {
            maskedValue = dmValuesMap.get(key);
        }

        if(maskedValue != null) {
    //log.info ("Already masked, maskedValue: " + maskedValue);
    return maskedValue;
        } else {// If the iValueToMask hasn't already been masked
            //log.info("Not Masked yet");

            dmValuesMap = new HashMap<String, String>();
            keySb.setLength(0);
            keySb.append("COUNTS");
            keySb.append(":");
            keySb.append(iObjectType);
            keySb.append(":");
            keySb.append(iObject);
            //key is COUNTS:iObjectType:iObject
            key = keySb.toString();
            // Check is the Object Type is a Table (ivv,vc or gc).
            if ("TABLE".equals(iObjectType.toUpperCase())) {
                // Check to see if the size (number of rows) in the Table is already known.
                //  This is done to eliminate the need to count this information more than once.
                if (dmValuesMap.get(key) == null) {
                    // If "No", calculate the number of rows.
                    int objectCount = (int) DBSelectValue(DB_INTERFACE_NAME, "SELECT COUNT(1) FROM " + '"' + iObject + '"', null);
                    // Update the Gloabal array so this calculation will not have to happen again.
                    dmValuesMap.put(key, Integer.toString(objectCount));
                }
                String rowCount = dmValuesMap.get(key);

                // Generate a random value representing the row number that will be used for the masked value.
                //  The random value is set between 1 and the number of rows in the object.
                int randRowValue = fnGetRandomNumber(1, Integer.parseInt(rowCount));

                // Select a new value to be used as the masked value based on the $randRowValue.
                String oRandomMaskedRowValue = (String) DBSelectValue(DB_INTERFACE_NAME, "SELECT " + '"' + iKey + '"' + " FROM " + '"' + iObject + '"' + " where rowid=" + '"' + randRowValue + '"', null);

                // Add support for apostrophe, replacing ' with '', e.g: O'Brain => O''Brain.
                String oRandomMaskedValue = oRandomMaskedRowValue.replaceAll("'", "''");
                keySb.setLength(0);
                keySb.append(iObjectType);
                keySb.append(":");
                keySb.append(iObject);
                keySb.append(":");
                keySb.append(iKey);
                keySb.append(":");
                keySb.append(iValueToMask);

                //key is iObjectType:iObject:iKey:iValueToMask
                dmValuesMap.put(keySb.toString(), oRandomMaskedValue);
                setThreadGlobals("DM", dmValuesMap);

                // Return the masked value.
                return oRandomMaskedValue;
            }
        }

        return null;
    }


    @desc("This function will return the State name based on the input Zip Code.\nThe function will translate the State name using the SAMPLE_POSTAL_CODES Global Container.")
    @type(LudbFunction)
    @out(name = "oState", type = String.class, desc = "The State name")
    public static String fnGetStateBasedOnZip(@desc("The Zip Code based on which to return the city name") String iZip) throws Exception {
        // Get the City name from the hcZipCodes Global Container.
        return (String)DBSelectValue(DB_INTERFACE_NAME,"SELECT state FROM SAMPLE_POSTAL_CODES WHERE postal_code="+'"'+iZip+'"',null);
    }






    @type(LudbFunction)
    @out(name = "oMaskedCreditScore", type = Integer.class, desc = "The new random credit score")
    public static Integer fnMaskCreditScore() throws Exception {
        return fnGetRandomNumber(350,850);
    }


    @desc("This function will mask the DOB (Date of Birth) by simply adding or subtracting number of days from its DOB.\r\nThe function will add a random number between 0 and the iDays value to the iDOB if iOperation is +, and will subtract them if iOperation is -.")
    @type(LudbFunction)
    @out(name = "iMaskedDOB", type = String.class, desc = "The Masked Date Of Birth")
    public static String fnMaskDOB(@desc("Date of Birth") String iDOB, @desc("+ to add days, - to subtract days") String iOperation, @desc("Max Number of days that will impact the original DOB") Integer iDays) throws Exception {
        final String SUM_OPER = "+";
        final String SUBSTRACT_OPER = "-";

        // Determine if to add or substract the iDays from/to iDOB
        if (iDOB!=null && !iDOB.isEmpty()){
    //format the date
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
    Calendar c = Calendar.getInstance();
    //set the time for the calendar object
    c.setTime(sdf.parse(iDOB));
    String oMaskedDOB = "";
    //generate random number which will be the number of days that will add/substracted to/from the input date
    int shiftDays = fnGetRandomNumber(0,iDays);

    if(iOperation.equals(SUM_OPER)){
        // Add shiftDays to iDOB
        c.add(Calendar.DATE, shiftDays);
    } else if(iOperation.equals(SUBSTRACT_OPER)){ // Substract days
        // Substract shiftDays from iDOB
        c.add(Calendar.DATE, -1*shiftDays);
    }else{//in case of invalid operator
        reportUserMessage("Invalid Operation Param");
        return oMaskedDOB;
    }
    oMaskedDOB = sdf.format(c.getTime());
    // Return the masked DOB
    return sdf.format(sdf.parse(oMaskedDOB));
        }

        return iDOB;
    }


    @desc("This function will mask an Email Address by randomly masking every letter in the email address expect '.' and '@' signs.")
    @type(LudbFunction)
    @out(name = "oMaskedEmail", type = String.class, desc = "The masked Email Address")
    public static String fnMaskEmail(@desc("The email address to mask") String iEmail, @desc("The Encryption to use to mask the Email Address") String iEncryption) throws Exception {
        // If the iEmail is NULL then return NULL.
        if(iEmail!=null && !iEmail.isEmpty()){
    // Initiate the Random Encryption argument.
    String randomEncryption = "";
    // Check if the function calls to use a hard-coded Encryption (provided as an input)
    // or a randomized value.
    //   If a random value is needed

    if(RANDOM_ENCRYPTION.equals(iEncryption.toUpperCase())){
        // For every charecter in the email address:
        for(int i = 0 ; i < iEmail.length() ; i++){
     char currentChar = iEmail.charAt(i);
     // If the current charecter equals . or @
     if(currentChar=='@' || currentChar=='.'){
        // Concatenate 0 to the Encrption arguent
        randomEncryption = randomEncryption + "0";
        // Else - The current charecter is not . or @.
     } else {
        // Concatenate a rnadom number to the Encryption argument
        randomEncryption = randomEncryption + Integer.toString(fnGetRandomNumber(0,9));
     }
        }
        // Else - use the provided encryption.
    } else {
        // Use the existing/provided Encryption.
        randomEncryption = iEncryption;
    }

    // Mask the Email address.
    return k2s_MaskField(iEmail,randomEncryption);
        }
        return null;
    }


    @desc("This function will mask an input field using AES encryption standard.\r\nThe returned value can be unreadable.")
    @type(LudbFunction)
    @out(name = "oMasedField", type = String.class, desc = "The mased field value.")
    public static String fnMaskFieldAES(@desc("The value of the field.") String iFieldValue) throws Exception {
        byte[] encrypted = null;
        final String key = "abcdefghijklmnop";
        final String initVector = org.apache.commons.lang3.StringUtils.repeat("a", 16);

        javax.crypto.spec.IvParameterSpec iv =new javax.crypto.spec.IvParameterSpec(initVector.getBytes("UTF-8"));
        javax.crypto.spec.SecretKeySpec skeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"), "AES");
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5PADDING");
        try {

    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec, iv);

    encrypted = cipher.doFinal(iFieldValue.getBytes());

    //encrypted = org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);

    //cipher.init(javax.crypto.Cipher.DECRYPT_MODE, skeySpec, iv);
    //String encryptStr = org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
    //byte[] original = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encryptStr));

    //return new String(original);
    String encryptStr = new String(encrypted);
    return encryptStr;
        } catch (Exception ex) {
    ex.printStackTrace();
        }
        return null;
    }


    @desc("This function will mask an input field using the iEncryption Key.\nIt will randomly generate the encryption.")
    @type(LudbFunction)
    @out(name = "oMasedField", type = String.class, desc = "The mased field value.")
    public static String fnMaskFieldRandomly(@desc("The value of the field.") String iFieldValue) throws Exception {
        // Create a random encryption # between 1000 and 100000
        int encryption = fnGetRandomNumber(1000,100000);
        // Mask the field,
        String oMaskedRowField = k2s_MaskField(iFieldValue,Integer.toString(encryption));
        String oMaskedField = null;
        // Check the globals flag to determine if to convert a single apostrophe to double apostrohe.
        if(REPLACE_APOSTROPHE_FLAG.equals(FLAG_ENABLED)){
    // Add supprot for apostrophes, replacing ' with '', e.g: O'Brian => O''Brian
    oMaskedField = oMaskedRowField.replaceAll("'","''" );

    // Else - If apostrophe flag is set to 0
        } else {

    // Keep the original value.
    oMaskedField = oMaskedRowField;
        }

        // Return the masked value.
        return oMaskedField;
    }




    @desc("This function is used for masking a given field with a constant value.\nSimply returns the input value.")
    @type(LudbFunction)
    @out(name = "oMaskedValue", type = String.class, desc = "Return the iStaticValue")
    public static String fnMaskStaticValue(@desc("A constant value that will be used for masking the field") String iStaticValue) throws Exception {
        return iStaticValue;
    }






    @desc("This function will mask a US ZIP code using the iEncryption Key.\nThe function will find the entry index of the Zip Code in the full list of all available US Zip Codes,\nThen it will add the Encryption value to the index value and modulo it based on the number of available US Zip Codes.\nThe new value is used as the index of the new Zip Code which will be returned.")
    @type(LudbFunction)
    @out(name = "oMaskedZipCode", type = String.class, desc = "The Masked Zip Code")
    public static String fnMaskZipCode(@desc("the zip code") String iZipCode, @desc("The encryption used to mask the Zip Code") String iEncryption) throws Exception {
        if(iZipCode!=null && !iZipCode.isEmpty()){

    // Take only 5 digits US Zip Code (remove any PLUS4 values).
    // Strip any non-numeric values and take the first 5 digits.
    String modifiedIzipCode = iZipCode.replaceAll("[^\\d]", "");
    //If the modifiedIzipCode is shorter than 5 charcaters pad it with leading zeros
    if (modifiedIzipCode.length() < 5)
        modifiedIzipCode = k2_pad(modifiedIzipCode,"0", 5,false);

    String zipNoPlus4 = modifiedIzipCode.substring(0,5);

    final String sql = "SELECT DISTINCT postal_code FROM SAMPLE_POSTAL_CODES ORDER BY postal_code";
    @SuppressWarnings("unchecked")
    List<String> indexdList =(List<String>) getThreadGlobals("ZipCodes");
    // Check if the list of ZIP Codes were already loaded into Global Memory.
    if (null == indexdList){
        // Get a list of all US based ZIP Codes.
        ResultSetWrapper results = DBQuery(DB_INTERFACE_NAME, sql, null);
        if(results!=null){
     // Prepare a new array to hold the indexed ZIP Codes.
     indexdList = new ArrayList<String>();

     // Loop through the restuls array and organize all ZIP codes under an indexed array.
     int index=0;int counter = 0;

     for(Object[] res:results){
        indexdList.add((String)res[0]);
     }
     setThreadGlobals("ZipCodes",indexdList);
        }
    }

    int zipIndex = indexdList.indexOf(zipNoPlus4);
    if(zipIndex < 0){
        zipIndex = 0;
    }
    int maskedZipIndex = 0;
    // Check if the encryption should be created using a random number or a hard-coded one.
    // If random encryption is required:
    if(RANDOM_ENCRYPTION.equals(iEncryption.toUpperCase())){
        // Get the new ZipIndex by adding a random number between 1 and 100000 to the ZipIndex
        // and using Modulo on the number of ZIP Codes available.
        maskedZipIndex = (zipIndex + fnGetRandomNumber(1,100000))% indexdList.size();
        // Use the hard-coded Encryption value.
    } else {
        // Get the new ZipIndex by adding the iEncryption to the ZipIndex
        // and using Modulo on the number of ZIP Codes available.
        maskedZipIndex = (zipIndex + Integer.parseInt(iEncryption))% indexdList.size();
    }
    // Retrieve the new Zip Code based on the maskedZipIndex.
    return indexdList.get(maskedZipIndex);
        }
        return iZipCode;
    }


    @desc("This function will mask a US ZIP code maintaining the State Code of the input ZIP code.\r\nThe function will find the state code of the given Zip Code \r\nin the list of available US Zip Codes.\r\nThen based on the retrieved State code, randomly return a valid ZIP code.")
    @type(LudbFunction)
    @out(name = "oMaskedZipCode", type = String.class, desc = "The masked Zip Code")
    public static String fnMaskZipCodeByState(@desc("The zip code") String iZipCode, @desc("The encryption used to mask the Zip Code") String iEncryption) throws Exception {
        if(iZipCode!=null && !iZipCode.isEmpty()){

    //remove all the non-digit characters from the zip code
    String modifiedIzipCode = iZipCode.replaceAll("[^\\d]", "");
    if(modifiedIzipCode.length() >= 5){
        String zipNoPlus4 = modifiedIzipCode.substring(0,5);
        //SQL Query for retrieving state based on the zipcode
        final String SELECT_STATE_SQL = "Select Distinct state From SAMPLE_POSTAL_CODES Where postal_code = "+'"'+zipNoPlus4+'"';

        String state = (String)DBSelectValue(DB_INTERFACE_NAME, SELECT_STATE_SQL, null);
        //SQL Query for retrieving state
        final String SELECT_STATE_ZIP_SQL = "SELECT DISTINCT postal_code FROM SAMPLE_POSTAL_CODES Where state = "+'"'+state+'"'+" ORDER BY postal_code";

        @SuppressWarnings("unchecked")
        Map<String, List<String>> zipCodeStateMap = (Map<String, List<String>>)getThreadGlobals("ZipCodesByState");
        List<String> zipCodeList = null;
        // Get the US State based on the zipNoPlus4.
        if(state!=null && !state.isEmpty() && !state.equalsIgnoreCase("NULL")) {
     if(zipCodeStateMap == null) zipCodeStateMap = new HashMap<String, List<String>>();

     // Check if the list of ZIP Codes were already loaded into Global Memory.
     if(zipCodeStateMap.get(state)==null){

        // Get a list of all US based ZIP Codes.
        ResultSetWrapper results = DBQuery(DB_INTERFACE_NAME, SELECT_STATE_ZIP_SQL, null);
        if(results != null){
     // Prepare a new list to hold the indexed ZIP Codes.
     zipCodeList = new ArrayList<String>();

     // Loop through the restuls array and organize all ZIP codes under an indexed list.
     for(Object[] res:results){
   zipCodeList.add((String)res[0]);
     }
     zipCodeStateMap.put(state,zipCodeList);
     setThreadGlobals("ZipCodesByState",zipCodeStateMap);
        }
     }
     zipCodeList = zipCodeStateMap.get(state);
     // Get the index/location of the Zip Code in the zipCodeList.
     int zipIndex =   zipCodeList.indexOf(zipNoPlus4);
     if(zipIndex < 0){
        zipIndex = 0;
     }
     int maskedZipIndex = 0;
     // Check if the encryption should be created using a random number or a hard-coded one.
     // If random encryption is required:
     if(RANDOM_ENCRYPTION.equalsIgnoreCase(iEncryption)){
        // Get the new ZipIndex by adding a random number between 1 and 100000 to the ZipIndex
        // and using Modulo on the number of ZIP Codes available.
        maskedZipIndex = (zipIndex + fnGetRandomNumber(1,100000))% zipCodeList.size();
        // Use the hard-coded Encryption value.
     } else {
        // Get the new ZipIndex by adding the iEncryption to the ZipIndex
        // and using Modulo on the number of ZIP Codes available.
        maskedZipIndex = (zipIndex + Integer.parseInt(iEncryption)) % zipCodeList.size();
     }
     // Retrieve the new Zip Code based on the maskedZipIndex.
     String oMaskedZipCode = zipCodeList.get(maskedZipIndex);

     // Return the new masked Zip Code.
     return (oMaskedZipCode);
        }
    }
        }
        return iZipCode;
    }


    @desc("This function masks the last 7 digits of the input phone number.\r\n" +
    " It does not change the format of the number.")
    @type(LudbFunction)
    @out(name = "NewPhone", type = String.class, desc = "The new Random phone number")
    public static String fnGenerateRandomPhoneNo(@desc("The phone number to be masked") String inpPhone) throws Exception {
        if (inpPhone == null) {
            return null;
        }
        Random generator = new Random();

        String num1;
        String num2;
        int currentPos = inpPhone.length() - 1;
        StringBuilder newPhone = new StringBuilder();
        newPhone.append(inpPhone);

        //If the phone number contains less than 7 digits, then call fnGetRandomNumber to create a random number
        if ((inpPhone.replaceAll("[^\\d]", "")).length() < 7) {
            return Integer.toString(fnGetRandomNumber(1000000, 9999999));
        }

        while (inpPhone.equals(newPhone.toString())) {
            num1 = Integer.toString(generator.nextInt(641) + 100); //number has to be less than 742//can't go below 100.
            num2 = Integer.toString(generator.nextInt(8999) + 1000); // make numbers 0 through 9 for each digit.//can't go below 1000.

            // Mask last 7 digits only, first mask the last 4 digits and after that the remaining 3 digits
            currentPos = inpPhone.length() - 1;
            for (int index = 3; index >= 0; index--) {
                while (!(Character.isDigit(newPhone.charAt(currentPos))) && currentPos > 0) {
                    currentPos--;
                }
                if (currentPos >= 0) {
                    newPhone.setCharAt(currentPos, num2.charAt(index));
                    currentPos--;
                }
            }

            for (int index = 2; index >= 0; index--) {
                while (!(Character.isDigit(newPhone.charAt(currentPos))) && currentPos > 0) {
                    currentPos--;
                }
                if (currentPos >= 0) {
                    newPhone.setCharAt(currentPos, num1.charAt(index));
                    currentPos--;
                }
            }

        }
        return (newPhone.toString());
    }


    @type(LudbFunction)
    @out(name = "fullName", type = String.class, desc = "")
    public static String generateFullName() throws Exception {
        // Get  firs and last name from the contact table
        String sql = "SELECT FIRSTNAME, LASTNAME FROM CONTACT";
        reportUserMessage("Before query results");
        Db.Rows rows = ludb().fetch(sql);
        for (Db.Row row:rows){
    reportUserMessage("FIRSTNAME: " + row.get("FIRSTNAME") + " - LASTNAME: " + row.get("LASTNAME"));
    // Build a fullName variable to return
    String fullName = row.get("FIRSTNAME") + " " + row.get("LASTNAME");
    return(fullName);
        }

        return("ANONYMOUS");
    }


    @type(LudbFunction)
    @out(name = "FIRSTNAME", type = String.class, desc = "")
    public static String returnedMaskedFirstName() throws Exception {
        String sql = "SELECT FIRSTNAME " +
        "FROM CONTACT";
        return (String)db("ludb").fetch(sql).firstValue();
    }


    @type(LudbFunction)
    @out(name = "LASTNAME", type = String.class, desc = "")
    public static String returnedMaskedLastName() throws Exception {
        String sql = "SELECT LASTNAME " +
        "FROM CONTACT";
        return (String)db("ludb").fetch(sql).firstValue();
    }


    @type(LudbFunction)
    @out(name = "fullName", type = String.class, desc = "")
    public static String returnedMaskedFullName() throws Exception {
        String fName = returnedMaskedFirstName();
        String lName = returnedMaskedLastName();

        String fullName = fName + " " + lName;
        return fullName;
    }


    @type(LudbFunction)
    @out(name = "maskAddress", type = String.class, desc = "")
    public static String fnGenerateStreetAddress(String address) throws Exception {
        return fnGetRandomNumber(1,20000) + " " + fnGetRandomReferenceValue("TABLE","SAMPLE_STREET_NAME","STREET_NAME",address);
    }


    @desc("This function will return the City name based on the input Zip Code.\nThe function will translate the City name using the SAMPLE_POSTAL_CODES Global Container")
    @out(name = "latitude", type = String.class, desc = "The Latitude associated with the input Zip")
    @out(name = "longitude", type = String.class, desc = "The Latitude associated with the input Zip")
    public static Object fnGetGeoBasedOnZip(@desc("The Zip Code based on which to return the city name") String iZip) throws Exception {
        // Get the Latitude, Longitude basd on a zip code
        Db.Row zipRow = ludb().fetch("SELECT latitude,longitude FROM SAMPLE_POSTAL_CODES WHERE postal_code=?",iZip).firstRow();


        if(!zipRow.isEmpty())
    return new Object[] {zipRow.get("latitude"),zipRow.get("longitude")};

        // Return empty
        return new Object[] {"",""};
    }


    @desc("This function will generate a random credit card based on an input type.\r\n" +
    "If the input is not provided it will select one in random")
    @out(name = "oManufacturedCreditCard", type = String.class, desc = "The manufactured Credit Card Number & Type")
    public static String fnManufactureCreditCardBasedOnType(String iCreditCardType) throws Exception {
        // List of valid Credit Card Type
        List<String> ccList = Arrays.asList("AMEX","DINERS","DISCOVER","JCB","LASERCARD","MAESTRO","MASTERCARD","SOLO","SWITCH","VISA");

        // If iCreditCardType is not provided, randomly select value
        if(StringUtils.isBlank(iCreditCardType) || !ccList.contains(iCreditCardType.toUpperCase()))
    iCreditCardType = ccList.get(new Random().nextInt(ccList.size()));

        // ############### Setup ############### //

        // Create the Array that will hold all the Credit Card's valid Prefixes.
        //  Each credit card has its own dedicated prefix numbers.
        Map<String, String[]> ccArray = new HashMap<String, String[]>();

        // Add the valid American Express (Amex) Prefixes:
        ccArray.put("AMEX_Length", new String[]{"15"});
        ccArray.put("AMEX_Prefix", new String[]{"34","37"});

        // Add the valid Diners Prefixes:
        ccArray.put("DINERS_Length", new String[]{"14","15","16"});
        ccArray.put("DINERS_Prefix", new String[]{"36","38","54","55","300","301","302","303","304","305","2014","2149"});

        // Add the valid Discover Prefixes:
        ccArray.put("DISCOVER_Length", new String[]{"14","16"});
        ccArray.put("DISCOVER_Prefix", new String[]{"64","65","622","6011"});

        // Add the valid JCB Prefixes:
        ccArray.put("JCB_Length", new String[]{"16"});
        ccArray.put("JCB_Prefix", new String[]{"35"});

        // Add the valid LaserCard Switch:
        ccArray.put("LASERCARD_Length", new String[]{"16","18","19"});
        ccArray.put("LASERCARD_Prefix", new String[]{"6304","6706","6771","6709"});

        // Add the valid Maestro Prefixes:
        ccArray.put("MAESTRO_Length", new String[]{"12","13","14","15","16","18","19"});
        ccArray.put("MAESTRO_Prefix", new String[]{"5018","5020","5038","6304","6759","6761","6762","6763"});

        // Add the valid MasterCard Prefixes:
        ccArray.put("MASTERCARD_Length", new String[]{"16"});
        ccArray.put("MASTERCARD_Prefix", new String[]{"51","52","53","54","55"});

        // Add the valid Solo Prefixes:
        ccArray.put("SOLO_Length", new String[]{"16","18","19"});
        ccArray.put("SOLO_Prefix", new String[]{"6334","6767"});

        // Add the valid Switch Switch:
        ccArray.put("SWITCH_Length", new String[]{"16","17","18","19"});
        ccArray.put("SWITCH_Prefix", new String[]{"4903","4905","4911","4936","564182","633110","6333","6759"});

        // Add the valid Visa Prefixes:
        ccArray.put("VISA_Length", new String[]{"16"});
        ccArray.put("VISA_Prefix", new String[]{"4","4485","4508","4532","4539","4556","4716","4844","4913","4916","4917","4929","417500","40240071"});

        // ############### Calculate a Random Valid Credit Card Number ############### //

        // Transform the Credit Card Type into an UpperCase text.
        String iCreditCardTypeUpper = iCreditCardType.toUpperCase();

        // Select a new random valid length per the Credit Card Type.
        //  Create a random position.
        int randomLengthPosition = fnGetRandomNumber(0,(ccArray.get(iCreditCardTypeUpper+"_Length")).length-1);
        //  Extract the a valid length.
        String newCreditCardLength = (ccArray.get(iCreditCardTypeUpper+"_Length"))[randomLengthPosition];

        // Select a random prefix poition for the new Credit Card.
        int randomPrefixPosition = fnGetRandomNumber(0, ccArray.get(iCreditCardTypeUpper+"_Prefix").length-1);
        // Extract the new prefix to be used by the masked value.
        String newCreditCardPrefix = (ccArray.get(iCreditCardTypeUpper+"_Prefix"))[randomPrefixPosition];

        // Generate the new manufactured Credit Card.
        String oManufacturedCreditCard = fnGenerateRandomValidCreditCard(newCreditCardLength,newCreditCardPrefix);

        // Return the manufactured Credit Card
        return oManufacturedCreditCard;
        //return Util.map("Credit Card",oManufacturedCreditCard,"Type",iCreditCardType);
    }


    @desc("This function will manufactue a US ZIP code based on an input State 2 letters abberviation.")
    @type(LudbFunction)
    @out(name = "oManufacturedState", type = String.class, desc = "The manufactured State")
    public static String fnManufactureZipCodeByState(@desc("The State in which the Zip will be manufactured") String iState) throws Exception {
        // List of valid States
        List<String> stateList = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC", "AS", "GU", "MP", "PR", "VI");

        // If iState is not provided, randomly select value
        if(StringUtils.isBlank(iState) || !stateList.contains(iState.toUpperCase()))
    iState = stateList.get(new Random().nextInt(stateList.size()));

        // Check if the list of availablle Zip Codes for the given state was already previously created
        @SuppressWarnings("unchecked")
        Map<String, List<String>> zipCodeStateMap = (Map<String, List<String>>)getThreadGlobals("ZipCodesByState");
        final List<String> zipCodeList;

        // If it wasn't created, initiate it.
        if(zipCodeStateMap == null) zipCodeStateMap = new HashMap<String, List<String>>();

        // Check if the list of ZIP Codes were already loaded into Global Memory.
        if(zipCodeStateMap.get(iState) == null){

    // Prepare a new list to hold the indexed ZIP Codes.
    zipCodeList = new ArrayList<String>();

    fabric().fetch("SELECT DISTINCT postal_code FROM SAMPLE_POSTAL_CODES Where state = ? ORDER BY postal_code",iState).each(row -> {
        zipCodeList.add("" + row.get("postal_code"));
    });

    zipCodeStateMap.put(iState,zipCodeList);
    setThreadGlobals("ZipCodesByState",zipCodeStateMap);

    // Else - Meaning the list was previously create
        } else {
    // Get the previously created list
    zipCodeList = zipCodeStateMap.get(iState);
        }

        // Retrieve the new Zip Code by randomly selecting aa zip from the list of applicable state zip codes.
        String oManufacturedState = zipCodeList.get(new Random().nextInt(zipCodeList.size()));

        // Return the new manufactured Zip Code.
        return (oManufacturedState);
    }


	@out(name = "strings", type = Object.class, desc = "")
	public static Object fnSmartSplit(String string) throws Exception {
		Object[] args = (Object[])string.split("(?:^|,)(?=(?:[^\']*(?:[\\\\][\'])*[^\']*(?<=[^\\\\])\'[^\']*(?:[\\\\][\'])*[^\']*(?<=[^\\\\])\')*[^\']*$)");
		Arrays.setAll(args, i -> args[i].toString().replaceAll("(?<=(?:^|[^\\\\]))\'",""));
		 
		return new Object[]{args};
	}


	@out(name = "maskedCreditcard", type = String.class, desc = "")
	public static String fnMaskCreditCard(String creditcard, String type) throws Exception {
		return fnManufactureCreditCardBasedOnType(type);
	}


	@out(name = "type", type = String.class, desc = "")
	public static String getCreditCardType(String creditcard) throws Exception {
		// List of valid Credit Card Type
		List<String> ccList = Arrays.asList("AMEX","DINERS","DISCOVER","JCB","LASERCARD","MAESTRO","MASTERCARD","SOLO","SWITCH","VISA");
		
		// ############### Setup ############### //
		
		// Create the Array that will hold all the Credit Card's valid Prefixes.
		//  Each credit card has its own dedicated prefix numbers.
		Map<String, String[]> ccArray = new HashMap<String, String[]>();
		 
		
		// Add the valid American Express (Amex) Prefixes:
		ccArray.put("AMEX_Length", new String[]{"15"});
		ccArray.put("AMEX_Prefix", new String[]{"34","37"});
		 
		// Add the valid Diners Prefixes:
		ccArray.put("DINERS_Length", new String[]{"14","15","16"});
		ccArray.put("DINERS_Prefix", new String[]{"36","38","54","55","300","301","302","303","304","305","2014","2149"});
		 
		// Add the valid Discover Prefixes:
		ccArray.put("DISCOVER_Length", new String[]{"14","16"});
		ccArray.put("DISCOVER_Prefix", new String[]{"64","65","622","6011"});
		 
		// Add the valid JCB Prefixes:
		ccArray.put("JCB_Length", new String[]{"16"});
		ccArray.put("JCB_Prefix", new String[]{"35"});
		 
		// Add the valid LaserCard Switch:
		ccArray.put("LASERCARD_Length", new String[]{"16","18","19"});
		ccArray.put("LASERCARD_Prefix", new String[]{"6304","6706","6771","6709"});
		 
		// Add the valid Maestro Prefixes:
		ccArray.put("MAESTRO_Length", new String[]{"12","13","14","15","16","18","19"});
		ccArray.put("MAESTRO_Prefix", new String[]{"5018","5020","5038","6304","6759","6761","6762","6763"});
		 
		// Add the valid MasterCard Prefixes:
		ccArray.put("MASTERCARD_Length", new String[]{"16"});
		ccArray.put("MASTERCARD_Prefix", new String[]{"51","52","53","54","55"});
		 
		// Add the valid Solo Prefixes:
		ccArray.put("SOLO_Length", new String[]{"16","18","19"});
		ccArray.put("SOLO_Prefix", new String[]{"6334","6767"});
		 
		// Add the valid Switch Switch:
		ccArray.put("SWITCH_Length", new String[]{"16","17","18","19"});
		ccArray.put("SWITCH_Prefix", new String[]{"4903","4905","4911","4936","564182","633110","6333","6759"});
		 
		// Add the valid Visa Prefixes:
		ccArray.put("VISA_Length", new String[]{"16"});
		ccArray.put("VISA_Prefix", new String[]{"4","4485","4508","4532","4539","4556","4716","4844","4913","4916","4917","4929","417500","40240071"});
		 
		// For all substrings of input Credit Card
		for(int i = 0; i < creditcard.length() ; ++i){
		    String subset = creditcard.substring(0,creditcard.length() - i);
		
		    Map.Entry<String,String[]> type = ccArray.entrySet().stream().filter(e -> e.getKey().matches(".*_Prefix")).filter(e -> Arrays.stream(e.getValue()).anyMatch(s -> s.equals(subset))).findFirst().orElse(null);
		    if(type != null) {
		     return type.getKey().substring(0, type.getKey().lastIndexOf('_'));
		    }
		}
		 
		     
		return "VISA";
	}


}
