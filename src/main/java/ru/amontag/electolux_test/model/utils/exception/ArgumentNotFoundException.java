package ru.amontag.electolux_test.model.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.amontag.electolux_test.model.utils.Command;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArgumentNotFoundException extends RuntimeException {
    public ArgumentNotFoundException(String command, Command.NameWithType descr) {
        super(String.format("Calling method '%s' must contains argument '%s' with type '%s'",
                command, descr.getName(), descr.getType().toString())
        );
    }
}
