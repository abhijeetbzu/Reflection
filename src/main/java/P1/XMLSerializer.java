package P1;

import org.jdom2.Document;
import org.jdom2.Element;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLSerializer {
    private static Document document;
    private static Map<Object, Integer> objectMap;

    private static void initialize() {
        objectMap = new HashMap<>();

        document = new Document();
        Element objectEle = new Element("serialized");
        document.setRootElement(objectEle);
    }

    private static void reset() {
        objectMap = null;
        document = null;
    }

    public static Document serialize(Object obj) throws Exception {
        initialize();

        beginSerialize(obj, objectMap);
        Document finalizedDoc = document;

        reset();
        return finalizedDoc;
    }

    private static Element getObjectElement(Object obj) {
        Element element = new Element("object");
        Class cls = obj.getClass();
        element.setAttribute("class", cls.getName());
        return element;
    }

    private static Element getArrayElement(Object obj) {
        Element element = new Element("array");
        Class cls = obj.getClass();
        element.setAttribute("class", cls.getName());
        return element;
    }

    private static Integer getIdForElement(Object obj, Map<Object, Integer> objectMap) {
        if (objectMap.containsKey(obj)) {
            return objectMap.get(obj);
        }
        return -1;
    }

    private static Integer getIdForElement(Object obj, Map<Object, Integer> objectMap, boolean putIfAbsent) {
        int id = getIdForElement(obj, objectMap);
        if (putIfAbsent && id == -1) {
            objectMap.put(obj, objectMap.size());
            return objectMap.size() - 1;
        }
        return id;
    }

    private static Element beginSerialize(Object obj, Map<Object, Integer> objectMap) throws Exception {
        try {
            Element parent = getObjectElement(obj);
            String elementId = String.valueOf(getIdForElement(obj, objectMap, true));
            parent.setAttribute("id", elementId);

            Field[] fields = getDeclaredFields(obj.getClass());
            for (Field field : fields) {
                Element child = serializeElement(obj, field, objectMap);
                parent.addContent(child);
            }

            document.getRootElement().addContent(parent);
            return parent;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static Element getArrayElement(Field field, Object array, Map<Object, Integer> objectMap) throws Exception {
        int length = Array.getLength(array);
        Element arrayElement = getArrayElement(array);
        for (int i = 0; i < length; i++) {
            Object val = Array.get(array, i);
            Element fieldElement = loadElement(field, val, objectMap);
            arrayElement.addContent(fieldElement);
        }
        return arrayElement;
    }

    private static Element loadElement(Field field, Object value, Map<Object, Integer> objectMap) throws Exception {
        Element element = new Element("field");
        String enclosingClassName = field.getDeclaringClass().getName();
        String className = value.getClass().getName();
        String fieldName = field.getName();

        element.setAttribute("enclosingClass", enclosingClassName);
        element.setAttribute("class", className);
        element.setAttribute("name", fieldName);

        Class cls = field.getType();
        if (cls.isPrimitive() || cls == String.class) {
            Element val = new Element("value");
            val.setText(value.toString());
            element.addContent(val);
        } else {
            Element ref = new Element("reference");
            Integer refId = getIdForElement(value, objectMap);
            if (refId != -1) //element not serialized yet
                ref.setText(refId.toString());
            else {
                beginSerialize(value, objectMap);
                ref.setText(String.valueOf(getIdForElement(value, objectMap))); //get new id of element after serialization
            }
            element.addContent(ref);
        }
        return element;
    }

    private static Element serializeElement(Object obj, Field field, Map<Object, Integer> objectMap) throws Exception {
        try {
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
            Object fieldObj = field.get(obj);
            Class cls = fieldObj.getClass();
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
}
