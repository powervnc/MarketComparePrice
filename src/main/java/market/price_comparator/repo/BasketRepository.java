package market.price_comparator.repo;

import market.price_comparator.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketRepository extends JpaRepository<Basket, String> {
    List<Basket> findAllByUserId(String userId);
}
