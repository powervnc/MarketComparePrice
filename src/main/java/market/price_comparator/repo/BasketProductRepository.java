package market.price_comparator.repo;

import market.price_comparator.model.BasketProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketProductRepository extends JpaRepository<BasketProduct, String> {
    @Query("SELECT bp.productId FROM BasketProduct bp WHERE bp.basketId = :basketId")
    List<String> findProductIdsByBasketId(@Param("basketId") String basketId);
}
