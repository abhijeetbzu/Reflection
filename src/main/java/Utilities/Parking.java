package Utilities;

import java.util.List;

public class Parking {
    private String name = "Sector1";
    private Car[] cars;

    public Parking(String name, List<Car> cars) {
        this(cars);
        this.name = name;
    }

    public Parking(List<Car> cars) {
        this.cars = cars.toArray(new Car[cars.size()]);
    }
}
