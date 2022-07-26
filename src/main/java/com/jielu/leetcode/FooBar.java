package com.jielu.leetcode;

import java.util.concurrent.Semaphore;

class FooBar {
    private int n;

    Semaphore foo = new Semaphore(1);
    Semaphore bar = new Semaphore(0);

    public FooBar(int n) {
        this.n = n;
    }

    public  void foo(Runnable printFoo) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            foo.acquire();
            printFoo.run();
            bar.release();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {

            bar.acquire();
            printBar.run();
            foo.release();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        NamedThreadFactory namedThreadFactory=new NamedThreadFactory();
        Runnable foo= () -> System.out.println("foo");
        Runnable bar= () -> System.out.println("bar");
        FooBar fooBar=new FooBar(10);
       new Thread(() -> {
           try {
               fooBar.foo(foo);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
       }).start();
        new Thread(() -> {
            try {
                fooBar.bar(bar);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}