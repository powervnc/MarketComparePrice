package market.price_comparator.service;



import market.price_comparator.dto.BestPriceDto;
import market.price_comparator.model.Discount;
import market.price_comparator.model.Price;
import market.price_comparator.model.Product;
import market.price_comparator.model.Store;
import market.price_comparator.repo.DiscountRepository;
import market.price_comparator.repo.PriceRepository;
import market.price_comparator.repo.ProductRepository;
import market.price_comparator.repo.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReccomendationsService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private StoreRepository storeRepository;

    public BestPriceDto getBestUnitPriceByProductNameAcrossStores(String productName) {
        List<Product> products = productRepository.findByProductName(productName);
        if (products.isEmpty()) return null;

        Date now = new Date();
        float bestUnitPrice = Float.MAX_VALUE;
        Price bestPriceEntry = null;
        Product bestProduct = null;
        Store bestStore = null;

        for (Product product : products) {
            List<Store> stores = storeRepository.findAll();

            for (Store store : stores) {
                List<Price> prices = priceRepository.findByProductIdAndStoreIdOrderByPriceDateDesc(
                        product.getProductId(), store.getStoreId()
                );

                if (prices.isEmpty()) continue;

                Price latestPrice = prices.get(0);


                List<Discount> discounts = discountRepository.findMostRecentDiscount(
                        store.getStoreId(), product.getProductId(), now,
                        org.springframework.data.domain.PageRequest.of(0,1)
                );

                float priceAfterDiscount = latestPrice.getPrice();
                if (!discounts.isEmpty()) {
                    Discount discount = discounts.get(0);
                    priceAfterDiscount = priceAfterDiscount * (1 - discount.getPercentageDiscount() / 100f);
                }

                if (product.getPackageQuantity() <= 0) continue;

                float unitPrice = priceAfterDiscount / product.getPackageQuantity();

                if (unitPrice < bestUnitPrice) {
                    bestUnitPrice = unitPrice;
                    bestPriceEntry = latestPrice;
                    bestProduct = product;
                    bestStore = store;
                }
            }
        }

        if (bestPriceEntry == null || bestProduct == null || bestStore == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(new Date());

        return new BestPriceDto(
                bestProduct.getProductName(),
                bestStore.getStoreName(),
                bestUnitPrice,
                bestPriceEntry.getCurrency(),
                formattedDate
        );
    }

}
