import java.util.*;

public class Args {
    private Set<Character> argsFound = new HashSet<>();
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<>();
    private Iterator<String> crrentArgument;
    private List<String> argsList;

    public Args(String schema, String[] args) throws ArgsException {
        this.argsList = Arrays.asList(args);
        parseSchema(schema);
        parseArguments();
    }

    private void parseArguments() throws ArgsException {
        for (this.crrentArgument = argsList.iterator(); crrentArgument.hasNext(); ) {
            parseArgument(crrentArgument.next());
        }
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-")) parseElements(arg);
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar)) this.argsFound.add(argChar);
        else throw new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, argChar, null);
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaler am = this.marshalers.get(argChar);
        if (am == null) return false;
        try {
            am.set(this.crrentArgument);
            return true;
        } catch (ArgsException e) {
            throw new ArgsException(e.getErrorCode(), argChar, e.getErrorParameter());
        }
    }

    private void parseSchema(String schema) throws ArgsException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) parseSchemaElement(element.trim());
        }
    }

    private void parseSchemaElement(String element) throws ArgsException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (elementTail.length() == 0) this.marshalers.put(elementId, new BooleanArgumentMarshaler());
        else if (elementTail.equals("*")) this.marshalers.put(elementId, new StringArgumentMarshaler());
        else if (elementTail.equals("#")) this.marshalers.put(elementId, new IntegerArgumentMarshaler());
        else throw new ArgsException(ArgsException.ErrorCode.INVALID_FORMAT, elementId, null);
    }

    private void validateSchemaElementId(char elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) throw new ArgsException(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME, elementId, null);
    }

    public int cardinarity() {
        return this.argsFound.size();
    }

    public boolean getBoolean(char arg) {
        return BooleanArgumentMarshaler.getValue(marshalers.get(arg));
    }

    public String getString(char arg) {
        return StringArgumentMarshaler.getValue(this.marshalers.get(arg));
    }

    public int getInt(char arg) {
        return IntegerArgumentMarshaler.getValue(this.marshalers.get(arg));
    }

    public boolean has(char arg) {
        return this.argsFound.contains(arg);
    }

}
