package com.jielu.user.context;

public class NameThreadLocal extends  ThreadLocal<MockedLoginUser>{

    public volatile   LoginContextType loginContextType;

    public NameThreadLocal(){
        this.loginContextType=loginContextType;
    }


    public    LoginContextType getLoginContextType(){
        if(loginContextType==null){
            throw  new RuntimeException("No Login context type is marked in the scope context of current Thread");
        }
        return  this.loginContextType;
    }


    @Override
    public MockedLoginUser get() {
        return super.get();
    }

    public MockedLoginUser getWithLoginType(LoginContextType loginContextType) {
        if(this.getLoginContextType()!=loginContextType){
            throw  new RuntimeException("The current LoginContextType has Changed," +
                    "Or you haven't set the LoginContext for the current thread Context");

        }
        return super.get();
    }
    @Override
    public void set(MockedLoginUser value) {
        super.set(value);
    }

    public  void setWithLoginType(LoginContextType loginContextType,Object value){
        this.loginContextType=loginContextType;
    }

}
