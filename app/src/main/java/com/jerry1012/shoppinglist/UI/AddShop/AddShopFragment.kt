package com.jerry1012.shoppinglist.UI.AddShop

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.jerry1012.shoppinglist.R
import com.jerry1012.shoppinglist.UI.Shop.ShopFragmentDirections
import com.jerry1012.shoppinglist.UI.Shop.ShopViewModel
import com.jerry1012.shoppinglist.databinding.FragmentShopnameBinding
import com.jerry1012.shoppinglist.room.Shop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import androidx.lifecycle.observe

@AndroidEntryPoint
class AddShopFragment :  Fragment(R.layout.fragment_shopname) {


    @Inject
    lateinit var addShopViewModelFactory: AddShopViewModelFactory

    private val addShopViewModel : AddShopViewModel by viewModels(){
        providerFactory(addShopViewModelFactory,this,null)
    }

    private val addShopFragmentArgs : AddShopFragmentArgs by navArgs<AddShopFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addshopbinding = FragmentShopnameBinding.bind(view)

        val isEdit = addShopFragmentArgs.isEdit


        addshopbinding.apply {

            fragmentshopnameToolbar.setTitle(R.string.addShopLabel)
            if(isEdit){
                addShopViewModel.isEdit.value = 1
                val shopDetails = addShopFragmentArgs.shop
                tvShopname.setText(shopDetails!!.shopName)
                tvAddress.setText(shopDetails!!.shopAddress)
            }

            addShopViewModel.shopNameEditText.observe(viewLifecycleOwner){
                if(it.isNotBlank()){
                    tvShopname.setText(it)
                }
            }

            addShopViewModel.shopAddressEditText.observe(viewLifecycleOwner){
                if(it.isNotBlank()){
                    tvAddress.setText(it)
                }
            }

            btnSaveShop.setOnClickListener {
                addShopViewModel.addShopToDatabase(Shop(tvShopname.text.toString(),tvAddress.text.toString(),0.00,0.00,System.currentTimeMillis(),0))
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {addShopViewModel.addshopEventsChannel.collect{ event ->
            when(event){
                is AddShopViewModel.AddShopEvents.NavigateToShopScreen -> {
                    val navigationAction = AddShopFragmentDirections.actionAddShopFragmentToShopFragment()
                    findNavController().navigate(navigationAction)
                    setFragmentResult(
                            "add_edit_shop_request",
                            bundleOf("add_edit_shop_request" to event.result)
                    )
                }
                is AddShopViewModel.AddShopEvents.ShowSnackBarErrorShopName -> {
                    Snackbar.make(requireView(),"Shop Name Cannot Be Empty",Snackbar.LENGTH_LONG).show()
                }
                is AddShopViewModel.AddShopEvents.ShowSnackBarErrorShopAddress -> {
                    Snackbar.make(requireView(),"Shop Address Cannot Be Empty",Snackbar.LENGTH_LONG).show()
                }
            }
            }
        }

    }


}