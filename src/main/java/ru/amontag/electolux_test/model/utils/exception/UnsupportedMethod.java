package ru.amontag.electolux_test.model.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedMethod extends RuntimeException {
    public UnsupportedMethod(String name, Map<String, String> paramerers) {
        super(String.format(
                "Unsupported method '%s' with parameters [%s]",
                name,
                paramerers.entrySet().stream().map(kv -> String.format("%s = %s", kv.getKey(), kv.getValue()))
                        .collect(Collectors.joining(",")))
        );
    }
}
