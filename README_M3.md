<h1>Milestone 3</h1>

Read an XML file into a JSON object, and add the prefix "swe262_" to all of its keys. 
Do it by adding an overloaded static method to the XML class with the signature

<h2>Overload function "toJSONObject" and "parse".</h2>

```
public static JSONObject toJSONObject(Reader reader, Function keyTransformer);

private static boolean parse(XMLTokener x, JSONObject context, String name, XMLParserConfiguration config, Function keyTransformer);
```


<h2>Maven</h2>
The project is based on Maven. 
Since Milestone 3 uses Function as a parameter, it needs to adjust the jdk to 1.8.

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

<p>src/test/java/org/json/junit/XMLMyTestM3.java:</p>

```
//replace key in client code
public void testToJSONKeyReplaceInClient();

//replace key in the library
public void testToJSONKeyReplaceWithKeyTransformer() throws IOException {
    ...
    Function func = cc-> {
        return "swe262_"+cc;
    };
    ...
}

//key reverse in the library
public void testToJSONKeyReverseWithKeyTransformer() throws IOException {
    ...
    Function func = cc-> {
        StringBuilder sb = new StringBuilder((String) cc);
        sb.reverse();
        return sb.toString();
    };
    ...
}
```


<h3>Key replace in client VS. doing it inside the library</h3>

| function \ file size | 8K | 250MB | 490MB |
| ---       | ---    | ---     | ---     |
| testToJSONKeyReplaceInClient | 18ms   | 43,045ms    | 103,838ms   |
| testToJSONKeyReplaceWithKeyTransformer | 78ms    | 33,132ms    | 89,854ms  |

Function "testToJSONKeyReplaceInClient" replaces key in client code. 
Function "testToJSONKeyReplaceWithKeyTransformer" replaces key in library, not in client code.

From testing result, we can see that for a small file, doing key replace in client code runs faster than doing it inside library.
But for big files, doing key replace inside library runs much faster than doing it in client code.

I guess that replacing the key in the library by passing a function of KeyTransformer has additional calling consuming time. 
For small files, this calling consuming time is significant. But for big files, it can be trivial. 


<h2>Implement</h2>

To execute the task of key transformation, put the function of "keyTransformer" in three places.

<h3>1. deals with global tagName for most cases: put keyTransformer inside callback "parse" (tagName meet with pair end tagName).</h3>

```
    if (parse(x, jsonObject, tagName, config, keyTransformer)) {
        tagName = (String) keyTransformer.apply(tagName);
        ...
    }
```

<h3>2. to deal with special xml format, like below</h3>

```xml
<last_update_posted type="Actual">September 12, 2019</last_update_posted>
```

```json
{
  "swe262_last_update_posted": {
    "swe262_type": "Actual",
    "swe262_content": "September 12, 2019"
  }
}
```

<h4>2.1 When First token is a String, second token is also a String, and third token is a "=".</h4>

Like below xml, first token is "last_update_posted", second token is "type", third token is "="

```xml
<last_update_posted type="Actual">September 12, 2019</last_update_posted>
```

If it comes across this case, it needs to put a keyTransformer here.

```
if (token instanceof String) {
    string = (String) token;
    //put keyTransformer
    string = (String) keyTransformer.apply(string);
    if (token == EQ) {
        if (config.isConvertNilAttributeToNull()
        } else if(config.getXsiTypeMap() != null && !config.getXsiTypeMap().isEmpty()
        } else if (!nilAttributeFound) {
               jsonObject.accumulate(string,
                    config.isKeepStrings()
                            ? ((String) token)
                            : stringToValue((String) token));
        }
        ...
    }
    ...
}
```

In this circumstance that it would have added "type" tag into "last_update_posted", like below:

```json
{
  "swe262_last_update_posted": {
    "swe262_type": "Actual"
  }
}
```

<h4>2.2 When it is going to add content of "September 12, 2019" directly to JsonObject "last_update_posted".</h4>

Here is to deal with this kind of token tagName "content".

```
//add "if" predicate logic
if(jsonObject.length()>0){
    //put keyTransformer
    String configTagName = (String) keyTransformer.apply(config.getcDataTagName());
    jsonObject.accumulate(configTagName,
    config.isKeepStrings() ? string : stringToValue(string));
}
else {
    jsonObject.accumulate(config.getcDataTagName(),
    config.isKeepStrings() ? string : stringToValue(string));
}
```

By putting keyTransformer in these two places, it can successfully deal with this case and turn it into correct output.

```json
{
  "swe262_last_update_posted": {
    "swe262_type": "Actual",
    "swe262_content": "September 12, 2019"
  }
}
```

