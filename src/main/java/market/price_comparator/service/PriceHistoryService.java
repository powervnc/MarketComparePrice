package market.price_comparator.service;

import market.price_comparator.dto.ProductDataPointDTO;
import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PriceHistoryService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private StoreRepository storeRepository;

    public List<ProductDataPointDTO> getCall(String productId){
        Optional<Product> product = productRepository.findById(productId);
        if(!product.isPresent()) return new ArrayList<>();
        Product p = product.get();
        List<Store> stores = storeRepository.findAll();
        return getDataPointsForProduct(p,stores);
    }

    public List<ProductDataPointDTO> getDataPointsForProduct(Product product, List<Store> stores){

        List<ProductDataPointDTO> dataPoints = new ArrayList<>();
        Optional<Category> category = categoryRepository.findById(product.getCategoryId());
        Optional<Brand> brand = brandRepository.findById(product.getBrandId());


        for(Store store : stores){
            String productId = product.getProductId();
            String storeId = store.getStoreId();
            List<Price> prices = priceRepository.findByProductIdAndStoreIdOrderByPriceDate(productId, storeId);
            List<Discount> discounts = discountRepository.findByProductIdAndStoreIdOrderByFromDate(productId, storeId);


            Set<Date> dateSet = new TreeSet<>();

            for (Price price : prices) {
                dateSet.add(price.getPriceDate());
            }

            for (Discount discount : discounts) {
                dateSet.add(discount.getFromDate());
                dateSet.add(discount.getToDate());
            }

            List<Date> timeline = new ArrayList<>(dateSet);

            int pricesIdx = 0;
            int discountsIdx = 0;
            int pricesSize = prices.size();
            int discountsSize = discounts.size();


            int timelineEnd = timeline.size()-1;
            for(int i=0;i<timelineEnd;i++){
                Date intervalStart = timeline.get(i);
                Date intervalEnd = timeline.get(i+1);

                //advance the pointer while the next price date is before the start of the interval or equal
                //because we want to have an interval like price1 <= start < price2
                while(pricesIdx+1 < pricesSize && prices.get(pricesIdx+1).getPriceDate().compareTo(intervalStart) <= 0 ){
                    pricesIdx++;
                }

                //advance the discount pointer while start date is before the start of the interval or equal
                //because we want to have an interval like discStart <= start <= discEnd
                //it s enough to only check the current discount and not its next neighbour

                while(discountsIdx < discountsSize && discounts.get(discountsIdx).getToDate().compareTo(intervalStart) < 0){
                    discountsIdx++;
                }

                //price calculation
                float basePrice = prices.get(pricesIdx).getPrice();

                //check if discount applies
                float discountPercentage = 0;

                if(discountsIdx < discountsSize) {
                    Discount discount = discounts.get(discountsIdx);
                    if(discount.getFromDate().compareTo(intervalStart) <= 0 && intervalStart.compareTo(discount.getToDate()) < 0){
                        //discount applies
                        discountPercentage = discount.getPercentageDiscount();
                    }
                }


                float finalPrice = basePrice - discountPercentage/100 * basePrice;


                //add a datapoint to the list
                ProductDataPointDTO dto = new ProductDataPointDTO(
                        product.getProductName(),
                        store.getStoreName(),
                        category.map(Category::getCategoryName).orElse("Unknown"),
                        brand.map(Brand::getBrandName).orElse("Unknown"),
                        intervalStart,
                        intervalEnd,
                        finalPrice
                );
                dataPoints.add(dto);
            }
        }

        return dataPoints;
    }
}
