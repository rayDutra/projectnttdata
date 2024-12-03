package com.nttdata.application.service;

import com.nttdata.application.response.ExchangeRateResponse;
import com.nttdata.domain.entity.CurrencyBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConversionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyConversionService.apiUrlTemplate = "http://mock-api.com?apiKey=%s&base=%s";
        currencyConversionService.apiKey = "testKey";
    }

    @Test
    void testGetExchangeRates_Success() {
        String baseCurrency = "USD";
        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put("EUR", 0.85);
        mockRates.put("JPY", 110.0);
        mockRates.put("BRL", 5.0);

        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setConversionRates(mockRates);

        String apiUrl = String.format(currencyConversionService.apiUrlTemplate, currencyConversionService.apiKey, baseCurrency);
        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class))
            .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        Map<String, Double> rates = currencyConversionService.getExchangeRates(baseCurrency);

        assertEquals(3, rates.size());
        assertEquals(0.85, rates.get("EUR"));
        assertEquals(110.0, rates.get("JPY"));
        assertEquals(5.0, rates.get("BRL"));
        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class);
    }

    @Test
    void testGetExchangeRates_ApiError() {
        String baseCurrency = "USD";
        String apiUrl = String.format(currencyConversionService.apiUrlTemplate, currencyConversionService.apiKey, baseCurrency);
        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class))
            .thenThrow(new RuntimeException("API error"));

        Map<String, Double> rates = currencyConversionService.getExchangeRates(baseCurrency);

        assertTrue(rates.isEmpty());
        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class);
    }

    @Test
    void testConvertToCurrencyBalance_Success() {
        String baseCurrency = "BRL";
        Double balance = 100.0;

        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put("EUR", 0.85);
        mockRates.put("JPY", 110.0);
        mockRates.put("BRL", 5.0);

        doReturn(mockRates).when(currencyConversionService).getExchangeRates(baseCurrency);

        CurrencyBalance result = currencyConversionService.convertToCurrencyBalance(balance, baseCurrency);

        assertNotNull(result);
        assertEquals("€ 117,65", result.getBalanceEuro());
        assertEquals("$ 100,00", result.getBalanceDolar());
        assertEquals("R$ 20,00", result.getBalanceReal());
        assertEquals("¥ 0,91", result.getBalanceIenes());
    }
    @Test
    void testGetExchangeRates_ApiError_Handling() {
        String baseCurrency = "USD";
        String apiUrl = String.format(currencyConversionService.apiUrlTemplate, currencyConversionService.apiKey, baseCurrency);

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class))
            .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        Map<String, Double> rates = currencyConversionService.getExchangeRates(baseCurrency);

        assertTrue(rates.isEmpty(), "Esperado um mapa vazio devido a erro da API");

        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class);
    }


}
