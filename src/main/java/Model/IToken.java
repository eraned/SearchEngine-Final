package Model;

import java.util.List;

/**
 * An interface that each of the classes that inherit it must implement the function 'TryParse'
 */
public interface IToken {
    ParsedResult TryParse(List<String> sentence);
}
