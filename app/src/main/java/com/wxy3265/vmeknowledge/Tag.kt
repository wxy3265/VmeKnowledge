package com.wxy3265.vmeknowledge

import android.os.Build
import androidx.annotation.RequiresApi

class Tag(var orstr: String?, var tSet: MutableSet<String>?) {
    val delim = "</!tag!\\>"
    constructor(orstr: String) : this(orstr, null) {
        tSet = orstr.split(delim).toMutableSet()
    }
    constructor(tList: MutableSet<String>?) : this("", tList) {


        
        for (str in tSet!!) {
            if (str != "")
                orstr += delim + str
        }
    }
    constructor() : this("", null)
    fun addTag(str: String) {
        orstr += delim + str
        tSet?.add(str)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun removeTag(str: String) {
        orstr = orstr?.replace(delim + str, "", ignoreCase = true)
        tSet?.removeIf{ it == str }
    }
    fun checkTag(str: String): Boolean = orstr?.indexOf(str) != -1 && orstr?.indexOf(str) != null

    @RequiresApi(Build.VERSION_CODES.N)
    fun clear() {
        for (str in tSet!!) {
            removeTag(str)
        }
        orstr = ""
    }
}