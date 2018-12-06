package Model;

/**
 * A class that represents a parse result
 */
public class ParsedResult {
    public Boolean IsMatch;
    public StringBuilder ParsedSentence;
    public int Index;

    /**
     * constructor
     * @param isMatch - A Boolean variable that represents whether a department has been found for the word
     * @param parsedSentence - Statement after parse
     * @param index - A few words were used out of the 8 words sent for testing
     */
    public ParsedResult(Boolean isMatch, StringBuilder parsedSentence, int index){
        IsMatch = isMatch;
        ParsedSentence = parsedSentence;
        Index = index;
    }
}

