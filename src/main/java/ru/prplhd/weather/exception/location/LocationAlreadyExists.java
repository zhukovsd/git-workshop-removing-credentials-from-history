package ru.prplhd.weather.exception.location;

public class LocationAlreadyExists extends RuntimeException {
    public LocationAlreadyExists(String message) {
        super(message);
    }
}
