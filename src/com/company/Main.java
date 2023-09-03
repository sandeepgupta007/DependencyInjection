package com.company;

import com.company.DependencyInjectionFramework.DependencyContainer;
import com.company.test.*;

import java.util.Collections;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

	    // write your code here
        DependencyContainer dependencyContainer = new DependencyContainer();
        dependencyContainer.register(Animal.class, true, Collections.emptySet());
        dependencyContainer.register(Cat.class, false, Set.of(Animal.class));
        dependencyContainer.register(Dog.class, true, Set.of(Animal.class));
        dependencyContainer.register(Lion.class, false, Set.of(Animal.class, Cat.class));
        dependencyContainer.register(Tiger.class, true, Set.of(Animal.class, Cat.class));

        Object tigerObject = dependencyContainer.resolve(Tiger.class);
        Object animalObject = dependencyContainer.resolve(Animal.class);
        Object tigerObjectAgain = dependencyContainer.resolve(Tiger.class);
        Object animalObjectAgain = dependencyContainer.resolve(Animal.class);

        System.out.println(tigerObject);
        System.out.println(animalObject);
        System.out.println(tigerObjectAgain);
        System.out.println(animalObjectAgain);
    }


}
