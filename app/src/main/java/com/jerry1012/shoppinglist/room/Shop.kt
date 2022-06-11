package com.jerry1012.shoppinglist.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = "Shop_Table")
data class Shop(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "shopName") val shopName : String,
        @ColumnInfo(name = "shop_Address") val shopAddress : String,
        @ColumnInfo(name = "shop_Long") val shoplongtitude : Double,
        @ColumnInfo(name = "shop_Lat") val shoplatitude : Double,
        @ColumnInfo(name = "shop_Added_Created") val shopaddedcreated : Long = System.currentTimeMillis(),
        @ColumnInfo(name = "Total_Item") var totalitem : Int = 0
) : Parcelable{
    val createdDate : String
        get() = DateFormat.getDateTimeInstance().format(shopaddedcreated)
}