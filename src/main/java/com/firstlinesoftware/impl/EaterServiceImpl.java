package com.firstlinesoftware.impl;

import com.firstlinesoftware.CandyServiceBase;
import com.firstlinesoftware.ICandy;
import com.firstlinesoftware.ICandyEater;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.*;

public class EaterServiceImpl extends CandyServiceBase {

    private final ExecutorService executorService;

    private final BlockingQueue<ICandy> queue;

    private final Set<Integer> candyTypesInUsing = ConcurrentHashMap.newKeySet();

    public EaterServiceImpl(ICandyEater[] candyEaters) {
        super(candyEaters);
        executorService = Executors.newFixedThreadPool(candyEaters.length);
        queue = new LinkedBlockingDeque<>();
        Arrays.stream(candyEaters).forEach(ce -> executorService.submit(() ->{
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
                        ce.eat(candy);
                        candyTypesInUsing.remove(candy.getCandyFlavour());
                    } else {
                        queue.put(candy);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    @Override
    public void addCandy(ICandy candy) {
        queue.add(candy);
    }
}
