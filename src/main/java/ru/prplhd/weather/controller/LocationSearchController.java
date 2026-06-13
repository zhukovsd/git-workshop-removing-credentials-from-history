package ru.prplhd.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.prplhd.weather.dto.location.SearchLocationDto;
import ru.prplhd.weather.dto.view.LocationSearchResultViewDto;
import ru.prplhd.weather.service.LocationSearchService;

import java.util.List;

@Controller
@RequestMapping("/search-results")
@RequiredArgsConstructor
public class LocationSearchController {

    private final LocationSearchService locationSearchService;

    @GetMapping()
    public String searchLocationsPage(@ModelAttribute @Valid SearchLocationDto searchLocationDto,
                                      BindingResult bindingResult,
                                      Model model
    ) {
        model.addAttribute("name", searchLocationDto.name());

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("locations", List.of());
            return "search-results";
        }

        List<LocationSearchResultViewDto> locations = locationSearchService.searchByName(searchLocationDto.name());
        model.addAttribute("locations", locations);

        return "search-results";
    }
}
