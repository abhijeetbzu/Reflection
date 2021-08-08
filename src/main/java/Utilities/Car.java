package Utilities;

public class Car {
    private String modelName;
    private String modelNumber;

    public Car() {

    }

    public Car(String modelName, String modelNumber) {
        this.modelName = modelName;
        this.modelNumber = modelNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
