package com.company.test;

public class Lion {
    public Lion(Animal animal, Cat cat) {
        System.out.println("Start Lion");
        animal.print();
        cat.print();
        System.out.println("End Lion");
    }

    public void print() {
        System.out.println("Lion");
    }

}
