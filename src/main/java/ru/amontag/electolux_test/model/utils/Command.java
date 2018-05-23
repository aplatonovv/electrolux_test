package ru.amontag.electolux_test.model.utils;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class Command {
    public static class NameWithType {
        private String name;
        private ParameterType type;

        public NameWithType() {
        }

        public NameWithType(String name, ParameterType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ParameterType getType() {
            return type;
        }

        public void setType(ParameterType type) {
            this.type = type;
        }
    }

    private List<NameWithType> parameters;

    public Command() {

    }

    public List<NameWithType> getParameters() {
        return parameters;
    }

    public void setParameters(List<NameWithType> parameters) {
        this.parameters = parameters;
    }
}
