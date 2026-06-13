package ru.prplhd.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.service.UserLocationService;

import java.util.List;

@Controller
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final UserLocationService userLocationService;

    @PostMapping()
    public String addLocation(@RequestAttribute("authenticatedUserDto") AuthenticatedUserDto userDto,
                              @ModelAttribute @Valid AddLocationDto addLocationDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();

            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);

            return "redirect:/search-results";
        }

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
