package gift.domain.product.dto;

import jakarta.validation.constraints.*;

public record ProductUpdateRequest(
        @NotBlank(message = "상품 이름은 비워둘 수 없습니다.")
        String name,

        @NotNull(message = "가격은 비워둘 수 없습니다.")
        @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
        @Max(value = 200_000_000, message = "가격은 2억원 이하이어야 합니다.")
        Long price,

        @NotBlank(message = "이미지 URL은 비워둘 수 없습니다.")
        String imageUrl) {
}
