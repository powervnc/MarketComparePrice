package market.price_comparator;


import market.price_comparator.model.Discount;
import market.price_comparator.repo.DiscountRepository;
import market.price_comparator.repo.PriceRepository;
import market.price_comparator.service.DiscountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiscountsServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private DiscountsService discountsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNewDiscounts() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, -24);
        Date twentyFourHoursAgo = cal.getTime();

        Discount discount = new Discount();
        List<Discount> discounts = Collections.singletonList(discount);

        when(discountRepository.findByFromDateBetween(twentyFourHoursAgo, now)).thenReturn(discounts);

        List<Discount> result = discountsService.getNewDiscounts(now);

        assertEquals(discounts, result);
        verify(discountRepository).findByFromDateBetween(twentyFourHoursAgo, now);
    }

    @Test
    void testFindMostRecentDiscountReturnsDiscount() {
        String storeId = "store1";
        String productId = "prod1";
        Date targetDate = new Date();

        Discount discount = new Discount();
        List<Discount> discounts = Collections.singletonList(discount);

        when(discountRepository.findMostRecentDiscount(storeId, productId, targetDate, Pageable.ofSize(1))).thenReturn(discounts);

        Discount result = discountsService.findMostRecentDiscount(productId, storeId, targetDate);

        assertNotNull(result);
        assertEquals(discount, result);
    }

    @Test
    void testFindMostRecentDiscountReturnsNullWhenEmpty() {
        String storeId = "store1";
        String productId = "prod1";
        Date targetDate = new Date();

        when(discountRepository.findMostRecentDiscount(storeId, productId, targetDate, Pageable.ofSize(1))).thenReturn(Collections.emptyList());

        Discount result = discountsService.findMostRecentDiscount(productId, storeId, targetDate);

        assertNull(result);
    }

    @Test
    void testFindBestDiscounts() {
        Date date = new Date();

        Discount discount1 = new Discount();
        Discount discount2 = new Discount();
        List<Discount> discounts = Arrays.asList(discount1, discount2);

        when(discountRepository.findMaxDiscountsByDate(date)).thenReturn(discounts);

        List<Discount> result = discountsService.findBestDiscounts(date);

        assertEquals(discounts, result);
        verify(discountRepository).findMaxDiscountsByDate(date);
    }
}
