import java.util.*;

public class Args {
    private Set<Character> argsFound;
    private Map<Character, ArgumentMarshaler> marshalers;
    private Iterator<String> crrentArgument;

    public Args(String schema, String[] args) throws ArgsException {
        this.argsFound  = new HashSet<>();
        this.marshalers = new HashMap<>();
        parseSchema(schema);
        parseArguments(Arrays.asList(args));
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
        if (elementTail.length() == 0) marshalers.put(elementId, new BooleanArgumentMarshaler());
        else if (elementTail.equals("*")) marshalers.put(elementId, new StringArgumentMarshaler());
        else if (elementTail.equals("#")) marshalers.put(elementId, new IntegerArgumentMarshaler());
        else throw new ArgsException(ArgsException.ErrorCode.INVALID_FORMAT, elementId, null);
    }

    private void validateSchemaElementId(char elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) throw new ArgsException(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME, elementId, null);
    }

    private void parseArguments(List<String> argsList) throws ArgsException {
        for (crrentArgument = argsList.iterator(); crrentArgument.hasNext(); ) {
            String arg = crrentArgument.next();
            if (arg.startsWith("-")) parseArgumentCharacters(arg);
        }
    }

    private void parseArgumentCharacters(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseArgumentCharacter(arg.charAt(i));
        }
    }

    private void parseArgumentCharacter(char argChar) throws ArgsException {
        ArgumentMarshaler am = marshalers.get(argChar);
        if (am == null) {
            throw new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, argChar, null);
        } else{
            argsFound.add(argChar);
            try {
                am.set(crrentArgument);
            } catch (ArgsException e) {
                throw new ArgsException(e.getErrorCode(), argChar, e.getErrorParameter());
            }
        }
    }

    public int cardinarity() {
        return argsFound.size();
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean getBoolean(char arg) {
        return BooleanArgumentMarshaler.getValue(marshalers.get(arg));
    }

    public String getString(char arg) {
        return StringArgumentMarshaler.getValue(marshalers.get(arg));
    }

    public int getInt(char arg) {
        return IntegerArgumentMarshaler.getValue(marshalers.get(arg));
    }

}
