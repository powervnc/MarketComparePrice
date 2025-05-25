package market.price_comparator;


import market.price_comparator.dto.BestPriceDto;
import market.price_comparator.model.Discount;
import market.price_comparator.model.Price;
import market.price_comparator.model.Product;
import market.price_comparator.model.Store;
import market.price_comparator.repo.DiscountRepository;
import market.price_comparator.repo.PriceRepository;
import market.price_comparator.repo.ProductRepository;
import market.price_comparator.repo.StoreRepository;
import market.price_comparator.service.ReccomendationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReccomendationsServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ReccomendationsService reccomendationsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBestUnitPriceByProductNameAcrossStores_multipleProductsSameName() {

        Product productA = new Product();
        productA.setProductId("1L");
        productA.setProductName("MultiProduct");
        productA.setPackageQuantity(5);

        Product productB = new Product();
        productB.setProductId("2L");
        productB.setProductName("MultiProduct");
        productB.setPackageQuantity(20); // bigger pack

        List<Product> products = Arrays.asList(productA, productB);

        // Stores
        Store storeX = new Store();
        storeX.setStoreId("100L");
        storeX.setStoreName("StoreX");

        Store storeY = new Store();
        storeY.setStoreId("200L");
        storeY.setStoreName("StoreY");

        List<Store> stores = Arrays.asList(storeX, storeY);


        Price priceAStoreX = new Price();
        priceAStoreX.setPrice(50f);
        priceAStoreX.setCurrency("RON");

        Price priceAStoreY = new Price();
        priceAStoreY.setPrice(48f);
        priceAStoreY.setCurrency("RON");


        Price priceBStoreX = new Price();
        priceBStoreX.setPrice(190f);
        priceBStoreX.setCurrency("RON");

        Price priceBStoreY = new Price();
        priceBStoreY.setPrice(185f);
        priceBStoreY.setCurrency("RON");


        Discount discountAStoreX = new Discount();
        discountAStoreX.setPercentageDiscount(1);

        Discount discountAStoreY = new Discount();
        discountAStoreY.setPercentageDiscount(10);

        Discount discountBStoreX = new Discount();
        discountBStoreX.setPercentageDiscount(5);

        Discount discountBStoreY = new Discount();
        discountBStoreY.setPercentageDiscount(1);


        when(productRepository.findByProductName("MultiProduct"))
                .thenReturn(products);


        when(storeRepository.findAll())
                .thenReturn(stores);


        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(productA.getProductId(), storeX.getStoreId()))
                .thenReturn(Collections.singletonList(priceAStoreX));
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(productA.getProductId(), storeY.getStoreId()))
                .thenReturn(Collections.singletonList(priceAStoreY));
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(productB.getProductId(), storeX.getStoreId()))
                .thenReturn(Collections.singletonList(priceBStoreX));
        when(priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(productB.getProductId(), storeY.getStoreId()))
                .thenReturn(Collections.singletonList(priceBStoreY));

        when(discountRepository.findMostRecentDiscount(eq(storeX.getStoreId()), eq(productA.getProductId()), any(Date.class), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(discountAStoreX));
        when(discountRepository.findMostRecentDiscount(eq(storeY.getStoreId()), eq(productA.getProductId()), any(Date.class), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(discountAStoreY));
        when(discountRepository.findMostRecentDiscount(eq(storeX.getStoreId()), eq(productB.getProductId()), any(Date.class), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(discountBStoreX));
        when(discountRepository.findMostRecentDiscount(eq(storeY.getStoreId()), eq(productB.getProductId()), any(Date.class), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(discountBStoreY));


        BestPriceDto result = reccomendationsService.getBestUnitPriceByProductNameAcrossStores("MultiProduct");



        assertNotNull(result);
        assertEquals("MultiProduct", result.getProductName());
        assertEquals("StoreY", result.getStoreName());
        assertEquals("RON", result.getCurrency());
        assertEquals(8.64f, result.getUnitPrice(), 0.001);
        assertNotNull(result.getPriceDate());

    }
}
