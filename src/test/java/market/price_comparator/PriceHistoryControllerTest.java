package market.price_comparator;

import market.price_comparator.controller.PriceHistoryController;
import market.price_comparator.dto.ProductDataPointDTO;
import market.price_comparator.service.PriceHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceHistoryControllerTest {

    @Mock
    private PriceHistoryService priceHistoryService;

    @InjectMocks
    private PriceHistoryController priceHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPriceHistory_ReturnsDataPoints() {
        String productId = "prod123";
        List<ProductDataPointDTO> dataPoints = Arrays.asList(
                new ProductDataPointDTO("Product1", "Store1", "Category1", "Brand1", new Date(), new Date(), 100f)
        );

        when(priceHistoryService.getCall(productId)).thenReturn(dataPoints);

        ResponseEntity<List<ProductDataPointDTO>> response = priceHistoryController.getPriceHistory(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(dataPoints, response.getBody());
        verify(priceHistoryService, times(1)).getCall(productId);
    }

    @Test
    void getPriceHistory_ReturnsNoContentWhenEmpty() {
        String productId = "prodEmpty";
        when(priceHistoryService.getCall(productId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProductDataPointDTO>> response = priceHistoryController.getPriceHistory(productId);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());

    }
}

