package com.jerry1012.shoppinglist.Util

import android.util.Log
import androidx.appcompat.widget.SearchView

inline fun SearchView.OnQueryTextChanged(crossinline listener : (String) -> Unit ){
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            Log.i("Data Item","$newText")
            listener(newText.orEmpty())
            return true
        }
    })
}