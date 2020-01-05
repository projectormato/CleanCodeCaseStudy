class ArgsException extends Exception {
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;

    public ArgsException() {
    }

    public ArgsException(ErrorCode errorCode, char errorArgumentId, String errorParameter) {
        this.errorCode = errorCode;
        this.errorArgumentId = errorArgumentId;
        this.errorParameter = errorParameter;
    }

    public ArgsException(String message) {
        super(message);
    }

    public ArgsException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ArgsException(ErrorCode errorCode, String errorParameter) {
        this.errorCode = errorCode;
        this.errorParameter = errorParameter;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public char getErrorArgumentId() {
        return this.errorArgumentId;
    }

    public String  getErrorParameter() {
        return this.errorParameter;
    }

    public String getErrorMessage() {
        switch (this.errorCode) {
            case UNEXPECTED_ARGUMENT:
                return String.format("引数 -%c は想定外です。", errorArgumentId);
            case MISSING_STRING:
                return String.format("次の引数の文字列パラメータが見つかりません -%c 。", errorArgumentId);
            case INVALID_INTEGER:
                return String.format("引数 -%c には整数が必要ですが、次の値が指定されました。 '%s' 。", errorArgumentId, errorParameter);
        }
        return "";
    }

    public enum ErrorCode {
        OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT, INVALID_ARGUMENT_NAME, INVALID_FORMAT
    }
}
