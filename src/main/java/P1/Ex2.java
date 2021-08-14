package P1;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Ex2 {
    Ex2(Parent parent){
        parent.sayHello();
    }
    public static Ex2 create(Parent parent) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            Class<Ex2> cls = Ex2.class;
            Constructor<Ex2> constructor = cls.getDeclaredConstructor(parent.getClass());
            return constructor.newInstance(parent);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw e;
        }
    }
    public static void main(String[] args) {
        Parent parent1 = new Child();
        Parent parent2 = new Parent();
        try {
            //compiler handle subtype but reflection should give exact type of object parameter, not subtype
            //! create(parent1);
            create(parent2);

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
