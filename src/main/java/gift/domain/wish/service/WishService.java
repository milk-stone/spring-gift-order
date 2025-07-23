package gift.domain.wish.service;

import gift.domain.member.Member;
import gift.domain.product.Product;
import gift.domain.product.repository.ProductRepository;
import gift.domain.wish.Wish;
import gift.domain.wish.dto.WishRequest;
import gift.domain.wish.dto.WishResponse;
import gift.domain.wish.dto.WishUpdateRequest;
import gift.domain.wish.repository.WishRepository;
import gift.global.dto.CustomPageResponse;
import gift.global.exception.BadRequestException;
import gift.global.exception.ProductNotFoundException;
import gift.global.exception.WishNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public void createWish(WishRequest wishRequest, Member member) {
        if (member.hasProductInWishList(wishRequest.productId())) {
            throw new IllegalArgumentException("이미 위시리스트에 추가된 상품입니다.");
        }
        Product product = productRepository.findById(wishRequest.productId()).orElseThrow(() -> new ProductNotFoundException("WishService : createWish() failed", wishRequest.productId()));
        Wish wish = new Wish(member, product, wishRequest.quantity());
        wishRepository.save(wish);
        member.getWishList().add(wish);
        product.getWishList().add(wish);
    }

    public void updateWish(Long id, WishUpdateRequest req, Member member) {
        Wish wish = wishRepository.findById(id).orElseThrow(() -> new WishNotFoundException("WishService : updateWish() failed", id));
        if (!wish.getMember().equals(member)) {
            throw new BadRequestException("WishService : updateWish() failed - Wrong member");
        }
        wish.updateQuantity(req.quantity());
        wishRepository.save(wish);
    }

    public void deleteWish(Long id, Member member) {
        Wish wish = wishRepository.findById(id).orElseThrow(() -> new WishNotFoundException("WishService : deleteWish() failed", id));
        if (!wish.getMember().equals(member)) {
            throw new BadRequestException("WishService : deleteWish() failed - Wrong member");
        }
        wishRepository.delete(wish);
    }

    public CustomPageResponse<WishResponse> getWishes(Member member, Pageable pageable) {
        return CustomPageResponse.from(
                wishRepository.findAllByMemberId(member.getId(), pageable)
                        .map(WishResponse::from)
        );
    }
}
