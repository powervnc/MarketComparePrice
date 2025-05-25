package market.price_comparator;


import market.price_comparator.controller.BasketController;
import market.price_comparator.dto.BestProductDto;
import market.price_comparator.service.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BasketControllerTest {

    @Mock
    private BasketService basketService;

    @InjectMocks
    private BasketController basketController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAllShoppingListsReturnsData() {
        String userId = "user1";
        List<List<BestProductDto>> allLists = Arrays.asList(
                Arrays.asList(new BestProductDto("Prod1", "StoreA", 10f, "RON", "2025-05-25")),
                Arrays.asList(new BestProductDto("Prod2", "StoreB", 20f, "RON", "2025-05-25"))
        );

        when(basketService.getAllShoppingLists(userId)).thenReturn(allLists);

        ResponseEntity<List<List<BestProductDto>>> response = basketController.getAllShoppingLists(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(allLists, response.getBody());
    }

    @Test
    void testGetAllShoppingListsReturnsNoContent() {
        String userId = "userEmpty";

        when(basketService.getAllShoppingLists(userId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<List<BestProductDto>>> response = basketController.getAllShoppingLists(userId);

        assertEquals(200, response.getStatusCodeValue());

    }
}
