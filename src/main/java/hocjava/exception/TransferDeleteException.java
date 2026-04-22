package hocjava.exception;

public class TransferDeleteException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    public TransferDeleteException(String message) {
        super(message);
    }
}
