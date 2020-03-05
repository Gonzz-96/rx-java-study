package com.gonzz.rx.java.study.chapterone;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class ChapterOneExamples {

    @Test
    public void introducingObservables() {
        Observable<String> myStrings = Observable.just(
                "Alpha", "Beta", "Gamma", "Delta"
        );

        myStrings
                //.map(string -> string.length())
                .subscribe(s -> System.out.println(s));

        printSeparator();
    }

    @Test
    public void pushingEvents() {
        Observable<Long> secondIntervals = Observable.interval(1, TimeUnit.SECONDS);

        secondIntervals.subscribe(s -> System.out.println(s));
        sleep(5000);
    }

    private void printSeparator() {
        System.out.println("\n****************\n");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
