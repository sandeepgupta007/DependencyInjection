package com.company.test;

public class Dog {

    public Dog(Animal animal) {
        animal.print();
    }

    public void print() {
        System.out.println("Dog");
    }
}
