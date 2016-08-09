package com.happyblueduck.lembas.core;

import java.util.Date;
import java.util.HashMap;

/**
 * User: anlcan
 * Date: 1/13/14
 * Time: 1:08 PM
 */
public class LembasConstants {

    private LembasConstants(){

    }

    public static final String BASE_OBJECT      = "BASE_OBJECT";
    public static final String BASE_RESPONSE    = "BASE_RESPONSE";
    public static final String BASE_REQUEST     = "BASE_REQUEST";

    public static final String OBJECT   = "MOBJECT";
    public static final String ENUM     = "MENUM";
    public static final String ARRAY    = "ARRAYOF_";

    public static final String INT      = "MINT";
    public static final String DOUBLE   = "MDOUBLE";
    public static final String LONG     = "MLONG";
    public static final String STRING   = "MSTRING";
    public static final String BOOL     = "MBOOL";
    public static final String DATE     = "MDATE";

    // look up the generate python utility to find its reciproque
    public static HashMap<Class, String> trans = new HashMap<Class, String>();


    static {

        trans.put(int.class,    INT);
        trans.put(Integer.class, INT);
        trans.put(double.class, DOUBLE);
        trans.put(Double.class, DOUBLE);
        trans.put(long.class,   LONG);
        trans.put(Long.class,   LONG);
        trans.put(Boolean.class, BOOL);
        trans.put(boolean.class, BOOL);
        trans.put(String.class, STRING);
        trans.put(Date.class,   DATE);

        //trans.put(ArrayList.class,  "ARRAYOF_"); // objective c generator wont search for type

    }
}
