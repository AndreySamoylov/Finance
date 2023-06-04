package com.goodlucky.finance

object MyConstants {
    const val STATE = "state"
    const val STATE_ADD = "add"
    const val STATE_CHANGE_OR_DELETE = "change_or_delete"

    const val NAME_CATEGORY = "name_category"
    const val ID_ACCOUNT = "id_account"
    const val ID_CURRENCY = "id_currency"
    const val COST = "cost"
    const val INCOME = "income"
    const val INITIAL_DATE = "initial_date"
    const val FINAL_DATE = "final_date"
    const val VALUE = "value"

    const val KEY_MY_ACCOUNT = "my_account"
    const val KEY_MY_CATEGORY = "my_category"
    const val KEY_MY_COST = "my_cost"
    const val KEY_MY_INCOME = "my_income"
    const val COLOR = "color"

    const val COLOR_ALPHA = "alpha"
    const val COLOR_RED = "red"
    const val COLOR_GREEN = "green"
    const val COLOR_BLUE = "blue"

    const val CATEGORY_TYPE_COST : Byte = 0
    const val CATEGORY_TYPE_INCOME : Byte = 1
    const val CATEGORY_UNDELEDATBLE_FALSE : Byte = 0
    const val CATEGORY_UNDELEDATBLE_TRUE : Byte = 1

    // Ограничения
    const val LIMIT_NO_MORE : Byte = 0 // Не более
    const val LIMIT_NO_LESS : Byte = 1 // Не менее

    const val USERS : String = "users"
    const val LIST_ACCOUNT : String = "listAccount"
    const val LIST_CATEGORY : String = "listCategory"
    const val LIST_COST : String = "listCost"
    const val LIST_INCOME : String = "listIncome"
}