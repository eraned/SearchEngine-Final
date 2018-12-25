package Model;

import java.util.List;

/**
 * This department is responsible for range token.
 *  if the word does not belong to the department, return Null value, Otherwise we will parse according to laws.
 */
public class RangeToken extends PercentageToken implements IToken {
    /**
     * A function that parse tokens that are a range.
     * @param sentence - list to parse with 8 words or less
     * @return - ParsedResult result after parse
     */
    public ParsedResult TryParse(List<String> sentence) {
        String first = sentence.get(0);
        StringBuilder result = new StringBuilder();
        Integer index = 0;
        int size = sentence.size();
        if ((!first.equals("Between") && !first.equals("between")) || (!first.equals("form") && !first.equals("Form"))) {
            return null;
        }
        // the size is more than 4
        else if (size > 3) {
            index++; //index=1
            //try parse without the first word (between /from )
            ParsedResult firstNumber = super.TryParse(sentence.subList(1, size));
            if (firstNumber.IsMatch && // We found a suitable department
                    (firstNumber.Index + 1) < size &&
                    (((first.equals("Form") || first.equals("form")) && sentence.get(firstNumber.Index + 1).equals("to")) ||
                            ((first.equals("between") || first.equals("Between")) && sentence.get(firstNumber.Index + 1).equals("and")))) {
                index += firstNumber.Index + 1;
                if ((firstNumber.Index + 2) < size) {
                    ParsedResult secondNumber = super.TryParse(sentence.subList(firstNumber.Index + 2, size));
                    if (secondNumber.IsMatch) {
                        index += secondNumber.Index;
                        result.append(String.format("%s-%s", firstNumber.ParsedSentence, secondNumber.ParsedSentence));
                    }
                }
            }
        }
        return new ParsedResult(true, result, index);
    }
}
