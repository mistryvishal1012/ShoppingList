package com.jerry1012.shoppinglist.UI.ShoppingItem

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.jerry1012.shoppinglist.room.Shop
import dagger.assisted.AssistedFactory


@AssistedFactory
interface ShoppingItemViewModelFactory {

    fun create(
            shop : Shop,
            handle : SavedStateHandle
    ) : ShoppingItemViewModel

}

fun providerFactory(
    assistedFactory : ShoppingItemViewModelFactory,
    shop : Shop,
    owner : SavedStateRegistryOwner,
    defaultArgs : Bundle? = null
) : AbstractSavedStateViewModelFactory = object : AbstractSavedStateViewModelFactory(owner, defaultArgs){

    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return assistedFactory.create(shop,handle) as T
    }
}