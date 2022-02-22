package org.json.junit;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: JSON-java
 * @description:
 * @author: Mr. Su
 * @create: 2022-02-18 11:15
 **/

public class M4StreamTest {
    JSONObject jsonObject = null;
    @Before
    public void loadJSONObject(){
        jsonObject = new JSONObject("{\"menu\": {\n" +
                "  \"id\": \"file\",\n" +
                "  \"value\": \"File\",\n" +
                "  \"popup\": {\n" +
                "    \"menuitem\": [\n" +
                "      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" +
                "      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" +
                "      {\"value\": \"Submit\", \"onclick\": \"SubmitDoc()\"},\n" +
                "      {\"value\": \"Delete\", \"onclick\": \"DeleteDoc()\"},\n" +
                "      {\"value\": \"Revise\", \"onclick\": \"ReviseDoc()\"},\n" +
                "      {\"value\": \"Confirm\", \"onclick\": \"ConfirmDoc()\"},\n" +
                "      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" +
                "    ]\n" +
                "  }\n" +
                "}}");
    }

    /*
     * JSONObject stream(): print key and value
     * */
    @Test
    public void testJSONObjectStreamForEach(){
        System.out.println("------------------------");
        System.out.println("JSONObject stream: print out key & value");
        jsonObject.stream()
                .forEach(System.out::println);
    }

    /*
     * JSONObject stream(): filter and transform key to upper case
     * */
    @Test
    public void testJSONObjectStreamKeyToUpperCase(){
        System.out.println("------------------------");
        System.out.println("JSONObject stream: print out key & value");
        jsonObject.stream()
                .filter(e -> e.getValue() instanceof String)
                .map(e->{
                    return new AbstractMap.SimpleEntry<>(e.getKey().toUpperCase(), e.getValue());
                })
                .forEach(e->{
            System.out.println(e.getKey() + " : " + e.getValue());
        });
    }

    /*
    * JSONObject stream() to Map
    * */
    @Test
    public void testJSONObjectStreamToMap(){
        System.out.println("------------------------");
        System.out.println("JSONObject stream: print out key & value");
        Map<String, Object> map = jsonObject.stream()
                .collect(Collectors.toMap(e -> e.getKey().toUpperCase(), e -> e.getValue(), (x1, x2) -> x1));
    }

    /*
     * JSONObject stream():
     * (1) filter key and get the first match value
     * (2) filter value and get the first match key
     * */
    @Test
    public void testJSONStreamMatch(){
        System.out.println("------------------------");
        System.out.println("JSONObject stream: Retrieving a Match by key \"onclick\"");
        //match key
        Optional<Object> firstValue = jsonObject.stream()
                .filter(e -> "onclick".equals(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
        System.out.println(firstValue.get());

        System.out.println("------------------------");
        System.out.println("JSONObject stream: Retrieving a Match by value of \"CreateNewDoc()\"");
        //match value
        Optional<String> firstKey = jsonObject.stream()
                .filter(e -> "CreateNewDoc()".equals(e.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
        System.out.println(firstKey.get());
    }

    /*
     * JSONObject stream(): filter key and turn values to list
     * */
    @Test
    public void testJSONStreamMatchToList(){

        System.out.println("------------------------");
        System.out.println("JSONObject stream: Retrieving Multiple Results");
        //Retrieving Multiple Results
        List<Object> buttons = jsonObject.stream()
                .filter(e->"onclick".equals(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        for(Object button : buttons){
            System.out.println(button);
        }
    }

    /*
     * JSONObject stream(): filter and sort by key
     * */
    @Test
    public void testJSONStreamSortByKey(){
        System.out.println("------------------------");
        System.out.println("JSONObject stream: sort");
        //sort
        jsonObject.stream()
                .filter(e->e.getValue() instanceof String)
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(System.out::println);
    }
}
