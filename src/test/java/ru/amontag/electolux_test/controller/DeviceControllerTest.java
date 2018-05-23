package ru.amontag.electolux_test.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.amontag.electolux_test.dao.DeviceDAO;
import ru.amontag.electolux_test.model.Device;
import ru.amontag.electolux_test.model.standard.Oven;
import ru.amontag.electolux_test.model.utils.Command;
import ru.amontag.electolux_test.model.utils.ModelMethod;
import ru.amontag.electolux_test.model.utils.ModelParameter;
import ru.amontag.electolux_test.model.utils.ParameterType;
import ru.amontag.electolux_test.model.utils.exception.BadArgumentValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static ru.amontag.electolux_test.Util.checkMaps;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeviceControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private DeviceDAO devices;

    public static class CustomCoffeeMachine extends Device {
        @JsonIgnore
        @ModelParameter
        private Double strength;
        @JsonIgnore
        @ModelParameter
        private Boolean isTurnedOn;
        @JsonIgnore
        @ModelParameter
        private Boolean hasBeans;

        public CustomCoffeeMachine(double strength, boolean isTurnedOn, boolean hasBeans) {
            this.strength = strength;
            this.isTurnedOn = isTurnedOn;
            this.hasBeans = hasBeans;
        }

        public Double getStrength() {
            return strength;
        }

        @ModelMethod
        public void setStrength(Double strength) {
            if (strength < 0 || strength > 1.0) {
                throw new BadArgumentValue(String.format("Strength value must be between 0 and 1"));
            }

            this.strength = strength;
        }

        public Boolean isTurnedOn() {
            return isTurnedOn;
        }

        @ModelMethod
        public void setTurnedOn(Boolean turnedOn) {
            isTurnedOn = turnedOn;
        }

        @ModelMethod
        public Boolean isHasBeans() {
            return hasBeans;
        }

        public String toJson() {
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private CustomCoffeeMachine testMachine;

    @Before
    public void setUp() throws Exception {
        testMachine = new CustomCoffeeMachine(0.5, false, false);
    }

    @Test
    public void registerStandardDevice() {
        String id = new String(webClient.post().uri("/device/register?device_type=OVEN")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk().expectBody().returnResult().getResponseBody());
        Optional<Device> optionalDevice = devices.Get(id);
        assertTrue(optionalDevice.isPresent());
        assertTrue(optionalDevice.get() instanceof Oven);
    }

    @Test
    public void registerStandardDeviceWithUnknownType() {
        webClient.post().uri("/device/register?device_type=COFFEE_MACHINE")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void registerCustomDevice() {
        String id = new String(webClient.post().uri("/device/register")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(testMachine.toJson())
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult().getResponseBody());
        Optional<Device> optionalDevice = devices.Get(id);
        assertTrue(optionalDevice.isPresent());
        Device device = optionalDevice.get();

        Map expectedFields = Map.of("strength", ParameterType.DOUBLE, "isTurnedOn", ParameterType.BOOL, "hasBeans", ParameterType.BOOL);
        checkMaps(expectedFields, device.getFields());
        Map<String, Command> commands = device.getCommands();
        List.of("setStrength", "setTurnedOn", "isHasBeans")
                .forEach(commandName -> assertTrue(commands.containsKey(commandName)));
    }

    @Test
    public void registerStandardDeviceWithEmptyDescription() {
        webClient.post().uri("/device/register")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void getUnknownDeviceStatus() {
        webClient.get().uri("/device/111/status")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getDeviceStatus() {
        String id = devices.Save(testMachine);
        webClient.get().uri(String.format("/device/%s/status", id))
                .exchange()
                .expectStatus().isOk();
//TODO
    }


    @Test
    public void getDeviceDescription() {
//TODO
    }

    @Test
    public void callDeviceCommand() {
//TODO
    }
}