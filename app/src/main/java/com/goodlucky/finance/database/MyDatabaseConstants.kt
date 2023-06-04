package com.goodlucky.finance.database

object MyDatabaseConstants{
    const val DB_NAME = "finance.db"
    const val DB_VERSION = 43

    //Таблица чеки
    const val TABLE_RECEIPTS = "receipts"
    const val ID_RECEIPT = "_id"
    const val CODE_RECEIPT = "code"
    const val DATE_RECEIPT = "date"
    const val SUM_RECEIPT = "sum"
    const val ITEMS_RECEIPT = "items"
    const val RETAIL_PLACE_RECEIPT = "retailPlace"
    const val RETAIL_PLACE_ADDRESS_RECEIPT = "retailPlaceAddress"
    const val TABLE_RECEIPTS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_RECEIPTS +
            " (" + ID_RECEIPT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CODE_RECEIPT + " TEXT UNIQUE NOT NULL," +
            DATE_RECEIPT + " TEXT NOT NULL," +
            SUM_RECEIPT + " REAL NOT NULL," +
            ITEMS_RECEIPT + " TEXT," +
            RETAIL_PLACE_RECEIPT + " TEXT," +
            RETAIL_PLACE_ADDRESS_RECEIPT + " TEXT);"
    const val TABLE_RECEIPTS_DROP = "DROP TABLE IF EXISTS $TABLE_RECEIPTS;"

    //Таблица валюты
    const val TABLE_CURRENCIES = "currencies"
    const val ID_CURRENCY = "_id"
    const val NAME_CURRENCY = "name_currency"
    const val NAME_SHORT_CURRENCY = "name_short_currency"
    const val EXCHANGE_RATE_CURRENCY = "exchange_rate_currency"
    const val TABLE_CURRENCIES_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_CURRENCIES +
            " (" + ID_CURRENCY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_CURRENCY + " TEXT UNIQUE NOT NULL," +
            NAME_SHORT_CURRENCY + " TEXT UNIQUE NOT NULL," +
            EXCHANGE_RATE_CURRENCY + " REAL);"
    const val TABLE_CURRENCIES_DROP = "DROP TABLE IF EXISTS $TABLE_CURRENCIES;"

    //Таблица банки
    const val TABLE_BANKS = "banks"
    const val ID_BANK = "_id"
    const val NAME_BANK = "name"
    const val TABLE_BANKS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_BANKS +
            " (" + ID_BANK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_BANK + " TEXT UNIQUE NOT NULL);"
    const val TABLE_BANKS_DROP = "DROP TABLE IF EXISTS $TABLE_BANKS;"

    //Таблица счета
    const val TABLE_ACCOUNTS = "accounts"
    const val ID_ACCOUNT = "_id"
    const val NAME_ACCOUNT = "name"
    const val ID_BANK_ACCOUNT = "id_bank"
    const val ID_CURRENCY_ACCOUNT = "id_currency"
    const val TABLE_ACCOUNTS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNTS +
            " (" + ID_ACCOUNT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_ACCOUNT + " TEXT UNIQUE NOT NULL," +
            ID_BANK_ACCOUNT + " INTEGER NOT NULL," +
            ID_CURRENCY_ACCOUNT + " INTEGER NOT NULL);"
    const val TABLE_ACCOUNTS_DROP = "DROP TABLE IF EXISTS $TABLE_ACCOUNTS;"

    ///Таблица категории
    const val TABLE_CATEGORIES = "categories"
    const val ID_CATEGORY = "_id"
    const val NAME_CATEGORY = "name"
    const val COLOR_CATEGORY = "color"
    const val IMAGE_CATEGORY = "image"
    const val TYPE_CATEGORY = "type"
    const val UNDELETEBLE_CATEGORY = "undeletable"
    const val TABLE_CATEGORIES_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES +
            " (" + ID_CATEGORY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_CATEGORY + " TEXT NOT NULL, " +
            COLOR_CATEGORY + " TEXT, " +
            IMAGE_CATEGORY + " TEXT," +
            TYPE_CATEGORY + " INTEGER," +
            UNDELETEBLE_CATEGORY + " INTEGER" +
            ");"
    const val TABLE_CATEGORIES_DROP = "DROP TABLE IF EXISTS $TABLE_CATEGORIES;"

    //Таблица расходы
    const val TABLE_COSTS = "costs"
    const val ID_COST = "_id"
    const val SUM_COST = "sum"
    const val DATE_COST = "date_cost"
    const val ID_ACCOUNT_COST = "id_account"
    const val COMMENT_COST = "comment"
    const val ID_CATEGORY_COST = "id_category"
    const val TABLE_COSTS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_COSTS +
            " (" + ID_COST + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SUM_COST + " REAL NOT NULL, " +
            DATE_COST + " TEXT NOT NULL, " +
            ID_ACCOUNT_COST + " INTEGER NOT NULL, " +
            COMMENT_COST + " TEXT, " +
            ID_CATEGORY_COST + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ID_ACCOUNT_COST + ") REFERENCES " + TABLE_ACCOUNTS + " (" + ID_ACCOUNT + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + ID_CATEGORY_COST + ") REFERENCES " + TABLE_CATEGORIES + " (" + ID_CATEGORY + ") ON DELETE CASCADE" +
            ");"
    const val TABLE_COSTS_DROP = "DROP TABLE IF EXISTS $TABLE_COSTS;"

    //Таблица доходы
    const val TABLE_INCOME = "income"
    const val ID_INCOME = "_id"
    const val SUM_INCOME = "sum"
    const val DATE_INCOME = "date_income"
    const val ID_ACCOUNT_INCOME = "id_account"
    const val COMMENT_INCOME = "comment"
    const val ID_CATEGORY_INCOME = "id_category"
    const val TABLE_INCOME_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_INCOME +
            " (" + ID_INCOME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SUM_INCOME + " REAL NOT NULL, " +
            DATE_INCOME + " TEXT NOT NULL, " +
            ID_ACCOUNT_INCOME + " INTEGER NOT NULL, " +
            COMMENT_INCOME + " TEXT, " +
            ID_CATEGORY_INCOME + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ID_ACCOUNT_COST + ") REFERENCES " + TABLE_ACCOUNTS + " (" + ID_ACCOUNT + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + ID_CATEGORY_COST + ") REFERENCES " + TABLE_CATEGORIES + " (" + ID_CATEGORY + ") ON DELETE CASCADE" +
            ");"
    const val TABLE_INCOME_DROP = "DROP TABLE IF EXISTS $TABLE_INCOME;"

    //Таблица ограничения
    const val TABLE_LIMITS = "limits"
    const val ID_LIMIT = "_id"
    const val TYPE_LIMIT = "type"
    const val SUM_LIMIT = "sum"
    const val ID_LIMIT_CATEGORY = "id_category"
    const val ID_LIMIT_CURRENCY = "id_currency"
    const val TABLE_LIMITS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_LIMITS +
            " (" + ID_LIMIT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TYPE_LIMIT + " INTEGER NOT NULL," +
            SUM_LIMIT + " REAL NOT NULL," +
            ID_LIMIT_CATEGORY + " INTEGER NOT NULL," +
            ID_LIMIT_CURRENCY + " INTEGER NOT NULL," +
            "FOREIGN KEY (" + ID_LIMIT_CATEGORY + ") REFERENCES " + TABLE_CATEGORIES + " (" + ID_CATEGORY + ") ON DELETE CASCADE" +
            ");"
    const val TABLE_LIMITS_DROP = "DROP TABLE IF EXISTS $TABLE_LIMITS;"
}