package gift.global.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message, Long id) {
        super(message + " - id가 " + id + "인 Product 객체가 존재하지 않습니다.");
    }
}
