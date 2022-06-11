package com.jerry1012.shoppinglist.UI.AddShop

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerry1012.shoppinglist.room.Shop
import com.jerry1012.shoppinglist.room.ShopDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



class AddShopViewModel @AssistedInject constructor(
        private val shopDAO : ShopDao,
        @dagger.assisted.Assisted private val handle : SavedStateHandle
) : ViewModel() {


    val isEdit = handle.getLiveData("ShopEdited",0)
    val shopNameEditText = handle.getLiveData("ShopNameAddShopFragment","")
    val shopAddressEditText = handle.getLiveData("ShopAddressShopFragment","")
    private val addShopChannel = Channel<AddShopEvents>()
    val addshopEventsChannel = addShopChannel.receiveAsFlow()

    fun addShopToDatabase(shopToBeAdd : Shop) = viewModelScope.launch{

        if(shopToBeAdd.shopName.isEmpty()){
            addShopChannel.send(AddShopEvents.ShowSnackBarErrorShopName)
        }else if(shopToBeAdd.shopAddress.isEmpty()){
            addShopChannel.send(AddShopEvents.ShowSnackBarErrorShopAddress)
        }else {
            viewModelScope.launch {
                shopDAO.addShop(shopToBeAdd)
            }
            if(isEdit.value == 1){
                addShopChannel.send(AddShopEvents.NavigateToShopScreen(1))
            }else
            {
                addShopChannel.send(AddShopEvents.NavigateToShopScreen(0))
            }

        }
    }

    sealed class AddShopEvents(){

        data class NavigateToShopScreen(val result : Int) : AddShopEvents()
        object ShowSnackBarErrorShopName : AddShopEvents()
        object ShowSnackBarErrorShopAddress : AddShopEvents()

    }
}