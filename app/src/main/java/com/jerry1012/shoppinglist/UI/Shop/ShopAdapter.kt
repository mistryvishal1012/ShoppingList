package com.jerry1012.shoppinglist.UI.Shop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jerry1012.shoppinglist.databinding.FragmentShopDetailsBinding
import com.jerry1012.shoppinglist.databinding.LayoutShopBinding
import com.jerry1012.shoppinglist.room.Shop
import javax.inject.Inject

class ShopAdapter(private val listener : OnItemClickListener) : ListAdapter<Shop,ShopAdapter.ShopViewHolder>(ShopDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val shoplayoutbinding = LayoutShopBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ShopViewHolder(shoplayoutbinding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ShopViewHolder(private val shoplayoutBinding : LayoutShopBinding) : RecyclerView.ViewHolder(shoplayoutBinding.root){

        init {

            shoplayoutBinding.apply {

                ivDeleteshop.setOnClickListener {
                    val positionOfShop = adapterPosition
                    if(positionOfShop != RecyclerView.NO_POSITION){
                        val shopToDelete = getItem(positionOfShop)
                        listener.onDeleteBoxClick(shopToDelete)
                    }
                }

                root.setOnClickListener {
                    val positionOfShop = adapterPosition
                    if(positionOfShop != RecyclerView.NO_POSITION){
                        val shopToShow = getItem(positionOfShop)
                        listener.onItemClick(shopToShow)
                    }
                }

                ivEditshop.setOnClickListener {
                    val positionOfShop = adapterPosition
                    if(positionOfShop != RecyclerView.NO_POSITION){
                        val shopToEdit = getItem(positionOfShop)
                        listener.onEditShopClick(shopToEdit)
                    }
                }

            }

        }

        @SuppressLint("SetTextI18n")
        fun bind(shop : Shop){
            shoplayoutBinding.apply {
                tvShopname.text = shop.shopName
                tvAddressShop.text = shop.shopAddress
                tvTime.text = shop.createdDate
                tvTotalQuantity.text = "Total Item : ${shop.totalitem}"
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(shop : Shop)
        fun onDeleteBoxClick(shop : Shop)
        fun onEditShopClick(shop: Shop)
    }

    class ShopDiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(oldItem: Shop, newItem: Shop) =
            oldItem.shopName == newItem.shopName

        override fun areContentsTheSame(oldItem: Shop, newItem: Shop) =
            oldItem == newItem
    }

}