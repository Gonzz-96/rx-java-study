package com.gonzz.rx.java.study.chapterthree

import io.reactivex.Observable
import io.reactivex.functions.Predicate
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class ChapterThreeExamples {

    private val commonObservable = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    // SUPPRESS OPERATORS

    @Test
    // Straightforward for an explanation
    fun `filter operator`() {
        commonObservable
            .filter { s -> s.length != 5 }
            .subscribe {
                println("Received: $it")
            }
    }

    @Test
    // Straightforward for an explanation
    fun `take operator`() {
        commonObservable
            //.take(10) // try with more elements
            .take(3)
            .subscribe { s -> println("Received: $s") }
    }

    @Test
    // This can be read as: this observables emits values every 300 milliseconds
    // but TAKE its values for only 2 seconds
    fun `take interval overload operator`() {
        Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(5, TimeUnit.SECONDS)
            .subscribe { println("Received: $it") }

        Thread.sleep(5000L)
    }

    @Test
    // Does the opposite of take()
    fun `skip operator`() {
        Observable.range(1, 100)
            .skip(90)
            .subscribe { println("Received: $it") }
    }

    @Test
    // When the predicate of the operator
    // is no more true, the emissions stop and
    // onComplete() method is called
    fun `take while operator`() {
        Observable.range(1, 100)
            .takeWhile { it < 5 }
            .subscribe { println("Recevide: $it") }
    }

    @Test
    // The opposite of takeWhile(): when the passed predicate
    // is no more satisfied, it will start emitting.
    fun `skip while operator`() {
        Observable.range(1, 100)
            .skipWhile { it <= 95 }
            .subscribe { println("Received: $it") }
    }

    @Test
    // It's similar to takeWhile(), but will
    // takes an observable as argument. It will be
    // taking emissions UNTIL the received observable
    // pushes a value
    // skipUntil() is the analogue of takeUntil()
    fun `take until operator`() {

        val source = Observable.create<String> {
            Thread.sleep(5_000L)
            "Observable 1: emitted!".let {s ->
                println(s)
                it.onNext(s)
            }
            it.onComplete()
        }

        Observable.interval(1_000L, TimeUnit.SECONDS)
            .takeUntil(source)
            .subscribe { println("Observable 2: $it") }

        Thread.sleep(10_000L)
    }

    @Test
    // It will supress any duplicate of the emitted
    // values
    fun `distinct operator`() {
        commonObservable
            .map(String::length)
            .distinct()
            .subscribe { println("Received: $it") }
    }

    @Test
    // This will ignore equal and consecutive emissions
    fun `distinct until changed`() {
        Observable.just(1, 1, 1, 2, 2, 3, 3, 2, 1, 1)
            .distinctUntilChanged()
            .subscribe { println("Received: $it") }

        commonObservable
            .distinctUntilChanged(String::length)
            .subscribe { println("Received: $it") }
    }

    @Test
    // Straightforward for an explanation
    fun `elementAt  operator`() {
        commonObservable
            .elementAt(3L) // Returns a Maybe
            .subscribe { println("Received: $it") }
    }
}