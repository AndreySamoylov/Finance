package com.goodlucky.finance.database

import android.annotation.SuppressLint

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.goodlucky.finance.items.*

class MyDbManager(context: Context) {
    private val myDbHelper: MyDbHelper = MyDbHelper(context)
    private var sqLiteDatabase: SQLiteDatabase? = null

    fun openDatabase() {
        sqLiteDatabase = myDbHelper.writableDatabase
    }

    fun closeDatabase() {
        myDbHelper.close()
    }

    fun insertToReceipt(receipt: MyReceipt){
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.CODE_RECEIPT, receipt.code)
        contentValues.put(MyDatabaseConstants.DATE_RECEIPT, receipt.date)
        contentValues.put(MyDatabaseConstants.SUM_RECEIPT, receipt.sum)
        contentValues.put(MyDatabaseConstants.ITEMS_RECEIPT, receipt.items)
        contentValues.put(MyDatabaseConstants.RETAIL_PLACE_RECEIPT, receipt.retailPlace)
        contentValues.put(MyDatabaseConstants.RETAIL_PLACE_ADDRESS_RECEIPT, receipt.retailPlaceAddress)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_RECEIPTS, null, contentValues)
    }

    fun insertToReceiptWithID(receipt: MyReceipt){
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_RECEIPT, receipt._id)
        contentValues.put(MyDatabaseConstants.CODE_RECEIPT, receipt.code)
        contentValues.put(MyDatabaseConstants.DATE_RECEIPT, receipt.date)
        contentValues.put(MyDatabaseConstants.SUM_RECEIPT, receipt.sum)
        contentValues.put(MyDatabaseConstants.ITEMS_RECEIPT, receipt.items)
        contentValues.put(MyDatabaseConstants.RETAIL_PLACE_RECEIPT, receipt.retailPlace)
        contentValues.put(MyDatabaseConstants.RETAIL_PLACE_ADDRESS_RECEIPT, receipt.retailPlaceAddress)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_RECEIPTS, null, contentValues)
    }

    fun updateInReceipts(receipt: MyReceipt) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.ID_RECEIPT, receipt._id)
            put(MyDatabaseConstants.CODE_RECEIPT, receipt.code)
            put(MyDatabaseConstants.DATE_RECEIPT, receipt.date)
            put(MyDatabaseConstants.SUM_RECEIPT, receipt.sum)
            put(MyDatabaseConstants.ITEMS_RECEIPT, receipt.items)
            put(MyDatabaseConstants.RETAIL_PLACE_RECEIPT, receipt.retailPlace)
            put(MyDatabaseConstants.RETAIL_PLACE_ADDRESS_RECEIPT, receipt.retailPlaceAddress)
        }
        val selection = "${MyDatabaseConstants.ID_RECEIPT} = ?"
        val selectionArgs = arrayOf(receipt._id.toString())
        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_RECEIPTS, values, selection, selectionArgs)
    }

    fun deleteFromReceipts(_id: Long) {
        val selection = "${MyDatabaseConstants.ID_RECEIPT} = ?"
        val selectionArgs = arrayOf(_id.toString())
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_RECEIPTS, selection, selectionArgs)
    }

    fun fromReceipts() : List<MyReceipt>{
        val tempList: MutableList<MyReceipt> = ArrayList()
        val sortOrder = MyDatabaseConstants.DATE_RECEIPT
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_RECEIPTS,null,null,
            null,null,null,sortOrder)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_RECEIPT))
            @SuppressLint("Range") val code = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.CODE_RECEIPT))
            @SuppressLint("Range") val date = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_RECEIPT))
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_RECEIPT))
            @SuppressLint("Range") val items = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.ITEMS_RECEIPT))
            @SuppressLint("Range") val retailPlace = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.RETAIL_PLACE_RECEIPT))
            @SuppressLint("Range") val retailPlaceAddress = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.RETAIL_PLACE_ADDRESS_RECEIPT))
            val receipt = MyReceipt(id, code, date, sum, items, retailPlace, retailPlaceAddress)
            tempList.add(receipt)
        }
        cursor.close()
        return tempList
    }

    fun fromReceipts(initialDate : String, finalDate : String) : List<MyReceipt>{
        val tempList: MutableList<MyReceipt> = ArrayList()
        val selection = "${MyDatabaseConstants.DATE_RECEIPT} >= ? AND ${MyDatabaseConstants.DATE_RECEIPT} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)
        val sortOrder = MyDatabaseConstants.DATE_RECEIPT
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_RECEIPTS,null,selection,selectionArgs,null,null,sortOrder)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_RECEIPT))
            @SuppressLint("Range") val code = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.CODE_RECEIPT))
            @SuppressLint("Range") val date = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_RECEIPT))
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_RECEIPT))
            @SuppressLint("Range") val items = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.ITEMS_RECEIPT))
            @SuppressLint("Range") val retailPlace = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.RETAIL_PLACE_RECEIPT))
            @SuppressLint("Range") val retailPlaceAddress = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.RETAIL_PLACE_ADDRESS_RECEIPT))
            val receipt = MyReceipt(id, code, date, sum, items, retailPlace, retailPlaceAddress)
            tempList.add(receipt)
        }
        cursor.close()
        return tempList
    }



    fun insertToBanksWithId(bank: MyBank){
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_BANK, bank._id)
        contentValues.put(MyDatabaseConstants.NAME_BANK, bank.name)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_BANKS, null, contentValues)
    }

    fun fromBanks() : List<MyBank>{
        val tempList: MutableList<MyBank> = ArrayList()
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_BANKS,null,null,
            null,null,null,null)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_BANK))
            @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_BANK))
            val bank = MyBank(id, name)
            tempList.add(bank)
        }
        cursor.close()
        return tempList
    }

    fun getBankName(id : Long) : String{
        val columns = arrayOf(MyDatabaseConstants.NAME_BANK)
        val selection = MyDatabaseConstants.ID_BANK + " = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = sqLiteDatabase!!.query(MyDatabaseConstants.TABLE_BANKS, columns, selection, selectionArgs,
            null,null,null,"1")
        var nameBank = ""
        while (cursor.moveToNext()) {nameBank = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseConstants.NAME_BANK))}
        cursor.close()
        return nameBank
    }

    fun insertToCurrenciesWithId(currency: MyCurrency){
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_RECEIPT, currency._id)
        contentValues.put(MyDatabaseConstants.NAME_CURRENCY, currency.name_full)
        contentValues.put(MyDatabaseConstants.NAME_SHORT_CURRENCY, currency.name_short)
        contentValues.put(MyDatabaseConstants.EXCHANGE_RATE_CURRENCY, currency.exchange_rate)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_CURRENCIES, null, contentValues)
    }

    fun fromCurrencies() : List<MyCurrency>{
        val tempList: MutableList<MyCurrency> = ArrayList()
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CURRENCIES,null,null,null,null,null,null)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CURRENCY))
            @SuppressLint("Range") val nameFull = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CURRENCY))
            @SuppressLint("Range") val nameShort = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_SHORT_CURRENCY))
            @SuppressLint("Range") val exchangeRate = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.EXCHANGE_RATE_CURRENCY))
            val currency = MyCurrency(id, nameFull, nameShort, exchangeRate)
            tempList.add(currency)
        }
        cursor.close()
        return tempList
    }

    fun getCurrencyName(id : Long) : String{
        val columns = arrayOf(MyDatabaseConstants.NAME_CURRENCY)
        val selection = MyDatabaseConstants.ID_CURRENCY + " = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = sqLiteDatabase!!.query(MyDatabaseConstants.TABLE_CURRENCIES, columns, selection, selectionArgs,
            null,null,null,"1")
        var nameCurrency = ""
        while (cursor.moveToNext()) {nameCurrency = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseConstants.NAME_CURRENCY))}
        cursor.close()
        return nameCurrency
    }

    fun insertToAccounts(account: MyAccount) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.NAME_ACCOUNT, account.name)
        contentValues.put(MyDatabaseConstants.ID_BANK_ACCOUNT, account.idBank)
        contentValues.put(MyDatabaseConstants.ID_CURRENCY_ACCOUNT, account.idCurrency)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_ACCOUNTS, null, contentValues)
    }

    fun insertToAccountsWithID(account: MyAccount) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT, account._id)
        contentValues.put(MyDatabaseConstants.NAME_ACCOUNT, account.name)
        contentValues.put(MyDatabaseConstants.ID_BANK_ACCOUNT, account.idBank)
        contentValues.put(MyDatabaseConstants.ID_CURRENCY_ACCOUNT, account.idCurrency)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_ACCOUNTS, null, contentValues)
    }

    fun updateInAccounts(account: MyAccount) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.ID_ACCOUNT, account._id)
            put(MyDatabaseConstants.NAME_ACCOUNT, account.name)
            put(MyDatabaseConstants.ID_BANK_ACCOUNT, account.idBank)
            put(MyDatabaseConstants.ID_CURRENCY_ACCOUNT, account.idCurrency)
        }
        val selection = "${MyDatabaseConstants.ID_ACCOUNT} = ?"
        val selectionArgs = arrayOf(account._id.toString())
        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_ACCOUNTS, values, selection, selectionArgs)
    }

    fun deleteFromAccounts(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_ACCOUNTS +
                    " WHERE " + MyDatabaseConstants.ID_ACCOUNT + "=" + _id + ";")
        )
    }

    val fromAccounts: List<MyAccount>
        get() {
            val tempList: MutableList<MyAccount> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_ACCOUNTS, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT))
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_ACCOUNT))
                @SuppressLint("Range") val idBank =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_BANK_ACCOUNT))
                @SuppressLint("Range") val idCurrency =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CURRENCY_ACCOUNT))
                val account = MyAccount(id, name, idBank, idCurrency)
                tempList.add(account)
            }
            cursor.close()
            return tempList
        }


    /// Метод возвращает список счетов, с выборкой по валютам
    fun fromAccountsByCurrency(idCurrency : Long) : List<MyAccount>{
        val selection = MyDatabaseConstants.ID_CURRENCY_ACCOUNT + " = ?"
        val selectionArgs = arrayOf(idCurrency.toString())

        val tempList: MutableList<MyAccount> = ArrayList()
        val cursor = sqLiteDatabase!!.query(MyDatabaseConstants.TABLE_ACCOUNTS,null,
            selection, selectionArgs,null,null,null,)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT))
            @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_ACCOUNT))
            @SuppressLint("Range") val idBank = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_BANK))
            val account = MyAccount(id, name, idBank, idCurrency)
            tempList.add(account)
        }
        cursor.close()
        return tempList
    }

    // Метод ищет название счёта по его id
    // В случае успеха возвращает имя, в противном случае пустую строку
    fun findAccountNameByID(id: Long): String {
        val columns = arrayOf(
            MyDatabaseConstants.NAME_ACCOUNT
        )
        val selection = MyDatabaseConstants.ID_ACCOUNT + " = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_ACCOUNTS,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var nameAccount = ""
        while (cursor.moveToNext()) {
            nameAccount = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseConstants.NAME_ACCOUNT))
        }
        cursor.close()
        return nameAccount
    }


    fun insertToCategories(category: MyCategory) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.NAME_CATEGORY, category._name)
        contentValues.put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
        contentValues.put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
        contentValues.put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
        contentValues.put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_CATEGORIES, null, contentValues)
    }

    fun insertToCategoriesWithID(category: MyCategory) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_CATEGORY, category._id)
        contentValues.put(MyDatabaseConstants.NAME_CATEGORY, category._name)
        contentValues.put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
        contentValues.put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
        contentValues.put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
        contentValues.put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_CATEGORIES, null, contentValues)
    }

    fun updateInCategories(category: MyCategory) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.NAME_CATEGORY, category._name)
            put(MyDatabaseConstants.COLOR_CATEGORY, category._color)
            put(MyDatabaseConstants.IMAGE_CATEGORY, category._image)
            put(MyDatabaseConstants.TYPE_CATEGORY, category._type)
            put(MyDatabaseConstants.UNDELETEBLE_CATEGORY, category._undeletable)
        }
        val selection = "${MyDatabaseConstants.ID_CATEGORY} = ?"
        val selectionArgs = arrayOf(category._id.toString())
        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_CATEGORIES, values, selection, selectionArgs)
    }

    fun deleteFromCategories(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_CATEGORIES +
                    " WHERE " + MyDatabaseConstants.ID_CATEGORY + "=" + _id + ";")
        )
    }

    //Метод возвращает список категорий
    val fromCategories: List<MyCategory>
        get() {
            val tempList: MutableList<MyCategory> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_CATEGORIES, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
                @SuppressLint("Range") val color =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
                @SuppressLint("Range") val image =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
                @SuppressLint("Range") val type =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY)).toByte()
                @SuppressLint("Range") val undeletable =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY)).toByte()
                val category = MyCategory(id, name, color, image, type, undeletable)
                tempList.add(category)
            }
            cursor.close()
            return tempList
        }

    //Метод возвращает список категорий, в формате для добавления в firebase realtime database
    val fromCategoriesFirebaseType: List<MyCategoryFirebase>
        get() {
            val tempList: MutableList<MyCategoryFirebase> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_CATEGORIES, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
                @SuppressLint("Range") val name =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
                @SuppressLint("Range") val color =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
                @SuppressLint("Range") val image =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
                @SuppressLint("Range") val type =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY))
                @SuppressLint("Range") val undeletable =
                    cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY))
                val category = MyCategoryFirebase(id, name, color, image, type, undeletable)
                tempList.add(category)
            }
            cursor.close()
            return tempList
        }

    // Метод возвращает список категорий
    // где typeCategory - тип категории
    // 0 - расход, 1 - доход
    fun fromCategories(typeCategory : Byte) : List<MyCategory>{
        // Filter results WHERE "TYPE_CATEGORY" = 'typeCategory'
        val selection = MyDatabaseConstants.TYPE_CATEGORY + " = ?"
        val selectionArgs = arrayOf(typeCategory.toString())

        val tempList: MutableList<MyCategory> = ArrayList()
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CATEGORIES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
            @SuppressLint("Range") val name =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
            @SuppressLint("Range") val color =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
            @SuppressLint("Range") val image =
                cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
            @SuppressLint("Range") val type =
                cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY)).toByte()
            @SuppressLint("Range") val undeletable =
                cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY)).toByte()
            val category = MyCategory(id, name, color, image, type, undeletable)
            tempList.add(category)
        }
        cursor.close()
        return tempList
    }

    // Метод ищет категорию по его идентификатору.
    // В случае успеха возвращает категорию, в противном случае пустоту
    fun findCategoryByID(id : Long) : MyCategory?{
        val selection = MyDatabaseConstants.ID_CATEGORY + " = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CATEGORIES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var category : MyCategory? = null
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val idCategory = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY))
            @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.NAME_CATEGORY))
            @SuppressLint("Range") val color = cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COLOR_CATEGORY))
            @SuppressLint("Range") val image = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.IMAGE_CATEGORY))
            @SuppressLint("Range") val type = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_CATEGORY)).toByte()
            @SuppressLint("Range") val undeletable = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.UNDELETEBLE_CATEGORY)).toByte()
            category = MyCategory(idCategory, name, color, image, type, undeletable)
        }
        cursor.close()
        return category
    }

    // Метод пытается найти ID категории по его названию
    // В случает успеха возвращает ID категории из базы данных
    // В противном случае возвращает -1
    fun findCategoryByName(name: String): Long {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val columns = arrayOf(
            MyDatabaseConstants.ID_CATEGORY
        )
        // Filter results WHERE "title" = 'My Title'
        val selection = MyDatabaseConstants.NAME_CATEGORY + " = ?"
        val selectionArgs = arrayOf(name)
        // How you want the results sorted in the resulting Cursor
        //String sortOrder =
        //        MyDatabaseConstants.ID_CATEGORY + " DESC";
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_CATEGORIES,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var id : Long = -1
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_CATEGORY))
        }
        cursor.close()
        return id
    }

    fun insertToCosts(cost: MyCost) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.SUM_COST, cost._sum)
        contentValues.put(MyDatabaseConstants.DATE_COST, cost._date_cost)
        contentValues.put(MyDatabaseConstants.COMMENT_COST, cost._comment)
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT_COST, cost._account)
        contentValues.put(MyDatabaseConstants.ID_CATEGORY_COST, cost._category)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_COSTS, null, contentValues)
    }

    fun insertToCostsWithID(cost: MyCost) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_COST, cost._id)
        contentValues.put(MyDatabaseConstants.SUM_COST, cost._sum)
        contentValues.put(MyDatabaseConstants.DATE_COST, cost._date_cost)
        contentValues.put(MyDatabaseConstants.COMMENT_COST, cost._comment)
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT_COST, cost._account)
        contentValues.put(MyDatabaseConstants.ID_CATEGORY_COST, cost._category)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_COSTS, null, contentValues)
    }

    fun updateInCosts(cost: MyCost) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.SUM_COST, cost._sum)
            put(MyDatabaseConstants.DATE_COST, cost._date_cost)
            put(MyDatabaseConstants.COMMENT_COST, cost._comment)
            put(MyDatabaseConstants.ID_ACCOUNT_COST, cost._account)
            put(MyDatabaseConstants.ID_CATEGORY_COST, cost._category)
        }
        val selection = "${MyDatabaseConstants.ID_COST} = ?"
        val selectionArgs = arrayOf(cost._id.toString())

        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_COSTS, values, selection, selectionArgs)
    }

    fun deleteFromCosts(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_COSTS +
                    " WHERE " + MyDatabaseConstants.ID_COST + "=" + _id + ";")
        )
    }

    // Метод позвращает список покупок, который находяться в базе данных
    val fromCosts: List<MyCost>
        get() {
            val tempList: MutableList<MyCost> = ArrayList()

            val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_COSTS, null, null, null,
                null, null, sortOrder
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
                @SuppressLint("Range") val sum =
                    cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
                @SuppressLint("Range") val dateCost =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
                @SuppressLint("Range") val comment =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
                @SuppressLint("Range") val idAccount =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
                @SuppressLint("Range") val idCategory =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
                val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
                tempList.add(cost)
            }
            cursor.close()
            return tempList
        }

    // Метод позвращает список покупок, который находяться в базе данных, где
    // category - идентификатор категория, по которой нужно делать выборку
    fun fromCosts(categoryID: Long) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    //Метод возвращает сумму расходов за всё время
    fun getSumCost()  : Double{
        var allSum = 0.0
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, null, null,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    //Метод возвращает сумму расходов по выбранному счёту
    fun getSumCostByAccount(idAccount : Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_ACCOUNT_COST} = ?"
        val selectionArgs = arrayOf(idAccount.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    fun getSumCostByAccount(initialDate : String, finalDate : String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    fun getSumCostByAccount(idAccount : Long, initialDate : String, finalDate : String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_ACCOUNT_COST} = ? AND ${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(idAccount.toString(), initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // где category - идентификатор категория, по которой нужно делать выборку
    fun getSumCostByCategory(categoryID: Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // а accountID - идентификатор аккаунта. по которому делается выборка
    fun getSumCostByCategory(categoryID: Long, accountID : Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND ${MyDatabaseConstants.ID_ACCOUNT_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // accountID - идентификатор аккаунта. по которому делается выборка
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumCostByCategory(categoryID: Long, initialDate: String, finalDate: String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND " +
                "${MyDatabaseConstants.DATE_COST} >= ? AND " +
                "${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(),  initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    fun getSumCostByCategory(categoryID: Long, initialDate: String, finalDate: String, accountID: Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND " +
                "${MyDatabaseConstants.DATE_COST} >= ? AND " +
                "${MyDatabaseConstants.DATE_COST} <= ? AND " +
                "${MyDatabaseConstants.ID_ACCOUNT_COST} = ?"
        val selectionArgs = arrayOf(categoryID.toString(),  initialDate, finalDate, accountID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму покупок по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // accountID - идентификатор аккаунта. по которому делается выборка,
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumCostByCategory(categoryID: Long, accountID : Long, initialDate: String, finalDate: String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_COST} = ? AND " +
                "${MyDatabaseConstants.ID_ACCOUNT_COST} = ? AND " +
                "${MyDatabaseConstants.DATE_COST} >= ? AND " +
                "${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString(), initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает список покупок, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку, а finalDate - дата, до которой нужно делать выборку
    fun fromCosts(initialDate : String, finalDate : String) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)
        val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает список покупок, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку; finalDate - дата, до которой нужно делать выборку,
    // а accountID - идентификатор счета. по которому будет сделана выборка
    fun fromCosts(initialDate : String, finalDate : String, accountID : Long) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ? AND ${MyDatabaseConstants.ID_ACCOUNT_COST} = ?"
        val selectionArgs = arrayOf(initialDate, finalDate, accountID.toString())
        val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    fun fromCosts(initialDate : String, finalDate : String, accountID : Long, categoryID: Long) : List<MyCost>{
        val tempList: MutableList<MyCost> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_COST} >= ? AND ${MyDatabaseConstants.DATE_COST} <= ? AND ${MyDatabaseConstants.ID_ACCOUNT_COST} = ? AND ${MyDatabaseConstants.ID_CATEGORY_COST} = ?"
        val selectionArgs = arrayOf(initialDate, finalDate, accountID.toString(), categoryID.toString())
        val sortOrder = "${MyDatabaseConstants.DATE_COST} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_COSTS, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_COST))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_COST))
            @SuppressLint("Range") val dateCost =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_COST))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_COST))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_COST))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_COST))
            val cost = MyCost(id, sum, dateCost, comment, idAccount, idCategory)
            tempList.add(cost)
        }
        cursor.close()
        return tempList
    }

    fun insertToIncome(income: MyIncome) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.SUM_INCOME, income._sum)
        contentValues.put(MyDatabaseConstants.DATE_INCOME, income._date_income)
        contentValues.put(MyDatabaseConstants.COMMENT_INCOME, income._comment)
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT_INCOME, income._account)
        contentValues.put(MyDatabaseConstants.ID_CATEGORY_INCOME, income._category)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_INCOME, null, contentValues)
    }

    fun insertToIncomeWithID(income: MyIncome) {
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_INCOME, income._id)
        contentValues.put(MyDatabaseConstants.SUM_INCOME, income._sum)
        contentValues.put(MyDatabaseConstants.DATE_INCOME, income._date_income)
        contentValues.put(MyDatabaseConstants.COMMENT_INCOME, income._comment)
        contentValues.put(MyDatabaseConstants.ID_ACCOUNT_INCOME, income._account)
        contentValues.put(MyDatabaseConstants.ID_CATEGORY_INCOME, income._category)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_INCOME, null, contentValues)
    }

    fun updateInIncome(income: MyIncome) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.SUM_INCOME, income._sum)
            put(MyDatabaseConstants.DATE_INCOME, income._date_income)
            put(MyDatabaseConstants.COMMENT_INCOME, income._comment)
            put(MyDatabaseConstants.ID_ACCOUNT_INCOME, income._account)
            put(MyDatabaseConstants.ID_CATEGORY_INCOME, income._category)
        }
        val selection = "${MyDatabaseConstants.ID_INCOME} = ?"
        val selectionArgs = arrayOf(income._id.toString())

        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_INCOME, values, selection, selectionArgs)
    }

    fun deleteFromIncome(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_INCOME +
                    " WHERE " + MyDatabaseConstants.ID_INCOME + "=" + _id + ";")
        )
    }

    val fromIncome: List<MyIncome>
        get() {
            val tempList: MutableList<MyIncome> = ArrayList()
            val cursor = sqLiteDatabase!!.query(
                MyDatabaseConstants.TABLE_INCOME, null, null, null,
                null, null, null
            )
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
                @SuppressLint("Range") val sum =
                    cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
                @SuppressLint("Range") val dateIncome =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
                @SuppressLint("Range") val comment =
                    cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
                @SuppressLint("Range") val idAccount =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
                @SuppressLint("Range") val idCategory =
                    cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
                val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
                tempList.add(income)
            }
            cursor.close()
            return tempList
        }

    // Метод позвращает список поступлений, который находяться в базе данных, где
    // category - идентификатор категория, по которой нужно делать выборку
    fun fromIncome(categoryID: Long) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает список поступлений, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку, а finalDate - дата, до которой нужно делать выборку
    fun fromIncome(initialDate : String, finalDate : String) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)
        val sortOrder = "${MyDatabaseConstants.DATE_INCOME} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    // Метод позвращает список поступлений, который находяться в базе данных, где
    // initialDate - дата, от которой нужно делать выборку; finalDate - дата, до которой нужно делать выборку,
    // а accountID - идентификатор счета. по которому будет сделана выборка
    fun fromIncome(initialDate : String, finalDate : String, accountID : Long) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ? AND ${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ?"
        val selectionArgs = arrayOf(initialDate, finalDate, accountID.toString())
        val sortOrder = "${MyDatabaseConstants.DATE_INCOME} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    fun fromIncome(initialDate : String, finalDate : String, accountID : Long, categoryID: Long) : List<MyIncome>{
        val tempList: MutableList<MyIncome> = ArrayList()

        val selection = "${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ? AND ${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ? AND ${MyDatabaseConstants.ID_CATEGORY_INCOME} = ?"
        val selectionArgs = arrayOf(initialDate, finalDate, accountID.toString(), categoryID.toString())
        val sortOrder = "${MyDatabaseConstants.DATE_INCOME} DESC"

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, sortOrder
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_INCOME))
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            @SuppressLint("Range") val dateIncome =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.DATE_INCOME))
            @SuppressLint("Range") val comment =
                cursor.getString(cursor.getColumnIndex(MyDatabaseConstants.COMMENT_INCOME))
            @SuppressLint("Range") val idAccount =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_ACCOUNT_INCOME))
            @SuppressLint("Range") val idCategory =
                cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_CATEGORY_INCOME))
            val income = MyIncome(id, sum, dateIncome, comment, idAccount, idCategory)
            tempList.add(income)
        }
        cursor.close()
        return tempList
    }

    //Метод возвращает сумму доходов за всё время
    fun getSumIncome()  : Double{
        var allSum = 0.0

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, null, null,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =  cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    //Метод возвращает сумму доходов по выбранному счёту
    fun getSumIncomeByAccount(idAccount : Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ?"
        val selectionArgs = arrayOf(idAccount.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    fun getSumIncomeByAccount(initialDate : String, finalDate : String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    fun getSumIncomeByAccount(idAccount : Long, initialDate : String, finalDate : String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ? AND ${MyDatabaseConstants.DATE_INCOME} >= ? AND ${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(idAccount.toString(), initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // где category - идентификатор категория, по которой нужно делать выборку
    fun getSumIncomeByCategory(categoryID: Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ?"
        val selectionArgs = arrayOf(categoryID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // а accountID - идентификатор аккаунта. по которому делается выборка
    fun getSumIncomeByCategory(categoryID: Long, accountID : Long) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ? AND ${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString())

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // accountID - идентификатор аккаунта. по которому делается выборка
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumIncomeByCategory(categoryID: Long, initialDate: String, finalDate: String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} >= ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(),  initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    // Метод позвращает сумму поступлений по определенной категории,
    // где category - идентификатор категория, по которой делается выборка,
    // accountID - идентификатор аккаунта. по которому делается выборка,
    // initialDate и finalDate промежуток дат, между которыми делается выборка
    fun getSumIncomeByCategory(categoryID: Long, accountID : Long, initialDate: String, finalDate: String) : Double{
        var allSum = 0.0

        val selection = "${MyDatabaseConstants.ID_CATEGORY_INCOME} = ? AND " +
                "${MyDatabaseConstants.ID_ACCOUNT_INCOME} = ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} >= ? AND " +
                "${MyDatabaseConstants.DATE_INCOME} <= ?"
        val selectionArgs = arrayOf(categoryID.toString(), accountID.toString(), initialDate, finalDate)

        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_INCOME, null, selection, selectionArgs,
            null, null, null
        )
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val sum =
                cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_INCOME))
            allSum += sum
        }
        cursor.close()
        return allSum
    }

    //*********************************** Таблица ограничения
    fun insertToLimits(limit: MyLimit){
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.TYPE_LIMIT, limit._type)
        contentValues.put(MyDatabaseConstants.SUM_LIMIT, limit._sum)
        contentValues.put(MyDatabaseConstants.ID_LIMIT_CATEGORY, limit._id_category)
        contentValues.put(MyDatabaseConstants.ID_LIMIT_CURRENCY, limit._id_currency)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_LIMITS, null, contentValues)
    }

    fun insertToLimitsWithID(limit: MyLimit){
        val contentValues = ContentValues()
        contentValues.put(MyDatabaseConstants.ID_LIMIT, limit._id)
        contentValues.put(MyDatabaseConstants.TYPE_LIMIT, limit._type)
        contentValues.put(MyDatabaseConstants.SUM_LIMIT, limit._sum)
        contentValues.put(MyDatabaseConstants.ID_LIMIT_CATEGORY, limit._id_category)
        contentValues.put(MyDatabaseConstants.ID_LIMIT_CURRENCY, limit._id_currency)
        sqLiteDatabase!!.insert(MyDatabaseConstants.TABLE_LIMITS, null, contentValues)
    }

    fun updateInLimits(limit: MyLimit) {
        val values = ContentValues().apply {
            put(MyDatabaseConstants.TYPE_LIMIT, limit._type)
            put(MyDatabaseConstants.SUM_LIMIT, limit._sum)
            put(MyDatabaseConstants.ID_LIMIT_CATEGORY, limit._id_category)
            put(MyDatabaseConstants.ID_LIMIT_CURRENCY, limit._id_currency)
        }
        val selection = "${MyDatabaseConstants.ID_LIMIT} = ?"
        val selectionArgs = arrayOf(limit._id.toString())
        sqLiteDatabase?.update(MyDatabaseConstants.TABLE_LIMITS, values, selection, selectionArgs)
    }

    fun deleteFromLimits(_id: Long) {
        sqLiteDatabase!!.execSQL(
            ("DELETE FROM " + MyDatabaseConstants.TABLE_LIMITS +
                    " WHERE " + MyDatabaseConstants.ID_LIMIT + "=" + _id + ";")
        )
    }

    fun fromLimits() : List<MyLimit>{
        val tempList: MutableList<MyLimit> = ArrayList()
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_LIMITS,null,null,null,null,null,null)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_LIMIT))
            @SuppressLint("Range") val type = cursor.getShort(cursor.getColumnIndex(MyDatabaseConstants.TYPE_LIMIT)).toByte()
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_LIMIT))
            @SuppressLint("Range") val idCategory = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_LIMIT_CATEGORY))
            @SuppressLint("Range") val idCurrency = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_LIMIT_CURRENCY))
            val limit = MyLimit(id, type, sum, idCategory, idCurrency)
            tempList.add(limit)
        }
        cursor.close()
        return tempList
    }

    fun fromLimitsFirebaseType() : List<MyLimitFirebase>{
        val tempList: MutableList<MyLimitFirebase> = ArrayList()
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_LIMITS,null,null,null,null,null,null)
        while (cursor.moveToNext()) {
            @SuppressLint("Range") val id = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_LIMIT))
            @SuppressLint("Range") val type = cursor.getInt(cursor.getColumnIndex(MyDatabaseConstants.TYPE_LIMIT))
            @SuppressLint("Range") val sum = cursor.getDouble(cursor.getColumnIndex(MyDatabaseConstants.SUM_LIMIT))
            @SuppressLint("Range") val idCategory = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_LIMIT_CATEGORY))
            @SuppressLint("Range") val idCurrency = cursor.getLong(cursor.getColumnIndex(MyDatabaseConstants.ID_LIMIT_CURRENCY))
            val limit = MyLimitFirebase(id, type, sum, idCategory, idCurrency)
            tempList.add(limit)
        }
        cursor.close()
        return tempList
    }

    fun findLimit(categoryID : Long, type : Byte) : MyLimit{
        val selection = "${MyDatabaseConstants.ID_LIMIT_CATEGORY} = ? AND ${MyDatabaseConstants.TYPE_LIMIT} = ?"
        val selectionArgs = arrayOf(categoryID.toString(), type.toString())
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_LIMITS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        val limit = MyLimit()
        while (cursor.moveToNext()) {
            limit._id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_LIMIT))
            limit._type = cursor.getShort(cursor.getColumnIndexOrThrow(MyDatabaseConstants.TYPE_LIMIT)).toByte()
            limit._sum = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseConstants.SUM_LIMIT))
            limit._id_category = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_LIMIT_CATEGORY))
            limit._id_currency = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_LIMIT_CURRENCY))
        }
        cursor.close()
        return limit
    }

    fun findLimit(categoryID : Long, type : Byte, currencyID : Long) : MyLimit{
        val selection = "${MyDatabaseConstants.ID_LIMIT_CATEGORY} = ? AND ${MyDatabaseConstants.TYPE_LIMIT} = ? AND ${MyDatabaseConstants.ID_LIMIT_CURRENCY} = ?"
        val selectionArgs = arrayOf(categoryID.toString(), type.toString(), currencyID.toString())
        val cursor = sqLiteDatabase!!.query(
            MyDatabaseConstants.TABLE_LIMITS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        val limit = MyLimit()
        while (cursor.moveToNext()) {
            limit._id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_LIMIT))
            limit._type = cursor.getShort(cursor.getColumnIndexOrThrow(MyDatabaseConstants.TYPE_LIMIT)).toByte()
            limit._sum = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseConstants.SUM_LIMIT))
            limit._id_category = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_LIMIT_CATEGORY))
            limit._id_currency = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseConstants.ID_LIMIT_CURRENCY))
        }
        cursor.close()
        return limit
    }
    //***********************************

    fun clearTableReceipts(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_RECEIPTS,null,null)
    }

    fun clearTableBanks(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_BANKS,null,null)
    }

    fun clearTableCurrencies(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_CURRENCIES,null,null)
    }

    fun clearTableAccounts(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_ACCOUNTS,null,null)
    }

    fun clearTableCategories(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_CATEGORIES,null,null)
    }

    fun clearTableCosts(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_COSTS,null,null)
    }

    fun clearTableIncome(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_INCOME,null,null)
    }

    fun clearTableLimits(){
        sqLiteDatabase!!.delete(MyDatabaseConstants.TABLE_LIMITS,null,null)
    }
}