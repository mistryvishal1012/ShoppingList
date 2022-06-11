package com.jerry1012.shoppinglist.UI.ShoppingItem

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jerry1012.shoppinglist.databinding.LayoutShoppingitemBinding
import com.jerry1012.shoppinglist.room.ShoppingItem

class ShoppingItemAdapter(private val shoppinglistener: OnShoppingItemClikListener) : ListAdapter<ShoppingItem , ShoppingItemAdapter.ShoppingItemViewHolder>(ShoppingItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
        val shoppingItemLayoutBinding = LayoutShoppingitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ShoppingItemViewHolder(shoppingItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ShoppingItemViewHolder(private val shoppingItemLayoutBinding : LayoutShoppingitemBinding) : RecyclerView.ViewHolder(shoppingItemLayoutBinding.root){

        init {

            shoppingItemLayoutBinding.apply {

                ivDeleteitem.setOnClickListener {
                    val positionOfShoppingItem = adapterPosition
                    if(positionOfShoppingItem != RecyclerView.NO_POSITION){
                        val shoppingItemToShow = getItem(positionOfShoppingItem)
                        shoppinglistener.OnDeleteBoxClickListen(shoppingItemToShow)
                    }
                }

                ivAdditem.setOnClickListener {
                    val positionOfShoppingItem = adapterPosition
                    if(positionOfShoppingItem != RecyclerView.NO_POSITION){
                        val shoppingItemToShow = getItem(positionOfShoppingItem)
                        shoppinglistener.OnAddItemClickListen(shoppingItemToShow)
                    }
                }

                ivMinusitem.setOnClickListener {
                    val positionOfShoppingItem = adapterPosition
                    if(positionOfShoppingItem != RecyclerView.NO_POSITION){
                        val shoppingItemToShow = getItem(positionOfShoppingItem)
                        shoppinglistener.OnMinusItemClickListen(shoppingItemToShow)
                    }
                }

                cbIsBought.setOnCheckedChangeListener { _, isChecked ->
                    val positionOfShoppingItem = adapterPosition
                    if(positionOfShoppingItem != RecyclerView.NO_POSITION){
                        val shoppingItemToShow = getItem(positionOfShoppingItem)
                        shoppinglistener.OnCheckBoxClickListen(shoppingItemToShow,isChecked)
                    }
                }

                root.setOnClickListener {
                    val positionOfShoppingItem = adapterPosition
                    if(positionOfShoppingItem != RecyclerView.NO_POSITION){
                        val shoppingItemToShow = getItem(positionOfShoppingItem)
                        shoppinglistener.OnItemClick(shoppingItemToShow)
                    }
                }

            }

        }

        fun bind(shoppingItem : ShoppingItem){
            shoppingItemLayoutBinding.apply {
                Log.i("Data Item Adapter","$shoppingItem")
                tvItemname.text = shoppingItem.shoppingItemName
                tvItemquantity.text = shoppingItem.quantity.toString()
                tvItemcategory.text = shoppingItem.itemaddedformat
                cbIsBought.isChecked = shoppingItem.isBought

            }

        }


    }

    interface OnShoppingItemClikListener {

        fun OnItemClick(shoppingItem : ShoppingItem)

        fun OnCheckBoxClickListen(shoppingItem: ShoppingItem,isChecked : Boolean)

        fun OnDeleteBoxClickListen(shoppingItem: ShoppingItem)

        fun OnAddItemClickListen(shoppingItem: ShoppingItem)

        fun OnMinusItemClickListen(shoppingItem: ShoppingItem)


    }

    class ShoppingItemDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {


        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem)=
                oldItem.shopName == newItem.shopName

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem)=
                oldItem == newItem
    }

}