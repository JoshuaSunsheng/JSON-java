package org.json.junit;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.XML;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.fail;

/**
 * @program: JSON-java
 * @description:
 * @author: Mr. Su
 * @create: 2022-01-25 11:26
 **/

public class XMLMyTest {
    JSONPointer path;
    Reader xmlReader;
    String xmlFileName;
    InputStream xmlStream = null;
    JSONObject replacement;
    @Before
    public void load() {

        xmlFileName = "Issue537.xml";
        replacement = XML.toJSONObject("<required_header><testKey>testContent</testKey></required_header>");

//                JSONPointer path = new JSONPointer("/clinical_study/required_header/download_date");//String
        JSONPointer path = new JSONPointer("/clinical_study/required_header");//JSONObject
//                JSONPointer path = new JSONPointer("/clinical_study/condition_browse/mesh_term"); //mesh_term is JSONArray
//                JSONPointer path = new JSONPointer("/clinical_study/condition_browse/mesh_term/1");
//                JSONPointer path = new JSONPointer("/clinical_study/condition_browse/mesh_term1"); //no exist
//                JSONPointer path = new JSONPointer("/condition_browse/mesh_term"); //no exist
//                JSONPointer path = new JSONPointer("/clinical_study/condition_browse1/mesh_term"); //no exist
//                JSONPointer path = new JSONPointer("/clinical_study/condition_browse/mesh_term/3"); //out of array
        this.path = path;
        xmlStream = XMLMyTest.class.getClassLoader().getResourceAsStream(xmlFileName);
        this.xmlReader = new InputStreamReader(xmlStream);

    }

    @After
    public void prepare() throws IOException {
        xmlStream.close();
    }

    /*
     * static JSONObject toJSONObject(Reader reader, JSONPointer path)
     */
    //not using library
    @Test
    public void testToJSONWithReaderAndPointer1() throws IOException {
        long startTime = System.currentTimeMillis();
        _testToJSONWithReaderAndPointer(false);
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONWithReaderAndPointer1 Run time：" + (endTime - startTime) + "ms");
    }
    //using library
    @Test
    public void testToJSONWithReaderAndPointer2() throws IOException {
        long startTime = System.currentTimeMillis();
        _testToJSONWithReaderAndPointer(true);
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONWithReaderAndPointer2 Run time：" + (endTime - startTime) + "ms");
    }

    private void _testToJSONWithReaderAndPointer(boolean isInLibrary) throws IOException {
        Object actual;
        if (isInLibrary) {
            //do query inside library
            actual = XML.toJSONObject(xmlReader, path);
            write2File(actual, "inLib");
        } else {
            //do query outside library
            actual = path.queryFrom(XML.toJSONObject(xmlReader));
            write2File(actual, "outLib");
        }
//        System.out.println(actual == null ? actual : actual.toString());
    }


    private void write2File(Object actual, String ex) throws IOException {
        String xmlRealPath = this.getClass().getResource("/" + xmlFileName).getPath();
        FileWriter out = new FileWriter(xmlRealPath.replace("xml", "json").replace(".json", "_" + ex + "_sub.json"));
        BufferedWriter bw = new BufferedWriter(out);
        bw.write(actual == null ? "" : actual.toString());
        bw.close();
    }

    /*
     * static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement)
     */
    //not using library
    @Test
    public void testToJSONWithReaderAndPointerWithReplace1() throws IOException {
        long startTime = System.currentTimeMillis();
        _testToJSONWithReaderAndPointerWithReplace(false);
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONWithReaderAndPointerWithReplace1 Run time：" + (endTime - startTime) + "ms");
    }
    //using library
    @Test
    public void testToJSONWithReaderAndPointerWithReplace2() throws IOException {
        long startTime = System.currentTimeMillis();
        _testToJSONWithReaderAndPointerWithReplace(true);
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("testToJSONWithReaderAndPointerWithReplace2 Run time：" + (endTime - startTime) + "ms");
    }

    private void _testToJSONWithReaderAndPointerWithReplace(boolean isInLibrary) throws IOException {
        Object actual;
        if (isInLibrary) {
            //do query inside library
            actual = XML.toJSONObject(xmlReader, path, replacement);
            write2File(actual, "replace_inLib");
        } else {
            //do query outside library
            String[] strs = path.toString().split("/");
            String keyName = strs[strs.length - 1];
            String parentPath = "";
            for(int i=1; i<strs.length-1; i++){
                parentPath +="/" + strs[i];
            }
            //JSONPointer.queryFrom not support end of "/"
            JSONObject jsonObject = XML.toJSONObject(xmlReader);
            actual =  new JSONPointer(parentPath).queryFrom(jsonObject);
            JSONObject parent = (JSONObject) actual;
            parent.put(keyName, replacement);
            write2File(jsonObject, "replace_outLib");
        }
//        System.out.println(actual == null ? actual : actual.toString());
    }
}
