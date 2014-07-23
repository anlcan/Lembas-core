package com.telera.lembas;

import com.happyblueduck.lembas.core.LembasUtil;
import com.happyblueduck.lembas.core.UtilSerializeException;
import com.happyblueduck.lembas.settings.Config;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * User: anlcan
 * Date: 5/30/14
 * Time: 5:27 PM
 */
public class Tests {

    @Before
    public void setup(){
        Config.addArtifact("com.telera.lembas");
    }

    @Test
    public void integrating() throws UtilSerializeException {

        Matrix one = new Matrix();
        one.neo  =" is the one";
        one.trinity = false;
        one.tank = 1l;
        one.cypher = 2d;
        one.objectKey = UUID.randomUUID().toString();

        JSONObject o = LembasUtil.serialize(one);

        Matrix two = (Matrix) LembasUtil.deserialize(o);

        assertEquals(one.neo, two.neo);
        assertEquals(one.trinity, two.trinity);
        assertEquals(one.tank, two.tank);
        assertEquals(one.cypher, two.cypher);
        assertEquals(one.objectKey, two.objectKey);
    }

}


