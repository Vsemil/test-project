package com.firstlinesoftware.impl;

import com.firstlinesoftware.CandyServiceBase;
import com.firstlinesoftware.ICandy;
import com.firstlinesoftware.ICandyEater;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class EaterServiceImpl extends CandyServiceBase {

    private final ExecutorService executorService;

    private final BlockingQueue<ICandy> queue;

    private final Set<Integer> candyTypesInUsing = ConcurrentHashMap.newKeySet();

    public EaterServiceImpl(ICandyEater[] candyEaters) {
        super(candyEaters);
        executorService = Executors.newFixedThreadPool(candyEaters.length);
        Comparator<ICandy> candyComparator = (candy1, candy2) -> {
            if (candyTypesInUsing.contains(candy1.getCandyFlavour()) && candyTypesInUsing.contains(candy2.getCandyFlavour())) {
                return 0;
            } else if (candyTypesInUsing.contains(candy1.getCandyFlavour())) {
                return 1;
            } else if (candyTypesInUsing.contains(candy2.getCandyFlavour())) {
                return -1;
            }
            return 0;
        };
        queue = new PriorityBlockingQueue<>(10, candyComparator);
        List<CustomCandyEater> eaterList = Arrays.stream(candyEaters)
                .map(ce -> new CustomCandyEater(ce, queue, candyTypesInUsing))
                .collect(Collectors.toList());
        eaterList.forEach(ce -> executorService.submit(ce::listen));
    }

    @Override
    public void addCandy(ICandy candy) {
        try {
            queue.put(candy);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
