package com.goodlucky.finance

import android.app.Activity
import android.os.Build
import java.io.Serializable

object MyFunction {

    //Функция использовуется для получения объекта из другой Activity
    fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)!!
        else
            activity.intent.getSerializableExtra(name) as T
    }
}