package market.price_comparator;

import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import market.price_comparator.service.PriceHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriceHistoryServiceTest {

    @InjectMocks
    private PriceHistoryService priceHistoryService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

    @Test
    void testGetDataPointsForProduct_withDiscounts() throws Exception {
        Product product = new Product();
        product.setProductId("prod1");
        product.setProductName("Test Product");
        product.setBrandId("brand1");
        product.setCategoryId("cat1");

        Store store = new Store();
        store.setStoreId("store1");
        store.setStoreName("Test Store");

        List<Price> prices = List.of(
                createPrice("01.10", 10),
                createPrice("10.10", 50)
        );

        List<Discount> discounts = List.of(
                createDiscount("02.10", "03.10", 10f)
        );

        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDate("prod1", "store1")).thenReturn(prices);
        when(discountRepository.findByProductIdAndStoreIdOrderByFromDate("prod1", "store1")).thenReturn(discounts);
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category("cat1", "Test Category")));
        when(brandRepository.findById("brand1")).thenReturn(Optional.of(new Brand("brand1", "Test Brand")));

        var result = priceHistoryService.getDataPointsForProduct(product, List.of(store));

        assertEquals(3, result.size());
        assertEquals(10.0f, result.get(0).getPrice());
        assertEquals(9.0f, result.get(1).getPrice());
        assertEquals(10.0f, result.get(2).getPrice());
    }

    @Test
    void testGetDataPointsForProduct_disjointDiscountsBetweenPrices() throws Exception {
        Product product = new Product();
        product.setProductId("prod1");
        product.setProductName("Test Product");
        product.setBrandId("brand1");
        product.setCategoryId("cat1");

        Store store = new Store();
        store.setStoreId("store1");
        store.setStoreName("Test Store");

        List<Price> prices = List.of(
                createPrice("01.10", 100),
                createPrice("10.10", 200)
        );

        List<Discount> discounts = List.of(
                createDiscount("03.10", "05.10", 10),
                createDiscount("11.10", "12.10", 20)
        );

        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDate("prod1", "store1")).thenReturn(prices);
        when(discountRepository.findByProductIdAndStoreIdOrderByFromDate("prod1", "store1")).thenReturn(discounts);
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category("cat1", "Test Category")));
        when(brandRepository.findById("brand1")).thenReturn(Optional.of(new Brand("brand1", "Test Brand")));

        var result = priceHistoryService.getDataPointsForProduct(product, List.of(store));

        List<Float> expected = List.of(100f, 90f, 100f, 200f, 160f);

        assertEquals(expected.size(), result.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), result.get(i).getPrice(), 0.01);
        }
    }

    private Price createPrice(String date, float value) throws Exception {
        Price p = new Price();
        p.setPriceDate(sdf.parse(date));
        p.setPrice(value);
        return p;
    }

    private Discount createDiscount(String from, String to, float percentage) throws Exception {
        Discount d = new Discount();
        d.setFromDate(sdf.parse(from));
        d.setToDate(sdf.parse(to));
        d.setPercentageDiscount((int) percentage);
        return d;
    }

    @Test
    void testGetDataPointsForProduct_multipleStoresAndDisjointDiscounts() throws Exception {
        Product product = new Product();
        product.setProductId("prod3");
        product.setProductName("Complex Product");
        product.setBrandId("brand3");
        product.setCategoryId("cat3");

        Store store1 = new Store();
        store1.setStoreId("store1");
        store1.setStoreName("Store One");

        Store store2 = new Store();
        store2.setStoreId("store2");
        store2.setStoreName("Store Two");


        List<Price> pricesStore1 = List.of(
                createPrice("01.10", 100),
                createPrice("08.10", 150),
                createPrice("15.10", 200)
        );


        List<Price> pricesStore2 = List.of(
                createPrice("02.10", 120),
                createPrice("10.10", 140)
        );


        List<Discount> discountsStore1 = List.of(
                createDiscount("03.10", "05.10", 10),
                createDiscount("12.10", "14.10", 20)
        );


        List<Discount> discountsStore2 = List.of(
                createDiscount("02.10", "12.10", 15)
        );


        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDate("prod3", "store1")).thenReturn(pricesStore1);
        when(discountRepository.findByProductIdAndStoreIdOrderByFromDate("prod3", "store1")).thenReturn(discountsStore1);

        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDate("prod3", "store2")).thenReturn(pricesStore2);
        when(discountRepository.findByProductIdAndStoreIdOrderByFromDate("prod3", "store2")).thenReturn(discountsStore2);

        when(categoryRepository.findById("cat3")).thenReturn(Optional.of(new Category("cat3", "Complex Category")));
        when(brandRepository.findById("brand3")).thenReturn(Optional.of(new Brand("brand3", "Complex Brand")));

        var results = priceHistoryService.getDataPointsForProduct(product, List.of(store1, store2));




        assertEquals(8, results.size());

        assertEquals("Store One", results.get(0).getStoreName());
        assertEquals(sdf.parse("01.10"), results.get(0).getFromDate());
        assertEquals(sdf.parse("03.10"), results.get(0).getToDate());
        assertEquals(100f, results.get(0).getPrice(), 0.01);

        assertEquals("Store One", results.get(1).getStoreName());
        assertEquals(sdf.parse("03.10"), results.get(1).getFromDate());
        assertEquals(sdf.parse("05.10"), results.get(1).getToDate());
        assertEquals(90f, results.get(1).getPrice(), 0.01);

        assertEquals("Store One", results.get(2).getStoreName());
        assertEquals(sdf.parse("05.10"), results.get(2).getFromDate());
        assertEquals(sdf.parse("08.10"), results.get(2).getToDate());
        assertEquals(100f, results.get(2).getPrice(), 0.01);

        assertEquals("Store One", results.get(3).getStoreName());
        assertEquals(sdf.parse("08.10"), results.get(3).getFromDate());
        assertEquals(sdf.parse("12.10"), results.get(3).getToDate());
        assertEquals(150f, results.get(3).getPrice(), 0.01);

        assertEquals("Store One", results.get(4).getStoreName());
        assertEquals(sdf.parse("12.10"), results.get(4).getFromDate());
        assertEquals(sdf.parse("14.10"), results.get(4).getToDate());
        assertEquals(120f, results.get(4).getPrice(), 0.01);

        assertEquals("Store One", results.get(5).getStoreName());
        assertEquals(sdf.parse("14.10"), results.get(5).getFromDate());
        assertEquals(sdf.parse("15.10"), results.get(5).getToDate());
        assertEquals(150f, results.get(5).getPrice(), 0.01);

        assertEquals("Store Two", results.get(6).getStoreName());
        assertEquals(sdf.parse("02.10"), results.get(6).getFromDate());
        assertEquals(sdf.parse("10.10"), results.get(6).getToDate());
        assertEquals(102f, results.get(6).getPrice(), 0.01);

        assertEquals("Store Two", results.get(7).getStoreName());
        assertEquals(sdf.parse("10.10"), results.get(7).getFromDate());
        assertEquals(sdf.parse("12.10"), results.get(7).getToDate());
        assertEquals(119f, results.get(7).getPrice(), 0.01);
    }

}
