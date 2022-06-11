package com.jerry1012.shoppinglist.UI.ShoppingItem

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jerry1012.shoppinglist.R
import com.jerry1012.shoppinglist.databinding.FragmentShopingitemBinding
import kotlinx.coroutines.flow.collect
import com.jerry1012.shoppinglist.room.Shop
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.jerry1012.shoppinglist.UI.AddItem.AddItemFragmentArgs
import com.jerry1012.shoppinglist.Util.OnQueryTextChanged
import com.jerry1012.shoppinglist.Util.ShoppingItemFragment_Sort_Order
import com.jerry1012.shoppinglist.databinding.LayoutShoppingitemBinding
import com.jerry1012.shoppinglist.room.ShoppingItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import javax.inject.Inject
import javax.sql.StatementEvent
import kotlin.math.log


@AndroidEntryPoint
class ShoppingItemFragment :  Fragment(R.layout.fragment_shopingitem),ShoppingItemAdapter.OnShoppingItemClikListener{

    @Inject
    lateinit var shoppingItemViewModelFactory: ShoppingItemViewModelFactory
    private val shoppingItemFragmentArgs  : ShoppingItemFragmentArgs by navArgs<ShoppingItemFragmentArgs>()
    private val shoppingItemViewModel : ShoppingItemViewModel by viewModels(){
        var shop : Shop = shoppingItemFragmentArgs.ShopDetails!!
        Log.i("Data Item","$shop")
        providerFactory(shoppingItemViewModelFactory,shop,this,null)
    }
    private lateinit var shopFromBought : Shop
    private var categoriesForShop : MutableList<String> = mutableListOf()
    private var mapOfCategoriesString : MutableMap<Int,String> = mutableMapOf()
    private var mapOfCategoriesView : MutableMap<Int,View> = mutableMapOf()
    private lateinit var shoppingItembinding : FragmentShopingitemBinding
    private var firstIdToChecked : Int = 0
    var size : Int = 0
    private lateinit var searchView: SearchView

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val Categories = resources.getStringArray(R.array.grocery_categories)
        var toolBarString = getString(R.string.shoppingItemLabel)
        shoppingItembinding = FragmentShopingitemBinding.bind(view)
        shopFromBought = shoppingItemFragmentArgs.ShopDetails!!
        val shopName = shopFromBought!!.shopName
        val additionalString = " From $shopName"
        val shoppingItemAdapter = ShoppingItemAdapter(this)

        shoppingItemViewModel.shoppingItems.observe(viewLifecycleOwner){
           shoppingItemAdapter.submitList(it)
        }


        shoppingItemViewModel.categoriesForShop.observe(viewLifecycleOwner){ categories ->
            Log.i("Observer","Categories")
            shoppingItemViewModel.size.observe(viewLifecycleOwner){
                Log.i("Observer","Size")
                size = it
                categoriesForShop.clear()
                if(size >= 1){
                    categoriesForShop.add("All Item")
                    Log.i("ShoppingItem","All Item")
                    categoriesForShop.addAll(categories)
                }
                else{
                    categoriesForShop.add("Please Enter Any Item")
                }
                addChip(categoriesForShop)
            }
        }



        shoppingItembinding!!.apply {

            toolBarString += additionalString
            fragmentitemrecyviewToolbar.title = toolBarString

            fabAddItem.setOnClickListener {
                shoppingItemViewModel.onAddItemScreen()
            }

            recviewShoppingitem.apply {
                setHasFixedSize(true)
                adapter = shoppingItemAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }


            fragmentitemrecyviewToolbar.inflateMenu(R.menu.menu_shoppingitem_fragment)
            val hideBoughtMenu = fragmentitemrecyviewToolbar.menu.findItem(R.id.shoppingitem_fragment_hideIsBought)
            val searchItem = fragmentitemrecyviewToolbar.menu.findItem(R.id.shoppingitem_fragment_search)
            shoppingItemViewModel.hideBought.observe(viewLifecycleOwner){
                if(it){
                    hideBoughtMenu.isChecked = it
                }else
                {
                    hideBoughtMenu.isChecked = it
                }
            }
            searchView = searchItem.actionView as SearchView
            fragmentitemrecyviewToolbar.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.shoppingitem_fragment_date->{
                        shoppingItemViewModel.onSortOrderSelected(ShoppingItemFragment_Sort_Order.BY_DATE)
                    }
                    R.id.shoppingitem_fragment_categories->{
                        shoppingItemViewModel.onSortOrderSelected(ShoppingItemFragment_Sort_Order.BY_CATEGORY)
                    }
                    R.id.shoppingitem_fragment_name->{
                        shoppingItemViewModel.onSortOrderSelected(ShoppingItemFragment_Sort_Order.BY_NAME)
                    }
                    R.id.shoppingitem_fragment_hideIsBought -> {
                        if(hideBoughtMenu.isChecked){
                            hideBoughtMenu.isChecked = false
                            shoppingItemViewModel.onHideBoughtSelected(false)
                        }else
                        {
                            hideBoughtMenu.isChecked = true
                            shoppingItemViewModel.onHideBoughtSelected(true)
                        }
                    }
                }
                true
            }


            searchView.OnQueryTextChanged {
                shoppingItemViewModel.searchQueryShoppingItemFragment.value = it
            }

            chipGroupChoice.setOnCheckedChangeListener { group, checkedId ->
                Log.i("Data Item","$checkedId")
                Log.i("Data Item","${mapOfCategoriesString[checkedId]}")
                if((checkedId == firstIdToChecked) || (checkedId == -1)){
                    shoppingItemViewModel.categorySearch.value = ""
                }else {
                    shoppingItemViewModel.categorySearch.value = mapOfCategoriesString[checkedId]!!
                }
            }
        }

        setFragmentResultListener("add_edit_shoppingitem_request"){ _ , bundle ->
            val result = bundle.getInt("add_edit_shoppingitem_request")
            shoppingItemViewModel.showResultShoppingItem(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            shoppingItemViewModel.shoppingItemEventsChannel.collect{ events ->
                when(events){
                    is ShoppingItemViewModel.ShoppingItemEvents.ShowSnackbarForDecresing -> {
                        Snackbar.make(requireView(),"Shopping Item Cannot Be Decreased",Snackbar.LENGTH_LONG).show()
                    }
                    is ShoppingItemViewModel.ShoppingItemEvents.NavigateToAdItemScreen -> {
                        val navigationAction = ShoppingItemFragmentDirections.actionShoppingItemFragmentToAddItemFragment(shopFromBought,false,null)
                        findNavController().navigate(navigationAction)
                    }
                    is ShoppingItemViewModel.ShoppingItemEvents.ShowSnackBarForDeleteShoppingItem -> {
                        Snackbar.make(requireView(),"${events.shoppingItem.shoppingItemName} is Deleted Successfully",Snackbar.LENGTH_LONG).setAction("UNDO"){
                            shoppingItemViewModel.addShoppingItemToDatabaseAgain(events.shoppingItem)
                        }.show()
                    }
                    is ShoppingItemViewModel.ShoppingItemEvents.NavigateToEditItemScreen -> {
                        val navigationAction = ShoppingItemFragmentDirections.actionShoppingItemFragmentToAddItemFragment(shopFromBought,true,events.shoppingItem)
                        findNavController().navigate(navigationAction)
                    }
                    is ShoppingItemViewModel.ShoppingItemEvents.ShowResultShoppingItem ->{
                        var message = "Shopping item"
                        message = if(events.result == 1){
                            "$message Edited Successsfully"
                        }else
                        {
                            "$message Added Successfully"
                        }
                        Snackbar.make(requireView(),message,Snackbar.LENGTH_LONG).show()
                    }
                }

            }
        }

        setHasOptionsMenu(true)

    }

    override fun OnItemClick(shoppingItem: ShoppingItem) {
        shoppingItemViewModel.onShoppingItemClick(shoppingItem)
    }

    override fun OnCheckBoxClickListen(shoppingItem: ShoppingItem,isChecked : Boolean) {
        shoppingItemViewModel.onBoughtItem(shoppingItem,isChecked)
    }

    override fun OnDeleteBoxClickListen(shoppingItem: ShoppingItem) {
        shoppingItemViewModel.onDeleteItem(shoppingItem)
    }

    override fun OnAddItemClickListen(shoppingItem: ShoppingItem) {
        shoppingItemViewModel.onPlusItemQuantity(shoppingItem)
    }

    override fun OnMinusItemClickListen(shoppingItem: ShoppingItem) {
        shoppingItemViewModel.onMinusItemQuantity(shoppingItem)
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun addChip(chipsToAdd : List<String>){
        deletChip()
        var CategoriesAdded : MutableList<String> = mutableListOf()
        if(mapOfCategoriesString.isEmpty() && mapOfCategoriesView.isEmpty()){
            for (i in chipsToAdd){
                if(!CategoriesAdded.contains(i)){
                    val chips = Chip(this.requireContext()).apply {
                        id = View.generateViewId()
                        Log.i("Data Item","$id")
                        isClickable = true
                        isCheckable = true
                        isCheckedIconVisible = true
                        isFocusable = true
                        text = i
                        if(i == "All Item"){
                            firstIdToChecked = id
                            isChecked = true
                        }
                    }
                    shoppingItembinding.chipGroupChoice.addView(chips).also {
                        CategoriesAdded.add(chips.text.toString())
                        mapOfCategoriesString.put(chips.id,chips.text.toString())
                        mapOfCategoriesView.put(chips.id,chips)
                    }

                }
            }
            Log.i("Data Item","$mapOfCategoriesString")
            Log.i("Data Item","$mapOfCategoriesView")
        }
    }

    fun deletChip(){
        for (i in mapOfCategoriesView){
            shoppingItembinding.chipGroupChoice.removeView(i.value)
        }
        mapOfCategoriesView.clear()
        mapOfCategoriesString.clear()
    }




    override fun onDestroyView() {
        val parentView = shoppingItembinding.root
        for (i in mapOfCategoriesView.keys){
            parentView.removeView(mapOfCategoriesView[i])
            mapOfCategoriesString.remove(i)
        }
        mapOfCategoriesView.clear()
        mapOfCategoriesString.clear()
        Log.i("Data Item","$mapOfCategoriesString")
        Log.i("Data Item","$mapOfCategoriesView")
        searchView.OnQueryTextChanged { null }
        super.onDestroyView()
    }

}