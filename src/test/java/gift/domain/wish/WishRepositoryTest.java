package gift.domain.wish;

import gift.domain.member.Member;
import gift.domain.member.repository.MemberRepository;
import gift.domain.product.Product;
import gift.domain.product.repository.ProductRepository;
import gift.domain.wish.repository.WishRepository;
import gift.global.dto.CustomPageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishRepositoryTest {
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;

    private Member testMember;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(new Member("test@example.com", "password", "tester"));
        testProduct1 = productRepository.save(new Product("상품1", 1000L, "image1.jpg"));
        testProduct2 = productRepository.save(new Product("상품2", 2000L, "image2.jpg"));
    }

    @Test
    @DisplayName("위시리스트 항목을 저장하고 ID로 조회하면, 저장된 항목이 반환되어야 한다.")
    void saveAndFindById() {
        // given
        Wish newWish = new Wish(testMember, testProduct1, 1);

        // when
        Wish savedWish = wishRepository.save(newWish);
        Optional<Wish> foundWishOptional = wishRepository.findById(savedWish.getId());

        // then
        assertThat(foundWishOptional).isPresent();
        Wish foundWish = foundWishOptional.get();
        assertThat(foundWish.getId()).isNotNull();
        assertThat(foundWish.getMember().getId()).isEqualTo(testMember.getId());
        assertThat(foundWish.getProduct().getId()).isEqualTo(testProduct1.getId());
        assertThat(foundWish.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 회원의 모든 위시리스트 항목을 조회하면, 해당 회원의 항목만 반환되어야 한다. 위시리스트를 반환할 때 페이지네이션이 적용 되었는지도 확인한다.")
    void findAllByMember() {
        // given
        Member otherMember = memberRepository.save(new Member("other@test.com", "password", "other"));

        wishRepository.save(new Wish(testMember, testProduct1, 1));
        wishRepository.save(new Wish(testMember, testProduct2, 2));
        wishRepository.save(new Wish(otherMember, testProduct1, 3));

        CustomPageRequest pageRequest = new CustomPageRequest(0, 5, "quantity,desc");
        Pageable pageable = pageRequest.toPageable();

        // when
        Page<Wish> testMemberWishes = wishRepository.findAllByMemberId(testMember.getId(), pageable);
        Page<Wish> otherMemberWishes = wishRepository.findAllByMemberId(otherMember.getId(), pageable);

        // then
        assertThat(testMemberWishes).hasSize(2);
        assertThat(testMemberWishes).extracting(wish -> wish.getProduct().getName())
                .containsExactlyInAnyOrder("상품1", "상품2");

        assertThat(otherMemberWishes).hasSize(1);
        assertThat(otherMemberWishes.get().toList().getFirst().getProduct().getName()).isEqualTo("상품1");
    }

    @Test
    @DisplayName("위시리스트 항목을 수정하면, 변경된 내용이 DB에 반영되어야 한다.")
    void updateWish() {
        // given
        Wish originalWish = wishRepository.save(new Wish(testMember, testProduct1, 5));
        Long wishId = originalWish.getId();

        // when
        Wish foundWish = wishRepository.findById(wishId).get();
        foundWish.updateQuantity(10);
        wishRepository.save(foundWish);

        // then
        Wish updatedWish = wishRepository.findById(wishId).get();
        assertThat(updatedWish.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("위시리스트 항목을 삭제하면, 더 이상 해당 항목이 조회되지 않아야 한다.")
    void deleteWish() {
        // given
        Wish wish = wishRepository.save(new Wish(testMember, testProduct1, 1));
        Long wishId = wish.getId();

        // when
        wishRepository.deleteById(wishId);

        // then
        Optional<Wish> foundWishOptional = wishRepository.findById(wishId);
        assertThat(foundWishOptional).isEmpty();
    }
}
