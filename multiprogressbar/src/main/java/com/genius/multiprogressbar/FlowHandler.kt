package com.genius.multiprogressbar

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

// TODO this implementation is not most accurate. There is an error of 100 milliseconds for 5 seconds
internal class FlowHandler(fromValue: Float, toValue: Float, durationInMillis: Long) :
    AnimationHandler(fromValue, toValue, durationInMillis), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job: Job = Job()

    private val sequence: Sequence<Float> = sequence {
        var cur = fromValue
        while (true) {
            yield(cur)
            cur += 1F
        }
    }
    private var progressEmitter = sequence.iterator()

    private val singleAwaitTime = durationInMillis / (toValue - fromValue)

    private val flow: Flow<Float> = flow {
        while (job.isActive) {
            delay(singleAwaitTime.toLong())
            val value = progressEmitter.next()
            emit(value)
        }
    }

    override fun start() {
        launch {
            flow.collect { value ->
                listener?.onChange(value)
            }
        }
    }

    override fun cancel() {
        job.cancel()
    }
}