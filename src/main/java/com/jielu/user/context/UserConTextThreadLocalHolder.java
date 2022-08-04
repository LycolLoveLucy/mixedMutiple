package com.jielu.user.context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserConTextThreadLocalHolder {

  private  static    NameThreadLocal nameThreadLocal=new NameThreadLocal();

  public static MockedLoginUser getUserByLoginContextType(final LoginContextType loginContextType){

    return  nameThreadLocal.getWithLoginType(loginContextType);
  }


  public static void set(final LoginContextType loginContextType,MockedLoginUser mockedLoginUser){

      nameThreadLocal.setWithLoginType(loginContextType,mockedLoginUser);
  }

  public static   void remove(){
      nameThreadLocal.remove();
  }
  public  void clean(){


  }

    //@Test
    public void test() throws InterruptedException {
            List<String> testList = new ArrayList<>();
            Thread[] tenThreads = new Thread[10];
            AtomicInteger atomicInteger = new AtomicInteger();
            atomicInteger.set(1);
            for (; atomicInteger.get() < 10; atomicInteger.incrementAndGet()) {
                tenThreads[atomicInteger.get()] = new Thread(
                        () -> {
                            LoginContextType loginContextType = atomicInteger.get() % 2 == 0 ? LoginContextType.H5 : LoginContextType.WEB;
                            tenThreads[atomicInteger.get()].setName("thread-" + loginContextType + "-" + atomicInteger.get());
                            UserConTextThreadLocalHolder.set(loginContextType,
                                    new MockedLoginUser(loginContextType + tenThreads[atomicInteger.get()].getName(),
                                            tenThreads[atomicInteger.get()].getId()));
                            testList.add("thread-" + loginContextType + "-" + atomicInteger.get());
                        }
                );
                tenThreads[atomicInteger.get()].start();
                tenThreads[atomicInteger.get()].join();

    }
    for (int i = 0; i < testList.size(); i++) {
        System.out.println(testList.get(i));
    }
}




}
