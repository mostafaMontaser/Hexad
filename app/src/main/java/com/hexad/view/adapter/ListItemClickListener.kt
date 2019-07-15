package com.hexad.view.adapter

interface ListItemClickListener<T> {
    fun  onListItemClick(data: T, position: Int)
}