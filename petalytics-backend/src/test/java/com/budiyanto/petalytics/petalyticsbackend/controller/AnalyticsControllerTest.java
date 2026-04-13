package com.budiyanto.petalytics.petalyticsbackend.controller;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.service.AnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@DisplayName("Analytics Controller Tests")
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @Nested
    @DisplayName("Location Analytics")
    class LocationAnalytics {

        @Test
        @DisplayName("Given request for summary of all location, when retrieved data, then return 200 Ok and data")
        void  given_requestForAllLocationSummary_when_retrievedData_then_returnOkAndData() throws Exception {
            // Given
            LocationSummaryDto summary1 = new LocationSummaryDto("Sumatera Selatan", "Kota Palembang", 20L, new BigDecimal("300"));
            LocationSummaryDto summary2 = new LocationSummaryDto("Sumatera Utara", "Kota Medan", 10L, new BigDecimal("200"));

            given(analyticsService.retrieveOrderSummaryByLocation()).willReturn(List.of(summary1, summary2));

            // When & Then
            mockMvc.perform(get("/api/analytics/location-summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].province").value("Sumatera Selatan"))
                    .andExpect(jsonPath("$[0].city").value("Kota Palembang"))
                    .andExpect(jsonPath("$[0].totalOrders").value(20))
                    .andExpect(jsonPath("$[0].totalRevenue").value(300));
        }
    }

    @Nested
    @DisplayName("Channel Analytics")
    class ChannelAnalytics {
        @Test
        @DisplayName("Given request for summary of all channels, when retrieved data, then return 200 Ok and data")
        void  given_requestForAllChannelSummary_when_retrievedData_then_returnOkAndData() throws Exception {
            // Given
            ChannelSummaryDto summary1 = new ChannelSummaryDto(Marketplace.SHOPEE, 15L, new BigDecimal("250"));
            ChannelSummaryDto summary2 = new ChannelSummaryDto(Marketplace.TIKTOK, 25L, new BigDecimal("500"));

            given(analyticsService.retrieveOrderSummaryByChannel()).willReturn(List.of(summary1, summary2));

            // When & Then
            mockMvc.perform(get("/api/analytics/channel-summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].marketplace").value(Marketplace.SHOPEE.name()))
                    .andExpect(jsonPath("$[0].totalOrders").value(15))
                    .andExpect(jsonPath("$[0].totalRevenue").value(250));
        }
    }

}
