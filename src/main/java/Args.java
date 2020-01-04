import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Args {
    private Boolean valid = true;
    private String[] args;
    private String schema;
    private Set<Character> argsFound = new HashSet<>();
    private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<>();
    private Map<Character, String> stringArgs = new HashMap<>();
    private Map<Character, Integer> intArgs = new HashMap<>();
    private int crrentArgument;
    private char errorArgumentId = '\0';
    private ErrorCode errorCode = ErrorCode.OK;
    private String errorParameter = "TILT";
    private Set<Character> unexpectedArguments = new HashSet<>();

    public Args(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.args = args;
        this.valid = parse();
    }

    private Boolean parse() throws ParseException {
        if (this.schema.length() == 0 && this.args.length == 0) {
            return true;
        }
        parseSchema();
        try {
            parseArguments();
        } catch (ArgsException ignored) {
        }
        return valid;
    }

    private boolean parseArguments() throws ArgsException {
        for (this.crrentArgument = 0; this.crrentArgument < args.length; crrentArgument ++) {
            String arg = args[crrentArgument];
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-")) {
            parseElements(arg);
        }
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar)) {
            this.argsFound.add(argChar);
        } else {
            this.unexpectedArguments.add(argChar);
            this.errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
            valid = false;
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        if (isBooleanArg(argChar)) {
            setBooleanArg(argChar, true);
        } else if (isStringArg(argChar)) {
            setStringArg(argChar);
        } else if (isIntArg(argChar)) {
            setIntArg(argChar);
        } else {
            return false;
        }
        return true;
    }

    private void setIntArg(char argChar) throws ArgsException {
        this.crrentArgument++;
        String parameter = null;
        try {
            parameter = this.args[this.crrentArgument];
            intArgs.put(argChar, Integer.valueOf(parameter));
        } catch (ArrayIndexOutOfBoundsException e) {
            valid = false;
            this.errorArgumentId = argChar;
            this.errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (NumberFormatException e) {
            valid = false;
            this.errorArgumentId = argChar;
            this.errorParameter = parameter;
            this.errorCode = ErrorCode.INVALID_INTEGER;
            throw new ArgsException();
        }
    }

    private boolean isIntArg(char argChar) {
        return this.intArgs.containsKey(argChar);
    }

    private void setStringArg(char argChar) throws ArgsException {
        this.crrentArgument++;
        try {
            this.stringArgs.put(argChar, args[crrentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.valid = false;
            this.errorArgumentId = argChar;
            this.errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    private boolean isStringArg(char argChar) {
        return this.stringArgs.containsKey(argChar);
    }

    private void setBooleanArg(char argChar, boolean value) {
        this.booleanArgs.get(argChar).setBooleanValue(value);
    }

    private boolean isBooleanArg(char argChar) {
        return this.booleanArgs.containsKey(argChar);
    }

    private void parseSchema() throws ParseException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
    }

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail)) {
            parseBooleanSchemaElement(elementId);
        } else if (isStringSchemaElement(elementTail)) {
            parseStringSchemaElement(elementId);
        } else if (isIntegerSchemaElement(elementTail)) {
            paeseIntegerSchemaElement(elementId);
        } else {
            throw new ParseException(
                    String.format("引数: %c の書式が不正です: %s.", elementId, elementTail), 0
            );
        }
    }

    private void paeseIntegerSchemaElement(char elementId) {
        this.intArgs.put(elementId, 0);
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private void parseStringSchemaElement(char elementId) {
        this.stringArgs.put(elementId, "");
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private void parseBooleanSchemaElement(char elementId) {
        this.booleanArgs.put(elementId, new BooleanArgumentMarshaler());
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException(
                    "不正な文字列:" + elementId + "が、次の書式に含まれています: " + schema , 0);
        }
    }

    public int cardinarity() {
        return this.argsFound.size();
    }

    public boolean getBoolean(char arg) {
        Args.ArgumentMarshaler am = booleanArgs.get(arg);
        return am != null && am.getBoolean();
    }

    public boolean has(char arg) {
        return this.argsFound.contains(arg);
    }

    public String getString(char arg) {
        return blankIfNull(stringArgs.get(arg));
    }

    private String blankIfNull(String s) {
        return s == null ? "" : s;
    }

    public int getInt(char arg) {
        return zeroIfNull(this.intArgs.get(arg));
    }

    private int zeroIfNull(Integer i) {
        return i == null ? 0 : i;
    }

    private class ArgsException extends Exception {
    }

    public enum ErrorCode {
        OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT;
    }

    private class ArgumentMarshaler {
        private boolean booleanValue = false;

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public Boolean getBoolean() {
            return this.booleanValue;
        }
    }

    private class BooleanArgumentMarshaler extends ArgumentMarshaler {

    }

    private class StringArgumentMarshaler extends ArgumentMarshaler {

    }

    private class IntegerArgumentMarshaler extends ArgumentMarshaler {

    }
}
