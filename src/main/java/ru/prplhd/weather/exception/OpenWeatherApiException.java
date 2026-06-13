package ru.prplhd.weather.exception;

public class OpenWeatherApiException extends RuntimeException {
    public OpenWeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenWeatherApiException(String message) {
        super(message);
    }
}
