package gift.global.exception;

public class NotAdminException extends RuntimeException {
    public NotAdminException(String Message) {
        super(Message);
    }
}
