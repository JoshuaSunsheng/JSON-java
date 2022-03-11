package org.json.junit;

import org.json.CustomFuture;
import org.json.JSONObject;
import org.json.XML;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: JSON-java
 * @description: Add asynchronous methods to the library that allow the client code to proceed
 * @author: Mr. Su
 * @create: 2022-02-28
 **/

public class XMLMyTestM5 {
    Reader xmlReader;
    String xmlFileName;
    InputStream xmlStream = null;
    long startTime;
//    CompletableFuture<String> future;
    CustomFuture<String> future;

    @Before
    public void load() {
        startTime = System.currentTimeMillis();

//        xmlFileName = "Issue537.xml";
        xmlFileName = "enwiki-250MB.xml";
//        xmlFileName = "enwiki-490MB.xml";
        xmlStream = XMLMyTestM3.class.getClassLoader().getResourceAsStream(xmlFileName);
        this.xmlReader = new InputStreamReader(xmlStream);
    }

    @After
    public void end() {
        long endTime = System.currentTimeMillis();    //get end time
        System.out.println("Main: testAsyncToJSON Run time：" + (endTime - startTime) + "ms");
    }

    //success case
    @Test
    public void testAsyncToJSON() throws IOException, ExecutionException, InterruptedException {
        BiFunction<JSONObject, String, String> funSuccess = (jsonObject, str) -> {
            System.out.println("Async: funSuccess writing into disk");
            try {
                write2File(jsonObject, str);
                return "Success";
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        future = XML.toJSONObject(xmlReader, funSuccess, funFail);
        System.out.println("Main: Approach the end of main code");
        System.out.println("Main: The whole application ends with " + future.get());
    }

    //Error case
    @Test
    public void testAsyncToJSONError() throws IOException, ExecutionException, InterruptedException {
        BiFunction<JSONObject, String, String> funSuccess = (jsonObject, str) -> {
            //simulate runtime exception
            if(true) throw new RuntimeException("test error in funFail ");
            return "";
        };
        future = XML.toJSONObject(xmlReader, funSuccess, funFail);
        System.out.println("Main: Approach the end of main code");
        System.out.println("Main: The whole application ends with " + future.get());
    }
    //Exception funciton
    Function<Throwable, Object> funFail = e -> {
        System.out.println("Async: Catch failure, now funFail printing stackTrace");
        e.printStackTrace();
        return "Error";
    };

    //Auxiliary function
    private void write2File(JSONObject json, String ex) throws IOException {
        String xmlRealPath = this.getClass().getResource("/" + xmlFileName).getPath();
        FileWriter out = new FileWriter(xmlRealPath.replace("xml", "json").replace(".json", "_" + ex + "_sub.json"));
        BufferedWriter bw = new BufferedWriter(out);
        bw.write(json == null ? "" : json.toString(4));
        bw.close();
    }
}
