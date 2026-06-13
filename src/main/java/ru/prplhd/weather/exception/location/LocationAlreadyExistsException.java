package ru.prplhd.weather.exception.location;

public class LocationAlreadyExistsException extends RuntimeException {
    public LocationAlreadyExistsException(String message) {
        super(message);
    }
}
