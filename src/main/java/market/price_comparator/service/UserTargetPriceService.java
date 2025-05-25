package market.price_comparator.service;

import market.price_comparator.dto.PriceAlertDto;
import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserTargetPriceService {

    @Autowired
    private UserTargetPriceRepository userTargetPriceRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository  discountRepository;

    @Autowired
    private StoreRepository storeRepository;

    public UserTargetPrice setTargetPrice(UserTargetPrice targetPrice) {
        Optional<UserTargetPrice> existing = userTargetPriceRepository
                .findByUserIdAndProductId(targetPrice.getUserId(), targetPrice.getProductId());

        if (existing.isPresent()) {
            UserTargetPrice existingTarget = existing.get();
            existingTarget.setThreshold(targetPrice.getThreshold());
            return userTargetPriceRepository.save(existingTarget);
        }

        return userTargetPriceRepository.save(targetPrice);
    }

    public List<PriceAlertDto> getAlerts(String userId, String storeId) {
        List<UserTargetPrice> targets = userTargetPriceRepository.findByUserId(userId);
        List<PriceAlertDto> alerts = new ArrayList<>();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(now);


        for (UserTargetPrice target : targets) {
            List<Price> prices = priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(target.getProductId(), storeId);

            if (!prices.isEmpty()) {
                Price latestPrice = prices.get(0);

                Optional<Product> productOpt = productRepository.findById(latestPrice.getProductId());
                if (productOpt.isEmpty()) continue;
                Product product = productOpt.get();

                Optional<Store> storeOpt = storeRepository.findById(latestPrice.getStoreId());
                if (storeOpt.isEmpty()) continue;
                Store store = storeOpt.get();

                // Calculate discount if any
                float finalPrice = latestPrice.getPrice();

                List<Discount> discounts = discountRepository.findMostRecentDiscount(
                        latestPrice.getStoreId(), latestPrice.getProductId(), now,
                        org.springframework.data.domain.PageRequest.of(0, 1));

                if (!discounts.isEmpty()) {
                    Discount discount = discounts.get(0);
                    float discountPercentage = discount.getPercentageDiscount();
                    finalPrice = finalPrice * (1 - discountPercentage / 100f);
                }

                if (finalPrice <= target.getThreshold()) {
                    alerts.add(new PriceAlertDto(
                            product.getProductName(),
                            store.getStoreName(),
                            finalPrice,
                            latestPrice.getCurrency(),
                            formattedDate
                    ));
                }
            }
        }
        return alerts;
    }

}