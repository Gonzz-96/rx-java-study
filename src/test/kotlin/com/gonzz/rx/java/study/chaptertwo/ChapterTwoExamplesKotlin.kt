package com.gonzz.rx.java.study.chaptertwo

import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ChapterTwoExamplesKotlin {

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
}