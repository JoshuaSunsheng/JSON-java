<h1>Milestone 2</h1>

<h2>Summary of Support & Not Support</h2>

<h3>toJSONObject(Reader reader, JSONPointer path)</h3>

<h4>support path:</h4>

```
JSONObject: /clinical_study/required_header

JSONArray: /clinical_study/condition_browse/mesh_term

Index of JSONArray: /clinical_study/condition_browse/mesh_term/1
```

<h4>not support path:</h4>

```
Nested JSONArray: /clinical_study/1/condition_browse/mesh_term/1
```

<h3>toJSONObject(Reader reader, JSONPointer path, JSONObject replacement)</h3>

<h4>support path:</h4>

```
JSONObject: /clinical_study/required_header

JSONArray: /clinical_study/condition_browse/mesh_term
```

<h4>not support path:</h4>

```
Index of JSONArray: /clinical_study/condition_browse/mesh_term/1
```

<h3>List of Functions</h3>
<p>org/json/XML.java:</p>

    static Object toJSONObject(Reader reader, JSONPointer path)
    static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement)
    static JSONObject getJsonObjectWithPath(Reader reader, JSONPointer path, JSONObject replace)
    static boolean parseWithPath(XMLTokener x, JSONObject context, String name, 
                                    XMLParserConfiguration config, String[] paths, 
                                int level, int pLevel, JSONObject replacement)

<p>org/json/execption/JSONFoundExecption.java:</p>

    JSONFoundExecption(Object jsonObject)


<h3>Test Units (Junit)</h3>
<p>org/json/junit/XMLMyTest.java:</p>

    testToJSONWithReaderAndPointer1();//not using library
    testToJSONWithReaderAndPointer2();//using library
    //_testToJSONWithReaderAndPointer(boolean isInLibrary)

    testToJSONWithReaderAndPointerWithReplace1();//not using library
    testToJSONWithReaderAndPointerWithReplace2();//using library
    //_testToJSONWithReaderAndPointerWithReplace(boolean isInLibrary)

<h3>Test Result (Issue537.xml)</h3>

<p>Task2 and task5 using library inside run less time than using library outside.</p>

```
testToJSONWithReaderAndPointer1 Run time：8ms
testToJSONWithReaderAndPointer2 Run time：3ms
testToJSONWithReaderAndPointerWithReplace1 Run time：5ms
testToJSONWithReaderAndPointerWithReplace2 Run time：5ms
```

	
<h2>Part 1: Implementations functions</h2>

<h3>static JSONObject toJSONObject(Reader reader, JSONPointer path) </h3>

```
	/**
     *  milestone 2
     *  Read an XML file into a JSON object, and extract some smaller sub-object inside,
     *  given a certain path (use JSONPointer). 
     * @param reader
     *            The source string.
     * @param path Configuration options for the parser.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown if there is an errors while parsing the string
     */
    public static Object toJSONObject(Reader reader, JSONPointer path) {
        try {
            getJsonObjectWithPath(reader, path, null);
        } catch (JSONFoundExecption e) {
            System.out.println(e.getMessage());
            if (e.getCode().equals("200")) {
                return e.getJsonObject();
            }
        }
        return null;
    }
```
```
    /*
    *
    * @param level: JSON level depth
    * @param pLevel: path level depth
    * @param replace: JSONObject
    * */
    private static JSONObject getJsonObjectWithPath(Reader reader, JSONPointer path, JSONObject replace) throws JSONFoundExecption {
        JSONObject jo = new JSONObject();
        XMLTokener x = new XMLTokener(reader);
        String[] paths = path.toString().split("/");
        paths = Arrays.copyOfRange(paths, 1, paths.length);

        while (x.more()) {
            x.skipPast("<");
            if (x.more()) {
                parseWithPath(x, jo, null, XMLParserConfiguration.ORIGINAL, paths, 0, 0, replace);
            }
        }
        return jo;
    }
```
```
    private static boolean parseWithPath(XMLTokener x, JSONObject context, String name, XMLParserConfiguration config, String[] paths, int level, int pLevel, JSONObject replacement)
            throws JSONException, JSONFoundExecption{
        if (token == BANG) {
        } else if (c == '[') {
        } else if (token == QUEST) {
        } else if (token == SLASH) {
        } else if (token instanceof Character) {
        } else {

            //add by Sunsheng Su
            if(level == pLevel){
                if(paths[pLevel].equals(tagName)){
                if(pLevel < paths.length-1){
                    pLevel++;
                }
    
                //for toJSONObject with replacement
                if(replacement != null && pLevel == level){ //paths.length -1
                    x.skipPast("</" +tagName+ ">");
                    //from professor Cristina test demo and responses to Justin, replacement has the last key of paths
                    context.put(tagName, replacement.get(tagName));
                    return false;
                }
            }
            else if(replacement == null){
                x.skipPast("</" +tagName+ ">");
                return false;
                }
            }

            for (;;){
                if (token == null) {
                if (token instanceof String) {
                } else if (token == SLASH) {
                } else if (token == GT) {
                    for (;;) {
                        if (token == null) {
                        } else if (token instanceof String) {
                        } else if (token == LT) {
                            if (parseWithPath(x, jsonObject, tagName, config, paths, level+1, pLevel, replacement)) {
                                if (config.getForceList().contains(tagName)) {
                                } else {
                                }
            
                                /*add by Sunsheng Su
                                pLevel == path.length-1 (no need)
                                pLevel not equal to level unless pLevel reach the last one
                                this block code only executed when tagName has been found totally, including children
                                 */
                                if(replacement == null  && pLevel == paths.length-1 && level == pLevel-1){
                                    //for normal path, , like /a/b/c, this is b level and to get tagName c
                                    if (paths[pLevel].split("[^0-9]").length != 1){
                                        throw new JSONFoundExecption( jsonObject.opt(paths[pLevel]));
                                    }
                                    //for path with index, like /a/b/c/1, this is c level, and accumulate until 1.
                                    else{
                                        int index = Integer.parseInt(paths[pLevel]);
                                        if(index == 0){
                                            throw new JSONFoundExecption( jsonObject.opt(config.getcDataTagName()));
                                        }
                                        else if(context.get(paths[pLevel-1]) instanceof JSONArray){
                                            if(context.getJSONArray(paths[pLevel-1]).length() == index+1)
                                                throw new JSONFoundExecption( jsonObject.opt(config.getcDataTagName()));
                                        }
                                    }
                                }
                                return false;
                            }
                        }
                    }
                }
            }    
        }
    }
```


<h3>static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement) </h3>

```
	/**
     *  milestone 2
     *  Read an XML file into a JSON object, replace a sub-object on a certain key path
     *  with another JSON object that you construct,
     *
     * @param reader
     *            The source string.
     * @param path Configuration options for the parser.
     * @param replacement Configuration options for the parser.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown if there is an errors while parsing the string
     */
    public static JSONObject toJSONObject(Reader reader, JSONPointer path, JSONObject replacement) {

        JSONObject jo = null;
        try {
            jo = getJsonObjectWithPath(reader, path, replacement);
        } catch (JSONFoundExecption e) {
            e.printStackTrace();
        }
        return jo;
    }
```

<h2>Part 2: Test methods</h2>
<p>We don't need to have a JSON docuemnt to work. This project also admits conversions from other type of files.</p>
<p>Secondly, we can also convert from JSON to those type of files.</p>
	
<h3>Test toJSONObject(Reader reader, JSONPointer path) </h3>

```
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
```


<h3>Test toJSONObject(Reader reader, JSONPointer path, JSONObject replacement)  </h3>

```
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
```

