package ru.prplhd.weather.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.service.UserLocationService;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserLocationService userLocationService;

    public HomeController(UserLocationService userLocationService) {
        this.userLocationService = userLocationService;
    }

    @GetMapping()
    public String home(@RequestAttribute("authenticatedUserDto") AuthenticatedUserDto userDto,
                       Model model
    ) {
        List<LocationWeatherViewDto> locations = List.of();

        if (userDto != null) {
            locations = userLocationService.findUserLocationsWithCurrentWeather(userDto.id());
        }

        model.addAttribute("locations", locations);
        return "index";
    }

    @DeleteMapping("/locations/{id}")
    public String deleteLocation(@RequestAttribute("authenticatedUserDto") AuthenticatedUserDto userDto,
                                 @PathVariable("id") Long locationId
    ) {
        userLocationService.deleteLocation(locationId, userDto.id());

        return "redirect:/";
    }

}
