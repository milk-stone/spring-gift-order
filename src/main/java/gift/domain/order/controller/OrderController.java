package gift.domain.order.controller;

import gift.domain.annotation.LoginMember;
import gift.domain.member.Member;
import gift.domain.order.dto.OrderRequest;
import gift.domain.order.dto.OrderResponse;
import gift.domain.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest,
            @LoginMember Member member
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequest, member));
    }
}
