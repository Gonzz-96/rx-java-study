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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class ChapterTwoExamples {

    private static int start = 1;
    private static int count = 5;

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

    @Test
    // There is an equivalent method used to emit larger numbers:
    //      Observable.rangeLong()
    public void observableRange() {
        Observable.range(1, 10)
                .subscribe(s -> System.out.println("RECEIVED: " + s));

        printSepator();
    }

    @Test
    public void connectableObservableAreCold() {
        Observable<Long> seconds = Observable.interval(1,
                TimeUnit.SECONDS);
        //Observer 1
        seconds.subscribe(l -> System.out.println("Observer 1: " + l));
        //sleep 5 seconds
        sleep(5000);
        //Observer 2
        seconds.subscribe(l -> System.out.println("Observer 2: " + l));
        //sleep 5 seconds
        sleep(5000);
    }

    @Test
    public void makingColdObservableHotObservables() {

        ConnectableObservable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS).publish();
        //observer 1
        seconds.subscribe(l -> System.out.println("Observer 1: " + l));
        seconds.connect();
        //sleep 5 seconds
        sleep(5000);
        //observer 2
        seconds.subscribe(l -> System.out.println("Observer 2: " + l));

        //sleep 5 seconds
        sleep(5000);
    }

    @Test
    public void creatingAnEmptyObservable() {
        // The empty observable will only call the 'onComplete' method
        // No value is emitted
        // Empty observable are the RxJava's concept of null
        Observable<String> empty = Observable.empty();
        empty.subscribe(System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done!"));

        printSepator();
    }

    @Test
    public void creatingA_NeverObservable() {
        // This will emit no value, like empty(), but
        // this method will never call onComplete() method
        // This observable is used for testing.
        Observable<String> empty = Observable.never();
        empty.subscribe(System.out::println,
                Throwable::printStackTrace,
                () -> System.out.println("Done!"));
        sleep(5000);
    }

    @Test
    public void creatingAnErrorObservable() {
        // This observable will emit no value, nor
        // call onComplete() method. Instead, it only calls
        // the onError() method
        Observable.error(new Exception("Crash and explodes!"))
            .subscribe(i -> System.out.println("RECEIVED: " + i),
                    Throwable::printStackTrace,
                    () -> System.out.println("Done!")
            );

    }

    @Test
    // if we want to create a stateful observable, the arguments
    // passed to the observable will remain the same, even when,
    // those parameters have changed.
    // The defer() method will create an observable from a lambda
    // passed as an argument. This allows us to create a stateful
    // observable with variable that may change.
    public void creatingADeferObservable() {
        Observable<Integer> source = Observable.defer(() -> Observable.range(start, count));

        source.subscribe(i -> System.out.println("Observer 1: " + i));

        // Modify count
        count = 10;

        source.subscribe(i -> System.out.println("Observer 2: " + i));
    }

    private void printSepator() {
        System.out.println("\n**********************\n");
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
