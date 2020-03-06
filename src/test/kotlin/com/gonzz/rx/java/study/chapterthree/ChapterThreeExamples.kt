package com.gonzz.rx.java.study.chapterthree

import io.reactivex.Observable
import io.reactivex.functions.Predicate
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class ChapterThreeExamples {

    val commonObservable = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

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
}