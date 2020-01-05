public class Main {
    public static void main(String[] args) {
        // for example.
        // run with java main -p 40 -d Hello -l true
        try {
            Args arg = new Args("l, p# ,d*", args);
            System.out.println("真偽値の引数: " + arg.getBoolean('l'));
            System.out.println("整数の引数: " + arg.getInt('p'));
            System.out.println("文字列の引数: " + arg.getString('d'));
        } catch (ArgsException e) {
            System.out.printf("引数エラー: %s\n", e.getErrorMessage());
        }
    }
}