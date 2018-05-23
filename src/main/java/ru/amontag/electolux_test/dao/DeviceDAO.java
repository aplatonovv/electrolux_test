package ru.amontag.electolux_test.dao;

import ru.amontag.electolux_test.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceDAO {
    Optional<Device> Get(String id);
    String Save(Device device);
    List<Device> GetAll();
}
