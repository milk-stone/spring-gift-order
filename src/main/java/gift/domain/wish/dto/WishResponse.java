package gift.domain.wish.dto;

import gift.domain.wish.Wish;

public record WishResponse(Long id, Long productId, int quantity) {
    public static WishResponse from(Wish wish) {
        return new WishResponse(wish.getId(), wish.getProduct().getId(), wish.getQuantity());
    }
}
