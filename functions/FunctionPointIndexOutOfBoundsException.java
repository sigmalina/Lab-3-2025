package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public FunctionPointIndexOutOfBoundsException() {
        super();
    }

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }

    public FunctionPointIndexOutOfBoundsException(int index) {
        super("Index out of range: " + index);
    }
}
