package ru.amontag.electolux_test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.amontag.electolux_test.controller.exception.DeviceNotFound;
import ru.amontag.electolux_test.controller.exception.EmptyDeviceDescription;
import ru.amontag.electolux_test.controller.exception.UnsupportedDeviceType;
import ru.amontag.electolux_test.dao.DeviceDAO;
import ru.amontag.electolux_test.model.Device;
import ru.amontag.electolux_test.model.standard.Oven;
import ru.amontag.electolux_test.model.utils.DeviceType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class DeviceController {
    @Autowired
    private DeviceDAO deviceDAO;

    @RequestMapping(value = "/device/register", method = RequestMethod.POST)
    @ResponseBody
    public String registerDevice(@RequestBody Optional<Device> device, @RequestParam("device_type") Optional<DeviceType> deviceType) {
        if (deviceType.isPresent()) {
            DeviceType deviceTypeValue = deviceType.get();
            switch (deviceTypeValue) {
                case OVEN:
                    return deviceDAO.Save(new Oven());
                default:
                    throw new UnsupportedDeviceType(deviceTypeValue);
            }
        } else {
            return deviceDAO.Save(device.orElseThrow(() -> new EmptyDeviceDescription()));
        }
    }

    @RequestMapping(value = "/device", method = RequestMethod.GET)
    @ResponseBody
    public List<Device> getAllDevices() {
        return deviceDAO.GetAll();
    }

    @RequestMapping(value = "/device/{id}/status", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getDeviceStatus(@PathVariable("id") String deviceId) {
        return deviceDAO.Get(deviceId).orElseThrow(() -> new DeviceNotFound(deviceId))
                .getStatus();
    }

    @RequestMapping(value = "/device/{id}/description", method = RequestMethod.GET)
    @ResponseBody
    public Device getDeviceDescription(@PathVariable("id") String deviceId) {
        return deviceDAO.Get(deviceId).orElseThrow(() -> new DeviceNotFound(deviceId));
    }

    @RequestMapping(value = "/device/{id}/command/{command_name}", method = RequestMethod.POST)
    @ResponseBody
    public Object callDeviceCommand(@PathVariable("id") String deviceId, @PathVariable("command_name") String commandId, @RequestParam Map<String, String> parameters) {
        return deviceDAO.Get(deviceId).orElseThrow(() -> new DeviceNotFound(deviceId)).call(commandId, parameters);
    }
}
