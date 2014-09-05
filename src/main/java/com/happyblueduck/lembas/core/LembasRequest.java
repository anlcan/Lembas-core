package com.happyblueduck.lembas.core;

import com.happyblueduck.lembas.commons.LembasFault;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 9/13/11
 * Time: 4:05 PM
 */
public abstract class LembasRequest extends LembasObject{

    private  Logger logger;

    public static String HOST;
    private String host;

    // ...only public instance vars are going to be serialized
    public String session;
    public String deviceId;

    private HashMap<String, String> additionalHeaders;

    public String verb;

    public String getVerb() {
        // By default, resolve VERB from ClassName
        if (null == verb) {

            String className = this.getClass().getSimpleName();
            this.verb = className.substring(0, className.length() - "Request".length());
        }
        return verb;
    }

    public String _type;

    private URLConnection connection;
    public URLConnection getConnection() {
        return connection;
    }



    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(this.getClass().getName());
            // logger.addHandler(new InnerLogger());
        }
        return logger;
    }

    public String getHost() {
        if ( null != host)
            return host;        else
            return HOST;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void addHeader(String header, String value) {
        if (additionalHeaders == null)
            additionalHeaders = new HashMap<String, String>();

        additionalHeaders.put(header, value);
    }


    public LembasResponse run() throws UtilSerializeException {

        getLogger().info("Sending Lembas Request: "+ this.getClass().getSimpleName());


        LembasResponse response = null;
        try {

            JSONObject request = LembasUtil.serialize(this);
            JSONObject wrapper = new JSONObject();
            wrapper.put("request", request);

            URL url = new URL(getHost() + getVerb());
            connection = url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("User-Agent", "google app engine");

            connection.setConnectTimeout(35 * 1000);

            if (additionalHeaders != null)
                for (String key : additionalHeaders.keySet())
                    connection.setRequestProperty(key, additionalHeaders.get(key));
            connection.setDoOutput(true);

            logger.info("sending ->    " + getHost() + verb);
            logger.info("content:      " + wrapper.toString());
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            out.write(wrapper.toString());
            out.close();

            response = this.readStream(connection.getInputStream());

        } catch (Exception e) {
            logger.warn("Exception while reading running:");
            logger.warn(e.getLocalizedMessage());
            logger.warn(e.getClass().getCanonicalName());

            response = new LembasFault("Run exception");
        }

        return response;
    }

    private LembasResponse readStream(InputStream is) throws UtilSerializeException, IOException {

        InputStreamReader isr = new InputStreamReader(is, "UTF8");

        StringBuffer buffer = new StringBuffer();
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char) ch);
        }


        String jsonString = buffer.toString().trim();
        logger.info("received->" + jsonString);
        JSONObject json = (JSONObject) JSONValue.parse(jsonString);
        // check for Error
        JSONObject jsonError = (JSONObject) json.get("Error");
        if (jsonError != null) {
            //LembasFault
            logger.error("REQUEST FAILED!!!!");
            //throw new RequestProcessException("Error", (String) jsonError.get("exceptionName") + ":" + jsonError.get("message"));
            throw new IOException(jsonError.get("exceptionName") + ":" + jsonError.get("message"));

        } else {
            JSONObject response = (JSONObject) json.get("Result");
            return (LembasResponse) LembasUtil.deserialize((response));
        }


    }

    public  class Wrapper {

        public LembasRequest request;

        private Wrapper(LembasRequest request) {
            this.request = request;
        }
    }

}
