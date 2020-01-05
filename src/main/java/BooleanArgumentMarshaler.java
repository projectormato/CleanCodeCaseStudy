import java.util.Iterator;

class BooleanArgumentMarshaler implements ArgumentMarshaler {
    private boolean booleanValue = false;

    public static boolean getValue(ArgumentMarshaler am) {
        if (am instanceof BooleanArgumentMarshaler) return ((BooleanArgumentMarshaler) am).booleanValue;
        else return false;
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
