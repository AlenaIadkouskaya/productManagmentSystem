package org.example.service.currency;

public class CurrencyConverter {
    private final CurrencyService currencyService;
    public CurrencyConverter(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
    public double convertDollarsToZloty(double priceInDollars, String date) {
        try {
            double exchangeRate = currencyService.getExchangeRate(date);
            return priceInDollars * exchangeRate;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }


}