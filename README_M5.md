<h1>Milestone 5</h1>

Add asynchronous methods to the library that allow the client code to proceed.

Methods: src/main/java/org/json/XML.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/main/java/org/json/XML.java)

CustomFuture: src/main/java/org/json/CustomFuture.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/main/java/org/json/CustomFuture.java)

Test Units Cases: src/test/java/org/json/junit/XMLMyTestM5.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/test/java/org/json/junit/XMLMyTestM5.java)

<h2>Two ways of asynchronous implementation</h2>

<h3>1. Custom Future </h3>

```java 
public static CustomFuture<String> toJSONObject(Reader reader, BiFunction<JSONObject, String, String> fun, Function<Throwable, Object> fail);
```

<h3>2. Java Concurrent CompletableFuture</h3>

```java 
public static CompletableFuture<String> toJSONObject(Reader reader, BiFunction<JSONObject, String, String> fun, Function<Throwable, Object> fail);
```

<h2>Test Units (Junit)</h2>

Test Units Cases: src/test/java/org/json/junit/XMLMyTestM5.java

(https://github.com/JoshuaSunsheng/JSON-java/blob/master/src/test/java/org/json/junit/XMLMyTestM5.java)

<h3>Test functions:</h3>

| test methods   | description |
| ---       | ---    | 
| testAsyncToJSON   | read xml into JSONObject and store in file  | 
| testAsyncToJSONError    | simulate failure case |

<h2>Implement</h2>

There are two ways.

<h3>1. Custom Future</h3>

```java 
public class XML{
    /*
    * Milestone 5
    * Add asynchronous methods to the library that allow the client code to proceed
    * example: XML.toJSONObject(aReader, (JSONObject jo) -> {jo.write(aWriter);}, (Exception e) -> { something went wrong });
    *
    * */
    static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);//LinkedBlockingQueue
    public static CustomFuture<String> toJSONObject(Reader reader, BiFunction<JSONObject, String, String> fun, Function<Throwable, Object> fail) throws ExecutionException, InterruptedException {
        Function futureFun = (a) -> {
            Object msg = null;
            System.out.println("Async: " + Thread.currentThread().getName() + " is transforming xml to JSON in the background.");
            try{
                JSONObject jsonObject = toJSONObject(reader, XMLParserConfiguration.ORIGINAL);
                System.out.println("Async: xml to toJSONObject done");
                msg  = fun.apply(jsonObject, "M5_inner");
            }catch (Exception e){
                System.out.println("Async: Exception happens!");
                msg = fail.apply(e);
            }catch (Error e){
                System.out.println("Async: Error happens!");
                msg = fail.apply(e);
            }
            return msg;
        };
        CustomFuture<String> customFuture = new CustomFuture(futureFun);
        executor.submit(customFuture);
        return customFuture;
    }
}

public class CustomFuture<T> implements Runnable {
    boolean finished = false;
    T result;
    Function<String, T> function;

    public CustomFuture(Function function) {
        this.function = function;
    }

    public T get() throws InterruptedException {
        while (true) {
            Thread.currentThread().sleep(50);
            if (finished) break;
        }
        return result;
    }

    @Override
    public void run() {
        result = function.apply("");
        finished = true;
    }
}
```

<h3>2. Java Concurrent CompletableFuture</h3>

```java 
public class XML{
    public static CompletableFuture<String> toJSONObject(Reader reader, BiFunction<JSONObject, String, String> fun, Function<Throwable, Object> fail) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String msg = null;
            System.out.println("Async: " + Thread.currentThread().getName() + " is transforming xml to JSON in the background.");
            try {
                JSONObject jsonObject = toJSONObject(reader, XMLParserConfiguration.ORIGINAL);
                System.out.println("Async: xml to toJSONObject done");
                msg = fun.apply(jsonObject, "M5_inner");
            } catch (Throwable e) {
                System.out.println("Async: Something wrong happens!");
                msg = (String) fail.apply(e);
            }
            return msg;
        });
        return completableFuture;
    }
}
```
