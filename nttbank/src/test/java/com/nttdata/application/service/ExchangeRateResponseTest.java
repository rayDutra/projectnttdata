package com.nttdata.application.service;

import com.nttdata.web.response.ExchangeRateResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExchangeRateResponseTest {

    @Test
    public void testSetAndGetResult() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setResult("success");
        assertEquals("success", response.getResult());
    }

    @Test
    public void testSetAndGetBaseCode() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setBaseCode("USD");
        assertEquals("USD", response.getBaseCode());
    }

    @Test
    public void testSetAndGetConversionRates() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.75);

        response.setConversionRates(rates);
        assertNotNull(response.getConversionRates());
        assertEquals(2, response.getConversionRates().size());
        assertEquals(0.85, response.getConversionRates().get("EUR"));
        assertEquals(0.75, response.getConversionRates().get("GBP"));
    }
}

