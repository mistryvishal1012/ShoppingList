package com.jerry1012.shoppinglist.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Shop::class,ShoppingItem::class],version = 7,exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun provideDAO() : ShopDao

}

