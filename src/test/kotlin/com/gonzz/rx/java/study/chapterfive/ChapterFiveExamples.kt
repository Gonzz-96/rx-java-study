package com.gonzz.rx.java.study.chapterfive

import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ChapterFiveExamples {

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
}
