package market.price_comparator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PriceAlertDto {
    private String productName;
    private String storeName;
    private float value;
    private String currency;

    private String currentDate;


    public String getProductName() {
        return productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public float getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrentDate() {
        return currentDate;
    }
}
