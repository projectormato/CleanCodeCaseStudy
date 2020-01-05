import java.util.Iterator;
import java.util.NoSuchElementException;

class StringArgumentMarshaler implements ArgumentMarshaler {
    private String stringValue;

    public static String getValue(ArgumentMarshaler am) {
        if (am instanceof StringArgumentMarshaler) return ((StringArgumentMarshaler) am).stringValue;
        else return "";
    }

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        try {
            this.stringValue = currentArgument.next();
        } catch (NoSuchElementException e) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_STRING);
        }
    }

}
