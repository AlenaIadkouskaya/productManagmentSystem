package org.example.service.currency;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CurrencyService{

    private static final String NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/a/usd/%s?format=json";

    public double getExchangeRate(String date) throws Exception {
        LocalDate requestedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

        double exchangeRate = tryGetExchangeRate(requestedDate);

        if (exchangeRate != -1) {
            return exchangeRate;
        } else {
            LocalDate previousDate = requestedDate.minusDays(1);
            return getExchangeRateFromPreviousDate(previousDate);
        }
    }

    private double tryGetExchangeRate(LocalDate date) throws Exception {
        String urlString = String.format(NBP_API_URL, date.format(DateTimeFormatter.ISO_DATE));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return parseExchangeRateFromJson(responseBody);
        } else {
            return -1;
        }
    }

    private double getExchangeRateFromPreviousDate(LocalDate date) throws Exception {
        String urlString = String.format(NBP_API_URL, date.format(DateTimeFormatter.ISO_DATE));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return parseExchangeRateFromJson(responseBody);
        } else {
            LocalDate previousDate = date.minusDays(1);
            return getExchangeRateFromPreviousDate(previousDate);
        }
    }

    private double parseExchangeRateFromJson(String jsonResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode ratesNode = rootNode.path("rates");

        if (ratesNode.isArray() && ratesNode.size() > 0) {
            JsonNode rateNode = ratesNode.get(0);
            return rateNode.path("mid").asDouble();
        } else {
            throw new Exception("Nie udało się znaleźć kursu w odpowiedzi.");
        }
    }
}