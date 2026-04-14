package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.service.LocationNormalizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@DisplayName("Shopee CSV Parser Tests")
@ExtendWith(MockitoExtension.class)
class ShopeeCsvParserTest {

    @Mock
    private LocationNormalizerService locationNormalizer;

    @InjectMocks
    private ShopeeExcelParser shopeeExcelParser;

    @Test
    @DisplayName("Given a valid Shopee CSV, when parse, then returns correctly mapped orders")
    void given_validShopeeCsv_when_parse_then_returnsCorrectlyMappedOrders() {
        // Given
        InputStream inputStream = getClass().getResourceAsStream("/shopee-dummy-data.xlsx");
        then(inputStream).as("Dummy Excel file not found in src/test/resources!").isNotNull();

        given(locationNormalizer.normalizeProvince(any())).willAnswer(returnsFirstArg());
        given(locationNormalizer.normalizeCity(any())).willAnswer(returnsFirstArg());

        // When
        List<Order> orders = shopeeExcelParser.parse(inputStream);

        // Then
        then(orders).hasSize(3);

        Order firstOrder = orders.getFirst();
        then(firstOrder.getOrderNo()).isEqualTo("2601020X4SFPSA");
        then(firstOrder.getCity()).isEqualTo("KOTA MEDAN");
        then(firstOrder.getProvince()).isEqualTo("SUMATERA UTARA");
        then(firstOrder.getUsername()).isEqualTo("kakduha02");
        then(firstOrder.getOrderItems()).hasSize(1);
        then(firstOrder.getOrderItems().getFirst().getSku()).isEqualTo("MUKENA ANAK ASYIFA-HITAM");
        then(firstOrder.getTotalAmount()).isEqualByComparingTo("97800");
        then(firstOrder.getTotalQuantity()).isEqualTo(2);

        Order secondOrder = orders.get(1);
        then(secondOrder.getOrderNo()).isEqualTo("2601020PYPM3P1");
        then(secondOrder.getCity()).isEqualTo("KOTA MEDAN");
        then(secondOrder.getProvince()).isEqualTo("SUMATERA UTARA");
        then(secondOrder.getUsername()).isEqualTo("ucisalsaafiqa291417");
        then(secondOrder.getOrderItems()).hasSize(2);
        then(secondOrder.getOrderItems().getFirst().getSku()).isEqualTo("JL0012-SAUDIA-PUTIH #03");
        then(secondOrder.getTotalAmount()).isEqualByComparingTo("78676");
        then(secondOrder.getTotalQuantity()).isEqualTo(5);

        Order thirdOrder = orders.getLast();
        then(thirdOrder.getOrderNo()).isEqualTo("2601020U0XPS4G");
        then(thirdOrder.getCity()).isEqualTo("KOTA PEKAN BARU");
        then(thirdOrder.getProvince()).isEqualTo("RIAU");
        then(thirdOrder.getUsername()).isEqualTo("tarytary123456");
        then(thirdOrder.getOrderItems()).hasSize(4);
        then(thirdOrder.getOrderItems().getFirst().getSku()).isEqualTo("TURBAN DENISA-FANTA");
        then(thirdOrder.getTotalAmount()).isEqualByComparingTo("74390");
        then(thirdOrder.getTotalQuantity()).isEqualTo(11);
    }

    @Test
    @DisplayName("Given a xlsx file with missing headers, when parse, then throws IllegalArgumentException")
    void given_xlsxWithMissingHeaders_when_parse_then_throwsIllegalArgumentException() {
        // Given: A xlsx file that is missing the "No. Pesanan" column
        InputStream inputStream = getClass().getResourceAsStream("/shopee-missing-headers.xlsx");
        then(inputStream).as("Missing Headers Excel file not found in src/test/resources!").isNotNull();

        // When & Then
        thenThrownBy(() -> shopeeExcelParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing required column");
    }

    @Test
    @DisplayName("Given a xlsx file with invalid numbers, when parse, then throws IllegalArgumentException")
    void given_xlsxWithInvalidNumbers_when_parse_then_throwsIllegalArgumentException() {
        // Given: Harga Setelah Diskon is "FREE" instead of a number
        InputStream inputStream = getClass().getResourceAsStream("/shopee-invalid-numbers.xlsx");
        then(inputStream).as("Invalid Numbers Excel file not found in src/test/resources!").isNotNull();

        // When & Then
        thenThrownBy(() -> shopeeExcelParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to parse number");
    }

    @Test
    @DisplayName("Given a xlsx file with invalid date, when parse, then throws IllegalArgumentException")
    void given_xlsxWithInvalidDate_when_parse_then_throwsIllegalArgumentException() {
        // Given: Waktu Pesanan Dibuat has a wrong date format
        InputStream inputStream = getClass().getResourceAsStream("/shopee-invalid-date.xlsx");
        then(inputStream).as("Invalid Date Excel file not found in src/test/resources!").isNotNull();

        // When & Then
        thenThrownBy(() -> shopeeExcelParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unable to parse date");
    }

}
