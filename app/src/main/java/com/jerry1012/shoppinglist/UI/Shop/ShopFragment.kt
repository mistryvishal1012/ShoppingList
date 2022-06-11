package com.jerry1012.shoppinglist.UI.Shop

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jerry1012.shoppinglist.R
import com.jerry1012.shoppinglist.Util.OnQueryTextChanged
import com.jerry1012.shoppinglist.Util.ShopFragment_Sort_Order
import com.jerry1012.shoppinglist.databinding.FragmentShopDetailsBinding
import com.jerry1012.shoppinglist.room.Shop
import com.jerry1012.shoppinglist.room.ShoppingItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_shop_details.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ShopFragment :  Fragment(R.layout.fragment_shop_details),ShopAdapter.OnItemClickListener {

    @Inject
    lateinit var shopViewModelFactory: ShopViewModelFactory

    private val shopViewModel : ShopViewModel by viewModels(){
        providerFactory(shopViewModelFactory,this,null)
    }
    private lateinit var searchView : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shopbinding = FragmentShopDetailsBinding.bind(view)
        val shopAdapter = ShopAdapter(this)


        shopbinding.apply {
            recviewShops.apply {
                adapter = shopAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            fragmentshoprecyviewToolbar.setTitle(R.string.shopsLabel)
            fragmentshoprecyviewToolbar.inflateMenu(R.menu.menu_shop_fragment)
            fragmentshoprecyviewToolbar.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.shop_fragment_search->{

                    }
                    R.id.shop_fragment_item->{
                        Log.i("Data Item","Sort By Item")
                        shopViewModel.onSortOrderSelected(ShopFragment_Sort_Order.BY_ITEM)
                    }
                    R.id.shop_fragment_name->{
                        Log.i("Data Item","Sort By Name")
                        shopViewModel.onSortOrderSelected(ShopFragment_Sort_Order.BY_NAME)
                    }
                    R.id.shop_fragment_date->{
                        Log.i("Data Item","Sort By Date")
                        shopViewModel.onSortOrderSelected(ShopFragment_Sort_Order.BY_DATE)
                    }
                }
                true
            }

            fabAddShopp.setOnClickListener {
                shopViewModel.onAddNewShop()
            }

            val searchItem = fragmentshoprecyviewToolbar.menu.findItem(R.id.shop_fragment_search)
            searchView = searchItem.actionView as SearchView
            val searchQueryShopFragment = shopViewModel.query.value
            if (searchQueryShopFragment != null && searchQueryShopFragment.isNotEmpty()){

                searchItem.expandActionView()
                searchView.setQuery(searchQueryShopFragment,false)

            }

            searchView.OnQueryTextChanged{
                shopViewModel.query.value = it
                Log.i("Data Item","Query $it")
            }

        }

        shopViewModel.shopsFind.observe(viewLifecycleOwner){
            shopAdapter.submitList(it)
            Log.i("Data Item Filtering","$it")
        }

        setFragmentResultListener("add_edit_shop_request"){ _ , bundle ->
            val result = bundle.getInt("add_edit_shop_request")
            shopViewModel.showResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {shopViewModel.shopEvent.collect{ event ->
                when(event){
                    is ShopViewModel.ShopsEvents.NavigateToAddShopScreen -> {
                        val navigationAction = ShopFragmentDirections.actionShopFragmentToAddShopFragment()
                        findNavController().navigate(navigationAction)
                    }
                    is ShopViewModel.ShopsEvents.NavigateToAddOrShowItemScreen -> {
                        Log.i("Data Item","${event.shop}")
                        val tempShop : Shop = event.shop
                        val navigationAction = ShopFragmentDirections.actionShopFragmentToShoppingItemFragment(tempShop!!)
                        findNavController().navigate(navigationAction)
                    }
                    is ShopViewModel.ShopsEvents.NavigateToEditShopScreen ->{
                        val navigationAction = ShopFragmentDirections.actionShopFragmentToAddShopFragment(event.shop,true)
                        findNavController().navigate(navigationAction)
                    }
                    is ShopViewModel.ShopsEvents.ShowSortOrderSelected ->{
                        Log.i("Data Item","${event.sortOrder}")
                    }
                    is ShopViewModel.ShopsEvents.ShowResult -> {
                        var message = "Shop"
                        message = if(event.result == 1){
                            "$message Edited Successfully"
                        }else {
                            "$message Added Successfully"
                        }
                        Snackbar.make(requireView(),message,Snackbar.LENGTH_LONG).show()
                    }
                    is ShopViewModel.ShopsEvents.ShowSnackbarDeleteShop -> {
                        Snackbar.make(requireView(),"${event.shopDeleted.shopName} is Deleted Successfully",Snackbar.LENGTH_LONG).setAction("UNDO"){
                            shopViewModel.addAllShoppingItemandShop(event.shopDeleted,event.shoppingItems)
                        }.show()
                    }
                }
            }
        }
    }


    override fun onItemClick(shop: Shop) {
        shopViewModel.onShopClickListener(shop)
    }

    override fun onDeleteBoxClick(shop: Shop) {

        showAlert("${shop.shopName}","Delete",shop)
    }

    override fun onEditShopClick(shop: Shop) {
        shopViewModel.onEditShop(shop)
    }


    override fun onDestroyView() {
        searchView.OnQueryTextChanged { null }
        super.onDestroyView()
    }

    fun showAlert(msg : String,action : String,shop : Shop){
        var alertTitle = " Shop $msg"
        var alertMessage =""
        var posititveMsg = "Yes"
        var negativeMsg = "No"
        when(action){
            "Delete"->{
                alertTitle = "Delete$alertTitle"
                alertMessage = " Are You Sure, You Want To Delete $msg ?"
            }
        }
        val actionAlert = AlertDialog.Builder(requireContext()).apply {
            setTitle(alertTitle)
            setMessage(alertMessage)
            setPositiveButton(posititveMsg){ _,_ ->
                shopViewModel.onDeleteShop(shop)
            }
            setNegativeButton(negativeMsg){_,_ ->
                null
            }
            setCancelable(false)
        }
        actionAlert.show()
    }
}
