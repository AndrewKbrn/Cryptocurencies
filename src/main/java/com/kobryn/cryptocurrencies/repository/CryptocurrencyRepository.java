package com.kobryn.cryptocurrencies.repository;

import com.kobryn.cryptocurrencies.entity.Cryptocurrency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CryptocurrencyRepository extends MongoRepository<Cryptocurrency, Long> {

    @Query("{ 'curr1' : ?0 }")
    Page<Cryptocurrency> findAllByCurr1(String curr1, Pageable pageable);

}
