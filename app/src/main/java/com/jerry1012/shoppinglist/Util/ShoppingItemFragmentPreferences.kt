package com.jerry1012.shoppinglist.Util

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.createDataStore
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class ShoppingItemFragment_Sort_Order { BY_NAME, BY_DATE , BY_CATEGORY }

data class ShoppingItemFilterPreferences(val sortOrder: ShoppingItemFragment_Sort_Order, val hideBought : Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStoreShoppingItem = context.createDataStore("shoppingItem_Fragment_preferences")

    val shoppingItemPreferencesFlow = dataStoreShoppingItem.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val sortOrder = ShoppingItemFragment_Sort_Order.valueOf(
                        preferences[ShoppingItemPreferencesKeys.SORT_ORDER] ?: ShoppingItemFragment_Sort_Order.BY_DATE.name
                )
                val hideCompleted = preferences[ShoppingItemPreferencesKeys.HIDE_COMPLETED] ?: false
                ShoppingItemFilterPreferences(sortOrder, hideCompleted)
            }

    suspend fun updateSortOrder(sortOrder: ShoppingItemFragment_Sort_Order) {
        dataStoreShoppingItem.edit { preferences ->
            preferences[ ShoppingItemPreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStoreShoppingItem.edit { preferences ->
            preferences[ ShoppingItemPreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object ShoppingItemPreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}