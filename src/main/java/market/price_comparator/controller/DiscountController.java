package market.price_comparator.controller;


import market.price_comparator.model.Discount;
import market.price_comparator.service.DiscountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountsService discountsService;

    @GetMapping("/new")
    public ResponseEntity<List<Discount>> getNewDiscounts() {
        Date now = new Date();
        List<Discount> discounts = discountsService.getNewDiscounts(now);
        if (discounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/best")
    public ResponseEntity<List<Discount>> getBestDiscounts() {
        Date now = new Date();
        List<Discount> bestDiscounts = discountsService.findBestDiscounts(now);
        if (bestDiscounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bestDiscounts);
    }
}
