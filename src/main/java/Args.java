import java.util.*;

public class Args {
    private Boolean valid = true;
    private String schema;
    private Set<Character> argsFound = new HashSet<>();
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<>();
    private Iterator<String> crrentArgument;
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ArgsException.ErrorCode errorCode = ArgsException.ErrorCode.OK;
    private Set<Character> unexpectedArguments = new HashSet<>();
    private List<String> argsList;

    public Args(String schema, String[] args) throws ArgsException {
        this.schema = schema;
        this.argsList = Arrays.asList(args);
        parse();
    }

    private void parse() throws ArgsException {
        parseSchema();
        parseArguments();
    }

    private void parseArguments() throws ArgsException {
        for (this.crrentArgument = argsList.iterator(); crrentArgument.hasNext(); ) {
            String arg = crrentArgument.next();
            parseArgument(arg);
        }
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
            throw new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, argChar, null);
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaler am = this.marshalers.get(argChar);
        if (am == null) return false;
        try {
            am.set(this.crrentArgument);
            return true;
        } catch (ArgsException e) {
            valid = false;
            this.errorArgumentId = argChar;
            throw e;
        }
    }

    private void parseSchema() throws ArgsException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
    }

    private void parseSchemaElement(String element) throws ArgsException {
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
            throw new ArgsException(ArgsException.ErrorCode.INVALID_FORMAT, elementId, null);
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

    private void validateSchemaElementId(char elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME,
                    elementId, null);
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
            try {
                this.stringValue = crrentArgument.next();
            } catch (NoSuchElementException e) {
                errorCode = ArgsException.ErrorCode.MISSING_STRING;
                throw new ArgsException();
            }
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
            String parameter = null;
            try {
                parameter = crrentArgument.next();
                this.intValue = Integer.parseInt(parameter);
            } catch (NoSuchElementException e) {
                errorCode = ArgsException.ErrorCode.MISSING_INTEGER;
                throw new ArgsException();
            } catch (NumberFormatException e) {
                errorParameter = parameter;
                errorCode = ArgsException.ErrorCode.INVALID_INTEGER;
                throw new ArgsException();
            }
        }

        @Override
        public Object get() {
            return this.intValue;
        }
    }
}
