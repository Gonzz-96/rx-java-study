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

    @Test
    // This will subscribe to observers sequentially.
    // It won't fire the subscription of one observable
    // if the first one has not called onComplete() method
    // THis factory/operator iwll guarantee order
    fun `concat factory`() {
        Observable.concat(firstCommonObservable, secondCommonObservable)
            .subscribe { println("Received: $it") }
    }

    @Test
    fun `concatWith() operator`() {
        firstCommonObservable.concatWith(secondCommonObservable)
            .subscribe { println("Received: $it") }
    }

    @Test
    // IMPORTANT NOTE: take() operator can make
    // an endless observable, finite.
    fun `using concat() with an endless observable`() {
        // emits every second, but only 2 elements are taken (finite)
        val source1 = Observable.interval(1, TimeUnit.SECONDS)
            .take(2)
            .map { l: Long -> l + 1 } // emit elapsed seconds
            .map { l: Long -> "Source1: $l seconds" }

        //emit every 300 milliseconds (infinite)
        val source2 =
            Observable.interval(300, TimeUnit.MILLISECONDS)
                .map { l: Long -> (l + 1) * 300 } // emit elapsed milliseconds
                .map { l: Long -> "Source2: $l milliseconds" }
        Observable.concat(source1, source2)
            .subscribe { i: String -> println("RECEIVED: $i") }

        //keep application alive for 5 seconds
        Thread.sleep(5000)
    }

    @Test
    // flatMap() doesn't care about observable order
    // It subscribes to every observable simultaneously.
    // concatMap() it's its counterpart: it CARES about order
    // and works almost the same as concat() [it won't subscribe]
    // to the next observable until the first source onComplete()
    // method is called
    fun `concatMap factory`() {
        firstCommonObservable.concatMap {
            Observable.fromIterable(it.toList())
        }.subscribe(::println)
    }

    @Test
    // amb() stands for Ambiguous. It will receive and
    // Iterable<Observable<T>> and will emit the values of
    // the "FASTEST" observable. THe others will be disposed.
    // It's useful when there are a lot of data sources,
    // and we want to send the fastest values
    fun `amb() operator`() {
        //emit every second
        val source1 = Observable.interval(1, TimeUnit.SECONDS)
            .take(2)
            .map { l -> l + 1 } // emit elapsed seconds
            .map { l -> "Source1: $l seconds" }

        // This observable is faster, so its elements will be pushed
        // down to the final stream.
        val source2 = Observable.interval(300L, TimeUnit.MILLISECONDS)
            .map { l -> (l + 1) * 300 }
            .map { l -> "Source 2: $l" }

        Observable.amb(listOf(source1, source2))
            .subscribe { println("Received: $it") }

        Thread.sleep(5_000L)
    }

    @Test
    // ambWith() it's the operator version of amb()
    fun `ambWith() operator`() {
        //emit every second
        val source1 = Observable.interval(1, TimeUnit.SECONDS)
            .take(2)
            .map { l -> l + 1 } // emit elapsed seconds
            .map { l -> "Source1: $l seconds" }

        // This observable is faster, so its elements will be pushed
        // down to the final stream.
        val source2 = Observable.interval(300L, TimeUnit.MILLISECONDS)
            .map { l -> (l + 1) * 300 }
            .map { l -> "Source 2: $l" }

        source1.ambWith(source2)
            .subscribe { println("Received: $it") }

        Thread.sleep(5_000L)
    }

}


