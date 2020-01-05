import junit.framework.TestCase;
import org.junit.Assert;

public class ArgsTest extends TestCase {

    public void testTrueIsTrue() {
        Assert.assertTrue(true);
    }

    public void testNoArgs() throws Exception {
        Args arg = new Args("", new String[0]);
        assertEquals(0, arg.cardinarity());
    }

    public void testNoSchemaArgs() throws Exception {
        try {
            new Args("", new String[]{"-x"});
            fail("Args constructor should have thrown exception.");
        } catch (ArgsException e) {
            assertEquals(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, e.getErrorCode());
            assertEquals('x', e.getErrorArgumentId());
        }
    }

    public void testNoSchemaMultiArgs() throws Exception {
        try {
            new Args("", new String[]{"-x", "-y"});
            fail("Args constructor should have thrown exception.");
        } catch (ArgsException e) {
            assertEquals(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, e.getErrorCode());
            assertEquals('x', e.getErrorArgumentId());
        }
    }

    public void testNonLetterSchema() throws Exception {
        try {
            new Args("*", new String[]{});
            fail("Args constructor should have thrown exception.");
        } catch (ArgsException e) {
            assertEquals(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME, e.getErrorCode());
            assertEquals('*', e.getErrorArgumentId());
        }
    }

    public void testInvalidArgumentFormat() throws Exception {
        try {
            new Args("f~", new String[]{});
            fail("Args constructor should have thrown exception.");
        } catch (ArgsException e) {
            assertEquals(ArgsException.ErrorCode.INVALID_FORMAT, e.getErrorCode());
            assertEquals('f', e.getErrorArgumentId());
        }
    }

    public void testOneArgs() throws Exception {
        Args args = new Args("x", new String[]{"-x"});
        assertEquals(1, args.cardinarity());
        assertTrue(args.getBoolean('x'));
    }

    public void testSimpleStringParent() throws Exception {
        Args args = new Args("x*", new String[]{"-x", "param"});
        assertEquals(1, args.cardinarity());
        assertTrue(args.has('x'));
        assertEquals("param", args.getString('x'));
    }

    public void testMissingStringArgument() throws Exception {
        try {
            new Args("x*", new String[]{"-x"});
            fail("Args constructor should have thrown exception.");
        } catch (ArgsException e) {
            assertEquals(ArgsException.ErrorCode.MISSING_STRING, e.getErrorCode());
            // 通るようにするべき...？
//            assertEquals('x', e.getErrorArgumentId());
        }
    }

    public void testSpaceInFormat() throws Exception {
        Args args = new Args("x, y", new String[]{"-xy"});
        assertEquals(2, args.cardinarity());
        assertTrue(args.has('x'));
        assertTrue(args.has('y'));
    }

    public void testSimpleIntParent() throws Exception {
        Args args = new Args("x# ", new String[]{"-x", "42"});
        assertEquals(1, args.cardinarity());
        assertTrue(args.has('x'));
        assertEquals(42, args.getInt('x'));
    }

    // 通るように実装していく
    public void testSimpleDoubleParent() throws Exception {
//        Args args = new Args("x# # ", new String[]{"-x", "42.3"});
//        assertEquals(1, args.cardinarity());
//        assertTrue(args.has('x'));
//        assertEquals(42.3, args.getDouble('x'));
    }
}
