package org.json.junit;

import org.json.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.function.Function;

/**
 * @program: JSON-java for Milestone 3
 * @description:  Read an XML file into a JSON object, and add the prefix "swe262_" to all of its keys.
 *                Unit tests for inside the library vs. doing it in client code
 * @author: Mr. Su
 * @create: 2022-01-25 11:26
 **/

public class XMLMyTestM3 {
    Reader xmlReader;
    String xmlFileName;
    InputStream xmlStream = null;
    @Before
    public void load() {
        xmlFileName = "Issue537.xml";
//        xmlFileName = "enwiki-250MB.xml";
//        xmlFileName = "enwiki-490MB.xml";
        xmlStream = XMLMyTestM3.class.getClassLoader().getResourceAsStream(xmlFileName);
        this.xmlReader = new InputStreamReader(xmlStream);
    }

    @After
    public void prepare() throws IOException {
        xmlStream.close();
    }

    /*
     * static JSONObject toJSONObject(Reader reader, JSONPointer path)
     * Two ways output file is different in key sequence.
     * because they calculate hashcode in different hash size
     * and the key will insert into different
     */
    //replace key in client code
    @Test
    public void testToJSONKeyReplaceInClient() throws IOException {
        long startTime = System.currentTimeMillis();
        //replace key outside library, in client code
        JSONObject jsonObject;
        jsonObject = XML.toJSONObject(xmlReader);
        refactorKeyName(jsonObject, "swe262_");
        write2File(jsonObject, "M3_outLib");
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONKeyReplaceInClient Run time：" + (endTime - startTime) + "ms");
    }

    //replace key in the library
    @Test
    public void testToJSONKeyReplaceWithKeyTransformer() throws IOException {
        long startTime = System.currentTimeMillis();
        Function func = cc-> {
            return "swe262_"+cc;
        };
        _testToJSONKeyReplaceWithKeyTransformer(func);
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONKeyReplaceWithKeyTransformer Run time：" + (endTime - startTime) + "ms");
    }

    //reverse key in the library
    @Test
    public void testToJSONKeyReverseWithKeyTransformer() throws IOException {
        long startTime = System.currentTimeMillis();
        Function func = cc-> {
            StringBuilder sb = new StringBuilder((String) cc);
            sb.reverse();
            return sb.toString();
        };
        _testToJSONKeyReplaceWithKeyTransformer(func);
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONKeyReplaceWithKeyTransformer Run time：" + (endTime - startTime) + "ms");
    }

//    @Test
    public void _testToJSONKeyReplaceWithKeyTransformer(Function func) throws IOException {
        try {
            //replace key inside library
            JSONObject jsonObject;
            jsonObject = XML.toJSONObject(xmlReader, func);
            write2File(jsonObject, "M3_inLib");
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    private static void refactorKeyName(JSONObject jsonObject, String prefix) {
        Set<String> sets = new HashSet(jsonObject.keySet());
        for(String key : sets){
            Object o = jsonObject.get(key);
            refactorByType(prefix, o);
            jsonObject.remove(key);
            jsonObject.put(prefix+key, o);
        }
    }

    //support both jsonObject and jsonArray
    private static void refactorByType(String prefix, Object object) {
        if (object instanceof JSONObject) {
            refactorKeyName((JSONObject) object, prefix);
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray)object;
            for(Object o : jsonArray) {
                refactorByType( prefix, o);
            }
        }
    }

    private void write2File(JSONObject json, String ex) throws IOException {
        String xmlRealPath = this.getClass().getResource("/" + xmlFileName).getPath();
        FileWriter out = new FileWriter(xmlRealPath.replace("xml", "json").replace(".json", "_" + ex + "_sub.json"));
        BufferedWriter bw = new BufferedWriter(out);
        bw.write(json == null ? "" : json.toString(4));
        bw.close();
    }


    @Test
    public void testHashMap1(){
        HashMap<String, String> map = new HashMap();
        map.put("verification_date", "verification_date");
        map.put("last_update_submitted", "last_update_submitted");
        for(String key: map.keySet()){
            String value = map.get(key);
            map.remove(key);
            key = "swe262_" + key;
            map.put(key, value);
            System.out.println(key + ":" + map.get(key));
        }
    }

    @Test
    public void testHashMap2(){
        HashMap<String, String> map = new HashMap();
        map.put("swe262_verification_date", "verification_date");
        map.put("swe262_last_update_submitted", "last_update_submitted");

        for(String key: map.keySet()){
            System.out.println(key + ":" + map.get(key));
        }
    }

    @Test
    public void testHashMap3(){
        HashMap<String, String> map = new HashMap();
        map.put("verification_date", "verification_date");
        map.put("last_update_submitted", "last_update_submitted");

        for(String key: map.keySet()){
            System.out.println(key + ":" + map.get(key));
        }
    }
}
