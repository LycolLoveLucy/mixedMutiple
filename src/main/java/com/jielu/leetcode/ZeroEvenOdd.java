package com.jielu.leetcode;

import java.util.IntSummaryStatistics;
import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

class ZeroEvenOdd {
    private int n;
    Semaphore[] sp = new Semaphore[3];
    public ZeroEvenOdd(int n) {
        this.n = n;
        sp[0] = new Semaphore(1);
        sp[1] = new Semaphore(0);
        sp[2] = new Semaphore(0);
    }


    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {

        for (int i = 1; i <= n; i++) {
            sp[0].acquire();
            printNumber.accept(0);
            System.out.print(0);
            if (i % 2 == 0) {
                sp[1].release();
            } else {
                sp[2].release();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            sp[1].acquire();
            printNumber.accept(i);
            System.out.print(i);
            sp[0].release();
        }

    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            sp[2].acquire();
            printNumber.accept(i);
            System.out.print(i);
            sp[0].release();
        }


    }

    public static void main(String[] args) {
        ZeroEvenOdd fooBar=new ZeroEvenOdd(10);
        IntSummaryStatistics intSummaryStatistic= new IntSummaryStatistics();

        new Thread(() -> {
            try {
                intSummaryStatistic.accept(0);
                fooBar.zero(intSummaryStatistic);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                fooBar.even(intSummaryStatistic);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                fooBar.odd(intSummaryStatistic);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}