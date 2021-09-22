package com.kobryn.cryptocurrencies.controller;


import com.kobryn.cryptocurrencies.entity.Cryptocurrency;
import com.kobryn.cryptocurrencies.service.CryptocurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cryptocurrencies")
public class CryptocurrencyController {

    public CryptocurrencyService cryptocurrencyService;

    @Autowired
    public void setCryptocurrencyService(CryptocurrencyService cryptocurrencyService) {
        this.cryptocurrencyService = cryptocurrencyService;
    }


    @GetMapping("/minprice")
    public ResponseEntity<Cryptocurrency> getCryptocurrencyMinPrice(@RequestParam("name") String cryptocurrencyName) {
        Cryptocurrency cryptocurrencyWithMinPrice = cryptocurrencyService.getOneCryptocurrencyFromDBWithSort(cryptocurrencyName,
                Sort.by(Sort.Direction.ASC, "lprice"));
        return new ResponseEntity<>(cryptocurrencyWithMinPrice, HttpStatus.OK);
    }

    @GetMapping("/maxprice")
    public ResponseEntity<Cryptocurrency> getCryptocurrencyMaxPrice(@RequestParam("name") String cryptocurrencyName) {
        Cryptocurrency cryptocurrencyWithMaxPrice = cryptocurrencyService.getOneCryptocurrencyFromDBWithSort(cryptocurrencyName,
                Sort.by(Sort.Direction.DESC, "lprice"));
        return new ResponseEntity<>(cryptocurrencyWithMaxPrice, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<Cryptocurrency>> getCryptocurrenciesPage(@RequestParam("name") String cryptocurrencyName,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<Cryptocurrency> cryptocurrencyPage = cryptocurrencyService.getCryptocurrenciesPage(cryptocurrencyName, page, size);
        return new ResponseEntity<>(cryptocurrencyPage, HttpStatus.OK);
    }

    @GetMapping("/csv")
    public String generateCSV(){
        return cryptocurrencyService.saveCSV(cryptocurrencyService.generateCSV());
    }
}
