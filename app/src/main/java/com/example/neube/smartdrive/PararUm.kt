package com.example.neube.smartdrive

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class PararUm (
        var PararUmEsquerda: Int? = 0,
        var PararUmDireita: Int? = 0,
        var PararDois: Int = 0,
        var DirecaoUm: Int = 0
        //  var fcmotoruma: Int? = 0
)

// http://blog.farifam.com/2017/10/25/android-kotlin-using-toobject-to-get-firestore-data/

//class Member(
//        var id: String ="",
//        var first: String = "",
//        var last: String = "",
//        var born: String=""
//)