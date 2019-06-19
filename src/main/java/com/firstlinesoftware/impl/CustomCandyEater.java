package com.firstlinesoftware.impl;

import com.firstlinesoftware.ICandy;
import com.firstlinesoftware.ICandyEater;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class CustomCandyEater implements ICandyEater {

    private static int counter = 0;

    private final int id = counter++;

    private final ICandyEater iCandyEater;

    private final BlockingQueue<ICandy> queue;

    private final Set<Integer> candyTypesInUsing;

    public CustomCandyEater(ICandyEater iCandyEater, BlockingQueue<ICandy> queue, Set<Integer> candyTypesInUsing) {
        this.iCandyEater = iCandyEater;
        this.queue = queue;
        this.candyTypesInUsing = candyTypesInUsing;
    }

    @Override
    public void eat(ICandy candy) throws Exception {
        System.out.println("start eat candy " + candy.getCandyFlavour() + " by eater with id " + id);
        iCandyEater.eat(candy);
//            Thread.sleep(100);
        System.out.println("end eat candy " + candy.getCandyFlavour() + " by eater with id " + id);
    }

    public void listen() {
        while (true) {
            try {
                final ICandy candy = queue.take();
                final boolean canEat;
                synchronized (candyTypesInUsing) {
                    if (!candyTypesInUsing.contains(candy.getCandyFlavour())) {
                        candyTypesInUsing.add(candy.getCandyFlavour());
                        canEat = true;
                    } else {
                        canEat = false;
                    }
                }
                if (canEat) {
                    eat(candy);
                    candyTypesInUsing.remove(candy.getCandyFlavour());
                } else {
                    queue.put(candy);
                    System.err.println("return");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
