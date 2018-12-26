package Model;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.List;

/**
 * This department is responsible for digit type words
 * if the word does not belong to the department, return Null value, Otherwise we will parse according to laws
 */
public class NumberToken implements IToken {
    private HashMap< String ,  Double > NumberByNumber;

    /**
     * Constructor
     */
    public NumberToken(){
        this.NumberByNumber = new HashMap<>();
        this.InitiateNumber();
    }

    /**
     * A function that converts all strings to be real numbers.
     * multiply each number by its value according to the dictionary
     * @param sentence list to parse with 8 words or less
     * size- size of list (sentence)
     * index - how much words from the sentence we used
     * @return ParsedResult result after parse
     */
    public ParsedResult TryParse(List<String> sentence) {
        int size = sentence.size();
        String first = sentence.get(0);
        //if size > 1 so second = sentence.get(1) else second = ""
        String second = size > 1 ? sentence.get(1) : "";
        //if size > 2 so third = sentence.get(1) else third = ""
        String third = size > 2 ? sentence.get(2) : "";
        StringBuilder result = new StringBuilder();
        Integer index = 1;
        String firstWithoutComma = first.replaceAll("," , "");
        //if the first token is not number
        if(!StringUtils.isNumeric(firstWithoutComma)){
            return null; }
        else {
            //its number -> parse real Double
            Double num = Double.parseDouble(firstWithoutComma);
            if (NumberByNumber.containsKey(second)){
                num*= NumberByNumber.get(second);
                index = 2 ;
            }// number with fraction and
            else if( second.contains("/") && NumberByNumber.containsKey(third)){
                num*= NumberByNumber.get(third);
                index = 3;
            }
            // return number as is - num without fraction
            if (num < 1000 && !second.contains("/")) {
                long endTime = System.nanoTime();
                return new ParsedResult(true , result.append(firstWithoutComma), 1);
            }
            //return number as is with fraction
            else if (num < 1000 && second.contains("/")) {
                return new ParsedResult(true , result.append(String.format(" %s %s", firstWithoutComma, second)), 2);
            }
            //Thousand
            else if (num >= 1000.0 && num < 1000000.0){
                result.append(FinallyParse (num ,second, 'K' ,NumberByNumber.get("Thousand")));
            }
            //Million
            else if (num >= 1000000.0 && num < 1000000000.0){
                result.append(FinallyParse (num ,second, 'M' ,NumberByNumber.get("Million")));
            }
            //Billion
            else if (num >= 1000000000.0 && num < 1000000000000.0){
                result.append(FinallyParse (num ,second, 'B' ,NumberByNumber.get("Billion")));
            }
            //more than Billion - trillion
            else if (num >= 1000000000000.0){
                result.append(FinallyParse (num ,second, 'B' ,NumberByNumber.get("Billion")));
            }
        }
        return new ParsedResult(true , result, index);
    }

    /**
     * a generic function that operates the rules according to the requirement
     * @param num -the number we want to parse when converted to double
     * @param token - the word we are check, when it is a string type
     * @param suffix - the number type - thousand, million, billion, trillion
     * @param div - the distribution value according to the dictionary
     * @return StringBuilder - Value after parse
     */
    private StringBuilder FinallyParse(Double num, String token, char suffix, Double div) {
        StringBuilder result = new StringBuilder();
        String divResult = String.valueOf(num/div).replace(".0", "");
        result.append(divResult);
        if (token.contains("/")){
            result.append(" ").append(token);
        }
        result.append(suffix);
        return result;
    }

    /**
     * Checks if the string is a number
     * @param s - String to check whether it is a number
     * @return - Boolean If the word is a number, return true if so
     */
//    public Boolean isNumeric (String s){
//        Boolean res;
//        res = (s != null && s.matches("[-+]?\\d*\\.?\\d+"));
//        return res;
//    }

    /**
     * a dictionary that links the name of the digit with its numerical value
     */
    private void InitiateNumber() {
        NumberByNumber.put("Thousand", 1000.0);
        NumberByNumber.put("Million", 1000000.0);
        NumberByNumber.put("Billion", 1000000000.0);
        NumberByNumber.put("Trillion", 1000000000000.0);
        NumberByNumber.put("thousand", 1000.0);
        NumberByNumber.put("million", 1000000.0);
        NumberByNumber.put("billion", 1000000000.0);
        NumberByNumber.put("trillion", 1000000000000.0);
    }

}
