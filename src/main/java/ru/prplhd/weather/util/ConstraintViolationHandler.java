package ru.prplhd.weather.util;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ConstraintViolationHandler {

    private static final String USERS_LOGIN_UNIQUE_CONSTRAINT = "uk_users_login_lower";

    public boolean isLoginUniqueConstraintViolation(DataIntegrityViolationException e) {
        Throwable currentException = e;

        while (currentException != null) {
            if (currentException instanceof ConstraintViolationException constraintException) {
                String constraintName = constraintException.getConstraintName();

                if (USERS_LOGIN_UNIQUE_CONSTRAINT.equals(constraintName)) {
                    return true;
                }
            }

            currentException = currentException.getCause();
        }

        return false;
    }
}
