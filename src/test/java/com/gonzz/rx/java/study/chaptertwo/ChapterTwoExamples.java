package com.gonzz.rx.java.study.chaptertwo;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class ChapterTwoExamples {

    @Test
    public void creatingObservbaleUsingCreate() {

        boolean raiseError = false;

        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");
            if (raiseError) {
                emitter.onError(new Exception());
            } else {
                emitter.onComplete();
            }
        });

        source.subscribe(s -> System.out.println("RECEIVED: " + s));
        printSepator();
    }

    @Test
    public void usingMapAndFilterOperatos() {
        Observable<String> source = Observable.create(emitter -> {
            try {
                emitter.onNext("Alpha");
                emitter.onNext("Beta");
                emitter.onNext("Gamma");
                emitter.onNext("Delta");
                emitter.onNext("Epsilon");
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });

        source.map(String::length)
                .filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));

        printSepator();
    }

    @Test
    public void usingJustToCreateObservbles() {
        Observable<String> source = Observable
                .just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        source.map(String::length)
                .filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
        printSepator();
    }

    @Test
    public void creatingObservableFromList() {
        List<String> items = Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        Observable<String> source = Observable.fromIterable(items);

        source.map(String::length)
                .filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }

    @Test
    public void implementingAndSubscribingAndObserver() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        Observer<Integer> myObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //do nothing with Disposable, disregard for now
            }
            @Override
            public void onNext(Integer value) {
                System.out.println("RECEIVED: " + value);
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
        };

        source.map(String::length).subscribe(myObserver);
        printSepator();
    }

    @Test
    public void usingLambdasInsteadOfObserver() {

        Consumer<Integer> onNext = i -> System.out.println("RECEIVED: " + i);
        Action onComplete = () -> System.out.println("Done!");
        Consumer<Throwable> onError = Throwable::printStackTrace;

        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        source.map(String::length).filter(i -> i >= 5)
                .subscribe(onNext, onError, onComplete);
        printSepator();
    }

    @Test
    public void coldObservables() {
        Observable<String> source = Observable
                .just("Alpha","Beta","Gamma","Delta","Epsilon");

        //first observer
        source.subscribe(s -> System.out.println("Observer 1 Received: " + s));

        //second observer
        source.subscribe(s -> System.out.println("Observer 2 Received: " + s));
    }

    @Test
    // Connectable observer won't start emitting values
    // when some observer gets subscribe, but when the method
    // 'connect()' is called.
    public void connectableObservablesAreHot() {
        ConnectableObservable<String> source =
                Observable.just("Alpha","Beta","Gamma","Delta","Epsilon").publish();

        //Set up observer 1
        source.subscribe(s -> System.out.println("Observer 1: " + s));
        //Set up observer 2
        source.map(String::length)
                .subscribe(i -> System.out.println("Observer 2: " + i));
        //Fire!
        source.connect();
    }



    private void printSepator() {
        System.out.println("\n**********************\n");
    }
}
