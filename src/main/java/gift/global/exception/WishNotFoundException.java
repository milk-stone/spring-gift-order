package gift.global.exception;

public class WishNotFoundException extends RuntimeException {
    public WishNotFoundException(String message, Long id) {
        super(message + " - id가 " + id + "인 Wish 객체가 존재하지 않습니다.");
    }
}
