package gift.domain.order.dto;

import gift.domain.order.Order;

import java.time.LocalDateTime;

public record OrderResponse(
        Long orderId,
        Long optionId,
        int quantity,
        LocalDateTime orderDateTime,
        String message
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getOption().getId(), order.getQuantity(), order.getOrderDateTime(), order.getMessage());
    }
}
