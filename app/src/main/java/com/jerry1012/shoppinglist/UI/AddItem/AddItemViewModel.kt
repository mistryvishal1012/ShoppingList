package com.jerry1012.shoppinglist.UI.AddItem


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry1012.shoppinglist.room.Shop
import com.jerry1012.shoppinglist.room.ShopDao
import com.jerry1012.shoppinglist.room.ShoppingItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddItemViewModel @AssistedInject constructor(
        private val shopDAO: ShopDao,
        @Assisted private var shop : Shop,
        @dagger.assisted.Assisted private val handle: SavedStateHandle
) : ViewModel() {


    val isEditShoppingItem = handle.getLiveData("ShoppingItemEdited",0)
    val shoppingItemNameEditText = handle.getLiveData("ShoppingItemNameAddShoppingItemFragment","")
    val shoppingItemCategoryEditText = handle.getLiveData("ShopingItemCategoryShoppingFragment","")
    val shoppingItemQuantityEditText = handle.getLiveData("ShoppingItemQuantityShoppingFragment",1.0)
    private val addItemChannel = Channel<AddItemEvents>()
    val addItemEventsChannel = addItemChannel.receiveAsFlow()


    fun addItem(shoppingItem : ShoppingItem) = viewModelScope.launch {
        if(shoppingItem.shoppingItemName.isEmpty()){
            addItemChannel.send(AddItemEvents.ShowSnackBarErrorItemName)
        }else if(shoppingItem.categories.isEmpty()){
            addItemChannel.send(AddItemEvents.ShowSnackBarErrorItemCategories)
        }else if(shoppingItem.quantity.equals(0.0)){
            addItemChannel.send(AddItemEvents.ShowSnackBarErrorItemQuantity)
        }else
        {
            shopDAO.addShoppingItem(shoppingItem)
            shop.totalitem = shop.totalitem + 1
            shopDAO.updateShop(shop)
            Log.i("ShoppingItemTotalNumber","${shop.totalitem}")
            if(isEditShoppingItem.value == 1){
                addItemChannel.send(AddItemEvents.NavigateToShoppingItemScreen(1))
            }else
            {
                addItemChannel.send(AddItemEvents.NavigateToShoppingItemScreen(0))
            }

        }
    }

    fun updateItem(shoppingItem : ShoppingItem) = viewModelScope.launch {
        if(shoppingItem.shoppingItemName.isEmpty()){
            addItemChannel.send(AddItemEvents.ShowSnackBarErrorItemName)
        }else if(shoppingItem.categories.isEmpty()){
            addItemChannel.send(AddItemEvents.ShowSnackBarErrorItemCategories)
        }else if(shoppingItem.quantity.equals(0.0)){
            addItemChannel.send(AddItemEvents.ShowSnackBarErrorItemQuantity)
        }else
        {
            shopDAO.updateShoppingItem(shoppingItem)
            if(isEditShoppingItem.value == 1){
                addItemChannel.send(AddItemEvents.NavigateToShoppingItemScreen(1))
            }else
            {
                addItemChannel.send(AddItemEvents.NavigateToShoppingItemScreen(0))
            }
        }
    }


    sealed class AddItemEvents(){

        data class NavigateToShoppingItemScreen(val result : Int) : AddItemEvents()
        object ShowSnackBarErrorItemName : AddItemEvents()
        object ShowSnackBarErrorItemCategories : AddItemEvents()
        object ShowSnackBarErrorItemQuantity : AddItemEvents()

    }

}