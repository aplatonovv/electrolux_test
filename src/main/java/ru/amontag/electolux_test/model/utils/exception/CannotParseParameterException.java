package ru.amontag.electolux_test.model.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.amontag.electolux_test.model.utils.Command;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotParseParameterException extends RuntimeException {
    public CannotParseParameterException(String command, Command.NameWithType descr, String value) {
        super(String.format("Cannot parse parameter '%s' of type '%s' of function '%s' with value '%s'", descr.getName(), descr.getType().toString(), command, value));
    }
}
