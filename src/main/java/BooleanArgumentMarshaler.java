import java.util.Iterator;

class BooleanArgumentMarshaler implements ArgumentMarshaler {
    private boolean booleanValue = false;

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        this.booleanValue = true;
    }

    @Override
    public Object get() {
        return booleanValue;
    }
}
