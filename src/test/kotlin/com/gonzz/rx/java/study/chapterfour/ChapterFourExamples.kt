package com.gonzz.rx.java.study.chapterfour

import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit


@RunWith(JUnit4::class)
class ChapterFourExamples {

    private val firstCommonObservable = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
    private val secondCommonObservable = Observable.just("Zeta", "Eta", "Theta")

    // **********************
    // MERGING FACTORIES AND OPERATORS
    // **********************

    @Test
    // This juts takes 2 or more observables and
    // consolidates them into a singles observable
    // The mergeWith() is the operator version (that
    // can be applied directly to a concrete observable.
    // These factory and operator will subscribe to all the
    // sources simultaneously.
    fun `merge() factory and mergeWith() operator`() {
        Observable
            .merge(firstCommonObservable, secondCommonObservable) // ORDER MATTERS!
            .subscribe { println("Received: $it") }

        Observable
            .merge(listOf(firstCommonObservable, secondCommonObservable))
            .subscribe { println("Received: $it") }

        firstCommonObservable
            .mergeWith(secondCommonObservable)
            .subscribe { println("Received: $it") }
    }

    @Test
    // This factory will explicitly fire the elements
    // of each observable sequentially and keep the emissions
    // in a sequential order.
    fun `concat() factory`() {
        Observable
            .concat(firstCommonObservable, secondCommonObservable)
            .subscribe { println("Received: $it") }
    }

    @Test
    fun `mergeArray() factory`() {
        val source1 =
            Observable.just("Alpha", "Beta")
        val source2 =
            Observable.just("Gamma", "Delta")
        val source3 =
            Observable.just("Epsilon", "Zeta")
        val source4 =
            Observable.just("Eta", "Theta")
        val source5 =
            Observable.just("Iota", "Kappa")

        Observable.mergeArray(
            source1,
            source2,
            source3,
            source4,
            source5)
            .subscribe { println("Received: $it") }
    }

    @Test
    fun `merging infinite streams`() {
        //emit every second
        val source1 = Observable.interval(
            1,
            TimeUnit.SECONDS
        )
            .map { l: Long -> l + 1 } // emit elapsed seconds
            .map { l: Long -> "Source1: $l seconds" }
        //emit every 300 milliseconds
        val source2 =
            Observable.interval(300, TimeUnit.MILLISECONDS)
                .map { l: Long -> (l + 1) * 300 } // emit elapsed milliseconds
                .map { l: Long -> "Source2: $l milliseconds" }
        //merge and subscribe
        Observable.merge(source1, source2)
            .subscribe { x: String? -> println(x) }
        //keep alive for 3 seconds
        Thread.sleep(10_000)
    }

    @Test
    fun `flatMap operator`() {
        firstCommonObservable
            .flatMap { Observable.fromIterable(it.toList()) }
            .subscribe(::println)
    }

    @Test
    fun `flatMap operator II`() {
        val source = Observable
            .just(
                "521934/2342/FOXTROT",
                "21962/12112/78886/TANGO",
                "283242/4542/WHISKEY/2348562"
            )

        source
            .flatMap { s ->
                Observable.fromIterable(s.split("/"))
                    .filter { it.matches("[0-9]+".toRegex()) }
            }
            .map(Integer::valueOf)
            .subscribe(::println)
    }

    @Test
    fun `another flat map example`() {
        val intervalArguments = Observable.just(2, 3, 10, 7)

        intervalArguments
            .flatMap { i ->
                Observable.interval(i.toLong(), TimeUnit.SECONDS)
                    .map { i.toString() + "s interval: " + ((i + 1) * i) + " elapsed" }
            }
            .subscribe(System.out::println)

        Thread.sleep(12_000L)
    }

    @Test
    fun `flatMap overload variant`() {
        firstCommonObservable
            .flatMap({ Observable.fromIterable(it.toList()) }) { original, flatten ->
                "$original-$flatten"
            }
            .subscribe(::println)
    }
}


