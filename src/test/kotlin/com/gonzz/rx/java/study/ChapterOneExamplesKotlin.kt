package com.gonzz.rx.java.study.chapterone

import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class ChapterOneExamplesKotlin {

    @Test
    fun introducingObservables() {
        val myStrings = Observable.just(
            "Alpha", "Beta", "Gamma", "Delta"
        )
        myStrings
            //.map(string -> string.length())
            .subscribe { s: String? -> println(s) }
        printSeparator()
    }

    @Test
    fun pushingEvents() {
        val secondIntervals = Observable.interval(1, TimeUnit.SECONDS)

        secondIntervals.subscribe { println(it) }
        sleep(5000L)
    }

    private fun printSeparator() {
        println("\n****************\n")
    }

    private fun sleep(millis: Long) = Thread.sleep(millis)
}