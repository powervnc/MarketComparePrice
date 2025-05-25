package market.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BestProductDto {
    private String productName;
    private String storeName;
    private float price;
    private String currency;
    private String priceDate;
}