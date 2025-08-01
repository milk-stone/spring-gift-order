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
import gift.global.exception.OptionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KakaoApiService kakaoApiService;
    private final OptionRepository optionRepository;

    public OrderService(OrderRepository orderRepository, KakaoApiService kakaoApiService, OptionRepository optionRepository) {
        this.orderRepository = orderRepository;
        this.kakaoApiService = kakaoApiService;
        this.optionRepository = optionRepository;
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

        return OrderResponse.from(order);
    }
}
