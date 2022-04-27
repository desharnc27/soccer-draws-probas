/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author desharnc27
 */
public class InitParseException extends Exception {

    public InitParseException(String message) {
        super(message);
    }

    public static InitParseException make(String filename, String badLine, String explanations) {
        String[] messageParts = new String[]{
            "An error occurered while parsing a file.",
            "File: " + filename,
            "Faulty line: " + badLine,
            "Explanations: " + explanations
        };
        return new InitParseException(String.join("\n", messageParts));
    }

    public static InitParseException make(String filename, String badLine) {
        return make(filename, badLine, "unavailable");
    }

    public static InitParseException makeVague(String filename, String badVal, String explanations) {
        return make(filename, badVal + " ...", explanations);
    }

    public static InitParseException makeMiss(String filename, String badLine, String value) {
        return make(filename, badLine, "\"" + value + "\" should have been defined before that line.");
    }

    /*public static InitParseException makeVagueMiss(String filename, String badVal, String value){
        return make(filename, badVal+ " ...", "\"" + value + "\" should have been defined before that line.");
    }*/
    public static InitParseException makeDataMismatch(String mismatchParameter) {
        String message = "the parameter file does not match the data file associated with it for at least one major parameter.";
        message += "\n" + "Inconsistent Parameter: " + mismatchParameter;
        return new InitParseException(message);
    }
    /*public static Exception makeNoImpl(String filename, String string, String tempMessage) {
        return 
    }*/
}
