package de.ka.jamit.tcalc.utils

import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

//
// Extension functions for less reactive boilerplate code
//

fun Completable.with(schedulerProvider: SchedulerProvider): Completable =
        this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun <T> Single<T>.with(schedulerProvider: SchedulerProvider): Single<T> =
        this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun <T> Observable<T>.with(schedulerProvider: SchedulerProvider): Observable<T> =
        this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun <T> Flowable<T>.with(schedulerProvider: SchedulerProvider): Flowable<T> =
        this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun <T> Maybe<T>.with(schedulerProvider: SchedulerProvider): Maybe<T> =
        this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

fun Disposable.start(compositeDisposable: CompositeDisposable, start: () -> Unit): Disposable {
    return apply {
        compositeDisposable.add(this)
        start()
    }
}