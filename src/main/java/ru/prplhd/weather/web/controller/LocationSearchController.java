package ru.prplhd.weather.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.prplhd.weather.dto.openweather.LocationResponseDto;
import ru.prplhd.weather.service.LocationSearchService;

import java.util.List;

@Controller
@RequestMapping("/search-results")
public class LocationSearchController {

    private final LocationSearchService locationSearchService;

    public LocationSearchController(LocationSearchService locationSearchService) {
        this.locationSearchService = locationSearchService;
    }

    @GetMapping()
    public String searchLocationsPage(@RequestParam(name = "name", required = false) String name,
                                      Model model) {

        model.addAttribute("name", name);

        if (name == null || name.isBlank()) {
            model.addAttribute("locations", List.of());
            return "search-results";
        }

        List<LocationResponseDto> locations = locationSearchService.searchByName(name);
        model.addAttribute("locations", locations);

        return "search-results";
    }
}
