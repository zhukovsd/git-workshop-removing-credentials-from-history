package ru.prplhd.weather.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.service.UserLocationService;

@Controller
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final UserLocationService userLocationService;

    @PostMapping()
    public String addLocation(@RequestAttribute("authenticatedUserDto") AuthenticatedUserDto userDto,
                              @ModelAttribute AddLocationDto addLocationDto
    ) {
        userLocationService.saveLocation(userDto.id(), addLocationDto);

        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteLocation(@RequestAttribute("authenticatedUserDto") AuthenticatedUserDto userDto,
                                 @PathVariable("id") Long locationId
    ) {
        userLocationService.deleteLocation(locationId, userDto.id());

        return "redirect:/";
    }
}
