package gift.global.exception;

import java.util.List;

public record ErrorResponse(String message, List<CustomFieldError> errors) {
}
