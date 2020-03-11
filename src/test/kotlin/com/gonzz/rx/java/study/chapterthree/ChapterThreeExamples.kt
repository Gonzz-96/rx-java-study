package com.gonzz.rx.java.study.chapterthree

import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class ChapterThreeExamples {

    private val commonObservable = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

    // *********************************
    // SUPPRESS OPERATORS
    // *********************************

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

    // *********************************
    // TRANSFORMING OPERATORS
    // *********************************

    @Test
    // Straightforward for an explanation
    fun `map operator`() {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")

        Observable.just("1/3/2016", "5/9/2016", "10/12/2016")
            .map { s -> LocalDate.parse(s, dateTimeFormatter) }
            .subscribe { println("RECEIVED: $it") }
    }

    @Test
    // This will cast every object in the stream
    // to a certain class
    fun `cast operator`() {
        commonObservable
            .cast(Any::class.java)
            .subscribe { println("${it.javaClass}") }
    }

    @Test
    // Straightforward for explanation
    fun `startWith operator`() {
        Observable.just("Coffee", "Tea", "Espresso", "Latte")
            //.startWith("----------------")
            //.startWith("COFFEE SHOP MENU")
            .startWithArray("COFFEE SHOP MENU", "----------------")
            .subscribe(::println)
    }

    @Test
    // This operator will send the received value
    // as a default in case the observable is empty.
    fun `defaultIfEmpty operator`() {
        commonObservable.filter { s -> s.startsWith("Z") }
            .defaultIfEmpty("None")
            .subscribe { x -> println(x) }
    }

    @Test
    // This operator will take a "backup" observable.
    // If the source observable is empty, the received
    // observable will push its values.
    fun `switchIfEmpty operator`() {
        commonObservable
            .filter { it.startsWith("Z") }
            .switchIfEmpty(Observable.just("Zeta", "Eta", "Theta"))
            .subscribe { println("Received: $it") }
    }

    @Test
    // The operator will sort any kind of elements that
    // implement the Comparable<T> interface
    // A comparator can be provided as an argument
    // Also, a lambda can be provided as comparator
    fun `sorted operator`() {
        Observable
            .just(6, 2, 5, 7, 1, 4, 9, 8, 3)
            .sorted(
                // Comparator.reverseOrder()
            )
            .subscribe(::println)
    }

    @Test
    // This operator will hold the emissions and delay each one
    // for specified time period. It can receive an observable
    // and push emissions everytime that observables pushes
    fun `delay operator`() {
        commonObservable.delay(3, TimeUnit.SECONDS)
            .subscribe { println("Received: $it") }

        Thread.sleep(5_000L)
    }

    @Test
    // This operator will repeat the whole stream
    // until the method onComplete() is called
    // the given number of times. If no argument is passed,
    // it will repeat forever. Another operator is repeatUntil()
    // that receives a boolean lambda and will repeat until it;s true
    fun `repeat operator`() {
        commonObservable
            .repeat(3)
            .subscribe { println("Received: $it") }
    }

    @Test
    // This will receive a lambda that will receive
    // and accumulator and the next value in the stream.
    // It can receive an initial value
    fun `scan operator`() {
        Observable.just(5, 3, 7, 10, 2, 14)
            .scan { accumulator, next -> accumulator + next}
            .subscribe(::println)
    }

    // *********************************
    // Reducing operators
    // *********************************

    // These operators typically return a Single
    // The are typically finite

    @Test
    // Straightforward
    fun `count operator`() {
        commonObservable
            .count()
            .subscribe { i ->
                println("Received: $i")
            }
    }

    @Test
    // This operator is almost like scan()
    // with the difference that it only emits
    // the final accumulation, with no intermediate values
    fun `reduce operator`() {
        val source = Observable.just(5, 3, 7, 10, 2, 14)

        source
            .reduce { accumulator, next -> accumulator + next }
            .subscribe { println("Received: $it") }

        source
            .reduce("") { accumulator, next ->
                (if (accumulator == "") "" else ",").let {
                    "$accumulator$it$next"
                }
            }
            .subscribe { final ->
                println("Received: $final")
            }
    }

    @Test
    // This will return true if all of the emisisons
    // satisfy the given predicate. Returns false otherwise.
    // If the observable is empty, it will return true.
    // It's a lazy operator: if there is a emission that
    // doesn't satisfy the predicate, the rest of the values are rejected
    fun `all operator`() {
        Observable.just(5, 3, 7, 11, 2, 14)
            .all { it < 10 }
            .subscribe { result -> println("Received: $result") }

        Observable.just(5, 3, 7, 9, 2, 1)
            .all { it < 10 }
            .subscribe { result -> println("Received: $result") }
    }

    @Test
    // This operator will return true if at least
    // one of the elements of the stream satisfies
    // the given predicate. It's lazy
    // If any has no arguments, it will emit false
    fun `any operator`() {
        Observable.just("2016-01-01", "2016-05-02", "2016-09-12", "2016-04-03")
            .map(LocalDate::parse)
            .any { localDate -> localDate.monthValue >= 6 }
            .subscribe { result -> println("Received: $result") }
    }

    @Test
    // Straightforward
    // It uses: hashCode() / equals()
    fun `contains operator`() {
        Observable.range(1, 10000)
            .contains(9563)
            .subscribe { s -> println("Received: $s") }
    }

    // ********************
    // Collection Operators
    // ********************

    @Test
    //Straightforward
    // You can give a capacityHint
    // It can receive a custom implementation of ArrayList
    fun `toList operator`() {
        commonObservable.toList()
            .subscribe { list -> println("Received: $list") }
    }

    @Test
    // Same as toList() but sorted
    fun `toSortedList operator`() {
        Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
            .toSortedList()
            .subscribe { sortedList: List<Int> -> println("Received: $sortedList") }
    }

    @Test
    // Straightforward
    // It can receive another lambda containing
    // a mapping function for the emitted elements
    // It can receive a third lambda that returns
    // a implementation of Map<K, V>
    // If there is a key that maps multiple elements,
    // the last emitted value will replace the others
    fun `toMap operator`() {
        commonObservable
            .toMap(String::length)
            .subscribe { map -> println("Received: $map") }
    }

    @Test
    // If the replaced elements have to be preserved,
    // the toMultiMap() operator is a better option
    fun `toMultiMap operator`() {
        commonObservable
            .toMultimap(String::length)
            .subscribe { map -> println("Received: $map") }
    }

    @Test
    // only Map and List are available to collect the elements
    // But collect() elements gives the possibility to use
    // another type
    // Two arguments are required: an 'initialValuSupplier',
    // that received the object who will collect, and a 'collector'
    // lambda, which is the function that handles every element
    fun `collect operator`() {
        commonObservable
            .collect({ HashSet<String>() }) { container, element ->
                container.add(element)
            }
            .subscribe { map -> println("Received: $map") }
    }
}