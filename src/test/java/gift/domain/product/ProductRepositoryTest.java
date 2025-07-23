package gift.domain.product;

import gift.domain.product.repository.ProductRepository;
import gift.global.dto.CustomPageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장하고 ID로 조회하면, 저장된 상품이 반환되어야 한다.")
    void saveAndFindById() {
        // given
        Product newProduct = new Product("테스트 키보드", 50000L, "keyboard.jpg");

        // when
        Product savedProduct = productRepository.save(newProduct);
        Optional<Product> foundProductOptional = productRepository.findById(savedProduct.getId());

        // then
        assertThat(foundProductOptional).isPresent();
        Product foundProduct = foundProductOptional.get();
        assertThat(foundProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(foundProduct.getName()).isEqualTo("테스트 키보드");
    }

    @Test
    @DisplayName("모든 상품을 조회하면, 저장된 모든 상품 리스트가 반환되어야 한다. 모든 상품을 반환할 때 페이지네이션이 적용 되었는지도 확인한다.")
    void findAllProducts() {
        // given
        Product product1 = new Product("상품1", 1000L, "1.jpg");
        Product product2 = new Product("상품2", 2000L, "2.jpg");
        productRepository.saveAll(List.of(product1, product2));

        CustomPageRequest customPageRequest = new CustomPageRequest(0, 5, null);
        Pageable pageable = customPageRequest.toPageable();

        // when
        Page<Product> products = productRepository.findAll(pageable);
        List<Product> productList = products.getContent();

        // then
        assertThat(productList).hasSize(2); // 리스트의 크기가 2인지 확인
        assertThat(productList).extracting(Product::getName) // 이름만 추출하여
                .containsExactlyInAnyOrder("상품1", "상품2"); // 순서에 상관없이 포함하는지 확인
    }

    @Test
    @DisplayName("상품 정보를 수정하면, 변경된 내용이 DB에 반영되어야 한다.")
    void updateProduct() {
        // given
        Product originalProduct = productRepository.save(new Product("원본 이름", 100L, "original.jpg"));
        Long productId = originalProduct.getId();

        // when
        Product foundProduct = productRepository.findById(productId).get();
        foundProduct.update("수정된 이름", 200L, "updated.jpg");
        productRepository.save(foundProduct); // 변경된 내용을 저장

        // then
        Product updatedProduct = productRepository.findById(productId).get();
        assertThat(updatedProduct.getName()).isEqualTo("수정된 이름");
        assertThat(updatedProduct.getPrice()).isEqualTo(200L);
    }

    @Test
    @DisplayName("상품을 삭제하면, 더 이상 해당 상품이 조회되지 않아야 한다.")
    void deleteProduct() {
        // given
        Product product = productRepository.save(new Product("삭제될 상품", 999L, "delete.jpg"));
        Long productId = product.getId();

        // when
        productRepository.deleteById(productId);

        // then
        Optional<Product> foundProductOptional = productRepository.findById(productId);
        assertThat(foundProductOptional).isEmpty();
    }
}
