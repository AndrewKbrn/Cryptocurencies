package com.kobryn.cryptocurrencies.service;

import com.google.gson.Gson;
import com.kobryn.cryptocurrencies.entity.Cryptocurrency;
import com.kobryn.cryptocurrencies.repository.CryptocurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class CryptocurrencyService {
    private final RestTemplate restTemplate = new RestTemplate();

    private CryptocurrencyRepository cryptocurrencyRepository;

    @Autowired
    public void setCryptocurrencyRepository(CryptocurrencyRepository cryptocurrencyRepository) {
        this.cryptocurrencyRepository = cryptocurrencyRepository;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Gets all json string from List, put them to  createNewCryptocurrencyObjectFromJson parameter and
     * saves every object to db
     */
    @Scheduled(fixedRate = 10000)
    public void saveCryptocurrenciesIntoDB() {
        Iterable<Cryptocurrency> cryptocurrencies = getCryptocurrenciesJson();
        cryptocurrencyRepository.saveAll(cryptocurrencies);
    }

    /**
     * The method gets json values every cryptocurrency using RestTemplate from CEX.io and adds to list
     *
     * @return list with json values every cryptocurrency
     */
    public Iterable<Cryptocurrency> getCryptocurrenciesJson() {
        String BTC = restTemplate.getForObject("https://cex.io/api/last_price/BTC/USD", String.class);
        String ETH = restTemplate.getForObject("https://cex.io/api/last_price/ETH/USD", String.class);
        String XRP = restTemplate.getForObject("https://cex.io/api/last_price/XRP/USD", String.class);

        return new ArrayList<>() {{
            add(createNewCryptocurrencyObjectFromJson(BTC));
            add(createNewCryptocurrencyObjectFromJson(ETH));
            add(createNewCryptocurrencyObjectFromJson(XRP));
        }};
    }

    /**
     * The method parses json using Gson and creates new Cryptocurrency object
     *
     * @param json json string
     * @return new Cryptocurrency object
     */
    public Cryptocurrency createNewCryptocurrencyObjectFromJson(String json) {
        Cryptocurrency cryptocurrency = new Gson().fromJson(json, Cryptocurrency.class);
        cryptocurrency.setCreatedAt(LocalDateTime.now().format(formatter));
        return cryptocurrency;
    }


    /**
     * the method gets record from DB with min or max price depending on sort direction
     *
     * @param cryptocurrencyName cryptocurrency name
     * @param sort               sort with specified sort direction
     * @return string record from db
     */
    public Cryptocurrency getOneCryptocurrencyFromDBWithSort(String cryptocurrencyName, Sort sort) {
        return cryptocurrencyRepository.findAllByCurr1(cryptocurrencyName, PageRequest.of(0, 1, sort))
                .stream()
                .findFirst()
                .orElse(new Cryptocurrency());
    }

    /**
     * the method gets Cryptocurrency page from DB
     * Page is ascending list
     * Then the method writes list to StringBuilder for easy display
     *
     * @param cryptocurrencyName cryptocurrency name
     * @param page               first element
     * @param size               quantity of elements
     * @return custom string list
     */
    public Page<Cryptocurrency> getCryptocurrenciesPage(String cryptocurrencyName, Integer page, Integer size) {
        return cryptocurrencyRepository.findAllByCurr1(cryptocurrencyName,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "lprice")));
    }

    /**
     * The method generates CSV report in format (Cryptocurrency name, min price, max price) using StringBuilder;
     *
     * @return CSV report
     */
    public String generateCSV() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<Cryptocurrency> cryptocurrencies = getCryptocurrenciesJson();
        for (Cryptocurrency cryptocurrency : cryptocurrencies) {
            stringBuilder.append(cryptocurrency.getCurr1())
                    .append(",")
                    .append(getOneCryptocurrencyFromDBWithSort(cryptocurrency.getCurr1(), Sort.by(Sort.Direction.ASC, "lprice")).getLprice())
                    .append(",")
                    .append(getOneCryptocurrencyFromDBWithSort(cryptocurrency.getCurr1(), Sort.by(Sort.Direction.DESC, "lprice")).getLprice())
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Saves CSV report info from {@link CryptocurrencyService#generateCSV()} into file
     *
     * @param info CSV report info
     * @return user-friendly message
     */
    public String saveCSV(String info) {
        File CSVReport = new File("CSV_report.txt");
        try (FileWriter writer = new FileWriter(CSVReport)) {
            writer.write(info);
        } catch (IOException e) {
            return "Something went wrong(";
        }
        return "Report was generated!";
    }

}
