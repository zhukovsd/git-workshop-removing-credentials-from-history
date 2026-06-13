package ru.prplhd.weather.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.dto.view.LocationSearchResultViewDto;
import ru.prplhd.weather.service.LocationSearchService;
import ru.prplhd.weather.service.UserLocationService;

import java.util.List;

@Controller
@RequestMapping("/search-results")
public class LocationSearchController {

    private final LocationSearchService locationSearchService;
    private final UserLocationService userLocationService;

    public LocationSearchController(LocationSearchService locationSearchService, UserLocationService userLocationService) {
        this.locationSearchService = locationSearchService;
        this.userLocationService = userLocationService;
    }

    @GetMapping()
    public String searchLocationsPage(@RequestParam(name = "name", required = false) String name,
                                      Model model
    ) {
        model.addAttribute("name", name);

        if (name == null || name.isBlank()) {
            model.addAttribute("locations", List.of());
            return "search-results";
        }

        List<LocationSearchResultViewDto> locations = locationSearchService.searchByName(name);
        model.addAttribute("locations", locations);

        return "search-results";
    }

    @PostMapping()
    public String addLocation(@RequestAttribute(name = "authenticatedUserDto", required = false) AuthenticatedUserDto userDto,
                              @ModelAttribute AddLocationDto addLocationDto
    ) {
        userLocationService.saveLocation(userDto.id(), addLocationDto);

        return "redirect:/";
    }
}
