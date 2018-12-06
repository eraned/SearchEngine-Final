package Model;

import java.util.List;

/**
 * This department is responsible for relevance type words.
 *  * if the word does not belong to the department, return Null value, Otherwise we will parse according to laws
 */
public class RelevanceToken implements IToken {
    /**
     * @param sentence - list to parse with 8 words or less
     * @return -ParsedResult result after parse
     */
    public ParsedResult TryParse(List<String> sentence) {
//        long startTime = System.nanoTime();
        String first = sentence.get(0);
        int size = first.length();
        Integer index = 1;
        if (size < 3) {
            return null;
        }
        if (first.substring(first.length() - 2).equals("'s")) {
            StringBuilder result = new StringBuilder();
            if(Character.isUpperCase(first.charAt(0))){
                first = first.toUpperCase();
            }
            first = first.substring(0, first.length() - 2);
            return new ParsedResult(true, result.append(first), index);
        }
        return null;
    }
}
