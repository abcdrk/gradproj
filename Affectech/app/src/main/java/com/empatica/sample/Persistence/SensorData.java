package com.empatica.sample.Persistence;

public class SensorData {
    private Type type;
    private Double value;
    private String timestamp;

    public Type getType() {
        return type;
    }

    public void setType(int type) {
        this.type = Type.values()[type];
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public enum Type {
        UNKNOWN(0),
        IBI(1),
        EDA(2),
        ACC_X(3),
        ACC_Y(4),
        ACC_Z(5);

        final int data;

        Type(int data) {
            this.data = data;
        }
    }

    public SensorData() {
    }

    public SensorData(Type type, Double value) {
        this.type = type;
        this.value = value;
    }

}