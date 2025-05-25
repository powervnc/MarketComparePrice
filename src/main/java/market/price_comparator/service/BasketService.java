package market.price_comparator.service;

import market.price_comparator.dto.BestPriceDto;
import market.price_comparator.dto.BestProductDto;
import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BasketService {
    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketProductRepository basketProductRepository;

    public BestProductDto getCheapestProductAcrossStores(Product product){
        Date now = new Date();
        float bestPrice = Float.MAX_VALUE;
        Price bestPriceEntry = null;
        Product bestProduct = null;
        Store bestStore = null;

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


            if (priceAfterDiscount < bestPrice) {
                bestPrice = priceAfterDiscount;
                bestPriceEntry = latestPrice;
                bestProduct = product;
                bestStore = store;
            }
        }

        if (bestPriceEntry == null || bestProduct == null || bestStore == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(new Date());

        return new BestProductDto(
                bestProduct.getProductName(),
                bestStore.getStoreName(),
                bestPrice,
                bestPriceEntry.getCurrency(),
                formattedDate
        );
    }

    public List<BestProductDto> getShoppingList(String basketId){
        List<String> productIdsInBasket =  basketProductRepository.findProductIdsByBasketId(basketId);
        List<Product> products = productRepository.findAllById(productIdsInBasket);

        List<BestProductDto> bestProducts = new ArrayList<>();
        for(Product product: products){
            BestProductDto result = getCheapestProductAcrossStores(product);
            if(result != null)
                bestProducts.add(result);
        }

        return bestProducts;
    }

    public List<List<BestProductDto>> getAllShoppingLists(String userId){
        List<Basket> userBaskets = basketRepository.findAllByUserId(userId);
        List<List<BestProductDto>> all = new ArrayList<>();

        for(Basket basket: userBaskets){
            all.add(getShoppingList(basket.getBasketId()));
        }

        return all;
    }
}
