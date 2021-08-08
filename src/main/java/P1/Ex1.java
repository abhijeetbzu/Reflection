package P1;

import P2.GrandParent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Parent {
    protected void sayHello() {
        System.out.println("Hello World!I am Parent");
    }
}

class Child extends Parent {
}

//extending class of different package
class GrandChild extends GrandParent {

}

public class Ex1 {
    private int i1 = 10;
    private String name = "Abhijeet";
    public static void sayHello(Object obj, String methodName, Class[] params) {
        try {
            Class cls = obj.getClass();
            //! Method method = cls.getDeclaredMethod(methodName,params);

            Method method = getDeclaredMethod(cls, methodName, params);

            method.invoke(obj, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Method getDeclaredMethod(Class cls, String methodName, Class[] params) throws NoSuchMethodException {
        if (cls == null) throw new NoSuchMethodException();

        try {
            return cls.getDeclaredMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            return getDeclaredMethod(cls.getSuperclass(), methodName, params);
        } catch (SecurityException e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        Child child = new Child();
        GrandChild grandChild = new GrandChild();
        sayHello(child, "sayHello", null);
        //! IllegalAccessException, protected member method of class in diff package not accessible
        //! sayHello(grandChild,"sayHello",null);

    }
}
