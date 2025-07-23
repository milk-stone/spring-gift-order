package gift.domain.product.repository;

import gift.domain.product.Option;
import gift.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllByProductId(Long productId);
    boolean existsByProductAndName(Product product, String name);
}
