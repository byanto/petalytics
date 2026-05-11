package com.budiyanto.petalytics.petalyticsbackend.ordering.infrastructure.adapter.out.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Component;

import com.budiyanto.petalytics.petalyticsbackend.location.application.port.in.NormalizeLocationUseCase;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.exception.InvalidFileContentException;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.CsvParserPort;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TiktokCsvParser implements CsvParserPort {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final NormalizeLocationUseCase normalizeLocationUseCase;

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
                            normalizeLocationUseCase.normalizeProvince(record.get("Province")),
                            normalizeLocationUseCase.normalizeCity(record.get("Regency and City")),
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
            throw new InvalidFileContentException("Invalid Tiktok CSV format: Unable to parse date: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            throw new InvalidFileContentException("Invalid Tiktok CSV format: Unable to parse number: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFileContentException("Invalid Tiktok CSV format: Missing required column: " + ex.getMessage());
        } catch (Exception ex) {
            throw new InvalidFileContentException("Failed to parse Tiktok CSV data: " + ex.getMessage());
        }

        return new ArrayList<>(orderMap.values());
    }
}