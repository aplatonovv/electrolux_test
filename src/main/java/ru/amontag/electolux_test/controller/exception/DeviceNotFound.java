package ru.amontag.electolux_test.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeviceNotFound extends RuntimeException {
    public DeviceNotFound(String id) {
        super(String.format("Device with id = %s not found", id));
    }
}
