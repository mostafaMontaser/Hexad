package com.hexad.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hexad.dispatcher.MainActivityDispatcher
import com.hexad.model.Movie
import com.hexad.model.Movies
import com.hexad.util.Keys
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivityViewModel : ViewModel() {
    private var disposables: CompositeDisposable = CompositeDisposable()
    private val movieLiveData = MutableLiveData<ArrayList<Movie>>()

    @Inject
    lateinit var mainActivityDispatcher: MainActivityDispatcher

    fun getMovieLiveData(): MutableLiveData<ArrayList<Movie>> {
        return movieLiveData
    }

    fun loadDataFirstTime() {
        mainActivityDispatcher.loadObjectFromAssetAndSaveToCache("movies", Keys.MOVIES, Movies::class.java)
    }

    fun getMovies() {
        val movieList = mainActivityDispatcher.getMovies()?.movies
        movieList?.sortWith(compareByDescending { it.rate })
        movieLiveData.value = movieList
    }

    fun rateMovie(movie: Movie?, newRate: Float) {
        val moviesModel: Movies? = mainActivityDispatcher.getMovies()
        if (moviesModel != null) {
            val moviesList: ArrayList<Movie>? = moviesModel.movies
            moviesList?.forEach {
                if (it.id == movie?.id) {
                    it.rate = newRate
                    return@forEach
                }
            }
            moviesList?.sortWith(compareByDescending { it.rate })
            mainActivityDispatcher.setMovies(Keys.MOVIES, moviesModel)
            movieLiveData.value = moviesList
        }
    }

    fun startRandomRanking() {
        val newInterval: PublishSubject<Int> = PublishSubject.create<Int>()
        val disposable: Disposable = newInterval.switchMap { currentPeriod ->
            Observable.interval(currentPeriod.toLong(), TimeUnit.MILLISECONDS).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
        }
            .doOnNext {}.subscribeWith(object : DisposableObserver<Any>() {
                override fun onComplete() {
                }

                override fun onNext(t: Any) {
                    val moviesModel: Movies? = mainActivityDispatcher.getMovies()
                    val index = (0..9).random()
                    val movie: Movie? = moviesModel?.movies?.get(index)
                    if (movie != null)
                        rateMovie(movie, (0..5).random().toFloat())
                    newInterval.onNext((1000..10000).random())
                }

                override fun onError(e: Throwable) {
                }

            })
        newInterval.onNext((1000..10000).random())
        disposables.add(disposable)
    }

    fun stopRandomRankingIfStarted() {
        disposables.clear()
    }
}
