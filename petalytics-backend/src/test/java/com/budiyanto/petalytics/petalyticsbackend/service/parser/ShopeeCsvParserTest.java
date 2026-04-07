package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shopee CSV Parser Tests")
class ShopeeCsvParserTest {

    private final ShopeeCsvParser shopeeCsvParser = new ShopeeCsvParser();

    @Test
    @DisplayName("Given a valid Shopee CSV, when parse, then returns correctly mapped orders")
    void given_validShopeeCsv_when_parse_then_returnsCorrectlyMappedOrders() {
        // Given
        String csvContent = """
            No. Pesanan, Waktu Pesanan Dibuat, Nama Produk, Nomor Referensi SKU, Harga Setelah Diskon, Jumlah, Username (Pembeli), Kota/Kabupaten, Provinsi, Waktu Pesanan Selesai
            2601020X4SFPSA, 2026-01-02 12:52, AliMaMa - MUKENA ANAK ASYIFA - Mukenah Jersey Jersi Polos, MUKENA ANAK ASYIFA-HITAM, 48900, 2, kakduha02, KOTA MEDAN, SUMATERA UTARA, 2026-01-02 15:42
            2601020PYPM3P1, 2026-01-02 10:45, AliMaMa Medan - SEGIEMPAT SAUDIA RAWIS - Jilbab Segi4 Saudia, JL0012-SAUDIA-PUTIH #03, 16900,3, ucisalsaafiqa291417, KOTA MEDAN, SUMATERA UTARA, 2026-01-02 19:33
            2601020PYPM3P1, 2026-01-02 10:45, AliMaMa - BERGO JERSEY UKURAN S M L - Jilbab Hamidah Sport, BERGO JERSEY-PUTIH-M, 13988, 2, ucisalsaafiqa291417, KOTA MEDAN, SUMATERA UTARA, 2026-01-02 19:33 
            2601020U0XPS4G, 2026-01-02 11:57, AliMaMa - TURBAN DENISA - Hijab Anak Bayi Perempuan, TURBAN DENISA-FANTA, 6990, 1, tarytary123456, KOTA PEKAN BARU, RIAU, 2026-01-03 12:18
            2601020U0XPS4G, 2026-01-02 11:57, AliMaMa - TURBAN DENISA - Hijab Anak Bayi Perempuan, TURBAN DENISA-MOCCA, 6990, 3, tarytary123456, KOTA PEKAN BARU, RIAU, 2026-01-03 12:18
            2601020U0XPS4G, 2026-01-02 11:57, AliMaMa - TURBAN PITA POLOS - Hijab Anak Bayi, TURBAN PITA POLOS-COKSU, 6490, 2, tarytary123456, KOTA PEKAN BARU, RIAU, 2026-01-03 12:18
            2601020U0XPS4G, 2026-01-02 11:57, AliMaMa - TURBAN PITA POLOS - Hijab Anak Bayi, TURBAN PITA POLOS-MAROON, 6490, 5, tarytary123456, KOTA PEKAN BARU, RIAU, 2026-01-03 12:18                
        """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // When
        List<Order> orders = shopeeCsvParser.parse(inputStream);

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

}

