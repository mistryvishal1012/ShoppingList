package com.jerry1012.shoppinglist.UI.AddItem

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.jerry1012.shoppinglist.R
import com.jerry1012.shoppinglist.UI.ShoppingItem.ShoppingItemFragmentArgs
import com.jerry1012.shoppinglist.databinding.FragmentAdditemBinding
import com.jerry1012.shoppinglist.room.Shop
import com.jerry1012.shoppinglist.room.ShoppingItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import androidx.lifecycle.observe


@AndroidEntryPoint
class AddItemFragment :  Fragment(R.layout.fragment_additem) {

    @Inject
    lateinit var addItemViewModelFactory : AddItemViewModelFactory

    private val addItemFragmentArgs : AddItemFragmentArgs by navArgs<AddItemFragmentArgs>()
    private val addItemViewModel : AddItemViewModel by viewModels(){
        var shop : Shop = addItemFragmentArgs.shopDetails!!
        Log.i("Data Item","$shop")
       providerFactory(addItemViewModelFactory,shop,this,null)
    }

    private lateinit var shopFromBought : Shop

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var toolBarString = getString(R.string.addItemLabel)
        shopFromBought = addItemFragmentArgs.shopDetails!!
        val shopName = shopFromBought!!.shopName
        val additionalString = " From $shopName"
        toolBarString += additionalString
        val spinnerCategories = resources.getStringArray(R.array.grocery_categories)
        val additembinding = FragmentAdditemBinding.bind(view)
        val isEdit = addItemFragmentArgs.isEdit
        additembinding.apply {

            if(isEdit){
                val shoppingItem = addItemFragmentArgs.shoppingItem
                Log.i("Shopping Item","$shoppingItem")
                tvItemname.setText(shoppingItem!!.shoppingItemName)
                numberpickerQuantity.setText(shoppingItem.quantity.toString())
                val indexOfCategory = spinnerCategories.indexOf(shoppingItem.categories)
                snCategory.setSelection(indexOfCategory)
                addItemViewModel.shoppingItemQuantityEditText.postValue(shoppingItem.quantity.toDouble())
                addItemViewModel.shoppingItemNameEditText.postValue(shoppingItem.shoppingItemName)
                addItemViewModel.shoppingItemCategoryEditText.postValue(shoppingItem.categories)
            }

            fragmentadditemToolbar.setTitle(toolBarString)
            var selectedCategories : String = spinnerCategories[16]
            var numOfQuantity : String = ""
            val spinnerAdapter : ArrayAdapter<String> = object : ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,spinnerCategories){
                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                    view.setTypeface(view.typeface, Typeface.BOLD)

                    if (position == snCategory.selectedItemPosition && position !=0 ){
                        view.background = ColorDrawable(Color.parseColor("#F7E7CE"))
                        view.setTextColor(Color.parseColor("#333399"))
                    }

                    if(position == 0){
                        view.setTextColor(Color.LTGRAY)
                    }

                    return view
                }

                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }
            }
            snCategory.adapter = spinnerAdapter
            snCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position != 0){
                        selectedCategories = parent!!.getItemAtPosition(position).toString()
                    }
                }
            }


            addItemViewModel.shoppingItemNameEditText.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    tvItemname.setText(it)
                }
            }

            addItemViewModel.shoppingItemCategoryEditText.observe(viewLifecycleOwner){
                if(spinnerCategories.contains(it)){
                    val indexOfCategory = spinnerCategories.indexOf(it)
                    snCategory.setSelection(indexOfCategory)
                }
            }

            addItemViewModel.shoppingItemQuantityEditText.observe(viewLifecycleOwner){
                if(it > 0){
                    Log.i("Shopping Item Fragment","${numberpickerQuantity.text.toString()} $it")
                    numberpickerQuantity.setText(it.toString())
                    numOfQuantity = numberpickerQuantity.text.toString()
                }
            }


            btnSaveitem.setOnClickListener {
                    if(isEdit){
                        val shoppingItem = addItemFragmentArgs.shoppingItem
                        numOfQuantity = numberpickerQuantity.text.toString()
                        addItemViewModel.updateItem(ShoppingItem(shoppingItem!!.ItemId,shopFromBought.shopName,tvItemname.text.toString(),numOfQuantity.toFloat(),selectedCategories,System.currentTimeMillis(),false))
                    }else{
                        numOfQuantity = numberpickerQuantity.text.toString()
                        addItemViewModel.addItem(ShoppingItem(0,shopFromBought.shopName,tvItemname.text.toString(),numOfQuantity.toString().toFloat(),selectedCategories,System.currentTimeMillis(),false))
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addItemViewModel.addItemEventsChannel.collect{event ->
                    when(event){
                        is AddItemViewModel.AddItemEvents.NavigateToShoppingItemScreen -> {
                            val navigationAction = AddItemFragmentDirections.actionAddItemFragmentToShoppingItemFragment(shopFromBought)
                            findNavController().navigate(navigationAction)
                            setFragmentResult("add_edit_shoppingitem_request",
                                    bundleOf("add_edit_shoppingitem_request" to event.result))
                        }
                        is AddItemViewModel.AddItemEvents.ShowSnackBarErrorItemName -> {
                            Snackbar.make(requireView(),"Item Name Cannot Be Empty", Snackbar.LENGTH_LONG).show()
                        }
                        is AddItemViewModel.AddItemEvents.ShowSnackBarErrorItemCategories -> {
                            Snackbar.make(requireView(),"Select Item Category",Snackbar.LENGTH_LONG).show()
                        }
                        is AddItemViewModel.AddItemEvents.ShowSnackBarErrorItemQuantity -> {
                            Snackbar.make(requireView(),"Item Quantity Cannot Be Zero",Snackbar.LENGTH_LONG).show()
                        }
                    }
            }
        }

    }

}