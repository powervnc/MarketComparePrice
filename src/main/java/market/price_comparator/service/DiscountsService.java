package market.price_comparator.service;

import market.price_comparator.model.Discount;
import market.price_comparator.repo.DiscountRepository;
import market.price_comparator.repo.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DiscountsService {
    @Autowired
    DiscountRepository discountRepository;
    @Autowired
    PriceRepository priceRepository;


    private Date getTodayDate(){
        Date today = new Date();
        return today;
    }

    public List<Discount> getNewDiscounts(Date now){
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, -24);
        Date twentyFourHoursAgo = cal.getTime();
        List<Discount> discounts = discountRepository.findByFromDateBetween(twentyFourHoursAgo, now);
        return discounts;
    }

    public Discount findMostRecentDiscount(String productId, String storeId, Date targetDate) {
        Pageable pageable = Pageable.ofSize(1);
        List<Discount> discounts = discountRepository.findMostRecentDiscount(storeId, productId, targetDate, pageable);
        return discounts.isEmpty() ? null : discounts.get(0);
    }

    public List<Discount> findBestDiscounts(Date date){
        return discountRepository.findMaxDiscountsByDate(date);
    }


}
