package cz.startnet.utils.pgdiff.parsers;

import java.util.Arrays;
import java.util.Locale;

/**
 * Class for parsing strings.
 * 
 * @author fordfrog
 */
public final class Parser {

    /**
     * String to be parsed.
     */
    private final String string;
    /**
     * Current position.
     */
    private int position;

    /**
     * Creates new instance of Parser.
     *
     * @param string {@link #string}
     */
    public Parser(final String string) {
        this.string = string;
        skipWhitespace();
    }

    /**
     * Checks whether the string contains given word on current position. If not
     * then throws an exception.
     * 
     * @param words list of words to check
     */
    public void expect(final String... words) {
        for (final String word : words) {
            expect(word, false);
        }
    }

    /**
     * Checks whether the string contains given word on current position. If not
     * and expectation is optional then position is not changed and method
     * returns true. If expectation is not optional, exception with error
     * description is thrown. If word is found, position is moved at first
     * non-whitespace character following the word.
     *
     * @param word word to expect
     * @param optional true if word is optional, otherwise false
     *
     * @return true if word was found, otherwise false
     */
    public boolean expect(final String word, final boolean optional) {
        final int wordEnd = position + word.length();

        if (wordEnd <= string.length()
                && string.substring(position, wordEnd).equalsIgnoreCase(word)
                && (wordEnd == string.length()
                || !Character.isLetter(string.charAt(wordEnd))
                || "(".equals(word) || ",".equals(","))) {
            position = wordEnd;
            skipWhitespace();

            return true;
        }

        if (optional) {
            return false;
        }

        throw new ParserException("Cannot parse string: " + string
                + "\nExpected " + word + " at position " + (position + 1)
                + " '" + string.substring(position, position + 20) + "'");
    }

    /**
     * Checks whether string contains at current position sequence of the words.
     *
     * @param words array of words
     *
     * @return true if whole sequence was found, otherwise false
     */
    public boolean expectOptional(final String... words) {
        final boolean found = expect(words[0], true);

        if (!found) {
            return false;
        }

        for (int i = 1; i < words.length; i++) {
            skipWhitespace();
            expect(words[i]);
        }

        return true;
    }

    /**
     * Moves position in the string to next non-whitespace string.
     */
    public void skipWhitespace() {
        for (; position < string.length(); position++) {
            if (!Character.isWhitespace(string.charAt(position))) {
                break;
            }
        }
    }

    /**
     * Parses identifier from current position. If identifier is quoted, it is
     * returned as it is. If the identifier is not quoted, it is converted to
     * lowercase. If identifier does not start with letter then exception is
     * thrown. Position is placed at next first non-whitespace character.
     * 
     * @return parsed identifier
     */
    public String parseIdentifier() {
        final boolean quoted = string.charAt(position) == '"';

        if (quoted) {
            final int endPos = string.indexOf('"', position + 1);
            final String result = string.substring(position + 1, endPos);
            position = endPos + 1;
            skipWhitespace();

            return result;
        } else {
            int endPos = position;

            if (!Character.isLetter(string.charAt(endPos))) {
                throw new ParserException("Cannot parse string: " + string
                        + "\nIdentifier must begin with letter at position "
                        + (position + 1) + " '"
                        + string.substring(position, position + 20) + "'");
            }

            for (endPos++; endPos < string.length(); endPos++) {
                final char chr = string.charAt(endPos);

                if (Character.isWhitespace(chr) || chr == ',' || chr == ')'
                        || chr == '(' || chr == ';') {
                    break;
                }
            }

            final String result =
                    string.substring(position, endPos).toLowerCase(
                    Locale.ENGLISH);

            position = endPos;
            skipWhitespace();

            return result;
        }
    }

    /**
     * Returns rest of the string. If the string ends with ';' then it is
     * removed from the string before returned. If there is nothing more in the
     * string, null is returned.
     *
     * @return rest of the string, without trailing ';' if present, or null if
     * there is nothing more in the string
     */
    public String getRest() {
        final String result;

        if (string.charAt(string.length() - 1) == ';') {
            if (position == string.length() - 1) {
                return null;
            } else {
                result = string.substring(position, string.length() - 1);
            }
        } else {
            result = string.substring(position);
        }

        position = string.length();

        return result;
    }

    /**
     * Parses integer from the string. If next word is not integer then
     * exception is thrown.
     *
     * @return parsed integer value
     */
    public int parseInteger() {
        int endPos = position;

        for (; endPos < string.length(); endPos++) {
            if (!Character.isLetterOrDigit(string.charAt(endPos))) {
                break;
            }
        }

        try {
            final int result =
                    Integer.parseInt(string.substring(position, endPos));

            position = endPos;
            skipWhitespace();

            return result;
        } catch (final NumberFormatException ex) {
            throw new ParserException("Cannot parse string: " + string
                    + "\nExpected integer at position: " + (position + 1)
                    + " '" + string.substring(position, position + 20) + "'");
        }
    }

    /**
     * Returns expression that is ended either with ',', ')' or with end of the
     * string. If expression is empty then exception is thrown.
     *
     * @return expression string
     */
    public String getExpression() {
        final int endPos = getExpressionEnd();

        if (position == endPos) {
            throw new ParserException("Cannot parse string: " + string
                    + "\nExpected expression at position " + (position + 1)
                    + " '" + string.substring(position, position + 20) + "'");
        }

        final String result = string.substring(position, endPos).trim();

        position = endPos;

        return result;
    }

    /**
     * Returns position of last character of single command within
     * larger command (like CREATE TABLE). Last character is either ',' or
     * ')'. If no such character is found and method reaches the end of the
     * command then position after the last character in the command is
     * returned.
     *
     * @param command command
     * @param start start position
     *
     * @return end position of the command
     */
    private int getExpressionEnd() {
        int bracesCount = 0;
        boolean singleQuoteOn = false;
        int charPos = position;

        for (; charPos < string.length(); charPos++) {
            final char chr = string.charAt(charPos);

            if (chr == '(') {
                bracesCount++;
            } else if (chr == ')') {
                if (bracesCount == 0) {
                    break;
                } else {
                    bracesCount--;
                }
            } else if (chr == '\'') {
                singleQuoteOn = !singleQuoteOn;
            } else if ((chr == ',') && !singleQuoteOn && (bracesCount == 0)) {
                break;
            } else if (chr == ';' && bracesCount == 0 && !singleQuoteOn) {
                break;
            }
        }

        return charPos;
    }

    /**
     * Returns current position in the string.
     *
     * @return current position in the string
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns parsed string.
     *
     * @return parsed string
     */
    public String getString() {
        return string;
    }

    /**
     * Throws exception about unsupported command.
     */
    public void throwUnsupportedCommand() {
        throw new ParserException("Cannot parse string: " + string
                + "\nUnsupported command at position " + (position + 1)
                + " '" + string.substring(position, position + 20) + "'");
    }

    /**
     * Checks whether one of the words is present at current position. If the
     * word is present then the word is returned and position is updated.
     *
     * @param words words to check
     *
     * @return found word or null if non of the words has been found
     *
     * @see #expectOptional(java.lang.String[])
     */
    public String expectOptionalOneOf(final String... words) {
        for (final String word : words) {
            if (expectOptional(word)) {
                return word;
            }
        }

        return null;
    }

    /**
     * Searches string forward from position to find one of following. If none
     * of words is found, exception is thrown, unless optional is true. Position
     * is not updated.
     * 
     * @param optional true if the strings are optional, otherwise false
     * @param words list of words
     *
     * @return position of the closest occurance of any of the words, or end of
     * expression if optional is true
     */
    public int findOneOfInExpression(final boolean optional,
            final String... words) {
        final int endOfExpression = getExpressionEnd();
        int minWordPos = -1;

        for (final String word : words) {
            final int pos = string.indexOf(word, position);

            if (pos != -1 && pos < endOfExpression
                    && (minWordPos == -1 || pos < minWordPos)) {
                minWordPos = pos;
            }
        }

        if (minWordPos != -1) {
            return minWordPos;
        } else if (optional) {
            return endOfExpression;
        }

        throw new RuntimeException("Cannot parse string: " + string
                + "\nCannot find one of " + Arrays.toString(words)
                + " from position " + position);
    }

    /**
     * Parses data type backward from given end position excluded. If parsing of
     * data type fails, exception is thrown. Position is not updated.
     *
     * @param endPosition end position from which data type should be parsed
     *
     * @return start position of the data type
     */
    public int parseDataTypeBackward(final int endPosition) {
        int curPos = endPosition;

        // get rid of whitespace
        while (Character.isWhitespace(string.charAt(curPos - 1))) {
            curPos--;
        }

        // if ')' is present we are handling something like 'varchar(num)'
        if (string.charAt(curPos - 1) == ')') {
            curPos--;

            while (string.charAt(curPos - 1) != '(') {
                curPos--;
            }

            curPos--;

            // get rid of whitespace
            while (Character.isWhitespace(string.charAt(curPos - 1))) {
                curPos--;
            }
        }

        final int wordEndPos = curPos - 1;
        while (!Character.isWhitespace(string.charAt(curPos - 1))
                && string.charAt(curPos - 1) != ','
                && string.charAt(curPos - 1) != '(') {
            curPos--;
        }

        // now we must handle multiword data types
        final String word = string.substring(curPos, wordEndPos + 1);

        if (word.equalsIgnoreCase("varying")
                || word.equalsIgnoreCase("precision")) {
            // get rid of whitespace
            while (Character.isWhitespace(string.charAt(curPos - 1))) {
                curPos--;
            }

            // parse to previous word beginning
            while (!Character.isWhitespace(string.charAt(curPos - 1))
                    && string.charAt(curPos - 1) != ','
                    && string.charAt(curPos - 1) != '(') {
                curPos--;
            }
        }

        if (curPos < position) {
            throw new RuntimeException("Cannot parse string: " + string
                    + "\nCannot parse data type between positions "
                    + position + " and " + endPosition);
        }

        return curPos;
    }

    /**
     * Returns substring from the string.
     *
     * @param startPos start position
     * @param endPos end position exclusive
     *
     * @return substring
     */
    public String getSubString(final int startPos, final int endPos) {
        return string.substring(startPos, endPos);
    }

    /**
     * Changes current position in the string.
     *
     * @param position new position
     */
    public void setPosition(final int position) {
        this.position = position;
    }
}
