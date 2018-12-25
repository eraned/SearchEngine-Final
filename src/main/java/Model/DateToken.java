package Model;

import java.util.HashMap;
import java.util.List;

/**
 * This department is responsible for date type words
 * if the word does not belong to the department, return Null value, Otherwise we will parse according to laws
 */
public class DateToken implements IToken {
    private HashMap<String, String> datesByNumber;

    /**
     * Constructor
     */
    public DateToken() {
        datesByNumber = new HashMap<>();
        this.InitiateDates();
    }

    /**
     * A function that parse tokens that are a date.
     * if the token does not a date and document length is less than 2 return null.
     * @param sentence -list to parse with 8 words or less
     * @return - An object that returns values ​​for the word we have parse.
     */
    public ParsedResult TryParse(List<String> sentence) {
        if (sentence.size() < 2) {
            return null;
        }
        String first = sentence.get(0);
        String second = sentence.get(1);
        StringBuilder parsedSentence = new StringBuilder();
        Boolean isMatch = false;
        // DD Month -- > MM-DD
        if (isNumeric(first) && datesByNumber.containsKey(second)) {
            int numeric = Integer.parseInt(first);
            if (numeric < 32 && numeric > 0) {
                parsedSentence.append(String.format("%s-%s", datesByNumber.get(second), numeric < 10 ? ("0" + String.valueOf(numeric)) : String.valueOf(numeric)));
                isMatch = true;
            }
        } else if (datesByNumber.containsKey(first) && isNumeric(second)) {
            int numeric = Integer.parseInt(second);
            // Month DD -- > MM-DD
            if (numeric < 32 && numeric > 0) {
                parsedSentence.append(String.format("%s-%s", datesByNumber.get(first), numeric < 10 ? ("0" + String.valueOf(numeric)) : String.valueOf(numeric)));
                isMatch = true;
            }
            //DD years -- > YYYY-MM
            else if (second.length() == 4) {
                parsedSentence.append(String.format("%s-%s", second, datesByNumber.get(first)));
                isMatch = true;
            }
        }
        return new ParsedResult(isMatch, parsedSentence, 2);
    }

    /**
     * Checks if the string is a number
     * @param str - String to check whether it is a number
     * @return - Boolean If the word is a number, return true if so
     */
    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    /**
     * a dictionary that links the name of the month with its numerical value
     */
    private void InitiateDates() {
        datesByNumber.put("JAN", "01");  datesByNumber.put("Jan", "01");  datesByNumber.put("January","01");    datesByNumber.put("JANUARY", "01");
        datesByNumber.put("FEB", "02");  datesByNumber.put("Feb", "02");  datesByNumber.put("February","02");   datesByNumber.put("FEBUARY", "02");
        datesByNumber.put("MAR", "03");  datesByNumber.put("Mar", "03");  datesByNumber.put("March","03");      datesByNumber.put("MARCH", "03");
        datesByNumber.put("APR", "04");  datesByNumber.put("Apr", "04");  datesByNumber.put("April","04");      datesByNumber.put("APRIL", "04");
        datesByNumber.put("MAY", "05");  datesByNumber.put("May", "05");
        datesByNumber.put("JUN", "06");  datesByNumber.put("Jun", "06");  datesByNumber.put("June","06");       datesByNumber.put("JUNE", "06");
        datesByNumber.put("JUL", "07");  datesByNumber.put("Jul", "07");  datesByNumber.put("July","07");       datesByNumber.put("JULY", "07");
        datesByNumber.put("AUG", "08");  datesByNumber.put("Aug", "08");  datesByNumber.put("August","08");     datesByNumber.put("AUGUST", "08");
        datesByNumber.put("SEP", "09");  datesByNumber.put("Sep", "09");  datesByNumber.put("September","09");  datesByNumber.put("SEPTEMBER", "09");
        datesByNumber.put("OCT", "10");  datesByNumber.put("Oct", "10");  datesByNumber.put("October", "10");   datesByNumber.put("OCTOBER", "10");
        datesByNumber.put("NOV", "11");  datesByNumber.put("Nov", "11");  datesByNumber.put("November", "11");  datesByNumber.put("NOVEMBER", "11");
        datesByNumber.put("DEC", "12");  datesByNumber.put("Dec", "12");  datesByNumber.put("December", "12");  datesByNumber.put("DECEMBER", "12");
    }
}
