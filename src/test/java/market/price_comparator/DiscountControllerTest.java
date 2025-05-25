package market.price_comparator;

import market.price_comparator.controller.DiscountController;
import market.price_comparator.model.Discount;
import market.price_comparator.service.DiscountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiscountControllerTest {

    @Mock
    private DiscountsService discountsService;

    @InjectMocks
    private DiscountController discountController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNewDiscountsReturnsDiscounts() {
        Date now = new Date();
        Discount discount = new Discount();
        List<Discount> discounts = Collections.singletonList(discount);

        when(discountsService.getNewDiscounts(now)).thenReturn(discounts);

        ResponseEntity<List<Discount>> response = discountController.getNewDiscounts();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(discounts, response.getBody());
    }

    @Test
    void testGetNewDiscountsReturnsNoContent() {
        Date now = new Date();
        when(discountsService.getNewDiscounts(now)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Discount>> response = discountController.getNewDiscounts();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testGetBestDiscountsReturnsDiscounts() {
        Date date = new Date();
        Discount discount = new Discount();
        List<Discount> discounts = Collections.singletonList(discount);

        when(discountsService.findBestDiscounts(date)).thenReturn(discounts);

        ResponseEntity<List<Discount>> response = discountController.getBestDiscounts();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(discounts, response.getBody());
    }

    @Test
    void testGetBestDiscountsReturnsNoContent() {
        Date date = new Date();
        when(discountsService.findBestDiscounts(date)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Discount>> response = discountController.getBestDiscounts();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
