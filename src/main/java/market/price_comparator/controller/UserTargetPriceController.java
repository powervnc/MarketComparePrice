package market.price_comparator.controller;

import market.price_comparator.dto.PriceAlertDto;
import market.price_comparator.model.Price;
import market.price_comparator.model.UserTargetPrice;
import market.price_comparator.service.UserTargetPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-target-price")
public class UserTargetPriceController {

    @Autowired
    private UserTargetPriceService userTargetPriceService;

    @PostMapping
    public UserTargetPrice setTargetPrice(@RequestBody UserTargetPrice targetPrice) {
        return userTargetPriceService.setTargetPrice(targetPrice);
    }

    @GetMapping("/alerts/{userId}/{storeId}")
    public List<PriceAlertDto> getAlerts(
            @PathVariable String userId,
            @PathVariable String storeId) {
        return userTargetPriceService.getAlerts(userId, storeId);
    }
}
