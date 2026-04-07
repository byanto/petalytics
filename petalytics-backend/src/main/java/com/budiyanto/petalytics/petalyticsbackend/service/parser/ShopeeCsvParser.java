package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.service.parser.MarketplaceCsvParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ShopeeCsvParser implements MarketplaceCsvParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public Marketplace getSupportedMarketplace() {
        return Marketplace.SHOPEE;
    }

    @Override
    public List<Order> parse(InputStream inputStream) {
        Map<String, Order> orderMap = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .get()
                    .parse(reader);

            for (CSVRecord record : csvParser) {
                String orderNo = record.get("No. Pesanan");

                // Find the existing order, or create it if this is the first time we see this OrderNo
                Order order = orderMap.computeIfAbsent(orderNo, key -> {
                    return Order.create(
                            orderNo,
                            Marketplace.SHOPEE,
                            LocalDateTime.parse(record.get("Waktu Pesanan Dibuat"), DATE_FORMATTER),
                            record.get("Username (Pembeli)"),
                            record.get("Provinsi"),
                            record.get("Kota/Kabupaten"),
                            LocalDateTime.parse(record.get("Waktu Pesanan Selesai"), DATE_FORMATTER)
                    );
                });

                // Add the order item to the order
                order.addOrderItem(
                        record.get("Nomor Referensi SKU"),
                        record.get("Nama Produk"),
                        new BigDecimal(record.get("Harga Setelah Diskon")),
                        Integer.parseInt(record.get("Jumlah"))
                );

            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Shopee CSV data", e);
        }

        return new ArrayList<>(orderMap.values());
    }
}