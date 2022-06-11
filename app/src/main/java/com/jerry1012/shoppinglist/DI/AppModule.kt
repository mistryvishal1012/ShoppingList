package com.jerry1012.shoppinglist.DI

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jerry1012.shoppinglist.room.ApplicationDatabase
import com.jerry1012.shoppinglist.room.ShopDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application : Application )= Room.databaseBuilder(application,ApplicationDatabase::class.java,"shop_database").fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideShopDao(database : ApplicationDatabase) : ShopDao = database.provideDAO()

    @Provides
    @Singleton
    fun providesApplicationContext(app : Application) : Context = app.applicationContext

   /*private val MIGRATION_2_TO_3 : Migration =  object : Migration(2,3){
                override fun migrate(database: SupportSQLiteDatabase) {
                   try {

                       val cursorForShopTable = database.query("SELECT * FROM Shop_Table")
                       cursorForShopTable.use {
                           if(it.moveToFirst()){
                               val cv = ContentValues()
                               cv.put("shopName",cursorForShopTable.getLong(cursorForShopTable.getColumnIndex("shopName")))
                               cv.put("shop_Address",cursorForShopTable.getLong(cursorForShopTable.getColumnIndex("shop_Address")))
                               cv.put("shop_Long",cursorForShopTable.getLong(cursorForShopTable.getColumnIndex("shop_Long")))
                               cv.put("shop_Lat",cursorForShopTable.getLong(cursorForShopTable.getColumnIndex("shop_Lat")))
                               cv.put("shop_Added_Created",cursorForShopTable.getLong(cursorForShopTable.getColumnIndex("shop_Added_Created")))
                               cv.put("Total_Item",cursorForShopTable.getLong(cursorForShopTable.getColumnIndex("Total_Item")))
                               database.execSQL("DROP TABLE IF EXISTS `Shop_Table`")
                               createShopTable(database)
                               database.insert("Shop_Table",0,cv)
                           }else{
                               database.execSQL("DROP TABLE IF EXISTS `Shop_Table`")
                               createShopTable(database)
                           }
                       }

                       val cursorForShoppingTable = database.query("SELECT * FROM ShoppingItem_Table")
                       cursorForShoppingTable.use {
                           if(it.moveToFirst()){
                               val cv = ContentValues()
                               cv.put("shopName",cursorForShoppingTable.getLong(cursorForShoppingTable.getColumnIndex("shopName")))
                               cv.put("shoppingItem_Name",cursorForShoppingTable.getLong(cursorForShoppingTable.getColumnIndex("shoppingItem_Name")))
                               cv.put("quantity",cursorForShoppingTable.getLong(cursorForShoppingTable.getColumnIndex("quantity")))
                               cv.put("categories",cursorForShoppingTable.getLong(cursorForShoppingTable.getColumnIndex("categories")))
                               cv.put("item_Added",cursorForShoppingTable.getLong(cursorForShoppingTable.getColumnIndex("item_Added")))
                               cv.put("isBought",cursorForShoppingTable.getLong(cursorForShoppingTable.getColumnIndex("isBought")))
                               database.execSQL("DROP TABLE IF EXISTS `ShoppingItem_Table`")
                               createShoppingItemTable(database)
                               database.insert("ShoppingItem_Table",0,cv)
                           }else{
                               database.execSQL("DROP TABLE IF EXISTS `ShoppingItem_Table`")
                               createShoppingItemTable(database)
                           }
                       }
                   }catch (e: SQLiteException){

                   }catch(e: Exception){

                   }
                }
            }


    fun createShoppingItemTable(database: SupportSQLiteDatabase){
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `ShoppingItem_Table`(
            `shopName` TEXT,
            `shoppingItem_Name` TEXT,
            `quantity` INTEGER,
            `categories` TEXT,
            `item_Added` INTEGER,
            `isBought` INTEGER,
            PRIMARY KEY(`shopName`)
            )
        """.trimIndent()
        )
    }

    fun createShopTable(database: SupportSQLiteDatabase){
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `Shop_Table`(
            `shopName` TEXT,
            `shop_Address` TEXT,
            `shop_Long` INTEGER,
            `shop_Lat` TEXT,
            `shop_Added_Created` INTEGER,
            `Total_Item` INTEGER,
            PRIMARY KEY(`shopName`)
            )
        """.trimIndent()
        )
    }*/



}
