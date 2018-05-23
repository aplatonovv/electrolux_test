package ru.amontag.electolux_test.model.standard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.amontag.electolux_test.model.Device;
import ru.amontag.electolux_test.model.utils.ModelMethod;
import ru.amontag.electolux_test.model.utils.ModelParameter;
import ru.amontag.electolux_test.model.utils.exception.BadArgumentValue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//Local POJO for representing device. Real device is remote and its state is owned to device driver
//this class is just example
public class Oven extends Device {
    //purely for example
    private static final Random random = new Random(System.currentTimeMillis());

    @ModelParameter
    @JsonIgnore
    private Boolean turnedOn = random.nextBoolean();
    @ModelParameter
    @JsonIgnore
    private Double temperature = (double) (random.nextInt() % 300);

    @ModelParameter
    @JsonIgnore
    private String mode = "standard mode";

    public Oven() {
        setName("Just example oven");
    }

    public Boolean getTurnedOn() {
        return turnedOn;
    }

    @ModelMethod
    public void setTurnedOn(Boolean value) {
        this.turnedOn = value;
    }

    public Double getTemperature() {
        return temperature;
    }

    @ModelMethod
    public void setTemperature(Double value) {
        if (value < 0.0 || value > 300.0) {
            throw new IllegalArgumentException();
        }

        this.temperature = value;
    }

    @ModelMethod
    @JsonIgnore
    public List<String> getModes() {
        return List.of("standard heating", "geek heating");
    }

    @JsonIgnore
    public String getMode() {
        return mode;
    }

    @ModelMethod
    public void setMode(String value) {
        if (!value.equals("standard heating") && !value.equals("geek heating")) {
            throw new BadArgumentValue(String.format(
                    "Mode value must has value from these [%s]",
                    getModes().stream().collect(Collectors.joining(",")))
            );
        }

        mode = value;
    }
}
