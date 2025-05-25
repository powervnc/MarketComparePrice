package market.price_comparator;

import market.price_comparator.controller.UserTargetPriceController;
import market.price_comparator.dto.PriceAlertDto;
import market.price_comparator.model.UserTargetPrice;
import market.price_comparator.service.UserTargetPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserTargetPriceService userTargetPriceService;

    @InjectMocks
    private UserTargetPriceController userTargetPriceController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userTargetPriceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSetTargetPrice() throws Exception {
        UserTargetPrice targetPrice = new UserTargetPrice();
        targetPrice.setUserId("user1");
        targetPrice.setProductId("prod1");
        targetPrice.setThreshold(10.5f);

        when(userTargetPriceService.setTargetPrice(any(UserTargetPrice.class)))
                .thenReturn(targetPrice);

        mockMvc.perform(post("/user-target-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetPrice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.productId").value("prod1"))
                .andExpect(jsonPath("$.threshold").value(10.5));
    }

    @Test
    public void testGetAlerts() throws Exception {
        PriceAlertDto alertDto = new PriceAlertDto();
        alertDto.setProductName("Test Product");
        alertDto.setStoreName("Test Store");
        alertDto.setValue(9.99f);
        alertDto.setCurrency("USD");
        alertDto.setCurrentDate("2025-05-25");

        when(userTargetPriceService.getAlerts("user1", "store1"))
                .thenReturn(List.of(alertDto));

        mockMvc.perform(get("/user-target-price/alerts/user1/store1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[0].storeName").value("Test Store"))
                .andExpect(jsonPath("$[0].value").value(9.99))
                .andExpect(jsonPath("$[0].currency").value("USD"))
                .andExpect(jsonPath("$[0].currentDate").value("2025-05-25"));
    }
}
