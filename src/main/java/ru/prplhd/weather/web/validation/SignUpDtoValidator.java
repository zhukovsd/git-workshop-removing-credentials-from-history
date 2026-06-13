package ru.prplhd.weather.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.prplhd.weather.dto.auth.SignUpDto;
import ru.prplhd.weather.persistence.repository.UserRepository;

import java.util.Objects;

@Component
public class SignUpDtoValidator implements Validator  {

    private final UserRepository userRepository;

    public SignUpDtoValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpDto signUpDto = (SignUpDto) target;

        validateLoginUniqueness(signUpDto, errors);
        validatePasswordMatch(signUpDto, errors);
    }

    private void validateLoginUniqueness(SignUpDto signUpDto, Errors errors) {
        String login = signUpDto.login();

        if (errors.hasFieldErrors("login")) {
            return;
        }

        if (userRepository.existsByLogin(login)) {
            errors.rejectValue("login", "login.alreadyExists", "This login is already taken");
        }
    }

    private void validatePasswordMatch(SignUpDto signUpDto, Errors errors) {
        String password = signUpDto.password();
        String confirmPassword = signUpDto.confirmPassword();

        if (errors.hasFieldErrors("password")) {
            return;
        }

        if (!Objects.equals(password, confirmPassword)) {
            errors.rejectValue("password", "password.mismatch", "Password confirmation does not match");
        }
    }
}
