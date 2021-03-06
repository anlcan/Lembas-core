package com.happyblueduck.lembas.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.happyblueduck.lembas.core.LembasConstants.*;
/**
 * User: anlcan
 * Date: 9/13/11
 * Time: 4:06 PM
 */
public class LembasObject extends Object {


    // these fields will be used by data managers if necessary
    public String createDate;
    public String updateDate;
    public String objectKey;

    public LembasObject() {
    }

    /**
     * Return the name of the class.
     * <p>
     * This is typically the same name as the constructor.
     * Classes extending ScriptableObject must implement this abstract
     * method.
     * </p>
     *
     * @return classname
     */

    public String getClassName() {
        return this.getClass().getName();
    }

    /**
     * Returns discovery description of the object parsable by the sync-server
     *
     * @throws IllegalAccessException
     * @return description of the object in JSONObject format
     */
    public JSONObject discoDescription() throws IllegalAccessException, InstantiationException {

        JSONObject result = new JSONObject();
        //HashMap<String, Object> result = new HashMap<>();
        result.put(LembasUtil.typeIdentifier, "MObjectDef");
        String className = this.getClass().getSimpleName();
        result.put("name", className);

        String ofType = BASE_OBJECT;
        if (className.endsWith("Request")) {
            ofType = BASE_REQUEST;
        } else if (className.endsWith("Response")) {
            ofType = BASE_RESPONSE;
        }
        result.put("ofType", ofType); // TODO

        JSONArray properties = new JSONArray();

        for (Field f : LembasUtil.getAllFields(this.getClass())) {

            int modifiers = f.getModifiers();

            if (Modifier.isPrivate(modifiers)) continue;
            if (Modifier.isStatic(modifiers)) continue;
            if (Modifier.isFinal(modifiers)) continue;
            Class declaringClass = f.getType();

            if (f.getName().startsWith("_")) continue;

            JSONObject property = new JSONObject();
            property.put(LembasUtil.typeIdentifier, "MPropertyDef");
            property.put("propName", f.getName());
            property.put("propExtData", declaringClass.getSimpleName());

            String propertyType = null;

            if (LembasObject.class.isAssignableFrom(declaringClass)) {
                propertyType = OBJECT;

            } else if (declaringClass.isEnum()) {
                propertyType = ENUM;
                JSONObject exData = new JSONObject();
                exData.put("name", declaringClass.getSimpleName());
                JSONArray enumValues = new JSONArray();

                int i = 0;
                Object[] constants = declaringClass.getEnumConstants();
                for (Object o : constants) {
                    JSONObject enumProp = new JSONObject();

                    enumProp.put(LembasUtil.typeIdentifier, "MEnumValue");
                    enumProp.put("name", o.toString());
                    enumProp.put("value", i);

                    enumValues.add(enumProp);
                }

                exData.put("values", enumValues);
                property.put("propExtData", exData);

            } else if (declaringClass.equals(ArrayList.class)) {

                Type type = f.getGenericType();
                System.out.println("type: " + type);
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;

//                    System.out.println("raw type: " + pt.getRawType());
//                    System.out.println("owner type: " + pt.getOwnerType());
//                    System.out.println("actual type args:");

                    propertyType = ARRAY;

                    Type[] args = pt.getActualTypeArguments();
                    if (args.length > 0) {
                        Type t = args[0];

                        Class clazz = (Class) t;
                        String typeName = clazz.getSimpleName();
                        if (trans.get(clazz) != null) {
                            propertyType += trans.get(clazz);
                        } else {
                            // MOBJECT
                            propertyType += OBJECT;
                            property.put("propExtData", typeName);
                        }

                    } else
                        propertyType += "String";

                    System.out.println(propertyType);

                }
            } else if (trans.keySet().contains(declaringClass)) {
                propertyType = trans.get(declaringClass);

            } else {
                //logger.warning("\n**skipping " + declaringClass + " " + f.getName());
            }

            property.put("propType", propertyType);
            properties.add(property);
        }

        result.put("properties", properties);
        return result;
    }


    // DEAR FUTURE ME, please do not merge this two methods, these are
    // called from completely different contexts

    /**
     * initialize the object with default values, used  for discovery methods.
     *
     */
    public void init() throws IllegalAccessException, InstantiationException {

        for (Field f : LembasUtil.getAllFields(this.getClass())) {

            int modifiers = f.getModifiers();

            // marking an object private makes it invisible to client-side
            if (Modifier.isPrivate(modifiers)) continue;
            if (Modifier.isStatic(modifiers)) continue;
            if (Modifier.isFinal(modifiers)) continue;

            Class declaringClass = f.getType();

            if (declaringClass.equals(int.class)) {
                f.setInt(this, 1);

            } else if (declaringClass.equals(double.class)) {
                f.set(this, 1.0);

            } else if (declaringClass.equals(long.class)) {
                f.setLong(this, 1l);

            } else if (declaringClass.equals(String.class)) {
                f.set(this, "");

            } else if (declaringClass.equals(ArrayList.class)) {
                f.set(this, new ArrayList());

            } else if (declaringClass.equals(boolean.class)) {
                f.set(this, false);

            } else if (LembasObject.class.isAssignableFrom(declaringClass)) {

                LembasObject bo = (LembasObject) declaringClass.newInstance();
                bo.init();
                f.set(this, bo);

            } else if (declaringClass.isEnum()) {

                f.set(this, declaringClass.getEnumConstants()[0]);

            } else {
                //logger.warning("\n**skipping " + declaringClass + " " + f.getName());
            }
        }
    }

    public void setField(Field f, Object value ) throws IllegalAccessException {

        if (f.getType() != value.getClass()){
//            logger.info(String.format("casting value from %s: %s to %s of %s",
//                    f.getName(),
//                    value.getClass(),
//                    f.getType(),
//                    value));
//                Object d = f.getType().cast(value);
//                f.set(this, d);

            //  playing nice with Google Datasore Long
            if(value.getClass() == Long.class) {
                Long lvalue = (Long) value;
                if (f.getType() == Double.class) {
                    Double d = lvalue.doubleValue();
                    f.set(this, d);
                } else if (f.getType() == boolean.class) {
                    f.set(this, (lvalue.intValue() == 1));
                } else if (f.getType() == int.class ) {
                    f.set(this, lvalue.intValue());
                }
            }  else {
                // use the force for the rest
                try{
                    f.set(this,value);
                } catch(Exception e){
                    //logger.warning(e.getLocalizedMessage());
                }
            }

        } else
            f.set(this, value);
    }

    @Override
    public String toString() {
        try {
            return LembasUtil.serialize(this).toJSONString();
        } catch (UtilSerializeException e) {
            return "failed to serialize object";
        }


    }
}
