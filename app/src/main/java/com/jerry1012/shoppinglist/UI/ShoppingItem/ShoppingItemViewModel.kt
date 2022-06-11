package com.jerry1012.shoppinglist.UI.ShoppingItem

import android.util.Log

import androidx.lifecycle.*
import com.jerry1012.shoppinglist.Util.PreferencesManager
import com.jerry1012.shoppinglist.Util.ShoppingItemFragment_Sort_Order
import com.jerry1012.shoppinglist.room.Shop
import com.jerry1012.shoppinglist.room.ShopDao
import com.jerry1012.shoppinglist.room.ShoppingItem
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ShoppingItemViewModel @AssistedInject constructor(
        private val shopDAO: ShopDao,
        private val shoppingItemFragmentPreferences: PreferencesManager,
        @dagger.assisted.Assisted private var shop : Shop,
        @dagger.assisted.Assisted private val handle: SavedStateHandle
) : ViewModel() {

    val size = shopDAO.getItemCount(shop.shopName).asLiveData()
    var hideBought = handle.getLiveData<Boolean>("HideBoughtShoppingItem")
    val searchQueryShoppingItemFragment = handle.getLiveData("SearchQueryShoppingItemFragment","")
    val categorySearch = handle.getLiveData("CategorySearched","")
    private val shoppingItemChannel = Channel<ShoppingItemEvents>()
    val shoppingItemEventsChannel = shoppingItemChannel.receiveAsFlow()
    val shopNamePassedFrom = shop.shopName
    val categoriesForShop = shopDAO.getCategoriesForShop(shopNamePassedFrom).asLiveData()
    val categoriesForKilogram = listOf<String>("Dairy","Goods","Frozen Food","Produce")
    val sortOrderFlowShoppingFragment = shoppingItemFragmentPreferences.shoppingItemPreferencesFlow
    val combineCategories = categoriesForShop.combineWith(size){ categories , size ->
        Log.i("CombinedData","$size")
    }


    private val shoppingItemFiltering = combine(searchQueryShoppingItemFragment.asFlow(),sortOrderFlowShoppingFragment,categorySearch.asFlow()){
        query , filterPrefernces , categorySearch ->
        hideBought.value = filterPrefernces.hideBought
        Triple(query,filterPrefernces,categorySearch)
    }.flatMapLatest { (query,filterPeferences,categorySearch) ->
        shopDAO.getShoppingItem(query,filterPeferences.sortOrder,filterPeferences.hideBought,categorySearch,shopNamePassedFrom)
    }


    val shoppingItems = shoppingItemFiltering.asLiveData()


    fun onAddItemScreen() = viewModelScope.launch {
        shoppingItemChannel.send(ShoppingItemEvents.NavigateToAdItemScreen)
    }


    fun onDeleteItem(shoppingItemToDelete : ShoppingItem) = viewModelScope.launch{
        var shopToUpdate = shop.copy()
        shopToUpdate.totalitem = shopToUpdate.totalitem - 1
        shopDAO.updateShop(shopToUpdate)
        shopDAO.deleteShoppingItem(shoppingItemToDelete)
        shop = shopToUpdate
        Log.i("ShoppingItemTotalNumber","${shopToUpdate.totalitem}")
        shoppingItemChannel.send(ShoppingItemEvents.ShowSnackBarForDeleteShoppingItem(shoppingItemToDelete))
    }


    fun addShoppingItemToDatabaseAgain(shoppingItem : ShoppingItem) = viewModelScope.launch {
        shopDAO.addShoppingItem(shoppingItem)
        var shopToUpdate = shop.copy()
        shopToUpdate.totalitem = shopToUpdate.totalitem + 1
        shopDAO.updateShop(shopToUpdate)
        shop = shopToUpdate
        Log.i("ShoppingItemTotalNumber","${shopToUpdate.totalitem}")
    }

    fun onPlusItemQuantity(shoppingItemToInc : ShoppingItem) = viewModelScope.launch{
        var tempShoppingItem = shoppingItemToInc.copy()
        if(categoriesForKilogram.contains(tempShoppingItem.categories)){
            tempShoppingItem.quantity = (tempShoppingItem.quantity + 0.5).toFloat()
        }else
        {
            tempShoppingItem.quantity = tempShoppingItem.quantity + 1
        }

        shopDAO.updateShoppingItem(tempShoppingItem)
        Log.i("Data","$tempShoppingItem")
    }

    fun onMinusItemQuantity(shoppingItemToDec : ShoppingItem)= viewModelScope.launch{
        var tempShoppingItem = shoppingItemToDec.copy()
        if(tempShoppingItem.quantity <= 1){
            shoppingItemChannel.send(ShoppingItemEvents.ShowSnackbarForDecresing)
        }else
        {
            if(categoriesForKilogram.contains(tempShoppingItem.categories)){
                tempShoppingItem.quantity = (tempShoppingItem.quantity - 0.5).toFloat()
            }else
            {
                tempShoppingItem.quantity = tempShoppingItem.quantity - 1
            }
            Log.i("Data","$tempShoppingItem")
            shopDAO.updateShoppingItem(tempShoppingItem)
        }
    }

    fun onBoughtItem(shoppingItemBought : ShoppingItem,isBought : Boolean)= viewModelScope.launch{
        var tempShoppingItem = shoppingItemBought.copy()
        tempShoppingItem.isBought = isBought
        Log.i("Data","$tempShoppingItem")
        shopDAO.updateShoppingItem(tempShoppingItem)
    }

    fun onShoppingItemClick(shoppingItemToEdit : ShoppingItem)= viewModelScope.launch{
        shoppingItemChannel.send(ShoppingItemEvents.NavigateToEditItemScreen(shoppingItemToEdit))
    }

    fun onSortOrderSelected(sortOrder : ShoppingItemFragment_Sort_Order) = viewModelScope.launch {
        shoppingItemFragmentPreferences.updateSortOrder(sortOrder)
        Log.i("Data Item","$sortOrder")
    }

    fun onHideBoughtSelected(hideBoughtSelected: Boolean) = viewModelScope.launch {
        Log.i("Data Item","$hideBoughtSelected")
        shoppingItemFragmentPreferences.updateHideCompleted(hideBoughtSelected)
    }


    fun showResultShoppingItem(result : Int) = viewModelScope.launch {
        shoppingItemChannel.send(ShoppingItemEvents.ShowResultShoppingItem(result))
    }


    fun <T, K, R> LiveData<T>.combineWith(
            liveData: LiveData<K>,
            block: (T?, K?) -> R
    ): LiveData<R> {
        val result = MediatorLiveData<R>()
        result.addSource(this) {
            result.value = block(this.value, liveData.value)
        }
        result.addSource(liveData) {
            result.value = block(this.value, liveData.value)
        }
        return result
    }

    sealed class ShoppingItemEvents(){

        object NavigateToAdItemScreen : ShoppingItemEvents()
        object ShowSnackbarForDecresing : ShoppingItemEvents()
        data class ShowSnackBarForDeleteShoppingItem(val shoppingItem : ShoppingItem) : ShoppingItemEvents()
        data class NavigateToEditItemScreen(val shoppingItem : ShoppingItem) : ShoppingItemEvents()
        data class ShowResultShoppingItem(val result : Int) : ShoppingItemEvents()
        object NavigateToShopFragment :ShoppingItemEvents()
    }

}