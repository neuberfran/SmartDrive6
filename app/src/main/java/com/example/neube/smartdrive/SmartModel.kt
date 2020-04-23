package com.example.neube.smartdrive

import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class SmartModel (

    var DirecaoDois: Int = 1,
    var PararDois: Int = 0,
    var DirecaoUm: Int = 0,
    var PararUmEsquerda: Int = 0,
    var PararUmDireita: Int = 0,
    var fcmotordoisa: Int = 0,
    var fcmotordoisb: Int = 0,
    var fcmotoruma: Int = 0,
    var fcmotorumb: Int = 0,
    var BaixaTemperatura: Boolean = false,
    var Chovendo: Boolean = false

)



//
//@IgnoreExtraProperties
//data class SmartModel (
////
////    companion object Factory {
////        fun create(): SmartModel = SmartModel()
////    }
//    var DirecaoDois: Int = 0,
//    var PararDois: Int = 0,
//    var DirecaoUm: Int = 0,
//    var PararUm: Int = 0,
//    var fcmotordoisa: Int = 0,
//    var fcmotordoisb: Int = 0,
//    var fcmotoruma: Int = 0,
//    var fcmotorumb: Int = 0,
//    var BaixaTemperatura: Boolean = false,
//    var Chovendo: Boolean = false
//
////            override fun(): String {
////        return "{SmartModel DirecaoDois=$DirecaoUm ticker PararUm=$PararUm}"
////    }
//
//)