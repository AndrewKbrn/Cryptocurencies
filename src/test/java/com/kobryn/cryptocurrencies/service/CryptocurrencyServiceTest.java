package com.kobryn.cryptocurrencies.service;

import com.google.gson.Gson;
import com.kobryn.cryptocurrencies.entity.Cryptocurrency;
import com.kobryn.cryptocurrencies.repository.CryptocurrencyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Some test(s) T_T...
 */
class CryptocurrencyServiceTest {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BTC = restTemplate.getForObject("https://cex.io/api/last_price/BTC/USD", String.class);


    @Test
    void createNewCryptocurrencyObjectFromJson() {
        Cryptocurrency cryptocurrency = new Gson().fromJson(BTC, Cryptocurrency.class);
        assertNotNull(cryptocurrency);
        assertEquals("BTC",cryptocurrency.getCurr1());
        assertEquals("USD", cryptocurrency.getCurr2());
    }

}