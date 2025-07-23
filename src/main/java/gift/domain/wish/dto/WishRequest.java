package gift.domain.wish.dto;

import jakarta.validation.constraints.Min;

public record WishRequest(Long productId, @Min(value = 1, message = "1 이상의 숫자만 허용됩니다.") int quantity) {
}
