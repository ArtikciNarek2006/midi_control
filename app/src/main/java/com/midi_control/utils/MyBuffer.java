package com.midi_control.utils;

import com.midi_control.utils.ML;

import java.util.LinkedList;

public class MyBuffer<T> {
    public static final String TAG = "MyBuffer<T>";
    public static final int DEFAULT_SIZE = 20;

    private LinkedList<T> list;
    private int size;

    public MyBuffer() {
        list = new LinkedList<T>();
        this.setSize(DEFAULT_SIZE);
    }

    public MyBuffer(int size) {
        list = new LinkedList<T>();
        this.setSize(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        while (list.size() > size) {
            list.removeLast();
        }
        while (list.size() < size) {
            list.addLast(null);
        }
    }

    public synchronized void pushToStart(T item) {
        list.removeLast();
        list.addFirst(item);
    }

    public T getI(int index) {
        return list.get(index);
    }

    public synchronized  LinkedList<T> getLinkedList() {
        return list;
    }

    public Object[] getList() {
        return list.toArray();
    }

    public void setList(LinkedList<T> list) {
        if (list != null) {
            this.list = list;
            this.setSize(size);
        } else {
            ML.log(TAG, "setList(list) argument null.");
        }
    }
}
