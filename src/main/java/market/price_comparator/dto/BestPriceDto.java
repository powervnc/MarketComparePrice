package market.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BestPriceDto {
    private String productName;
    private String storeName;
    private float unitPrice;
    private String currency;
    private String priceDate;
}
