import java.util.Iterator;
import java.util.NoSuchElementException;

class IntegerArgumentMarshaler implements ArgumentMarshaler {
    private int intValue;

    public static int getValue(ArgumentMarshaler am) {
        if (am instanceof IntegerArgumentMarshaler) return ((IntegerArgumentMarshaler) am).intValue;
        else return 0;
    }

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        String parameter = null;
        try {
            parameter = currentArgument.next();
            this.intValue = Integer.parseInt(parameter);
        } catch (NoSuchElementException e) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_INTEGER);
        } catch (NumberFormatException e) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER, parameter);
        }
    }

}
