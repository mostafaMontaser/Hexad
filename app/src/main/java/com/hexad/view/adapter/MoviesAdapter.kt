package com.hexad.view.adapter

import android.content.Context
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.hexad.model.Movie
import com.squareup.picasso.Picasso


class MoviesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185"
    private var movies: ArrayList<Movie> = arrayListOf()
    private var clickListener: ListItemClickListener<Movie>? = null

    fun setClickListener(itemClickListener: ListItemClickListener<Movie>) {
        this.clickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(com.hexad.R.layout.movie_item, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movieHolder = holder as CardViewHolder
        val movie = movies.get(position)

        movieHolder.movieNameTxt.text = movie.title
        movieHolder.movieDateTxt.text = movie.releaseDate
        Picasso.get().load(IMAGE_BASE_URL+movie.posterPath).into(movieHolder.movieImg);
        if (movie.rate != null)
            movieHolder.movieRateBar.rating = movie.rate!!

    }

    inner class CardViewHolder : RecyclerView.ViewHolder, View.OnClickListener {
        @BindView(com.hexad.R.id.movie_cell)
        lateinit var movieCell: RelativeLayout
        @BindView(com.hexad.R.id.movie_name_txt)
        lateinit var movieNameTxt: TextView
        @BindView(com.hexad.R.id.movie_date_txt)
        lateinit var movieDateTxt: TextView
        @BindView(com.hexad.R.id.movie_rating_bar)
        lateinit var movieRateBar: AppCompatRatingBar
        @BindView(com.hexad.R.id.movie_img)
        lateinit var movieImg: ImageView
         var context:Context?=null

        constructor(view: View) : super(view) {
            ButterKnife.bind(this, itemView)
            movieCell.setOnClickListener(this)
            context=view.context
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            clickListener?.onListItemClick(movies.get(position), position)
        }
    }

    fun setItems(items: ArrayList<Movie>) {
        movies.clear()
        movies.addAll(items)
        notifyDataSetChanged()
    }

}