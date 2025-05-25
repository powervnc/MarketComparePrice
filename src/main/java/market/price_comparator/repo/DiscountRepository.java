package market.price_comparator.repo;

import market.price_comparator.model.Discount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    List<Discount> findByFromDateBetween(Date start, Date end);

    @Query("""
            SELECT d FROM Discount d
            WHERE d.storeId = :storeId
                AND d.productId = :productId
                AND :targetDate BETWEEN d.fromDate AND d.toDate
            ORDER BY d.fromDate DESC
            """)
    List<Discount> findMostRecentDiscount(
            @Param("storeId") String storeId,
            @Param("productId") String productId,
            @Param("targetDate") Date targetDate,
            Pageable pageable
    );

    @Query("""
                SELECT d FROM Discount d
                WHERE :targetDate BETWEEN d.fromDate AND d.toDate
                  AND d.percentageDiscount = (
                      SELECT MAX(d2.percentageDiscount) FROM Discount d2
                      WHERE  :targetDate BETWEEN d2.fromDate AND d2.toDate
                  )
            """)
    List<Discount> findMaxDiscountsByDate(@Param("targetDate") Date targetDate);


    List<Discount> findByProductIdAndStoreIdOrderByFromDate(String productId, String storeId);


    @Query("SELECT d FROM Discount d WHERE d.productId = :productId AND d.storeId = :storeId " +
            "AND NOT (d.toDate < :fromDate OR d.fromDate > :toDate)")
    List<Discount> findOverlappingDiscounts(
            @Param("productId") String productId,
            @Param("storeId") String storeId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    List<Discount> findByProductIdAndStoreIdAndFromDate(String productId,String storeId,Date fromDate);
    List<Discount> findByProductIdAndStoreIdAndToDate(String productId, String storeId,Date toDate);
}
