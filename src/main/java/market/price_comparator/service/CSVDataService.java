package market.price_comparator.service;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import market.price_comparator.model.*;
import market.price_comparator.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
brand inserted separately
categories inserted separately
shops inserted separately
*/
@Service
public class CSVDataService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    PriceRepository priceRepository;
    @Autowired
    AccountedFilesRepository accountedFilesRepository;
    @Autowired
    DiscountRepository discountRepository;

    final private String categoriesSourceFileName = "\\setup\\categories.csv";
    final private String storesSourceFileName = "\\setup\\stores.csv";
    final private String brandsSourceFileName = "\\setup\\brands.csv";

    private void insertCategories() throws Exception{
        categoryRepository.deleteAll();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(categoriesSourceFileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in classpath: " + categoriesSourceFileName);
        }

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()){
            String[] line;
            while ((line = reader.readNext()) != null) {
                Category category = new Category();
                category.setCategoryName(line[0]);
                categoryRepository.save(category);
            }
        }
    }

    private void insertStores() throws Exception{
        storeRepository.deleteAll();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(storesSourceFileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in classpath: " + storesSourceFileName);
        }

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()){
            String[] line;
            while ((line = reader.readNext()) != null) {
                Store store = new Store();
                store.setStoreName(line[0]);
                storeRepository.save(store);
            }
        }
    }

    private void insertBrands() throws Exception{
        brandRepository.deleteAll();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(brandsSourceFileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in classpath: " + brandsSourceFileName);
        }

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()){
            String[] line;
            while ((line = reader.readNext()) != null) {
                Brand brand = new Brand();
                brand.setBrandName(line[0]);
                brandRepository.save(brand);
            }
        }
    }

    public void setUp() throws Exception{

        if(accountedFilesRepository.findByFileName(storesSourceFileName) == null){
            insertStores();

            AccountedFiles file = new AccountedFiles();
            file.setFileName( storesSourceFileName);
            accountedFilesRepository.save(file);
        }

        if(accountedFilesRepository.findByFileName(categoriesSourceFileName) == null){
            insertCategories();

            AccountedFiles file = new AccountedFiles();
            file.setFileName(categoriesSourceFileName);
            accountedFilesRepository.save(file);
        }

        if(accountedFilesRepository.findByFileName(brandsSourceFileName) == null){
            insertBrands();

            AccountedFiles file = new AccountedFiles();
            file.setFileName(brandsSourceFileName);
            accountedFilesRepository.save(file);
        }

    }

    public void importFromProductCSV(String fileName) throws Exception {

        if(accountedFilesRepository.findByFileName(fileName) != null) return;

        //product_id;product_name;product_category;brand;package_quantity;package_unit;price;currency


        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in classpath: " + fileName);
        }
        String fullFileName = fileName.replaceFirst("^.*[\\\\/]", "");
        String fileNameWithoutExt = fullFileName.replaceFirst("[.][^.]+$", "");
        String[] parts = fileNameWithoutExt.split("_");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid file name format. Expected: store_yyyy_MM_dd.csv");
        }

        String storeName = parts[0];
        Store store = storeRepository.findByStoreName(storeName);
        if (store == null) {
            throw new IllegalArgumentException(String.format("Unknown or invalid store name: %s", storeName));
        }
        String storeId = store.getStoreId();

        String dateString = parts[1] + "-" + parts[2] + "-" + parts[3]; // e.g., "2025-05-01"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date when = format.parse(dateString);


        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                String productId = line[0];
                String productName = line[1];
                String categoryName = line[2];

                Category category = categoryRepository.findByCategoryName(categoryName);
                if (category == null) {
                    throw new IllegalArgumentException(String.format("Unknown or invalid category name: %s", categoryName));
                }

                String brandName  = line[3];

                Brand brand = brandRepository.findByBrandName(brandName);
                if (brand == null) {
                    throw new IllegalArgumentException(String.format("Unknown or invalid brand name: %s", brandName));
                }

                Float packageQuantity = Float.parseFloat(line[4]);
                String packageUnit = line[5];
                float priceValue = Float.parseFloat(line[6]);
                String currency = line[7];

                Product product = new Product(productId,productName, packageQuantity, packageUnit, category.getCategoryId(), brand.getBrandId());
                Product insertedProduct = productRepository.save(product);

                Price price = new Price();
                price.setProductId(insertedProduct.getProductId());
                price.setStoreId(storeId);
                price.setPrice(priceValue);
                price.setCurrency(currency);
                price.setPriceDate(when);

                priceRepository.save(price);
            }

        }
        AccountedFiles accountedFile = new AccountedFiles();
        accountedFile.setFileName(fileName);
        accountedFilesRepository.save(accountedFile);
    }

//    public void importFromDiscountCSV(String fileName) throws Exception {
//
//        if(accountedFilesRepository.findByFileName(fileName) != null) return;
//
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
//        if (inputStream == null) {
//            throw new IllegalArgumentException("File not found in classpath: " + fileName);
//        }
//
//        String fileNameWithoutExt = fileName.replaceFirst("[.][^.]+$", "");
//        String[] parts = fileNameWithoutExt.split("_");
//        if (parts.length < 5) {
//            throw new IllegalArgumentException("Invalid file name format. Expected: store_discounts_yyyy_MM_dd.csv");
//        }
//
//        String storeName = parts[0];
//        Store store = storeRepository.findByStoreName(storeName);
//        if (store == null) {
//            throw new IllegalArgumentException(String.format("Unknown or invalid store name: %s", storeName));
//        }
//        String storeId = store.getStoreId();
//
//        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
//                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
//                .build()) {
//
//            String[] line;
//            reader.readNext();
//
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//
//            while ((line = reader.readNext()) != null) {
//                String productId = line[0];
//                String productName = line[1];
//                String brandName = line[2];
//                Float packageQuantity = Float.parseFloat(line[3]);
//                String packageUnit = line[4];
//                String categoryName = line[5];
//                Date fromDate = format.parse(line[6]);
//                Date toDate = format.parse(line[7]);
//                int percentageDiscount = Integer.parseInt(line[8]);
//
//                Category category = categoryRepository.findByCategoryName(categoryName);
//                if (category == null) {
//                    throw new IllegalArgumentException(String.format("Unknown or invalid category name: %s", categoryName));
//                }
//
//                Brand brand = brandRepository.findByBrandName(brandName);
//                if (brand == null) {
//                    throw new IllegalArgumentException(String.format("Unknown or invalid brand name: %s", brandName));
//                }
//
//                Product product = productRepository.findById(productId).orElse(null);
//                if (product == null) {
//                    product = new Product(productId, productName, packageQuantity, packageUnit, category.getCategoryId(), brand.getBrandId());
//                    productRepository.save(product);
//                }
//
//                Discount discount = new Discount();
//                discount.setProductId(product.getProductId());
//                discount.setStoreId(storeId);
//                discount.setFromDate(fromDate);
//                discount.setToDate(toDate);
//                discount.setPercentageDiscount(percentageDiscount);
//
//                discountRepository.save(discount);
//            }
//        }
//    }

    private void addDiscountWithConflictResolution(
            String productId,
            String storeId,
            Date fromDate,
            Date toDate,
            int percentageDiscount
    ) throws Exception {
        // 1. Delete any discounts that start on the same day
        List<Discount> sameStartDiscounts =
                discountRepository.findByProductIdAndStoreIdAndFromDate(productId, storeId, fromDate);
        for (Discount discount : sameStartDiscounts) {
            discountRepository.delete(discount);
        }

        // 2. Shorten any discounts that end on the same day as the new discount starts
        List<Discount> sameEndDiscounts =
                discountRepository.findByProductIdAndStoreIdAndToDate(productId, storeId, fromDate);
        for (Discount discount : sameEndDiscounts) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            cal.add(Calendar.DATE, -1); // one day before new discount starts
            Date newEndDate = cal.getTime();

            // Only update if the new end date is still valid
            if (!discount.getFromDate().after(newEndDate)) {
                discount.setToDate(newEndDate);
                discountRepository.save(discount);

                //throw exception?
                throw new Exception("There are overlapping discounts");
            }
        }

        // 3. Save the new discount
        Discount newDiscount = new Discount();
        newDiscount.setProductId(productId);
        newDiscount.setStoreId(storeId);
        newDiscount.setFromDate(fromDate);
        newDiscount.setToDate(toDate);
        newDiscount.setPercentageDiscount(percentageDiscount);

        discountRepository.save(newDiscount);
    }


    public void importFromDiscountCSV(String fileName) throws Exception {

        if(accountedFilesRepository.findByFileName(fileName) != null) return;

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in classpath: " + fileName);
        }
        String fullFileName = fileName.replaceFirst("^.*[\\\\/]", "");
        String fileNameWithoutExt = fullFileName.replaceFirst("[.][^.]+$", "");
        String[] parts = fileNameWithoutExt.split("_");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid file name format. Expected: store_discounts_yyyy_MM_dd.csv");
        }

        String storeName = parts[0];
        Store store = storeRepository.findByStoreName(storeName);
        if (store == null) {
            throw new IllegalArgumentException(String.format("Unknown or invalid store name: %s", storeName));
        }
        String storeId = store.getStoreId();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            String[] line;
            reader.readNext();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            while ((line = reader.readNext()) != null) {
                String productId = line[0];
                String productName = line[1];
                String brandName = line[2];
                Float packageQuantity = Float.parseFloat(line[3]);
                String packageUnit = line[4];
                String categoryName = line[5];
                Date fromDate = format.parse(line[6]);
                Date toDate = format.parse(line[7]);
                int percentageDiscount = Integer.parseInt(line[8]);

                Category category = categoryRepository.findByCategoryName(categoryName);
                if (category == null) {
                    throw new IllegalArgumentException(String.format("Unknown or invalid category name: %s", categoryName));
                }

                Brand brand = brandRepository.findByBrandName(brandName);
                if (brand == null) {
                    throw new IllegalArgumentException(String.format("Unknown or invalid brand name: %s", brandName));
                }

                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) {
                    product = new Product(productId, productName, packageQuantity, packageUnit, category.getCategoryId(), brand.getBrandId());
                    productRepository.save(product);
                }
//
//                // Find existing discounts overlapping with the new discount
//                List<Discount> overlappingDiscounts = discountRepository.findOverlappingDiscounts(
//                        product.getProductId(),
//                        storeId,
//                        fromDate,
//                        toDate
//                );
//
//                for (Discount existingDiscount : overlappingDiscounts) {
//                    // If existing discount starts before new discount and overlaps
//                    if (existingDiscount.getFromDate().before(fromDate) &&
//                            !existingDiscount.getToDate().before(fromDate)) {
//
//                        // Set existing discount end date to one day before new discount starts
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(fromDate);
//                        cal.add(Calendar.DATE, -1);
//                        Date newEndDate = cal.getTime();
//
//                        // Only update if it doesn't create invalid range
//                        if (!existingDiscount.getFromDate().after(newEndDate)) {
//                            existingDiscount.setToDate(newEndDate);
//                            discountRepository.save(existingDiscount);
//                        }
//                    }
//                }
//
//                Discount discount = new Discount();
//                discount.setProductId(product.getProductId());
//                discount.setStoreId(storeId);
//                discount.setFromDate(fromDate);
//                discount.setToDate(toDate);
//                discount.setPercentageDiscount(percentageDiscount);
//
//                discountRepository.save(discount);
                addDiscountWithConflictResolution(productId, storeId, fromDate, toDate, percentageDiscount);
            }
        }
        AccountedFiles accountedFile = new AccountedFiles();
        accountedFile.setFileName(fileName);
        accountedFilesRepository.save(accountedFile);
    }


}
