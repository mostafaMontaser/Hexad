package com.hexad.view

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.widget.Button
import android.widget.Switch
import butterknife.BindView
import butterknife.ButterKnife
import com.hexad.model.Movie
import com.hexad.view.adapter.ListItemClickListener
import com.hexad.view.adapter.MoviesAdapter
import com.hexad.viewmodel.DaggerViewModelComponent
import com.hexad.viewmodel.MainActivityViewModel




class MainActivity : AppCompatActivity() {
    @BindView(com.hexad.R.id.ranking_btn)
    lateinit var rankingSwitch: Switch
    @BindView(com.hexad.R.id.movies_list)
    lateinit var moviesList: RecyclerView
    private var mainActivityViewModel: MainActivityViewModel? = null
    private var movieAdapter: MoviesAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.hexad.R.layout.activity_main)
        ButterKnife.bind(this)
        initUI()
        initViewModel()
        mainActivityViewModel?.loadDataFirstTime()
        mainActivityViewModel?.getMovies()
    }

    private fun initUI() {
        var layoutResultsManager = LinearLayoutManager(this)
        movieAdapter = MoviesAdapter()
        moviesList.layoutManager = layoutResultsManager
        moviesList.adapter = movieAdapter
        movieAdapter?.setClickListener(object : ListItemClickListener<Movie> {
            override fun onListItemClick(data: Movie, position: Int) {
                showDialog(data)
            }

        })
        rankingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                mainActivityViewModel?.startRandomRanking()
            else
                mainActivityViewModel?.stopRandomRankingIfStarted()
        }
    }

    private fun initViewModel() {
        mainActivityViewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val mainActivityViewModel = MainActivityViewModel()
                DaggerViewModelComponent.create().inject(mainActivityViewModel)
                return mainActivityViewModel as T
            }
        }).get(MainActivityViewModel::class.java)
        mainActivityViewModel?.getMovieLiveData()
            ?.observe(this, Observer { movies: ArrayList<Movie>? -> (showMovies(movies)) })

    }

    private fun showMovies(movies: ArrayList<Movie>?) {
        if (movies != null)
            movieAdapter?.setItems(movies)
        val layoutManager = moviesList
            .getLayoutManager() as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(0, 0)
    }

    private fun showDialog(movie: Movie) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(com.hexad.R.layout.rate_movie)

        val rateBar = dialog.findViewById(com.hexad.R.id.rating_bar) as AppCompatRatingBar
        if (movie.rate != null)
            rateBar.rating = movie.rate!!
        val cancelBtn = dialog.findViewById(com.hexad.R.id.movie_rate_cancel_btn) as Button
        val rateBtn = dialog.findViewById(com.hexad.R.id.movie_rate_btn) as Button
        cancelBtn.setOnClickListener { dialog.dismiss() }
        rateBtn.setOnClickListener {
            if (movie.rate != rateBar.rating)
                mainActivityViewModel?.rateMovie(movie, rateBar.rating)
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onStop() {
        super.onStop()
        mainActivityViewModel?.stopRandomRankingIfStarted()
    }
}
