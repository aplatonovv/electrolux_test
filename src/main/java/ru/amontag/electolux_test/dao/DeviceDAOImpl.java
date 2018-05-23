package ru.amontag.electolux_test.dao;

import org.springframework.stereotype.Service;
import ru.amontag.electolux_test.model.Device;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceDAOImpl implements DeviceDAO {
    private ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>();

    @Override
    public Optional<Device> Get(String id) {
        return Optional.ofNullable(devices.get(id));
    }

    @Override
    public String Save(Device device) {
        String newID = UUID.randomUUID().toString();
        device.setId(newID);
        devices.put(newID, device);
        return newID;
    }

    @Override
    public List<Device> GetAll() {
        return Collections.list(devices.elements());
    }
}
