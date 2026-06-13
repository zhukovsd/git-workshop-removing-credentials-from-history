package ru.prplhd.weather.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.service.UserLocationService;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final UserLocationService userLocationService;

    @GetMapping()
    public String home(@RequestAttribute("authenticatedUserDto") AuthenticatedUserDto userDto,
                       Model model
    ) {
        List<LocationWeatherViewDto> locations = userLocationService.findUserLocationsWithCurrentWeather(userDto.id());

        model.addAttribute("locations", locations);
        return "index";
    }
}
