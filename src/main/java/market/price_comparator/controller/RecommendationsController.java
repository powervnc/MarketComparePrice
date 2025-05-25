package market.price_comparator.controller;

import market.price_comparator.dto.BestPriceDto;
import market.price_comparator.service.ReccomendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationsController {

    @Autowired
    private ReccomendationsService reccomendationsService;

    @GetMapping("/best-price")
    public ResponseEntity<BestPriceDto> getBestPrice(@RequestParam String productName) {
        BestPriceDto bestPrice = reccomendationsService.getBestUnitPriceByProductNameAcrossStores(productName);
        if (bestPrice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bestPrice);
    }
}