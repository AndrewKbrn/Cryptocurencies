package com.kobryn.cryptocurrencies.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "cryptocurrencies")
@Getter
@Setter
public class Cryptocurrency {
    @Id
    private String id;
    private String lprice;
    private String curr1;
    private String curr2;
    private String createdAt;

    @Override
    public String toString() {
        return "Cryptocurrency{" +
                "id='" + id + '\'' +
                ", lprice='" + lprice + '\'' +
                ", curr1='" + curr1 + '\'' +
                ", curr2='" + curr2 + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
