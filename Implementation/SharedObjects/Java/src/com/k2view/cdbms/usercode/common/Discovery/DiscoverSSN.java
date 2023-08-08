package com.k2view.cdbms.usercode.common.Discovery;

import com.k2view.broadway.model.Actor;
import com.k2view.broadway.model.Context;
import com.k2view.broadway.model.Data;

@SuppressWarnings({ "DefaultAnnotationParam" })
public class DiscoverSSN implements Actor {

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

        // Convert Object input to String & remove any non-digits characters
        String ssn = value.toString().replaceAll("\\D+", "");

        // *************** SSN Validation Rules *************** //
        /*
         * The following rules are used to identify if a SSN is NOT valid:
         * Numbers are 9 digits long.
         * Numbers with all zeros in any digit group (000-##-####, ###-00-####,
         * ###-##-0000).
         * Numbers with 666 or 900–999 (Individual Taxpayer Identification Number) in
         * the first digit group.
         */

        // Check that the SSN is made from 9 digits:
        // - A valid SSN must be 9 digits long
        // If the SSN is not made of 9 digits, return false to indicate the SSN is not
        // valid
        if (ssn.length() != 9) {
            output.put(OUTPUT_DECISION, false);
            return;
        }

        // Check that the SSN's Area-Number (the first 3 digits) meet the Area-Number
        // pattern:
        // - A valid SSN cannot start with an Area Number in these ranges
        // 000,666,900-999
        int ssnAreaNumber = Integer.parseInt(ssn.substring(0, 3));
        // If the Area Number is in one of the above ranges, return false to indicate
        // the SSN is not valid
        if (ssnAreaNumber == 0 || ssnAreaNumber == 666 || (ssnAreaNumber >= 900 && ssnAreaNumber <= 999)) {
            output.put(OUTPUT_DECISION, false);
            return;
        }

        // Check that the SSN's Group Number (the second 2 digits) meet the Group Number
        // pattern:
        // - Group Number with 00 is not valid
        int ssnGroupNumber = Integer.parseInt(ssn.substring(3, 5));
        // If the Group Number is in 00, return false to indicate the SSN is not valid
        if (ssnGroupNumber == 0) {
            output.put(OUTPUT_DECISION, false);
            return;
        }

        // Check that the SSN's Serial Number (the last 4 digits) meet the Serial Number
        // pattern:
        // - Serial Number with 0000 is not valid
        int ssnSerialNumber = Integer.parseInt(ssn.substring(5, 9));
        // If the Serial Number is in 00000, return false to indicate the SSN is not
        // valid
        if (ssnSerialNumber == 0) {
            output.put(OUTPUT_DECISION, false);
            return;
        }

        // If all validation passed, return true to indicate the SSN is valid
        output.put(OUTPUT_DECISION, true);
    }

    @Override
    public void close() throws Exception {
        // resultMap.clear();
    }
}
