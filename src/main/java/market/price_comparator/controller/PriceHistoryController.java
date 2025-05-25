package market.price_comparator.controller;

import market.price_comparator.dto.ProductDataPointDTO;
import market.price_comparator.service.PriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price-history")
public class PriceHistoryController {

    @Autowired
    private PriceHistoryService priceHistoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductDataPointDTO>> getPriceHistory(@PathVariable String productId) {
        List<ProductDataPointDTO> dataPoints = priceHistoryService.getCall(productId);
        if (dataPoints.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dataPoints);
    }
}
