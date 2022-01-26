package org.json.execption;

import org.json.JSONObject;

/**
 * @program: JSON-java
 * @description:
 * @author: Mr. Su
 * @create: 2022-01-22 15:33
 **/

public class JSONFoundExecption extends Exception {
    private String code;

    private Object jsonObject;



    public JSONFoundExecption(String code, String message) {
        super(message);
        this.setCode(code);
    }

    public JSONFoundExecption(String code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }
    public JSONFoundExecption(Object jsonObject) {
        super("success");
        if(jsonObject==null){
            this.setCode("404");
        }
        else{
            this.setCode("200");
        }
        this.setJsonObject(jsonObject);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

}
