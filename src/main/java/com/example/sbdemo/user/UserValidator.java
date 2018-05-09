package com.example.sbdemo.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator  {
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (!user.getPassword().isEmpty()) {
            if (user.getPassword().length() < 6) {
                errors.rejectValue("password", "user.validator.password.size");
            }
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                errors.rejectValue("password", "user.validator.password");
            }
        }
    }
}
