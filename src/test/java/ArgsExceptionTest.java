import junit.framework.TestCase;
import org.junit.Assert;

public class ArgsExceptionTest extends TestCase {

    public void testUnexpected() throws Exception {
        ArgsException e = new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, 'x', null);
        assertEquals("引数 -x は想定外です。", e.getErrorMessage());
    }

    public void testMissingString() throws Exception {
        ArgsException e = new ArgsException(ArgsException.ErrorCode.MISSING_STRING, 'x', null);
        assertEquals("次の引数の文字列パラメータが見つかりません -x 。", e.getErrorMessage());
    }
}
