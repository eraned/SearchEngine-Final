package Model;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.List;

/**
 * This department is responsible for price type words
 * if the word does not belong to the department, return Null value, Otherwise we will parse according to laws
 */
public class PriceToken extends NumberToken implements IToken {
    private HashMap<String, Double> NumberByNumber;

    /**
     * Constructor
     */
    public PriceToken() {
        this.NumberByNumber = new HashMap<>();
        this.InitiateNumber();
    }

    /**
     * A function that converts all strings to be real numbers.
     * multiply each number by its value according to the dictionary
     * @param sentence - list to parse with 8 words or less
     * @return - arsedResult result after parse
     */
    public ParsedResult TryParse(List<String> sentence) {
        int size = sentence.size();
        String first = sentence.get(0);
        String second = size > 1 ? sentence.get(1) : "";
        String third = size > 2 ? sentence.get(2) : "";
        String fourth = size > 3 ? sentence.get(3) : "";
        StringBuilder result = new StringBuilder();
        String withoutDollar = first;
        Integer index = 1;
        boolean flag = false;

        //it's not price
        if (first.charAt(0) != '$' && !second.equals("Dollars") && !second.equals("dollars") && !third.equals("Dollars") && !third.equals("dollars") && !fourth.equals("Dollars") && !fourth.equals("dollars")) {
            return null;
        }
        // if there is $ - cut him
        if (first.charAt(0) == '$') {
            withoutDollar = first.substring(1, first.length());
        }
        //if the last char is 'm' so cut him
        else if (first.charAt(withoutDollar.length() - 1) == 'm') {
            withoutDollar = first.substring(0, first.length() - 1);
            flag = true; //we need to multiply by a million

        }
        String firstWithoutCommaAndDollar = withoutDollar.replaceAll(",", "");

        //if the first token is not number
        if (!StringUtils.isNumeric(firstWithoutCommaAndDollar)) {
            return null;
        }

        //it's number - change to number and parse like price
        Double numPrice = Double.parseDouble(firstWithoutCommaAndDollar);
        if (flag) {
            numPrice *= 1000000;
        }

        if (NumberByNumber.containsKey(second)) {
            numPrice *= NumberByNumber.get(second);
            if ((third.equals("U.S.") || third.equals("U.S")) && ((fourth.equals("Dollars") || fourth.equals("dollars")))) {
                index = 4;
            } else if (third.equals("Dollars") || third.equals("dollars")) {
                index = 3;
            } else {
                index = 2;
            }
            result.append(FinallyParse(numPrice));
        } else if (numPrice < 1000000) {
            if (second.contains("/") && (third.equals("Dollars") || third.equals("dollars"))) {
                return new ParsedResult(true, result.append(String.format("%s %s Dollars", first, second)), 3);
            }
            // return number as is - num without fraction
            else if (!second.contains("/") && (third.equals("Dollars") || second.equals("Dollars"))) {
                return new ParsedResult(true, result.append(first).append(" Dollars"), 2);
            } else {
                return new ParsedResult(true, result.append(withoutDollar).append(" Dollars"), 1);
            }
        } else if (numPrice > 1000000 && (second.equals("Dollars") || second.equals("dollars"))) {
            index = 2;
            result.append(FinallyParse(numPrice));
        } else {
            index = 1;
            result.append(FinallyParse(numPrice));
        }
        //it's number with fraction and dollar - 22 3/4 Dollars
        return new ParsedResult(true, result, index);
    }

    /**
     * Generic function that parse to words that are from the class of prices
     * @param num - A number to parse
     * @return - String after parse
     */
    private StringBuilder FinallyParse(Double num) {
        StringBuilder result = new StringBuilder();
        String divResult = String.valueOf(num / 1000000.0).replace(".0", "");
        result.append(divResult).append(" ").append("M").append(" ").append("Dollars");
        return result;
    }

    /**
     * a dictionary that links the name of the digit with its numerical value
     */
    private void InitiateNumber() {
        NumberByNumber.put("Thousand", 1000.0);
        NumberByNumber.put("thousand", 1000.0);
        NumberByNumber.put("Million", 1000000.0);
        NumberByNumber.put("million", 1000000.0);
        NumberByNumber.put("m", 1000000.0);
        NumberByNumber.put("bn", 1000000000.0);
        NumberByNumber.put("Billion", 1000000000.0);
        NumberByNumber.put("billion", 1000000000.0);
        NumberByNumber.put("Trillion", 1000000000000.0);
        NumberByNumber.put("trillion", 1000000000000.0);
    }
}
