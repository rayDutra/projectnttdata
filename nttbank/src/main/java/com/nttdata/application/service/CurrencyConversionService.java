package com.nttdata.application.service;

import com.nttdata.domain.entity.CurrencyBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

@Service
public class CurrencyConversionService {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/27091f71d355f60163ee9ecb/latest/"; // URL base corrigida

    private final RestTemplate restTemplate;

    @Autowired
    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        String url = API_URL + fromCurrency;  // A URL base + moeda de origem

        ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, ExchangeRateResponse.class);

        if (response.getBody() != null) {
            Double rate = response.getBody().getRates().get(toCurrency);
            if (rate != null) {
                return rate;
            } else {
                throw new IllegalArgumentException("Não foi possível encontrar a taxa de câmbio para " + toCurrency);
            }
        } else {
            throw new IllegalArgumentException("Erro ao obter dados de câmbio da API");
        }
    }

    public CurrencyBalance convertToCurrencyBalance(Double balanceReal) {
        Double balanceDolar = balanceReal * getExchangeRate("BRL", "USD");
        Double balanceEuro = balanceReal * getExchangeRate("BRL", "EUR");
        Double balanceIenes = balanceReal * getExchangeRate("BRL", "JPY");

        return new CurrencyBalance(balanceReal, balanceDolar, balanceEuro, balanceIenes);
    }
}
