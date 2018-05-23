package ru.amontag.electolux_test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.beanutils.PropertyUtils;
import ru.amontag.electolux_test.model.utils.*;
import ru.amontag.electolux_test.model.utils.exception.ArgumentNotFoundException;
import ru.amontag.electolux_test.model.utils.exception.CannotConstructDeviceDescription;
import ru.amontag.electolux_test.model.utils.exception.CannotParseParameterException;
import ru.amontag.electolux_test.model.utils.exception.UnsupportedMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Device {
    private String id;
    private String name;

    private Map<String, ParameterType> fields;
    private Map<String, Command> commands;
    private Map<String, Method> methodByName;

    public Device() {
        fields = new HashMap<>();
        commands = new HashMap<>();
        methodByName = new HashMap<>();

        Arrays.stream(getClass().getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(ModelParameter.class))
                .forEach(field -> fields.put(field.getName(), getType(field.getType())));

        Arrays.stream(getClass().getDeclaredMethods())
                .filter(x -> x.isAnnotationPresent(ModelMethod.class))
                .forEach(method -> {
                    commands.put(method.getName(), createCommandDescription(method));
                    methodByName.put(method.getName(), method);
                });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, ParameterType> getFields() {
        return fields;
    }

    public void setFields(Map<String, ParameterType> fields) {
        this.fields = fields;
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Map<String, Object> getStatus() {
        HashMap<String, Object> values = new HashMap<>();

        fields.forEach((name, type) -> {
            Object fieldValue = null;
            try {
                fieldValue = PropertyUtils.getProperty(this, name);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException e) { //ignore all purely for example
                fieldValue = getRemoteValue(name);
            }

            if (fieldValue == null) {
                fieldValue = "unknown";
            }

            values.put(name, fieldValue);
        });

        return values;
    }

    @JsonIgnore
    //must be extended in child-class
    protected Object getRemoteValue(String name) {
        return null;
    }

    public Object call(String command, Map<String, String> parameters) {
        Optional<Method> optMethod = Optional.ofNullable(methodByName.get(command));

        if (optMethod.isPresent()) {
            Method method = optMethod.get();
            Command descr = commands.get(command);

            Object[] args = descr.getParameters().stream()
                    .map(parameterName -> parseParameterValue(command, parameterName, parameters))
                    .toArray();
            try {
                return method.invoke(this, args);
            } catch (IllegalAccessException | InvocationTargetException e) { //ignore all purely for example
                throw new UnsupportedMethod(command, parameters);
            }
        } else {
            return remoteCall(command, parameters);
        }
    }

    private Object parseParameterValue(String command, Command.NameWithType descr, Map<String, String> parameters) {
        String stringValue = Optional.ofNullable(parameters.get(descr.getName()))
                .orElseThrow(() -> new ArgumentNotFoundException(command, descr));

        return parseParameterValue(command, stringValue, descr);
    }

    private Object parseParameterValue(String command, String value, Command.NameWithType descr) {
        try {
            switch (descr.getType()) {
                case INT:
                    return Integer.parseInt(value);
                case BOOL:
                    return Boolean.parseBoolean(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case STRING:
                    return value;
                default:
                    throw new CannotParseParameterException(command, descr, value);
            }
        } catch (NumberFormatException | NullPointerException e) {
            throw new CannotParseParameterException(command, descr, value);
        }

    }

    protected Object remoteCall(String command, Map<String, String> parameters) {
        throw new UnsupportedMethod(command, parameters);
    }

    private ParameterType getType(Class clazz) {
        if (clazz.equals(Boolean.class)) {
            return ParameterType.BOOL;
        } else if (clazz.equals(Integer.class)) {
            return ParameterType.INT;
        } else if (clazz.equals(String.class)) {
            return ParameterType.STRING;
        } else if (clazz.equals(Double.class)) {
            return ParameterType.DOUBLE;
        } else {
            throw new CannotConstructDeviceDescription();
        }
    }

    private Command createCommandDescription(Method method) {
        Command command = new Command();
        command.setParameters(Arrays.stream(method.getParameters())
                .map(param -> new Command.NameWithType(param.getName(), getType(param.getType())))
                .collect(Collectors.toList()));
        return command;
    }
}
