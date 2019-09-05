package com.example.sbdemo.news;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class NewsValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        News news  = (News) target;
        if (news.getMultipartFile().isEmpty()) {
            return;
        }

        MultipartFile multipartFile = news.getMultipartFile();
        if (multipartFile.getSize() > 1024 * 1024) {
            errors.rejectValue("multipartFile", "news.validator.size");
        }

        String contentType = multipartFile.getContentType();
        if (!Objects.requireNonNull(contentType).equals("image/jpeg") && !contentType.equals("image/png")) {
            errors.rejectValue("multipartFile", "news.validator.image");
        }
    }
}
