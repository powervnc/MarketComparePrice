package market.price_comparator.repo;

import market.price_comparator.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    Store findByStoreName(String storeName);
}
