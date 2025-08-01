package gift.domain.order.dto;

public record OrderRequest(Long optionId, int quantity, String message) {
}
