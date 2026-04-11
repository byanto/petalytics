package com.budiyanto.petalytics.petalyticsbackend.controller;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.service.OrderIngestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderIngestionController.class)
@DisplayName("Order Ingestion Controller Tests")
class OrderIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderIngestionService orderIngestionService;

    @Nested
    @DisplayName("Ingest Tests")
    class Ingest {

        @Test
        @DisplayName("Given a valid CSV file and marketplace, when uploaded, then returns 200 OK")
        void given_validCsvFileAndMarketplace_when_uploaded_then_returnsOk() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "shopee_orders.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    "dummy csv".getBytes()
            );

            // When
            doNothing().when(orderIngestionService).ingest(any(), eq(Marketplace.SHOPEE));

            // Then
            mockMvc.perform(multipart("/api/ingestion/upload")
                        .file(file)
                        .param("marketplace", "SHOPEE"))
                    .andExpect(status().isOk());

            verify(orderIngestionService).ingest(any(), eq(Marketplace.SHOPEE));
        }

        @Test
        @DisplayName("Given an invalid marketplace parameter, when uploaded, then returns 400 Bad Request")
        void given_invalidMarketplace_when_uploaded_then_returnsBadRequest() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "shopee_orders.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    "dummy csv".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/ingestion/upload")
                            .file(file)
                            .param("marketplace", "INVALID_MARKETPLACE"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given a missing marketplace parameter, when uploaded, then returns 400 Bad Request")
        void given_missingMarketplace_when_uploaded_then_returnsBadRequest() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "shopee_orders.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    "dummy csv".getBytes()
            );

            // When & Then
            mockMvc.perform(multipart("/api/ingestion/upload")
                            .file(file))
                    .andExpect(status().isBadRequest());
        }
    }
}
