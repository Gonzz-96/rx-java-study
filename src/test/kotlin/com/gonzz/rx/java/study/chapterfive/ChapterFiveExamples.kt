package com.gonzz.rx.java.study.chapterfive

import io.reactivex.Observable
import io.reactivex.subjects.*
import io.reactivex.subjects.UnicastSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit
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

    @Test
    // This operator will receive an integer
    // that represents the number of subscriptions
    // the observable will receive. For example, if we have autoConnect(3),
    // the observable will start emitting after 3 observers are subscribed to it
    // When there are no observers available, it won't dispose itself.
    fun `autoConnect() operator`() {
        val threeRandoms = threeCommonInteger
            .map { randomInt() }
            .publish()
            .autoConnect(2) // With no parameters, only one observer is needed
        // If 0 is received, it will start firing emissions from the very beginning

        threeRandoms.subscribe { println("Observer 1: $it") }
        threeRandoms.reduce(0) { acc, next ->
            acc + next
        }.subscribe { result -> println("Observer 2: Total sum: $result") }
    }

    // !!!!!!!!!!!!!!!!!!!!!! Importat !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // NOTE: autoConnect() and refCount() doesn't represent the maximum number
    // of allowerd subscriber, but the quantity of subscriptions it needs
    // to start emitting values
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Test
    // This operator is similar to autoConnect(),
    // but it will be disposed when there is no more available observers
    // It doesn't persist the subscription.
    // In short, it can start over!
    fun `refCount() oeprator`() {
        val seconds = Observable.interval(1, TimeUnit.SECONDS)
            .publish()
            .refCount()

        seconds.take(5)
            .subscribe { println("Observer 1: $it") }

        Thread.sleep(3_000L)

        seconds.take(2)
            .subscribe { println("Observer 2: $it") }

        Thread.sleep(3000) // In this points, 'seconds' has no more observers, ConnectableObservable is disposed

        seconds.take(2)
            .subscribe { println("Observer 3: $it") }

        Thread.sleep(3_000L)
    }

    @Test
    // This operator is a fusion between publish().refCount()
    fun `share() oeprator`() {
        val seconds = Observable.interval(1, TimeUnit.SECONDS)
            //.publish()
            //.refCount()
            .share()

        seconds.take(5)
            .subscribe { println("Observer 1: $it") }

        Thread.sleep(3_000L)

        seconds.take(2)
            .subscribe { println("Observer 2: $it") }

        Thread.sleep(3000) // In this points, 'seconds' has no more observers, ConnectableObservable is disposed

        seconds.take(2)
            .subscribe { println("Observer 3: $it") }

        Thread.sleep(3_000L)
    }

    @Test
    // replay() can receive a parameter, which corresponds
    // to the buffer size, and will cache the latest elements
    fun `replay() operator`() {
        val seconds = Observable.interval(1, TimeUnit.SECONDS)
            //.replay()
            .replay(2)
            .autoConnect()

        //Observer 1
        seconds.subscribe { println("Received I: $it") }

        Thread.sleep(3_000L)

        // Observer 2
        seconds.subscribe { println("Received II: $it") }

        Thread.sleep(3_000L)
    }

    @Test
    fun `to persist the emissions, use autoConnect() instead of refCount() since it's not disposed`() {
        val source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
            .replay(1)
            .autoConnect()

        source.subscribe { println("Observer 1: $it") }
        source.subscribe { println("Observer 2: $it") }

    }

    @Test
    fun `replay() operator overload`() {
        val seconds = Observable.interval(300, TimeUnit.MILLISECONDS)
            .map { (it + 1) * 300 } // map to elapsed milliseconds
            .replay(1, TimeUnit.SECONDS)
            .autoConnect()

        seconds.subscribe { println("Observer 1: $it") }

        Thread.sleep(2_000L)

        seconds.subscribe { println("Observer 2: $it") }

        Thread.sleep(1_000L)
    }

    @Test
    fun `cache() operator`() {
        val cachedRollingTotals =
            Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
                .scan(0,
                    { total, next -> total + next }
                )
                .cache()

        cachedRollingTotals.subscribe(::println)
    }

    @Test
    // This operator is equivalent to cache(),
    // cacheWithInitialCapacity(16) == cache()
    fun `cacheWithInitialCapacity() operator`() {
        val cachedRollingTotals =
            Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
                .scan(0,
                    { total, next -> total + next }
                )
                .cacheWithInitialCapacity(9)

        cachedRollingTotals.subscribe(::println)
    }

    private fun randomInt() = Random.nextInt(0, 100_00)

    @Test
    fun `PublishSubjects are Observers and Observables at the same time`() {
        val subject: Subject<String> = PublishSubject.create()

        subject
            .map(String::length)
            .subscribe(::println)

        subject.onNext("Alpha")
        subject.onNext("Beta")
        subject.onNext("Gamma")
        subject.onComplete()
    }

    @Test
    // This means that you will most likely use Subjects for infinite,
    // event-driven (that is, user action-driven) Observables.
    fun `when to use Subjects`() {
        val source1 = Observable.interval(1, TimeUnit.SECONDS)
            .map { l: Long -> (l + 1).toString() + " seconds" }
        val source2 =
            Observable.interval(300, TimeUnit.MILLISECONDS)
                .map { l: Long -> ((l + 1) * 300).toString() + " milliseconds" }

        val subject: Subject<String> = PublishSubject.create()

        subject.subscribe { x: String? -> println(x) }

        source1.subscribe(subject)
        source2.subscribe(subject)

        Thread.sleep(5_000L)
    }

    @Test
    // Subjects are hot observable, so its emissions
    // can be missed if there are no observers available
    fun `Subjects are hot observables`() {
        val subject: Subject<String> = PublishSubject.create()

        subject.onNext("Alpha")
        subject.onNext("Beta")
        subject.onNext("Gamma")
        subject.onComplete()

        // This observer lost all of the emissions
        subject.map { obj: String -> obj.length }
            .subscribe { x: Int? -> println(x) }
    }

    // IMPORTANT: subjects are no disposable!! No dispose() method available

    @Test
    // MEthods in a subject are no thredsafe, so they can be called
    // from multiple threads, breaking the sequential contract of the observables
    // The toSerialized() is the solution to this
    fun `toSerialize() method on subjects`() {
        val subject: Subject<String> =
            PublishSubject
                .create<String>()
                .toSerialized()

    }

    @Test
    // BehaviorSubject will cache the last element of the Subject
    // This is equivalent to replay(1).autoConnect()
    fun `Behavior Subject example`() {

        val subject: Subject<String> = BehaviorSubject.create()

        subject.subscribe { println("Observer 1: $it") }

        subject.onNext("Alpha")
        subject.onNext("Beta")
        subject.onNext("Gamma")

        subject.subscribe { println("Observer 2: $it") }
    }

    @Test
    // ReplaySubject will cache all of the emissions of the
    // subject
    fun `Replay Subject example`() {
        val subject: Subject<String> = ReplaySubject.create()

        subject.subscribe { println("Observer 1: $it") }

        subject.onNext("Alpha")
        subject.onNext("Beta")
        subject.onNext("Gamma")

        subject.subscribe { println("Observer 2: $it") }
    }

    @Test
    // The AsyncSubject has a highly tailored, finite-specific behavior:
    // it will only push the last value it receives,
    // followed by an onComplete() event
    // This Subject is not ideal for infinite sources
    // since onComplete() is never called
    // AsyncSubject behavior can be imitate with takeLast(1).replay(1)
    fun `Async Subject example`() {
        val subject: Subject<String> = AsyncSubject.create()

        subject.subscribe(
            { s: String -> println("Observer 1: $s") },
            { obj: Throwable -> obj.printStackTrace() },
            { println("Observer 1 done!") } )

        subject.onNext("Alpha")
        subject.onNext("Beta")

        // This is the only emitted value
        subject.onNext("Gamma")
        subject.onComplete()

        subject.subscribe(
            { println("Observer 2: $it") },
            Throwable::printStackTrace,
            { println("Observer 2 done!") } )
    }

    @Test
    // It will buffer all the emissions it received until
    // an observer subscribes to it, and then it will
    // release all these emissions to the Observer and
    // clear its cache
    fun `Unicast Subject example`() {
        val subject: Subject<String> = UnicastSubject.create()

        Observable.interval(300, TimeUnit.MILLISECONDS)
            .map { l: Long -> ((l + 1) * 300).toString() + " milliseconds" }
            .subscribe(subject)

        Thread.sleep(2000L)

        subject.subscribe { println("Observer 1: $it") }

        Thread.sleep(2_000L)
    }
}
