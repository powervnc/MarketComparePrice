package market.price_comparator.repo;

import market.price_comparator.model.UserTargetPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTargetPriceRepository extends JpaRepository<UserTargetPrice, String> {
    Optional<UserTargetPrice> findByUserIdAndProductId(String userId, String productId);
    List<UserTargetPrice> findByUserId(String userId);

}
