package com.example.flora

import android.media.Image
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "document")
data class Flower(
    @Element
    val root: Root
)

@Xml(name = "root")
data class Root(
    @Element
    val result: MutableList<Result>
)

@Xml
data class Result(
    @PropertyElement(name = "dataNo") var dataNo: String? ,
    @PropertyElement(name = "flowNm") var name: String? ,
    @PropertyElement(name = "fEngNm") var engname: String? ,
    @PropertyElement(name = "flowLang") var flowLang: String? ,
    @PropertyElement(name = "fContent") var content: String? ,
    @PropertyElement(name = "fUse") var used: String? ,
    @PropertyElement(name = "fGrow") var grow: String? ,
    @PropertyElement(name = "publishOrg") var publishOrg: String? ,
    @PropertyElement(name = "imgUrl1") var file1: String? ,
    @PropertyElement(name = "imgUrl2") var file2: String? ,
    @PropertyElement(name = "imgUrl3") var file3: String? ,
)

data class Flist(var dataNo: String, var name: String, var lang:String, var file1: String)
