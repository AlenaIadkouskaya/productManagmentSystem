package org.example.service.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

public class CurrencyConverterTest {
    private CurrencyService currencyService;
    private CurrencyConverter currencyConverter;

    @BeforeEach
    void setUp() {
        currencyService = mock(CurrencyService.class);
        currencyConverter = new CurrencyConverter(currencyService);
    }

    @Test
    void should_convert_from_usd_to_pln_successfully_with_valid_rate() throws Exception {
        // given
        double priceInDollars = 100.0;
        String date = "2024-04-10";
        double expectedRate = 4.5;

        when(currencyService.getExchangeRate(date)).thenReturn(expectedRate);

        // when
        double result = currencyConverter.convertDollarsToZloty(priceInDollars, date);

        // then
        assertThat(result).isEqualTo(450.0);
        assertThat(result).isCloseTo(450.0, within(0.0001));
        verify(currencyService).getExchangeRate(date);
    }

    @Test
    void should_return_zero_when_date_is_invalid() throws Exception {
        // given
        double priceInDollars = 100.0;
        String date = "invalid-date";

        when(currencyService.getExchangeRate(date)).thenThrow(new RuntimeException("Invalid date"));

        // when
        double result = currencyConverter.convertDollarsToZloty(priceInDollars, date);

        // then
        assertThat(result).isEqualTo(0.0);
        assertThat(result).isCloseTo(0.0, within(0.0001));
        verify(currencyService).getExchangeRate(date);
    }

    @Test
    void should_return_zero_when_price_in_usd_is_zero() throws Exception {
        // given
        double priceInDollars = 0.0;
        String date = "2024-04-10";
        double expectedRate = 4.5;

        when(currencyService.getExchangeRate(date)).thenReturn(expectedRate);

        // when
        double result = currencyConverter.convertDollarsToZloty(priceInDollars, date);

        // then
        assertThat(result).isEqualTo(0.0);
        assertThat(result).isCloseTo(0.0, within(0.0001));
    }
}