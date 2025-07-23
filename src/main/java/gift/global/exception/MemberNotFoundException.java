package gift.global.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message, Long id) {
        super(message + " - id가 " + id + "인 Member 객체가 존재하지 않습니다.");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
