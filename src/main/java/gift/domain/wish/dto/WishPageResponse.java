package gift.domain.wish.dto;

import gift.global.dto.CustomPageResponse;

public record WishPageResponse(CustomPageResponse<WishResponse> wishResponses) {
}
