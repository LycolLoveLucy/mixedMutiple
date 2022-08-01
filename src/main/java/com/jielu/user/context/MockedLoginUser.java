package com.jielu.user.context;

public class MockedLoginUser {
    public MockedLoginUser(String userName, Long userId) {
        this.userName = userName;
        this.userId = userId;
    }

    private  String userName;

    private  Long  userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
