package com.jerry1012.shoppinglist.room


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "ShoppingItem_Table",foreignKeys = arrayOf(ForeignKey(entity = Shop::class, parentColumns = arrayOf("shopName"), childColumns = arrayOf("shopName"), onDelete = CASCADE)))
@Parcelize
data class ShoppingItem(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "Item_Id")var ItemId: Int = 0,
        @ColumnInfo(name = "shopName") val shopName : String,
        @ColumnInfo(name = "shoppingItem_Name") val shoppingItemName : String,
        @ColumnInfo(name = "quantity") var quantity : Float,
        @ColumnInfo(name = "categories") var categories : String,
        @ColumnInfo(name = "item_Added") val itemAdded : Long = System.currentTimeMillis(),
        @ColumnInfo(name = "isBought") var isBought : Boolean = false
) : Parcelable{

    val itemaddedformat : String
        get() = DateFormat.getDateTimeInstance().format(itemAdded)
}