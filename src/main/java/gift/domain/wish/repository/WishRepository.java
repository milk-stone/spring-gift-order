package gift.domain.wish.repository;

import gift.domain.wish.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findAllByMemberId(Long memberId, Pageable pageable);
}
