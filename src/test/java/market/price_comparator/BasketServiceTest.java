package market.price_comparator;



import market.price_comparator.dto.BestProductDto;
import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import market.price_comparator.service.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BasketServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketProductRepository basketProductRepository;

    @InjectMocks
    private BasketService basketService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCheapestProductAcrossStores() {
        Product product = new Product();
        product.setProductId("prod1");
        product.setProductName("TestProduct");

        Store store1 = new Store();
        store1.setStoreId("store1");  // String ID
        store1.setStoreName("Store1");

        Store store2 = new Store();
        store2.setStoreId("store2");  // String ID
        store2.setStoreName("Store2");

        Price price1 = new Price();
        price1.setPrice(100f);
        price1.setCurrency("RON");

        Price price2 = new Price();
        price2.setPrice(90f);
        price2.setCurrency("RON");

        Discount discount = new Discount();
        discount.setPercentageDiscount(10);

        when(storeRepository.findAll()).thenReturn(Arrays.asList(store1, store2));

        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(eq("prod1"), eq("store1")))
                .thenReturn(Collections.singletonList(price1));
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(eq("prod1"), eq("store2")))
                .thenReturn(Collections.singletonList(price2));

        when(discountRepository.findMostRecentDiscount(eq("store1"), eq("prod1"), any(Date.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());
        when(discountRepository.findMostRecentDiscount(eq("store2"), eq("prod1"), any(Date.class), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(discount));

        BestProductDto cheapest = basketService.getCheapestProductAcrossStores(product);

        // store1 price after discount = 100 (no discount)
        // store2 price after discount = 90 * 0.9 = 81
        assertNotNull(cheapest);
        assertEquals("TestProduct", cheapest.getProductName());
        assertEquals("Store2", cheapest.getStoreName());
        assertEquals(81f, cheapest.getPrice(), 0.001f);
        assertEquals("RON", cheapest.getCurrency());
    }

    @Test
    void testGetShoppingList() {
        String basketId = "basket123";
        List<String> productIds = Arrays.asList("prod1", "prod2");

        Product product1 = new Product();
        product1.setProductId("prod1");
        product1.setProductName("Product1");

        Product product2 = new Product();
        product2.setProductId("prod2");
        product2.setProductName("Product2");

        when(basketProductRepository.findProductIdsByBasketId(basketId)).thenReturn(productIds);
        when(productRepository.findAllById(productIds)).thenReturn(Arrays.asList(product1, product2));

        BestProductDto dto1 = new BestProductDto("Product1", "Store1", 10f, "RON", "2025-05-25");
        BestProductDto dto2 = new BestProductDto("Product2", "Store2", 20f, "RON", "2025-05-25");

        BasketService spyService = Mockito.spy(basketService);
        doReturn(dto1).when(spyService).getCheapestProductAcrossStores(product1);
        doReturn(dto2).when(spyService).getCheapestProductAcrossStores(product2);

        List<BestProductDto> result = spyService.getShoppingList(basketId);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void testGetAllShoppingLists() {
        String userId = "user1";

        Basket basket1 = new Basket();
        basket1.setBasketId("b1");
        Basket basket2 = new Basket();
        basket2.setBasketId("b2");

        when(basketRepository.findAllByUserId(userId)).thenReturn(Arrays.asList(basket1, basket2));

        BasketService spyService = Mockito.spy(basketService);
        doReturn(Collections.singletonList(new BestProductDto())).when(spyService).getShoppingList("b1");
        doReturn(Collections.singletonList(new BestProductDto())).when(spyService).getShoppingList("b2");

        List<List<BestProductDto>> allLists = spyService.getAllShoppingLists(userId);

        assertEquals(2, allLists.size());
    }
}
