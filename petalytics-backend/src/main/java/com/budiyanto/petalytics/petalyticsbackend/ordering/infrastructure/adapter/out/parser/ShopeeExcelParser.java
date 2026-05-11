package com.budiyanto.petalytics.petalyticsbackend.ordering.infrastructure.adapter.out.parser;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;

import com.budiyanto.petalytics.petalyticsbackend.location.application.port.in.NormalizeLocationUseCase;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.exception.InvalidFileContentException;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.CsvParserPort;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShopeeExcelParser implements CsvParserPort {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final NormalizeLocationUseCase normalizeLocationUseCase;

    @Override
    public Marketplace getSupportedMarketplace() {
        return Marketplace.SHOPEE;
    }

    @Override
    public List<Order> parse(InputStream inputStream) {
        Map<String, Order> orderMap = new LinkedHashMap<>();

        try (ReadableWorkbook wb = new ReadableWorkbook(inputStream)) {
            Sheet sheet = wb.getFirstSheet();
            
            try (Stream<Row> rows = sheet.openStream()) {
                Iterator<Row> rowIterator = rows.iterator();
                if (!rowIterator.hasNext()) return new ArrayList<>();

                // 1. Map header names to their column indexes
                Row headerRow = rowIterator.next();
                Map<String, Integer> headerMap = new HashMap<>();
                for (int i = 0; i < headerRow.getCellCount(); i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell != null && !cell.getText().isBlank()) {
                        headerMap.put(cell.getText().trim(), i);
                    }
                }

                // 2. Iterate through the actual data rows
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getCellCount() == 0) continue; // Skip completely empty rows

                    String orderNo = getCellValue(row, "No. Pesanan", headerMap);
                    if (orderNo.isBlank()) continue;

                    // Find the existing order, or create it if this is the first time we see this OrderNo
                    Order order = orderMap.computeIfAbsent(orderNo, key -> {
                        return Order.create(
                                orderNo,
                                Marketplace.SHOPEE,
                                getDateTimeValue(row, "Waktu Pesanan Dibuat", headerMap),
                                getCellValue(row, "Username (Pembeli)", headerMap),
                                normalizeLocationUseCase.normalizeProvince(getCellValue(row, "Provinsi", headerMap)),
                                normalizeLocationUseCase.normalizeCity(getCellValue(row, "Kota/Kabupaten", headerMap)),
                                getDateTimeValue(row, "Waktu Pesanan Selesai", headerMap)
                        );
                    });

                    // Add the order item to the order
                    order.addOrderItem(
                                getCellValue(row, "Nomor Referensi SKU", headerMap),
                                getCellValue(row, "Nama Produk", headerMap),
                                getNumericValue(row, "Harga Setelah Diskon", headerMap),
                                getNumericValue(row, "Jumlah", headerMap).intValue()
                    );
                }
            }
        } catch (InvalidFileContentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidFileContentException("Failed to parse Shopee Excel file: " + ex.getMessage());
        }

        return new ArrayList<>(orderMap.values());
    }

    // Helper method to safely extract Strings
    private String getCellValue(Row row, String columnName, Map<String, Integer> headerMap) {
        Integer colIndex = getColumnIndex(columnName, headerMap);
        
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        return cell.getText().trim();
    }

    // Helper method to safely extract Dates (Protects against the Excel Date Trap)
    private LocalDateTime getDateTimeValue(Row row, String columnName, Map<String, Integer> headerMap) {
        Integer colIndex = getColumnIndex(columnName, headerMap);
        
        Cell cell = row.getCell(colIndex);
        if (cell == null || cell.getText().isBlank()) return null;
        
        // FastExcel magically handles standard Excel Date serial numbers natively
        if (cell.getType() == CellType.NUMBER) {
            LocalDateTime date = cell.asDate();
            if (date != null) return date;
        }
        
        // Fallback: If Shopee exported it as plain text instead of an Excel Date, parse it manually
        try {
            return LocalDateTime.parse(cell.getText().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new InvalidFileContentException("Unable to parse date in column: " + columnName + " with value: " + cell.getText());
        }
    }

    // Helper method to safely extract raw Numbers (Protects against the Excel Locale Format)
    private BigDecimal getNumericValue(Row row, String columnName, Map<String, Integer> headerMap) {
        Integer colIndex = getColumnIndex(columnName, headerMap);

        Cell cell = row.getCell(colIndex);
        if (cell == null || cell.getText().isBlank()) return BigDecimal.ZERO;

        // FastExcel grabs the pure mathematical value, ignoring any visual "." or "," formatting
        if (cell.getType() == CellType.NUMBER) {
            return cell.asNumber();
        }

        // Fallback: If Shopee exported the number as raw Text, strip the Indonesian thousand separators
        String text = cell.getText().trim().replace(".", "");
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            throw new InvalidFileContentException("Unable to parse number in column: " + columnName + " with value: " + cell.getText());
        }
    }

    private int getColumnIndex(String columnName, Map<String, Integer> headerMap) {
        Integer colIndex = headerMap.get(columnName);
        if (colIndex == null) throw new InvalidFileContentException("Missing required column: " + columnName);
        return colIndex;
    }
}