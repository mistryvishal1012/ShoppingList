package com.jerry1012.shoppinglist.Util

import android.content.Context
import android.util.Log
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

enum class ShopFragment_Sort_Order {BY_NAME,BY_DATE,BY_ITEM }

data class ShopFragmentPreferencesSortOrder(val sortOrderShopFragment : ShopFragment_Sort_Order)

@Singleton
class ShopFragmentPreferences @Inject constructor(
    @ApplicationContext context: Context
){

    private val dataStoreShopFragment = context.createDataStore("shop_fragment_sort_preferences")
    val shopFragmentSortOrderFlow = dataStoreShopFragment.data
            .catch { exception ->
                if(exception is IOException){
                    emit(emptyPreferences())
                }else
                {
                    throw exception
                }
            }
            .map { preferences ->
        val sort_Order = ShopFragment_Sort_Order.valueOf(
            preferences[ShopFragmentPreferencesKey.SHOP_FRAGMENT_SORT_ORDER] ?: ShopFragment_Sort_Order.BY_DATE.name
        )
        Log.i("Data Item Retrive","$sort_Order")
        ShopFragmentPreferencesSortOrder(sort_Order)
    }


    suspend fun updateShopFragmentSortOrder(sortOrder : ShopFragment_Sort_Order){
        dataStoreShopFragment.edit {preference ->
            preference[ShopFragmentPreferencesKey.SHOP_FRAGMENT_SORT_ORDER] = sortOrder.name
            Log.i("Data Item Update","$sortOrder")
        }
    }

    private object ShopFragmentPreferencesKey{
        val SHOP_FRAGMENT_SORT_ORDER = preferencesKey<String>("Shop_Fragment_Sort_Order")
    }

}
