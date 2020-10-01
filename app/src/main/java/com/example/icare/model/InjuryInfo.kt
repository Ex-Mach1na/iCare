package com.example.icare.model

data class InjuryInfo(val id:String, val Name:String, val Solution:String){
    constructor(): this(id = "", Name="", Solution="")

}