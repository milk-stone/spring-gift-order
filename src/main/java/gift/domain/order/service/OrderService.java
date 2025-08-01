package gift.domain.order.service;

import gift.domain.auth.dto.KakaoLink;
import gift.domain.auth.dto.KakaoTalkMessage;
import gift.domain.auth.service.KakaoApiService;
import gift.domain.member.Member;
import gift.domain.order.Order;
import gift.domain.order.dto.OrderRequest;
import gift.domain.order.dto.OrderResponse;
import gift.domain.order.repository.OrderRepository;
import gift.domain.product.Option;
import gift.domain.product.repository.OptionRepository;
import gift.domain.wish.repository.WishRepository;
import gift.global.exception.GlobalExceptionHandler;
import gift.global.exception.OptionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final OrderRepository orderRepository;
    private final KakaoApiService kakaoApiService;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;

    public OrderService(OrderRepository orderRepository, KakaoApiService kakaoApiService, OptionRepository optionRepository, WishRepository wishRepository) {
        this.orderRepository = orderRepository;
        this.kakaoApiService = kakaoApiService;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Member member) {
        Option option = optionRepository.findById(orderRequest.optionId()).orElseThrow(() -> new OptionNotFoundException("해당 옵션은 존재하지 않습니다."));
        option.subtractQuantity(orderRequest.quantity());

        Order order = new Order(option, orderRequest.quantity(), orderRequest.message());
        orderRepository.save(order);

        String orderUrl = "http://localhost:8080/api/orders/" + order.getId();

        KakaoLink link = new KakaoLink(orderUrl, orderUrl);

        KakaoTalkMessage message = new KakaoTalkMessage(
                "text",
                "주문이 성공적으로 완료되었습니다.\n주문번호: " + order.getId(),
                link,
                "주문 상세보기"
        );

        kakaoApiService.postSelfKakaoTalk(member.getKakaoAccessToken(), message);

        log.info("삭제될 위시리스트 항목: {}", member.getWishList());
        member.getWishList().removeIf(wish -> wish.getProduct().getId().equals(option.getProduct().getId()));
        log.info("삭제 완료 후 위시리스트: {}", member.getWishList());
        return OrderResponse.from(order);
    }
}
