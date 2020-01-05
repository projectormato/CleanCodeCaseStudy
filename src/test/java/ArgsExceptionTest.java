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

    public void testInvalidString() throws Exception {
        ArgsException e = new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER, 'x', "Forty two");
        assertEquals("引数 -x には整数が必要ですが、次の値が指定されました。 'Forty two' 。", e.getErrorMessage());
    }

}
