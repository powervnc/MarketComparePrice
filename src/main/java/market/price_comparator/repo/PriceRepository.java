package market.price_comparator.repo;

import market.price_comparator.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, String> {
    List<Price> findByProductIdAndStoreIdOrderByPriceDate(String productId, String storeId);
    List<Price> findByProductIdAndStoreIdOrderByPriceDateDesc(String productId, String storeId);
}
