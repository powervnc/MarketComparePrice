package market.price_comparator;

import market.price_comparator.service.CSVDataService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Component
public class StartupInitializer implements ApplicationRunner {

    @Autowired
    private CSVDataService csvDataService;
    private static final String productsFolder = "\\prices\\";
    private static final String discountsFolder = "\\discounts\\";
    private static final List<String> productFileNames = List.of("lidl_2025_05_01.csv", "profi_2025_05_25.csv", "lidl_2025_05_25.csv");
    private static final List<String> discountFileNames = List.of("lidl_discounts_2025_05_01.csv", "profi_discounts_2025_05_25.csv", "profi_discounts_2025_05_20.csv");

    @Override
    public void run(ApplicationArguments args) {
        try {
            csvDataService.setUp();
            for (String fileName : productFileNames) {
                csvDataService.importFromProductCSV(productsFolder + fileName);
            }
            for (String fileName : discountFileNames) {
                csvDataService.importFromDiscountCSV(discountsFolder + fileName);
            }

        } catch (Exception e) {
            System.err.println("Failed during startup CSV import:");
            e.printStackTrace();
        }
    }
}
