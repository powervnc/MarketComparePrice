package market.price_comparator.controller;

import market.price_comparator.dto.BestProductDto;
import market.price_comparator.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/basket")
public class BasketController {

    @Autowired
    private BasketService basketService;


    @GetMapping("/user/{userId}/all-shopping-lists")
    public ResponseEntity<List<List<BestProductDto>>> getAllShoppingLists(@PathVariable String userId) {
        List<List<BestProductDto>> allLists = basketService.getAllShoppingLists(userId);
//        if (allLists.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
        return ResponseEntity.ok(allLists);
    }
}
