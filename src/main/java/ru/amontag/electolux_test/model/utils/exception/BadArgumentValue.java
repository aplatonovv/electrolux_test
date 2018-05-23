package ru.amontag.electolux_test.model.utils.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadArgumentValue extends RuntimeException {
    public BadArgumentValue() {
    }

    public BadArgumentValue(String message) {
        super(message);
    }
}
