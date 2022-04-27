package dev.shuanghua.weather.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "param_outer")
data class OuterParam(
    val type: String = OuterParam.type,
    val ver: String = OuterParam.ver,
    val rever: String = OuterParam.rever,
    val net: String = OuterParam.net,

    @PrimaryKey
    val pcity: String = "",
    val parea: String = "",
    val lon: String = "",
    val lat: String = "",

    val gif: String = OuterParam.gif,
    val uid: String = OuterParam.uid,
    val uname: String = OuterParam.uname,
    val token: String = OuterParam.token,
    val os: String = OuterParam.os,
) {
    companion object {
        val type = "1"
        val ver = "v5.7.0"
        val rever = "578"
        val net = "WIFI"
        val gif = "true"
        val uid = "Rjc4qedi323eK4PGsztqsztq"
        val uname = ""
        val token = ""
        val os = "android30"

        val HEIGHT = "2215"
        val WIDTH = "1080"
        val ISLOCATION = "0"
        val CITYIDS = "28060159493,32010145005,28010159287,02010058362,01010054511"
        val DEFAULT = OuterParam()
    }
}