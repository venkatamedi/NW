package com.k2view.cdbms.usercode.common.Discovery;

import org.apache.commons.lang3.StringUtils;

import com.k2view.broadway.model.Actor;
import com.k2view.broadway.model.Context;
import com.k2view.broadway.model.Data;

@SuppressWarnings({ "DefaultAnnotationParam"})
public class DiscoverCreditCard implements Actor {

    static private String INPUT_VALUE     = "discover";
    static private String OUTPUT_DECISION = "decision";

    /**
     * Action performed by the actor.
     *
     * @param input   expected input is resource name (or relative or absolute path
     *                for resources)
     * @param output  provided output consists of document only
     * @param context
     * @throws Exception
     */
    @Override
    public void action(Data input, Data output, Context context) throws Exception {

        // Get input value
        String value = input.string(INPUT_VALUE);

        // Convert Object input to String
        String cc = value.toString();

        // If input string is empty or null, return false to indicate the string is not
        // a valid credit card
        if (StringUtils.isBlank(cc)) {
            output.put(OUTPUT_DECISION, false);
            return;
        }
        // If the input string has characters other than digits or spaces\tabs, return
        // false to indicate the string is not a valid credit card
        if (!cc.matches("^[0-9 \t]+$")) {
            output.put(OUTPUT_DECISION, false);
            return;
        }
        // Check that the credit card number is between 12 and 19
        if (cc.replaceAll("[^0-9]", "").length() < 12 || cc.replaceAll("[^0-9]", "").length() > 19) {
            output.put(OUTPUT_DECISION, false);
            return;
        }

        // Extract only the digits of the credit card
        String ccNum = cc.replaceAll("\\D+", "");
        // Extract the checksum of the credit card
        String ccChecksum = ccNum.substring(ccNum.length() - 1);

        // Define the parameter that would hold the sum of the numbers (needed for the
        // Modulus 10 Algorithm).
        int sumCCnumbers = 0;

        // Reverse the Credit Card Number so to make it easier to apply the Modulus 10
        // algorithm.
        String reversedCC = new StringBuilder(ccNum.substring(0, ccNum.length() - 1)).reverse().toString();

        // Apply the Mudolus 10 Algorithm.
        // For information on this algorithm please see:
        // http://en.wikipedia.org/wiki/Luhn_algorithm.
        int oddNumberDouble = 0;
        for (int i = 1; i <= reversedCC.length(); i++) {
            // Check if it's an odd digit.
            if ((i & 1) != 0) {
                // If Yes, multiply it by 2.
                oddNumberDouble = java.lang.Character.getNumericValue(reversedCC.charAt(i - 1)) * 2;
                // If the new value is larger than 9, subtract 9 from it.
                oddNumberDouble = (oddNumberDouble > 9) ? oddNumberDouble - 9 : oddNumberDouble;
                // Add the value to the overall Credit Card summation.
                sumCCnumbers += oddNumberDouble;
                // If the digit is Even.
            } else {
                // Even
                // Add the value of the even digit to the overall Credit Card summation.
                sumCCnumbers += java.lang.Character.getNumericValue(reversedCC.charAt(i - 1));
            }
        }

        // Calcualte the checksum digit by substracting the units digit from 10.
        int checkSumDigit = 10 - (sumCCnumbers % 10);
        // In case checkSumDigit equals to 10 then change it to 0
        checkSumDigit = (checkSumDigit == 10) ? 0 : checkSumDigit;

        // Verify if the calculated checksum matches the input credit card one, if yes,
        // return true;
        if (ccChecksum.matches("" + checkSumDigit)) {
            output.put(OUTPUT_DECISION, true);
            return;
        }

        // else - return false
        output.put(OUTPUT_DECISION, false);
    }

    @Override
    public void close() throws Exception {
        // resultMap.clear();
    }
}
