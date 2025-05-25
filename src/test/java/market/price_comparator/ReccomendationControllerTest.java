package market.price_comparator;



import market.price_comparator.controller.RecommendationsController;
import market.price_comparator.dto.BestPriceDto;
import market.price_comparator.service.ReccomendationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class RecommendationsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReccomendationsService reccomendationsService;

    @InjectMocks
    private RecommendationsController recommendationsController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationsController).build();
    }

    @Test
    void testGetBestPrice_found() throws Exception {
        BestPriceDto dto = new BestPriceDto(
                "ProductX",
                "StoreY",
                9.99f,
                "RON",
                "2025-05-25"
        );

        when(reccomendationsService.getBestUnitPriceByProductNameAcrossStores(eq("ProductX")))
                .thenReturn(dto);

        mockMvc.perform(get("/best-price")
                        .param("productName", "ProductX")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("ProductX"))
                .andExpect(jsonPath("$.storeName").value("StoreY"))
                .andExpect(jsonPath("$.unitPrice").value(9.99))
                .andExpect(jsonPath("$.currency").value("RON"))
                .andExpect(jsonPath("$.priceDate").value("2025-05-25"));
    }

    @Test
    void testGetBestPrice_notFound() throws Exception {
        when(reccomendationsService.getBestUnitPriceByProductNameAcrossStores(eq("UnknownProduct")))
                .thenReturn(null);

        mockMvc.perform(get("/best-price")
                        .param("productName", "UnknownProduct")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
