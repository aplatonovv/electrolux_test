package ru.amontag.electolux_test.model;

import org.junit.Before;
import org.junit.Test;
import ru.amontag.electolux_test.model.standard.Oven;
import ru.amontag.electolux_test.model.utils.exception.ArgumentNotFoundException;
import ru.amontag.electolux_test.model.utils.exception.CannotParseParameterException;
import ru.amontag.electolux_test.model.utils.exception.UnsupportedMethod;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static ru.amontag.electolux_test.Util.checkMaps;

public class DeviceTest {
    private Oven oven = new Oven();

    @Before
    public void setUp() throws Exception {
        oven.setTemperature(0.0);
        oven.setTurnedOn(false);
        oven.setMode("geek heating");
    }

    @Test
    public void getStatus() {
        Map expected = Map.of("temperature", 0.0, "turnedOn", false, "mode", "geek heating");
        checkMaps(expected, oven.getStatus());
    }

    @Test
    public void call() {
        Map expected = Map.of("temperature", 120.5, "turnedOn", true, "mode", "standard heating");
        oven.call("setTemperature", Map.of("value", "120.5"));
        oven.call("setTurnedOn", Map.of("value", "true"));
        oven.call("setMode", Map.of("value", "standard heating"));
        checkMaps(expected, oven.getStatus());
    }

    @Test(expected = UnsupportedMethod.class)
    public void callUnknownMethod() {
        oven.call("setGeekMode", Map.of("value", "standard heating"));
    }

    @Test(expected = ArgumentNotFoundException.class)
    public void callWithoutArgument() {
        oven.call("setMode", Map.of());
    }

    @Test(expected = CannotParseParameterException.class)
    public void callWithBadArgument() {
        oven.call("setTemperature", Map.of("value", "false"));
    }


}