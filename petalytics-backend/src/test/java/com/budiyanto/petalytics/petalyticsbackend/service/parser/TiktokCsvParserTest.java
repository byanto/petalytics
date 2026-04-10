package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@DisplayName("Tiktok CSV Parser Tests")
class TiktokCsvParserTest {

    private final TiktokCsvParser tiktokCsvParser = new TiktokCsvParser();

    @Test
    @DisplayName("Given a valid Tiktok CSV, when parse, then returns correctly mapped orders")
    void given_validTiktokCsv_when_parse_then_returnsCorrectlyMappedOrders() {
        // Given
        String csvContent = """
            Order ID, Created Time, Product Name, Seller SKU, SKU Subtotal After Discount, Quantity, Buyer Username, Regency and City, Province, Delivered Time
            2601020X4SFPSA, 02/01/2026 12:52:38, AliMaMa - MUKENA ANAK ASYIFA - Mukenah Jersey Jersi Polos, MUKENA ANAK ASYIFA-HITAM, 97800, 2, kakduha02, KOTA MEDAN, SUMATERA UTARA, 07/01/2026 12:52:38
            2601020PYPM3P1, 03/01/2026 10:45:50, AliMaMa Medan - SEGIEMPAT SAUDIA RAWIS - Jilbab Segi4 Saudia, JL0012-SAUDIA-PUTIH #03, 50700,3, ucisalsaafiqa291417, KOTA MEDAN, SUMATERA UTARA, 08/01/2026 10:45:50
            2601020PYPM3P1, 03/01/2026 10:45:50, AliMaMa - BERGO JERSEY UKURAN S M L - Jilbab Hamidah Sport, BERGO JERSEY-PUTIH-M, 27976, 2, ucisalsaafiqa291417, KOTA MEDAN, SUMATERA UTARA, 08/01/2026 10:45:50 
            2601020U0XPS4G, 04/01/2026 11:57:38, AliMaMa - TURBAN DENISA - Hijab Anak Bayi Perempuan, TURBAN DENISA-FANTA, 6990, 1, tarytary123456, KOTA PEKAN BARU, RIAU, 09/01/2026 11:57:38
            2601020U0XPS4G, 04/01/2026 11:57:38, AliMaMa - TURBAN DENISA - Hijab Anak Bayi Perempuan, TURBAN DENISA-MOCCA, 20970, 3, tarytary123456, KOTA PEKAN BARU, RIAU, 09/01/2026 11:57:38
            2601020U0XPS4G, 04/01/2026 11:57:38, AliMaMa - TURBAN PITA POLOS - Hijab Anak Bayi, TURBAN PITA POLOS-COKSU, 12980, 2, tarytary123456, KOTA PEKAN BARU, RIAU, 09/01/2026 11:57:38
            2601020U0XPS4G, 04/01/2026 11:57:38, AliMaMa - TURBAN PITA POLOS - Hijab Anak Bayi, TURBAN PITA POLOS-MAROON, 32450, 5, tarytary123456, KOTA PEKAN BARU, RIAU, 09/01/2026 11:57:38              
        """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // When
        List<Order> orders = tiktokCsvParser.parse(inputStream);

        // Then
        then(orders).hasSize(3);

        Order firstOrder = orders.getFirst();
        then(firstOrder.getOrderNo()).isEqualTo("2601020X4SFPSA");
        then(firstOrder.getShippingCity()).isEqualTo("KOTA MEDAN");
        then(firstOrder.getShippingProvince()).isEqualTo("SUMATERA UTARA");
        then(firstOrder.getUsername()).isEqualTo("kakduha02");
        then(firstOrder.getOrderItems()).hasSize(1);
        then(firstOrder.getOrderItems().getFirst().getSku()).isEqualTo("MUKENA ANAK ASYIFA-HITAM");
        then(firstOrder.getTotalAmount()).isEqualByComparingTo("97800");
        then(firstOrder.getTotalQuantity()).isEqualTo(2);

        Order secondOrder = orders.get(1);
        then(secondOrder.getOrderNo()).isEqualTo("2601020PYPM3P1");
        then(secondOrder.getShippingCity()).isEqualTo("KOTA MEDAN");
        then(secondOrder.getShippingProvince()).isEqualTo("SUMATERA UTARA");
        then(secondOrder.getUsername()).isEqualTo("ucisalsaafiqa291417");
        then(secondOrder.getOrderItems()).hasSize(2);
        then(secondOrder.getOrderItems().getFirst().getSku()).isEqualTo("JL0012-SAUDIA-PUTIH #03");
        then(secondOrder.getTotalAmount()).isEqualByComparingTo("78676");
        then(secondOrder.getTotalQuantity()).isEqualTo(5);

        Order thirdOrder = orders.getLast();
        then(thirdOrder.getOrderNo()).isEqualTo("2601020U0XPS4G");
        then(thirdOrder.getShippingCity()).isEqualTo("KOTA PEKAN BARU");
        then(thirdOrder.getShippingProvince()).isEqualTo("RIAU");
        then(thirdOrder.getUsername()).isEqualTo("tarytary123456");
        then(thirdOrder.getOrderItems()).hasSize(4);
        then(thirdOrder.getOrderItems().getFirst().getSku()).isEqualTo("TURBAN DENISA-FANTA");
        then(thirdOrder.getTotalAmount()).isEqualByComparingTo("73390");
        then(thirdOrder.getTotalQuantity()).isEqualTo(11);
    }

    @Test
    @DisplayName("Given a CSV with missing headers, when parse, then throws IllegalArgumentException")
    void given_csvWithMissingHeaders_when_parse_then_throwsIllegalArgumentException() {
        // Given: A CSV that is missing the "No. Pesanan" column
        String badCsv = """
            WrongColumn, Created Time
            123, 04/01/2026 11:57:38
            """;
        InputStream inputStream = new ByteArrayInputStream(badCsv.getBytes(StandardCharsets.UTF_8));

        // When & Then
        thenThrownBy(() -> tiktokCsvParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing required columns");
    }

    @Test
    @DisplayName("Given a CSV with invalid numbers, when parse, then throws IllegalArgumentException")
    void given_csvWithInvalidNumbers_when_parse_then_throwsIllegalArgumentException() {
        // Given: Harga Setelah Diskon is "FREE" instead of a number
        String badCsv = """
            Order ID, Created Time, Product Name, Seller SKU, SKU Subtotal After Discount, Quantity, Buyer Username, Regency and City, Province, Delivered Time
            2601020X4SFPSA, 02/01/2026 12:52:38, Product A, SKU 1, FREE, 2, kakduha02, KOTA MEDAN, SUMATERA UTARA, 07/01/2026 12:52:38
            """;
        InputStream inputStream = new ByteArrayInputStream(badCsv.getBytes(StandardCharsets.UTF_8));

        // When & Then
        thenThrownBy(() -> tiktokCsvParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to parse numbers");
    }

    @Test
    @DisplayName("Given a CSV with invalid date, when parse, then throws IllegalArgumentException")
    void given_csvWithInvalidDate_when_parse_then_throwsIllegalArgumentException() {
        // Given: Waktu Pesanan Dibuat has a wrong date format
        String badCsv = """
            Order ID, Created Time, Seller SKU, Product Name, SKU Subtotal After Discount, Quantity
            123, 2026.01.02 12.52, SKU1, Product A, 12900, 2
            """;
        InputStream inputStream = new ByteArrayInputStream(badCsv.getBytes(StandardCharsets.UTF_8));

        // When & Then
        thenThrownBy(() -> tiktokCsvParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to parse dates");
    }

}

