package by.ladz.gusakov.SocialMedialApp.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

public class ExceptionUtil {

    public static String generateErrorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> error.getField() + " – " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }
        return null;
    }

}

