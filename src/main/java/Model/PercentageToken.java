package Model;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

/**
 * This department is responsible for Percent numbers type words
 * if the word does not belong to the department, return Null value, Otherwise we will parse according to laws
 */
public class PercentageToken implements IToken {
    /**
     * A function that parse tokens that are a percentage.
     * if the token does not contain $ or percent, is out of the class
     * @param sentence - A sentence of 8 words to parse.
     * @return - An object that returns values ​​for the word we have parse.
     */
    public ParsedResult TryParse(List<String> sentence) {
        int size = sentence.size();
        String first = sentence.get(0);
        String second = size > 1 ? sentence.get(1) : "";
        StringBuilder result = new StringBuilder();
        Integer index = 1;
        // it's not percent
        if (!first.endsWith("%") && (!second.equals("percent") || !second.equals("percentage"))) {
            return null;
        }
        // last char is percent -'%' and the first token is number
        else if (first.endsWith("%")) {
            first = first.substring(0, first.length() - 1);
            if (StringUtils.isNumeric(first)) {
                index = 1;
            }
            else
                return null;
        }

        // the second token is 'percent' or 'percentage' anf the first token is number
        else if (StringUtils.isNumeric(first) && (second.equals("percent") || second.equals("percentage"))) {
            index = 2;
        }
        return new ParsedResult(true, result.append(first).append('%'), index);
    }

}