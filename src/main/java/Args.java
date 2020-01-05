import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.*;

public class Args {
    private Boolean valid = true;
    private String schema;
    private Set<Character> argsFound = new HashSet<>();
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<>();
    private Iterator<String> crrentArgument;
    private char errorArgumentId = '\0';
    private ErrorCode errorCode = ErrorCode.OK;
    private String errorParameter = "TILT";
    private Set<Character> unexpectedArguments = new HashSet<>();
    private List<String> argsList;

    public Args(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.argsList = Arrays.asList(args);
        this.valid = parse();
    }

    private Boolean parse() throws ParseException {
        if (this.schema.length() == 0 && this.argsList.size() == 0) {
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
        for (this.crrentArgument = argsList.iterator(); crrentArgument.hasNext();) {
            String arg = crrentArgument.next();
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
        ArgumentMarshaler am = this.marshalers.get(argChar);
        if (am == null) return false;
        try {
            if (am instanceof BooleanArgumentMarshaler) {
                setBooleanArg(am, this.crrentArgument);
            } else if (am instanceof StringArgumentMarshaler) {
                setStringArg(am);
            } else if (am instanceof IntegerArgumentMarshaler) {
                setIntArg(am);
            }
        } catch (ArgsException e) {
            valid = false;
            this.errorArgumentId = argChar;
            throw e;
        }
        return true;
    }

    private void setIntArg(ArgumentMarshaler am) throws ArgsException {
        String parameter = null;
        try {
            parameter = this.crrentArgument.next();
            am.set(parameter);
        } catch (NoSuchElementException e) {
            valid = false;
            this.errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (NumberFormatException e) {
            valid = false;
            this.errorParameter = parameter;
            this.errorCode = ErrorCode.INVALID_INTEGER;
            throw new ArgsException();
        }
    }

    private void setStringArg(ArgumentMarshaler am) {
        try {
            am.set(crrentArgument.next());
        } catch (NoSuchElementException e) {
            this.valid = false;
            this.errorCode = ErrorCode.MISSING_STRING;
        }
    }

    private void setBooleanArg(ArgumentMarshaler am, Iterator<String> crrentArgument) {
        am.set("true");
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
            this.marshalers.put(elementId, new BooleanArgumentMarshaler());
        } else if (isStringSchemaElement(elementTail)) {
            this.marshalers.put(elementId, new StringArgumentMarshaler());
        } else if (isIntegerSchemaElement(elementTail)) {
            this.marshalers.put(elementId, new IntegerArgumentMarshaler());
        } else {
            throw new ParseException(
                    String.format("引数: %c の書式が不正です: %s.", elementId, elementTail), 0
            );
        }
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException(
                    "不正な文字列:" + elementId + "が、次の書式に含まれています: " + schema, 0);
        }
    }

    public int cardinarity() {
        return this.argsFound.size();
    }

    public boolean getBoolean(char arg) {
        Args.ArgumentMarshaler am = marshalers.get(arg);
        return am != null && (Boolean) am.get();
    }

    public boolean has(char arg) {
        return this.argsFound.contains(arg);
    }

    public String getString(char arg) {
        Args.ArgumentMarshaler am = this.marshalers.get(arg);
        return am == null ? "" : (String) am.get();
    }

    public int getInt(char arg) {
        Args.ArgumentMarshaler am = this.marshalers.get(arg);
        return am == null ? 0 : (int) am.get();
    }

    private class ArgsException extends Exception {
    }

    public enum ErrorCode {
        OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT;
    }

    private abstract class ArgumentMarshaler {
        public abstract void set(String s);

        public abstract void set(Iterator<String> currentArgument) throws ArgsException;

        public abstract Object get();
    }

    private class BooleanArgumentMarshaler extends ArgumentMarshaler {
        private boolean booleanValue = false;

        @Override
        public void set(String s) {
            this.booleanValue = true;
        }

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            this.booleanValue = true;
        }

        @Override
        public Object get() {
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler extends ArgumentMarshaler {
        private String stringValue;

        @Override
        public void set(String s) {
            this.stringValue = s;
        }

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {

        }

        @Override
        public Object get() {
            return this.stringValue;
        }
    }

    private class IntegerArgumentMarshaler extends ArgumentMarshaler {
        private int intValue;

        @Override
        public void set(String s) {
            this.intValue = Integer.parseInt(s);
        }

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            
        }

        @Override
        public Object get() {
            return this.intValue;
        }
    }
}
