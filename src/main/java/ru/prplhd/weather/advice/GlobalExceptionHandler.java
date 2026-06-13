package ru.prplhd.weather.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.prplhd.weather.exception.location.LocationAlreadyExistsException;
import ru.prplhd.weather.exception.location.LocationNotFoundException;
import ru.prplhd.weather.exception.openweather.OpenWeatherApiException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException e) {
        ModelAndView modelAndView = new ModelAndView("404");

        modelAndView.setStatus(HttpStatus.NOT_FOUND);

        return modelAndView;
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ModelAndView handleLocationNotFoundException (LocationNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error");

        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(LocationAlreadyExistsException.class)
    public ModelAndView handleLocationAlreadyExists (LocationAlreadyExistsException e) {
        ModelAndView modelAndView = new ModelAndView("error");

        modelAndView.setStatus(HttpStatus.CONFLICT);
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(OpenWeatherApiException.class)
    public ModelAndView handleOpenWeatherApiException (OpenWeatherApiException e) {
        ModelAndView modelAndView = new ModelAndView("error");

        modelAndView.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedException (Exception ignored) {
        ModelAndView modelAndView = new ModelAndView("error");

        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("message", "Internal server error. Please try again later.");

        return modelAndView;
    }
}
