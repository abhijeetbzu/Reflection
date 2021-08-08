package Utilities;

import P1.XMLSerializer;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.ArrayList;
import java.util.List;

public class SerializerTest {
    public static void main(String[] args) {
        List<Car> cars = new ArrayList<>();
        String[] carNames = new String[]{
                "Maruti",
                "Scorpio",
                "XUV",
                "Tata"
        };
        String[] modelNumbers = new String[]{
                "XVC",
                "DFG",
                "ERT",
                "FYZ"
        };
        for (int i = 0; i < carNames.length; i++) {
            Car car = new Car(carNames[i], modelNumbers[i]);
            cars.add(car);
        }

        try {
            Document document = XMLSerializer.serialize(new Parking(cars));
            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
            xmlOutput.output(document, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
