package edu.gvsu.cis;

public class Counter {

    private int counter = 0;

    void increment() {
        counter = counter + 1;
    }

    synchronized void safeIncrement() {
        counter = counter + 1;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
