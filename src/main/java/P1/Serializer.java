package P1;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serializer {
    public static Document serialize(Object obj) throws Exception {
        Map<Object, Integer> objectMap = new HashMap<>();
        Element objectEle = beginSerialize(obj, objectMap);
        return new Document(objectEle);
    }

    private static Element beginSerialize(Object obj, Map<Object, Integer> objectMap) throws Exception {
        try {
            Element element = new Element("object");
            element.setAttribute("id", String.valueOf(objectMap.size()));
            objectMap.put(obj, objectMap.size());

            Field[] fields = getDeclaredFields(obj.getClass());
            for (Field field : fields) {
                element.addContent(getElement(obj, field, objectMap));
            }

            return element;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static Element getArrayElement(Field field, Object array, Map<Object, Integer> objectMap) throws Exception {
        int length = Array.getLength(array);
        Element arrayElement = new Element("array");
        for (int i = 0; i < length; i++) {
            Object val = Array.get(array, i);
            Element fieldElement = loadElement(field, val, objectMap);
            arrayElement.addContent(fieldElement);
        }
        return arrayElement;
    }

    private static Element loadElement(Field field, Object value, Map<Object, Integer> objectMap) throws Exception {
        Element element = new Element("field");
        String className = field.getDeclaringClass().getName();
        String fieldName = field.getName();

        element.setAttribute("class", className);
        element.setAttribute("name", fieldName);

        Class cls = field.getType();
        if (cls.isPrimitive() || cls == String.class) {
            Element val = new Element("value");
            val.setText(value.toString());
            element.addContent(val);
        } else {
            Element ref = new Element("reference");
            if (objectMap.containsKey(value))
                ref.setText(objectMap.get(value).toString());
            else {
                ref.setText(String.valueOf(objectMap.size()));
                beginSerialize(value, objectMap);
            }
            element.addContent(ref);
        }
        return element;
    }

    private static Element getElement(Object obj, Field field, Map<Object, Integer> objectMap) throws Exception {
        try {
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
            Class cls = field.getClass();
            Object fieldObj = field.get(obj);
            if (cls.isArray()) {
                return getArrayElement(field, fieldObj, objectMap);
            } else {
                return loadElement(field, fieldObj, objectMap);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static Field[] getDeclaredFields(Class cls) throws Exception {
        if (cls == null) throw new Exception("Class not provided");
        List<Field> desiredFields = new ArrayList<>();
        try {
            while (cls != null) {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        desiredFields.add(field);
                    }
                }
                cls = cls.getSuperclass();
            }
            return desiredFields.toArray(new Field[desiredFields.size()]);
        } catch (SecurityException e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            Document document = serialize(new Ex1());
            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
            xmlOutput.output(document,System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
