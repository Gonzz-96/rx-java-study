package com.gonzz.rx.java.study.chaptertwo;

import io.reactivex.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class ChapterTwoExamples {

    private static final CompositeDisposable disposables = new CompositeDisposable();

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
        printSeparator();
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

        printSeparator();
    }

    @Test
    public void usingJustToCreateObservbles() {
        Observable<String> source = Observable
                .just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");

        source.map(String::length)
                .filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
        printSeparator();
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
        printSeparator();
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
        printSeparator();
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

        printSeparator();
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

        printSeparator();
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

    @Test
    // The fromCallable() method is useful when the chunk
    // of code that is going to be emitted has high probabilities
    // of throwing an exception. In that case, Observable will
    // emit (throw) the error, instead of being thrown ouside the observable
    public void creatingObservableFromCallable() {

        //Observable.just(1 / 0) // Here, the exception will be thrown OUTSIDE the observable
        Observable.fromCallable(() -> 1 / 0) // Here, the exception will be thrown INSIDE the observable, and emitted by it
                .subscribe(i -> System.out.println("Received: " + i),
                        e -> System.out.println("Error Captures: " + e));

    }

    @Test
    // The single is an observable that will only emit one item.
    // onSuccess(T value) = onNext(T value) + onComplete()
    // The first() operator of Observable will return a Single
    public void creatingSingle() {

        Single.just("Hello")
                .map(String::length)
                .subscribe(System.out::println,
                        Throwable::printStackTrace);
    }

    @Test
    // Maybe will emit 0 or 1 values.
    // In both cases, onComplete() is called.
    // In the case of 1 value, onSucces(T value) is called
    public void creatingMaybe() {
        // Has emission
        Maybe<Integer> presentSource = Maybe.just(100);

        presentSource.subscribe(s -> System.out.println("Process 1 received: " + s),
            Throwable::printStackTrace,
            () -> System.out.println("Process 1 done!"));

        // No emission
        Maybe<Integer> emptySource = Maybe.empty();

        emptySource.subscribe(s -> System.out.println("Process 2 received: " + s),
            Throwable::printStackTrace,
            () -> System.out.println("Process 2 done!"));
    }

    @Test
    // Completable are generally tied to an action that
    // is going to be executed
    public void creatingCompletable() {

        Completable.fromRunnable(() -> runProcess())
            .subscribe(() -> System.out.println("Done!"));
    }

    @Test
    // The dispose() method will terminate the emission of values
    // from the observable to the observer in order to free resources.
    public void disposingSubscriptions() {

        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);

        Disposable disposable =
                seconds.subscribe(l -> System.out.println("Received: " + l));

        // sleep 5 seconds
        sleep(5000);

        // dispose and stop emissiones
        disposable.dispose();

        // sleep another 5 seconds
        sleep(5000);
    }

    @Test
    public void usingResourceObserver() {

        Observable<Long> source =
                Observable.interval(1, TimeUnit.SECONDS);

        ResourceObserver<Long> myObserver = new ResourceObserver<Long>() {
            @Override
            public void onNext(Long value) {
                System.out.println(value);
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

        Disposable disposable = source.subscribeWith(myObserver);
    }

    @Test
    public void usingCompositeDisposable() {
        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);
        //subscribe and capture disposables
        Disposable disposable1 =
                seconds.subscribe(l -> System.out.println("Observer 1: " +
                        l));
        Disposable disposable2 =
                seconds.subscribe(l -> System.out.println("Observer 2: " +
                        l));
        //put both disposables into CompositeDisposable
        disposables.addAll(disposable1, disposable2);
        //sleep 5 seconds
        sleep(5000);
        //dispose all disposables
        disposables.dispose();
        //sleep 5 seconds to prove
        //there are no more emissions
        sleep(5000);
    }

    private void printSeparator() {
        System.out.println("\n**********************\n");
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runProcess() { }
}
