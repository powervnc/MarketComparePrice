package market.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDataPointDTO {
    private String productName;
    private String storeName;
    private String categoryName;
    private String brandName;
    private Date fromDate;
    private Date toDate;
    private float price;
}
