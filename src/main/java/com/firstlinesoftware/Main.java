package com.firstlinesoftware;

import com.firstlinesoftware.impl.EaterServiceImpl;

import java.util.Random;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        final ICandyEater[] eaters =
                Stream.<ICandyEater>generate(() -> candy -> {})
                        .limit(10)
                        .toArray(ICandyEater[]::new);
        final EaterServiceImpl service = new EaterServiceImpl(eaters);
        final Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            final int flavour = rand.nextInt(1);
            service.addCandy(() -> flavour);
        }
    }
}
