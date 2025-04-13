package org.example.service.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.net.http.HttpResponse;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyServiceTest {
    private CurrencyService currencyService;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyService();
    }

    @Test
    void should_return_exchange_rate_for_valid_date() throws Exception {
        // given
        String date = "2024-04-10";
        double expectedRate = 3.9264;

        // when
        double result = currencyService.getExchangeRate(date);

        // then
        assertThat(result).isEqualTo(expectedRate);
    }

    @Test
    void should_return_exchange_rate_for_previous_day_when_current_day_is_not_available() throws Exception {
        // given
        String date = "2024-04-10";
        double expectedRate = 3.9264;

        // when
        double result = currencyService.getExchangeRate(date);

        // then
        assertThat(result).isEqualTo(expectedRate);
    }

    @Test
    void should_return_zero_when_exchange_rate_is_not_found() throws Exception {
        // given
        String date = "invalid-date";

        // when
        Executable e = () -> currencyService.getExchangeRate(date);

        // then
        assertThrows(DateTimeParseException.class, e);
    }

    @Test
    void should_throw_exception_when_invalid_date_format_is_provided() {
        // given
        String invalidDate = "2024-04-31";

        // when
        Executable e = () -> currencyService.getExchangeRate(invalidDate);

        // then
        assertThrows(DateTimeParseException.class, e);
    }

}