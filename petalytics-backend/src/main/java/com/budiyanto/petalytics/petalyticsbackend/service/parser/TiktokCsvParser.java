package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.RoundingMode;
import org.apache.commons.io.input.BOMInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TiktokCsvParser implements MarketplaceCsvParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public Marketplace getSupportedMarketplace() {
        return Marketplace.TIKTOK;
    }

    @Override
    public List<Order> parse(InputStream inputStream) {
        Map<String, Order> orderMap = new LinkedHashMap<>();

        // We wrap the inputStream in the BOMInputStream before giving it to the reader
        try (BOMInputStream bomInputStream = BOMInputStream.builder().setInputStream(inputStream).get();
             BufferedReader reader = new BufferedReader(new InputStreamReader(bomInputStream, StandardCharsets.UTF_8))) {
             
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .get()
                    .parse(reader);

            for (CSVRecord record : csvParser) {
                String orderNo = record.get("Order ID");

                // Find the existing order, or create it if this is the first time we see this OrderNo
                Order order = orderMap.computeIfAbsent(orderNo, key -> {
                    return Order.create(
                            orderNo,
                            Marketplace.TIKTOK,
                            LocalDateTime.parse(record.get("Created Time"), DATE_FORMATTER),
                            record.get("Buyer Username"),
                            record.get("Province"),
                            record.get("Regency and City"),
                            LocalDateTime.parse(record.get("Delivered Time"), DATE_FORMATTER)
                    );
                });

                int quantity = Integer.parseInt(record.get("Quantity"));
                
                // Defensive Currency Math: Handle non-terminating decimals and divide-by-zero
                BigDecimal subtotal = new BigDecimal(record.get("SKU Subtotal After Discount"));
                BigDecimal price = quantity > 0 
                        ? subtotal.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP) 
                        : subtotal;

                // Add the order item to the order
                order.addOrderItem(
                        record.get("Seller SKU"),
                        record.get("Product Name"),
                        price,
                        quantity
                );

            }
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid data format in Tiktok CSV: Unable to parse dates.", ex);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid data format in Tiktok CSV: Unable to parse numbers.", ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Tiktok CSV format: Missing required columns: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse Tiktok CSV data", ex);
        }

        return new ArrayList<>(orderMap.values());
    }
}