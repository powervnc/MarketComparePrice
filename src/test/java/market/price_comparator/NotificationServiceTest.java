package market.price_comparator;

import market.price_comparator.dto.PriceAlertDto;
import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import market.price_comparator.service.UserTargetPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private UserTargetPriceService service;

    @Mock
    private UserTargetPriceRepository userTargetPriceRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAlerts_withDiscountAndPriceBelowThreshold() {
        String userId = "user1";
        String storeId = "store1";

        UserTargetPrice targetPrice = new UserTargetPrice("target1", "product1", userId, 100f);
        List<UserTargetPrice> targets = List.of(targetPrice);

        Price latestPrice = new Price();
        latestPrice.setPriceId("price1");
        latestPrice.setProductId("product1");
        latestPrice.setStoreId(storeId);
        latestPrice.setPrice(120f);
        latestPrice.setCurrency("USD");
        latestPrice.setPriceDate(new Date());

        Product product = new Product("product1", "Product A", 1f, "kg", "category1", "brand1");
        Store store = new Store();
        store.setStoreId(storeId);
        store.setStoreName("Super Store");

        Discount discount = new Discount();
        discount.setDiscountId("discount1");
        discount.setProductId("product1");
        discount.setStoreId(storeId);
        discount.setPercentageDiscount(20);
        discount.setFromDate(new Date(System.currentTimeMillis() - 1000 * 60 * 60));
        discount.setToDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60));

        when(userTargetPriceRepository.findByUserId(userId)).thenReturn(targets);
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc("product1", storeId))
                .thenReturn(List.of(latestPrice));
        when(productRepository.findById("product1")).thenReturn(Optional.of(product));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(discountRepository.findMostRecentDiscount(eq(storeId), eq("product1"), any(Date.class), any(PageRequest.class)))
                .thenReturn(List.of(discount));

        List<PriceAlertDto> alerts = service.getAlerts(userId, storeId);

        assertEquals(1, alerts.size());

        PriceAlertDto alert = alerts.get(0);
        assertEquals("Product A", alert.getProductName());
        assertEquals("Super Store", alert.getStoreName());
        assertEquals(96f, alert.getValue(), 0.001);
        assertEquals("USD", alert.getCurrency());

        String todayFormatted = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        assertEquals(todayFormatted, alert.getCurrentDate());

        verify(userTargetPriceRepository).findByUserId(userId);
        verify(priceRepository).findByProductIdAndStoreIdOrderByPriceDateDesc("product1", storeId);
        verify(productRepository).findById("product1");
        verify(storeRepository).findById(storeId);
        verify(discountRepository).findMostRecentDiscount(eq(storeId), eq("product1"), any(Date.class), any(PageRequest.class));
    }

    @Test
    void testGetAlerts_priceAboveThreshold_noAlert() {
        String userId = "user1";
        String storeId = "store1";

        UserTargetPrice targetPrice = new UserTargetPrice("target1", "product1", userId, 50f);
        List<UserTargetPrice> targets = List.of(targetPrice);

        Price latestPrice = new Price();
        latestPrice.setPriceId("price1");
        latestPrice.setProductId("product1");
        latestPrice.setStoreId(storeId);
        latestPrice.setPrice(60f);
        latestPrice.setCurrency("USD");
        latestPrice.setPriceDate(new Date());

        Product product = new Product("product1", "Product A", 1f, "kg", "category1", "brand1");
        Store store = new Store();
        store.setStoreId(storeId);
        store.setStoreName("Super Store");

        List<Discount> noDiscounts = Collections.emptyList();

        when(userTargetPriceRepository.findByUserId(userId)).thenReturn(targets);
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc("product1", storeId))
                .thenReturn(List.of(latestPrice));
        when(productRepository.findById("product1")).thenReturn(Optional.of(product));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(discountRepository.findMostRecentDiscount(eq(storeId), eq("product1"), any(Date.class), any(PageRequest.class)))
                .thenReturn(noDiscounts);

        List<PriceAlertDto> alerts = service.getAlerts(userId, storeId);

        assertTrue(alerts.isEmpty());
    }

    @Test
    void testGetAlerts_noPrices_noAlert() {
        String userId = "user1";
        String storeId = "store1";

        UserTargetPrice targetPrice = new UserTargetPrice("target1", "product1", userId, 50f);
        List<UserTargetPrice> targets = List.of(targetPrice);

        when(userTargetPriceRepository.findByUserId(userId)).thenReturn(targets);
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc("product1", storeId))
                .thenReturn(Collections.emptyList());

        List<PriceAlertDto> alerts = service.getAlerts(userId, storeId);

        assertTrue(alerts.isEmpty());
    }
}
