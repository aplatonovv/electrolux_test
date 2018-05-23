package ru.amontag.electolux_test.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.amontag.electolux_test.model.utils.DeviceType;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnsupportedDeviceType extends RuntimeException {
    public UnsupportedDeviceType(DeviceType deviceTypeValue) {
        super(String.format("Unsupport device type = %s", deviceTypeValue.toString()));
    }
}
