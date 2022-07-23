package com.jielu.web;

public class R<W> {

    public  static  final  int SUCCESS_CODE=20000;

    public  static  final  int FAIL_CODE=50000;

    private final   W w;

    private  int responseCode;

    private  String message;

    public R(W w) {
        this.w = w;
    }

    public R(W w, int responseCode) {
        this.w = w;
        this.responseCode = responseCode;
    }

    public R(W w, int responseCode, String message) {
        this.w = w;
        this.responseCode = responseCode;
        this.message = message;
    }

    public  static <W>  R<W> success(W w){
        return  new R(w,SUCCESS_CODE);
    }

    public  static final  <W>  R<W> success(W w,String message){
        return  new R(w,SUCCESS_CODE,message);
    }

    public W getW() {
        return w;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    public  static final <W>  R<W> fail(W w){
        return  new R(w,FAIL_CODE);
    }

    public  static final  <W>  R<W> fail(W w,String message){
        return  new R(w,FAIL_CODE,message);
    }


}
