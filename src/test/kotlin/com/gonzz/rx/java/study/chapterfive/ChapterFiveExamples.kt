package com.gonzz.rx.java.study.chapterfive

import io.reactivex.Observable
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

@RunWith(JUnit4::class)
class ChapterFiveExamples {

    private val threeCommonInteger = Observable.range(1, 3)

    @Test
    fun `different streams for different observers`() {

        val threeInteger = Observable.range(1, 3)

        threeInteger.subscribe { println("Observer 1: $it") }
        threeInteger.subscribe { println("Observer 2: $it") }
    }

    @Test
    // The publish() method will force the source to be
    // a hot observable, pushing a single stream to all the observers
    fun `creating one single stream using publish() method`() {

        val threeInteger = Observable.range(1, 3).publish()

        threeInteger.subscribe { println("Observer 1: $it") }
        threeInteger.subscribe { println("Observer 2: $it") }

        threeInteger.connect()
    }

    @Test
    fun `multicasting with operators`() {
        threeCommonInteger.map { Random.nextInt(1, 100_00) }.subscribe { println("Observer 1: $it") }
        threeCommonInteger.map { Random.nextInt(1, 100_00) }.subscribe { println("Observer 2: $it") }
    }

    @Test
    // here, the connectable.map() will create different streams
    // for each observable. Every operator added to the ConnectableObservable
    // will fork the single stream into different streams
    fun `creating random numbers AFTER publishing the observable`() {
        val connectable = threeCommonInteger.publish()
        val threeRandomInts = connectable.map { Random.nextInt(1, 100_00) }

        threeRandomInts.subscribe { println("Observer 1: $it") }
        threeRandomInts.subscribe { println("Observer 2: $it") }

        connectable.connect()
    }

    @Test
    fun `applying map operator before publish() is called`() {
        val connectable = threeCommonInteger.map { Random.nextInt(1, 100_00) }
        val threeRandomInts = connectable.publish()

        threeRandomInts.subscribe { println("Observer 1: $it") }
        threeRandomInts.subscribe { println("Observer 2: $it") }

        threeRandomInts.connect()
    }

    // An observabe should be only published when there
    // are more than one observer. Otherwise, there could be
    // CPU and memory overhead
    @Test
    fun `multiple observers subscribing to one ConnectableObservable`() {
        val connectable = threeCommonInteger.map { Random.nextInt(1, 100_00) }.publish()

        connectable.subscribe { println("Observer 1: $it") }
        connectable.reduce(0) { acc, next ->
            acc + next
        }.subscribe { result -> println("Observer 2: $result") }

        connectable.connect()
    }
}
