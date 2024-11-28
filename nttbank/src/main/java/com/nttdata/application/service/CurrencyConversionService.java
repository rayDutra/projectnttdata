package com.nttdata.application.service;

import com.nttdata.domain.entity.CurrencyBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class CurrencyConversionService {

    private static final String API_URL_TEMPLATE = "https://v6.exchangerate-api.com/v6/27091f71d355f60163ee9ecb/latest/%s";
    private final RestTemplate restTemplate;

    @Autowired
    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> getExchangeRates(String baseCurrency) {
        try {
            String apiUrl = String.format(API_URL_TEMPLATE, baseCurrency);
            ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getConversionRates();
            } else {
                throw new IllegalArgumentException("Erro ao acessar API de taxas de câmbio.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter taxas de câmbio: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public CurrencyBalance convertToCurrencyBalance(Double balance, String baseCurrency) {
        Map<String, Double> rates = getExchangeRates(baseCurrency);

        if (rates.isEmpty()) {
            throw new IllegalArgumentException("Falha ao obter taxas de câmbio para a moeda base: " + baseCurrency);
        }

        Double rateEuro = rates.get("EUR");
        Double rateUsd = rates.get("USD");
        Double rateJpy = rates.get("JPY");
        Double rateBrl = rates.get("BRL");

        if (rateEuro == null || rateUsd == null || rateJpy == null || rateBrl == null) {
            throw new IllegalArgumentException("Taxas de câmbio incompletas retornadas pela API.");
        }

        Double balanceEuro = Math.round((balance / rateEuro) * 100.0) / 100.0;
        Double balanceUsd = Math.round((balance / rateUsd) * 100.0) / 100.0;
        Double balanceJpy = Math.round((balance / rateJpy) * 100.0) / 100.0;
        Double balanceBrl = Math.round((balance / rateBrl) * 100.0) / 100.0;

        return new CurrencyBalance(balanceBrl, balanceUsd, balanceEuro, balanceJpy);
    }

}
