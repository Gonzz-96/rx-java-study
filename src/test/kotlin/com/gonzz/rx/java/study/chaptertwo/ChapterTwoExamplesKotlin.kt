package com.gonzz.rx.java.study.chaptertwo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.ResourceObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class ChapterTwoExamplesKotlin {

    private val disposables = CompositeDisposable()

    @Test
    // if we want to create a stateful observable, the arguments
    // passed to the observable will remain the same, even when,
    // those parameters have changed.
    // The defer() method will create an observable from a lambda
    // passed as an argument. This allows us to create a stateful
    // observable with variable that may change.
    fun `deferred Observable`() {

        var start = 1
        var count = 5

        val source = Observable.defer<Int> {
            Observable.range(start, count)
        }

        source.subscribe {
            println("Observer 1: $it")
        }

        // Modify count
        count = 10

        source.subscribe {
            println("Observer 2: $it")
        }
    }

    @Test
    // The fromCallable() method is useful when the chunk
    // of code that is going to be emitted has high probabilities
    // of throwing an exception. In that case, Observable will
    // emit (throw) the error, instead of being thrown ouside the observable
    fun creatingObservableFromCallable() {

        //Observable.just(1 / 0) // Here, the exception will be thrown OUTSIDE the observable
        Observable.fromCallable { 1 / 0 } // Here, the exception will be thrown INSIDE the observable, and emitted by it
            .subscribe({ i: Int -> println("Received: $i") },
                { e: Throwable -> println("Error Captures: $e") })
    }

    @Test
    // The single is an observable that will only emit one item.
    // onSuccess(T value) = onNext(T value) + onComplete()
    // The first() operator of Observable will return a Single
    fun `creating Single`() {

        Single.just("Hello")
            .map(String::length)
            .subscribe(::println, Throwable::printStackTrace)
    }

    @Test
    // Maybe will emit 0 or 1 values.
    // In both cases, onComplete() is called.
    // In the case of 1 value, onSucces(T value) is called
    fun `creating Maybe`() {
        // Has emission
        // Has emission
        val presentSource = Maybe.just(100)

        presentSource.subscribe(
            { s: Int -> println("Process 1 received: $s") },
            { obj: Throwable -> obj.printStackTrace() }
        ) { println("Process 1 done!") }

        // No emission
        // No emission
        val emptySource = Maybe.empty<Int>()

        emptySource.subscribe(
            { s: Int -> println("Process 2 received: $s") },
            { obj: Throwable -> obj.printStackTrace() },
            { println("Process 2 done!") })
    }

    @Test
    // Completable are generally tied to an action that
    // is going to be executed
    fun creatingCompletable() {
        Completable.fromRunnable { runProcess() }
            .subscribe { println("Done!") }
    }

    @Test
    fun disposingSubscriptions() {
        val seconds = Observable.interval(1, TimeUnit.SECONDS)
        val disposable =
            seconds.subscribe { l: Long -> println("Received: $l") }
        // sleep 5 seconds
        Thread.sleep(5000L)

        // dispose and stop emissiones
        disposable.dispose()

        // sleep another 5 seconds
        Thread.sleep(5000L)
    }

    @Test
    fun usingResourceObserver() {
        val source = Observable.interval(1, TimeUnit.SECONDS)
        val myObserver: ResourceObserver<Long> = object : ResourceObserver<Long>() {
            override fun onNext(value: Long) {
                println(value)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onComplete() {
                println("Done!")
            }
        }
        val disposable: Disposable = source.subscribeWith(myObserver)
    }

    @Test
    fun usingCompositeDisposable() {
        val seconds = Observable.interval(1, TimeUnit.SECONDS)
        //subscribe and capture disposables
        val disposable1 = seconds.subscribe { l: Long ->
            println(
                "Observer 1: " +
                        l
            )
        }
        val disposable2 = seconds.subscribe { l: Long ->
            println(
                "Observer 2: " +
                        l
            )
        }
        //put both disposables into CompositeDisposable
        disposables.addAll(disposable1, disposable2)
        //sleep 5 seconds
        Thread.sleep(5000)
        //dispose all disposables
        disposables.dispose()
        //sleep 5 seconds to prove
        //there are no more emissions
        Thread.sleep(5000)
    }


    @Test
    fun `handling Disposal with Observable create`() {

        Observable.create<Int> { observableEmitter ->
            try {
                for (i in 0 until 1000) {
                    while (!observableEmitter.isDisposed) {
                        observableEmitter.onNext(i)
                    }
                    if (observableEmitter.isDisposed) return@create
                }
                observableEmitter.onComplete()
            } catch (e: Throwable) {
                observableEmitter.onError(e)
            }
        }
    }

    private fun runProcess() { }
}