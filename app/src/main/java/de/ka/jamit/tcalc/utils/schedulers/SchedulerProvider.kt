package de.ka.jamit.tcalc.utils.schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * A scheduler abstraction for android.
 *
 * Created by Thomas Hofmann on 17.07.20
 **/
interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
}

class AndroidSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.io()
    override fun ui() = AndroidSchedulers.mainThread()
    override fun computation() = Schedulers.computation()
}

class TestsSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.trampoline()
    override fun ui() = Schedulers.trampoline()
    override fun computation() = Schedulers.trampoline()
}