package com.nttdata.application.service;

import com.nttdata.application.response.ExchangeRateResponse;
import com.nttdata.domain.entity.CurrencyBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class CurrencyConversionService {

    @Value("${currency.api.url}")
    String apiUrlTemplate;

    @Value("${currency.api.key}")
    String apiKey;

    private final RestTemplate restTemplate;

    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> getExchangeRates(String baseCurrency) {
        try {
            String apiUrl = String.format(apiUrlTemplate, apiKey, baseCurrency);
            ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, null, ExchangeRateResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getConversionRates();
            } else {
                System.err.println("API retornou erro: " + response.getStatusCode());
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter taxas de câmbio: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public CurrencyBalance convertToCurrencyBalance(Double balance, String baseCurrency) {
        Map<String, Double> rates = getExchangeRates(baseCurrency);

        Double rateEuro = rates.getOrDefault("EUR", 1.0);
        Double rateUsd = rates.getOrDefault("USD", 1.0);
        Double rateJpy = rates.getOrDefault("JPY", 1.0);
        Double rateBrl = rates.getOrDefault("BRL", 1.0);

        Double balanceEuro = Math.round((balance / rateEuro) * 100.0) / 100.0;
        Double balanceUsd = Math.round((balance / rateUsd) * 100.0) / 100.0;
        Double balanceJpy = Math.round((balance / rateJpy) * 100.0) / 100.0;
        Double balanceBrl = Math.round((balance / rateBrl) * 100.0) / 100.0;

        return new CurrencyBalance(balanceBrl, balanceUsd, balanceEuro, balanceJpy);
    }

}
