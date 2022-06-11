package com.jerry1012.shoppinglist.UI.Shop

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.jerry1012.shoppinglist.Util.ShopFragmentPreferences
import com.jerry1012.shoppinglist.Util.ShopFragmentPreferencesSortOrder
import com.jerry1012.shoppinglist.Util.ShopFragment_Sort_Order
import com.jerry1012.shoppinglist.Util.ShoppingItemFragment_Sort_Order
import com.jerry1012.shoppinglist.room.Shop
import com.jerry1012.shoppinglist.room.ShopDao
import com.jerry1012.shoppinglist.room.ShoppingItem
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewScoped
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class ShopViewModel @AssistedInject constructor(
    private val shopDAO: ShopDao,
    private val shopFragmentPreferences : ShopFragmentPreferences,
    @dagger.assisted.Assisted private val handle: SavedStateHandle
) : ViewModel() {


    val query = handle.getLiveData("ShopFragmentSearchQuery", "")
    private val shopEventChannel = Channel<ShopsEvents>()
    val shopEvent = shopEventChannel.receiveAsFlow()
    val sortOrderShopFragmentFlow = shopFragmentPreferences.shopFragmentSortOrderFlow

    val shopsFind = combine(query.asFlow(), sortOrderShopFragmentFlow) { query, sortOrder ->
        Pair(query, sortOrder.sortOrderShopFragment)
    }.flatMapLatest {
        Log.i("Data Item ViewModel", "${it.first}")
        shopDAO.getTask(it.second, it.first)
    }.asLiveData()

    fun onAddNewShop() = viewModelScope.launch {
        shopEventChannel.send(ShopsEvents.NavigateToAddShopScreen)
    }

    fun onEditShop(shopToEdit: Shop) = viewModelScope.launch {
        shopEventChannel.send(ShopsEvents.NavigateToEditShopScreen(shopToEdit))
    }

    fun onDeleteShop(shop: Shop) = GlobalScope.async{
        val shoppingItems = shopDAO.getShoppingItemToAdd(shop.shopName)
        Log.i("All Shopping Item","${shoppingItems}")
        shopDAO.deleteShoppingItemFromShop(shop.shopName)
        shopDAO.deleteShop(shop)
        shopEventChannel.send(ShopsEvents.ShowSnackbarDeleteShop(shop,shoppingItems))
    }

    fun addAllShoppingItemandShop(shop : Shop,allShoppingItemsToAdd : List<ShoppingItem>) = GlobalScope.async{
        shopDAO.addShop(shop)
        Log.i("All Shopping Item","${allShoppingItemsToAdd}")
        for (i in allShoppingItemsToAdd){
            shopDAO.addShoppingItem(i)
        }
    }

    fun onShopClickListener(shopToShow: Shop) = viewModelScope.launch {
        shopEventChannel.send(ShopsEvents.NavigateToAddOrShowItemScreen(shopToShow))
    }

    fun onSortOrderSelected(sortOrder: ShopFragment_Sort_Order) = viewModelScope.launch {
        shopFragmentPreferences.updateShopFragmentSortOrder(sortOrder)
    }

    fun showResult(result: Int) = viewModelScope.launch {
        shopEventChannel.send(ShopsEvents.ShowResult(result))
    }

    fun addShop(shopToAdd: Shop) = viewModelScope.launch {
        shopDAO.addShop(shopToAdd)
        Log.i("Data Item Add","${shopToAdd.shopName}")
    }

    sealed class ShopsEvents(){
        object NavigateToAddShopScreen : ShopsEvents()
        data class NavigateToAddOrShowItemScreen(val shop : Shop) : ShopsEvents()
        data class NavigateToEditShopScreen(val shop : Shop) : ShopsEvents()
        data class ShowSortOrderSelected(val sortOrder : ShopFragment_Sort_Order) : ShopsEvents()
        data class ShowResult(val result : Int) : ShopsEvents()
        data class ShowSnackbarDeleteShop(val shopDeleted : Shop,val shoppingItems : List<ShoppingItem>) : ShopsEvents()
    }
}
