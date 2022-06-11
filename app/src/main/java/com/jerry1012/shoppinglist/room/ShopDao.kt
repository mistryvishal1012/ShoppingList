package com.jerry1012.shoppinglist.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.jerry1012.shoppinglist.Util.ShopFragment_Sort_Order
import com.jerry1012.shoppinglist.Util.ShoppingItemFragment_Sort_Order
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    fun getTask(shopFragmentSortOrder : ShopFragment_Sort_Order,searchQuery : String) : Flow<List<Shop>> =
            when(shopFragmentSortOrder){
                ShopFragment_Sort_Order.BY_NAME->{
                    Log.i("Data Item DAO","$shopFragmentSortOrder$searchQuery")
                    getTaskByName(searchQuery)
                }
                ShopFragment_Sort_Order.BY_ITEM->{
                    Log.i("Data Item DAO","$shopFragmentSortOrder$searchQuery")
                    getTaskByItem(searchQuery)
                }
                ShopFragment_Sort_Order.BY_DATE->{
                    Log.i("Data Item DAO","$shopFragmentSortOrder$searchQuery")
                    getTaskByDate(searchQuery)
                }
            }


    fun getShoppingItem(searchQuery : String,shoppingItemFragmentSortOrder : ShoppingItemFragment_Sort_Order,hideBought : Boolean,category: String,shopName: String) : Flow<List<ShoppingItem>> =
            when(shoppingItemFragmentSortOrder){
                ShoppingItemFragment_Sort_Order.BY_NAME->{
                    Log.i("Data Item DAO","$shoppingItemFragmentSortOrder$searchQuery")
                    getShoppingItemByName(searchQuery,hideBought,category,shopName)
                }
                ShoppingItemFragment_Sort_Order.BY_CATEGORY->{
                    Log.i("Data Item DAO","$shoppingItemFragmentSortOrder$searchQuery")
                    getShoppingItemByItem(searchQuery,hideBought,category,shopName)
                }
                ShoppingItemFragment_Sort_Order.BY_DATE->{
                    Log.i("Data Item DAO","$shoppingItemFragmentSortOrder$searchQuery")
                    getShoppingItemByDate(searchQuery,hideBought,category,shopName)
                }
            }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShop(shop : Shop)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShoppingItem(shoppingItem: ShoppingItem)

    @Update
    suspend fun updateShop(shop: Shop)

    @Update
    suspend fun updateShoppingItem(shoppingItem : ShoppingItem)

    @Delete
    suspend fun deleteShop(shop : Shop)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem : ShoppingItem)

    @Query("SELECT COUNT(*) FROM SHOPPINGITEM_TABLE WHERE shopName = :shopNameGiven")
    fun getItemCount(shopNameGiven : String) : Flow<Int>

    @Query("DELETE FROM SHOPPINGITEM_TABLE WHERE shopName = :shopName")
    suspend fun deleteShoppingItemFromShop(shopName : String)

    @Query("SELECT COUNT(*) FROM SHOPPINGITEM_TABLE WHERE shopName = :shopNameGiven AND categories = :CategoryToSearch ")
    suspend fun getItemByCount(shopNameGiven: String, CategoryToSearch : String) : Int

    @Query("SELECT * FROM SHOP_TABLE ORDER BY shopName")
    fun retriveShopByName() :Flow<List<Shop>>

    @Query("SELECT * FROM SHOP_TABLE WHERE shopName = :shopNameGiven")
    suspend fun retriveShop(shopNameGiven : String) : Shop

    @Query("SELECT * FROM ShoppingItem_Table WHERE shopName = :shopName ")
    fun getShoppingItem(shopName : String) : Flow<List<ShoppingItem>>

    @Query("SELECT * FROM ShoppingItem_Table WHERE shopName = :shopName ")
    fun getShoppingItemToAdd(shopName : String) : List<ShoppingItem>

    @Query("SELECT DISTINCT categories FROM ShoppingItem_Table WHERE shopName = :shopName")
    fun getCategoriesForShop(shopName : String) : Flow<List<String>>

    @Query("SELECT * FROM ShoppingItem_Table WHERE shopName = :shopNameSearching AND categories = :categoriesForSearching")
    fun getShoppingItemByCategory(shopNameSearching : String,categoriesForSearching : String) : Flow<List<ShoppingItem>>

    @Query("SELECT COUNT(*) FROM SHOPPINGITEM_TABLE WHERE shopName = :shopNamePasssed")
    suspend fun getTotalItemInShop(shopNamePasssed : String) : Int


    @Query("Select * FROM SHOP_TABLE WHERE shopName LIKE '%' || :serachShop || '%' ORDER BY Total_Item DESC")
    fun getTaskByItem(serachShop : String) : Flow<List<Shop>>

    @Query("Select * FROM SHOP_TABLE WHERE shopName LIKE '%' || :serachShop || '%' ORDER BY shopName ASC")
    fun getTaskByName(serachShop : String) : Flow<List<Shop>>

    @Query("Select * FROM SHOP_TABLE WHERE shopName LIKE '%' || :serachShop || '%' ORDER BY shop_Added_Created DESC")
    fun getTaskByDate(serachShop : String) : Flow<List<Shop>>

    @Query("Select * FROM ShoppingItem_Table WHERE (isBought != :hideBoughtItem OR isBought = 0) AND shoppingItem_Name LIKE '%' || :serachShop || '%' AND categories LIKE '%' || :category || '%' AND shopName = :shopNamePassed ORDER BY categories DESC")
    fun getShoppingItemByItem(serachShop : String,hideBoughtItem : Boolean,category: String,shopNamePassed : String) : Flow<List<ShoppingItem>>

    @Query("Select * FROM ShoppingItem_Table WHERE (isBought != :hideBoughtItem OR isBought = 0) AND shoppingItem_Name LIKE '%' || :serachShop || '%' AND categories LIKE '%' || :category || '%' AND shopName = :shopNamePassed ORDER BY shoppingItem_Name ASC")
    fun getShoppingItemByName(serachShop : String,hideBoughtItem : Boolean,category: String,shopNamePassed : String) : Flow<List<ShoppingItem>>

    @Query("Select * FROM ShoppingItem_Table WHERE (isBought != :hideBoughtItem OR isBought = 0) AND shoppingItem_Name  LIKE '%' || :serachShop || '%' AND categories LIKE '%' || :category || '%' AND shopName = :shopNamePassed ORDER BY item_Added DESC")
    fun getShoppingItemByDate(serachShop : String,hideBoughtItem : Boolean,category: String,shopNamePassed : String) : Flow<List<ShoppingItem>>

    @Query("Select * FROM ShoppingItem_Table WHERE (isBought != :hideBoughtItem OR isBought = 0) AND shoppingItem_Name  LIKE '%' || :serachShop || '%' ORDER BY item_Added DESC")
    fun getAllShoppingItem(serachShop : String,hideBoughtItem : Boolean) : Flow<List<ShoppingItem>>


}