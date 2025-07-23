package gift.domain.product.dto;

import gift.global.dto.CustomPageResponse;

public record ProductPageResponse(CustomPageResponse<ProductResponse> productResponseList) {
}
