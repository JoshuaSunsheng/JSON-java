<h1>Milestone 4</h1>

Add streaming methods to the library that allow the client code to chain operations on JSON nodes.

Streaming methods: src/main/java/org/json/JSONObject.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/main/java/org/json/JSONObject.java)

Test Units Cases: src/test/java/org/json/junit/M4StreamTest.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/test/java/org/json/junit/M4StreamTest.java)

Turn the JSONObject to the stream in the type of Map.Entry<String, Object>. 
In this way, it keeps the original key of JSONObject and easily be filtered, sorted, and applied to other actions.

For JSONArray in JSONObject, I add an index to each object in JSONArray as the key and add them into the stream.

<h2>Two ways of transforming JSONObject to stream</h2>

<h3>1. Recursive Stream</h3>

```java 
public Stream<Map.Entry<String, Object>> stream();
```

<h3>2. Spliterator of Stream</h3>

```java 
public Stream<Map.Entry<String, Object>> stream();
public Spliterator<Map.Entry<String, Object>> spliterator();
static class JSONObjectSpliterator implements Spliterator<Map.Entry<String, Object>>{
    public int characteristics();
    public long estimateSize();
    public boolean tryAdvance(Consumer<? super Map.Entry<String, Object>> action);
}
```

<p>In my opinion, the first way is more abstract but the code is more elegant.
The second way is more comprehensive and helpful in understanding stream. </p>

<h2>Maven</h2>
The project is based on Maven. 
Since Milestone 4 uses Stream, it needs to adjust the JDK to 1.8.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>2.3.2</version>
    <configuration>
        <!-- <source>1.6</source>-->
        <!-- <target>1.6</target>-->
        <source>1.8</source>
        <target>1.8</target>
    </configuration>
</plugin>
```

<h2>Test Units (Junit)</h2>

Test Units Cases: src/test/java/org/json/junit/M4StreamTest.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/test/java/org/json/junit/M4StreamTest.java)

<h3>Test streams features:</h3>

| methods   | Params |
| ---       | ---    | 
| forEach   | System.out::println | 
| filter    | e.getKey() or e.getValue() | 
| map       | e.getKey().toUpperCase() or Map.Entry::getValue | 
| collect   |  Collectors.toMap or Collectors.toList() | 
| findFirst | | 
| sorted    | Comparator.comparing(Map.Entry::getKey) | 

<h3>Test Unites Cases:</h3>

```java 
//JSONObject stream(): print key and value
@Test
public void testJSONObjectStreamForEach();


//JSONObject stream(): filter and transform key to upper case
@Test
public void testJSONObjectStreamKeyToUpperCase();

//JSONObject stream() to Map
@Test
public void testJSONObjectStreamToMap();

//JSONObject stream(): 
//(1) filter and get the first match value
//(2) filter value and get the first match key
@Test
public void testJSONStreamMatch();

//JSONObject stream(): filter key and turn values to list
@Test
public void testJSONStreamMatchToList()
```

<h2>Implement</h2>

Streaming methods: src/main/java/org/json/JSONObject.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/main/java/org/json/JSONObject.java)

There are two ways to transform JSONObject into stream.

<h3>1. Recursive Stream</h3>

```java 
/*
*   Recursive Stream
* */
public Stream<Map.Entry<String, Object>> stream(){
    Stream<Map.Entry<String, Object>> resultingStream = null;
    for(Map.Entry<String, Object> entry: map.entrySet()){
        //first, add new stuff to stream
        if(resultingStream == null){
            resultingStream = Stream.of(entry);
        }
        else{
            resultingStream = Stream.concat(resultingStream, Stream.of(entry));
        }
        //then, check whether to recursion
        Object object = entry.getValue();
        if(object instanceof JSONObject){
            //recursive call
            resultingStream = Stream.concat(resultingStream, ((JSONObject) object).stream());
        }
        else if(object instanceof  JSONArray){
            //transform a JSONArray to a JSONObject, then recursive call
            JSONArray jsonArray  = (JSONArray)(object);
            JSONObject json = new JSONObject(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                json.put(i + "", jsonArray.opt(i));
            }
            resultingStream = Stream.concat(resultingStream, json.stream());
        }
    }
    return resultingStream;
}
```

<h3>2. Spliterator</h3>

```java 
static class JSONObjectSpliterator implements Spliterator<Map.Entry<String, Object>> {
    private final JSONObject root;
    private JSONObject tree;
    
    public int characteristics() {...}
    public long estimateSize() {...}
    
    @Override
    public boolean tryAdvance(Consumer<? super Map.Entry<String, Object>> action) {
        JSONObject current = tree;

        for(Map.Entry<String, Object> entry : tree.entrySet()){
            action.accept(entry);
            Object value = entry.getValue();
            if(value instanceof  JSONObject){
                tree = (JSONObject) value;
                tryAdvance(action);
            }
            else if(value instanceof  JSONArray){
                 //transform a JSONArray to a JSONObject, then recursive call
                JSONArray jsonArray  = (JSONArray)(value);
                JSONObject json = new JSONObject(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    json.put(i + "", jsonArray.opt(i));
                }
                tree = json;
                tryAdvance(action);
            }
        }

        //Returns: false if no remaining elements existed upon entry to this method, else true.
        tree = current;

        if (tree == root)
            return false;
        else
            return true;
    }
}

```
