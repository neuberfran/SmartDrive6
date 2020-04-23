package com.example.neube.smartdrive

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.*
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import com.google.common.base.Predicates.or
import com.google.firebase.database.*
import com.google.firebase.firestore.*
//import com.google.firebase.firestore.auth.User
import com.neuberfran.androidthings.driver.SmartDrive.SmartDrive
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.logging.Logger
import com.neuberfran.androidthings.driver.SmartDrive.SmartDrive.*
import kotlinx.coroutines.async
import java.lang.ref.Reference

class MainActivity : Activity() {

    private val LOG = Logger.getLogger(this.javaClass.name)
    private val TAG = MainActivity::class.java.simpleName

    var db = FirebaseFirestore.getInstance()
    var JanelaUmEsquerda = db.collection("smartmodel").document("motores")
    var JanelaUmDireita  = db.collection("smartmodel").document("motores")

    var fcmotorUmAvalue0=hashMapOf("fcmotoruma" to 0)

    var fcmotorUmAvalue1=hashMapOf("fcmotoruma" to 1)

    var fcmotorUmBvalue0=hashMapOf("fcmotorumb" to 0)

    var fcmotorUmBvalue1=hashMapOf("fcmotorumb" to 1)

    // Pus isso e OnDestroy(()
 //   var FCMotorUmA: Gpio? = null

    var mSmartDrive: SmartDrive? = null

    internal var I2C_PIN_NAME = "I2C1"
    internal val I2C_ADDRESS_SMARTDRIVE = 0x1B
    internal var SmartDrive_COMMAND = 0x41

    internal var CMD_R = 0x52
    internal var CMD_S = 0x53

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSmartDrive = SmartDrive(I2C_PIN_NAME, I2C_ADDRESS_SMARTDRIVE)

        mSmartDrive?.command(CMD_R)

        val manager = PeripheralManager.getInstance()

        var FCMotorUmA: Gpio? = null

        try {

            FCMotorUmA = manager.openGpio(BoardDefaults.getGPIOForButton21())

            Log.i(ContentValues.TAG, "99 99 99 ")

            FCMotorUmA.setDirection(Gpio.DIRECTION_IN)
            // Step 3. Enable edge trigger events.
            FCMotorUmA.setEdgeTriggerType(Gpio.EDGE_BOTH)

            FCMotorUmA.registerGpioCallback(mCallUmEsquerda)

            if (FCMotorUmA.value) {

                db.collection("smartmodel").document("motores")
                    .set(fcmotorUmAvalue1, SetOptions.merge())

            } else if (!FCMotorUmA.value) {

                db.collection("smartmodel").document("motores")
                    .set(fcmotorUmAvalue0, SetOptions.merge())
            }

        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "Error on PeripheralIO API", e)
        }

        JanelaUmEsquerda.addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(LOG.toString(), "Listen failed.", e)
                return@addSnapshotListener
            }
        }

        db.collection("smartmodel").document("motores").set(fcmotorUmAvalue0, SetOptions.merge())

        var FCMotorUmB: Gpio? = null

        try {

            FCMotorUmB = manager.openGpio(BoardDefaults.getGPIOForButton18())

            Log.i(ContentValues.TAG, "99 99 99 ")

            FCMotorUmB.setDirection(Gpio.DIRECTION_IN)
            // Step 3. Enable edge trigger events.
            //
            FCMotorUmB.setEdgeTriggerType(Gpio.EDGE_BOTH)

            FCMotorUmB.registerGpioCallback(mCallUmDireita)

            if (FCMotorUmB.value) {

                db.collection("smartmodel").document("motores")
                    .set(fcmotorUmBvalue1, SetOptions.merge())

            } else if (!FCMotorUmB.value) {

                db.collection("smartmodel").document("motores")
                    .set(fcmotorUmBvalue0, SetOptions.merge())
            }

        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "Error on PeripheralIO API", e)
        }

        JanelaUmDireita.addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(LOG.toString(), "Listen failed.", e)
                return@addSnapshotListener
            }

            var DirecaoUm=snapshot?.toObject(SmartModel::class.java)?.DirecaoUm

            if (DirecaoUm!!.equals(2)){

                Log.i(ContentValues.TAG, "DirecaoUm 94301 94301 94301" + DirecaoUm)
                if (FCMotorUmB != null) {
                    RodaMotorUmDireita(FCMotorUmB)
                }
     //           FCMotorUmB!!.registerGpioCallback(mCallUmDireita)

                Log.i(ContentValues.TAG, "DirecaoUm 94302 94302 94302" + DirecaoUm)
             }
        }

        db.collection("smartmodel").document("motores").set(fcmotorUmBvalue0, SetOptions.merge())
    }

    var mCallUmEsquerda = object : GpioCallback {

        override fun onGpioEdge(buttonUmA: Gpio): Boolean {

            Thread(Runnable {

                Log.i(ContentValues.TAG, "GPIO changed, button 94101 94101 94101" + buttonUmA.value)

                var pararRefUmEsquerda = db.collection("smartmodel").document("motores")

                var taskUmEsquerda: Task<DocumentSnapshot> = pararRefUmEsquerda.get()

                var snapUmEsquerda: DocumentSnapshot = Tasks.await(taskUmEsquerda)

                var xPararUmEsquerda = snapUmEsquerda.toObject(PararUm::class.java)?.PararUmEsquerda

                if (buttonUmA.value) {

                    db.collection("smartmodel").document("motores").set(fcmotorUmAvalue1, SetOptions.merge())

                    Log.i(ContentValues.TAG, "passei 3 passei 3 passei 3")

                } else if (!buttonUmA.value && xPararUmEsquerda!!.equals(0)) {

                    db.collection("smartmodel").document("motores").set(fcmotorUmAvalue0,SetOptions.merge())

                    Log.i(ContentValues.TAG, "passei 911 passei 911 passei 911" + buttonUmA.value)

                    var pararRefUmEsquerda = db.collection("smartmodel").document("motores")

                    var pauloUmEsquerda = true

                    while (!buttonUmA.value && pauloUmEsquerda) {

                        mSmartDrive?.SmartDrive_Run_Unlimited(SmartDrive.SmartDrive_Motor_1, SmartDrive.SmartDrive_Direction_Forward,100)

                        var taskUmEsquerda: Task<DocumentSnapshot> = pararRefUmEsquerda.get()

                        var snapUmEsquerda: DocumentSnapshot = Tasks.await(taskUmEsquerda)

                        var xPararUmEsquerda = snapUmEsquerda.toObject(PararUm::class.java)?.PararUmEsquerda

                        Log.i(TAG, "antes, antes = 9409 9409 9409" + xPararUmEsquerda)

                        if (xPararUmEsquerda!!.equals(1)) {

                            var taskUmEsquerda: Task<DocumentSnapshot> = pararRefUmEsquerda.get()

                            var snapUmEsquerda: DocumentSnapshot = Tasks.await(taskUmEsquerda)

                            var xPararUmEsquerda = snapUmEsquerda.toObject(PararUm::class.java)?.PararUmEsquerda

                            pauloUmEsquerda = false

                            mSmartDrive?.command(CMD_R)

                            mSmartDrive?.SmartDrive_Stop(
                                SmartDrive_Motor_1,
                                SmartDrive_Next_Action_Brake
                            )

                        }

                        Log.i(TAG, "girando, girando, =  9411 9411 9411" + xPararUmEsquerda)

                        Log.i(
                            ContentValues.TAG,
                            "nao entrei no if and else if 9412 9412 9412" + buttonUmA.value
                        )
                    }
                }
            }).start()

            db.collection("smartmodel").document("motores").set(fcmotorUmAvalue0, SetOptions.merge())

            return true
        }

        override fun onGpioError(gpio: Gpio?, error: Int) = LOG.severe("$gpio Error event $error")
    }

    var mCallUmDireita = object : GpioCallback {

        override fun onGpioEdge(buttonUmB: Gpio): Boolean {

            Thread(Runnable {

            Log.i(ContentValues.TAG, "GPIO changed, button 94201 94201 94201" + buttonUmB.value)

                var pararRefUmDireita = db.collection("smartmodel").document("motores")

                var taskUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()

                var snapUmDireita: DocumentSnapshot = Tasks.await(taskUmDireita)

                var xPararUmDireita = snapUmDireita.toObject(PararUm::class.java)?.PararUmDireita

            if (buttonUmB.value) {

                db.collection("smartmodel").document("motores").set(fcmotorUmBvalue1, SetOptions.merge())

                Log.i(ContentValues.TAG, "passei 23 passei 23 passei 23")

            } else if (!buttonUmB.value && xPararUmDireita!!.equals(0)) {

                db.collection("smartmodel").document("motores").set(fcmotorUmBvalue0,SetOptions.merge())

                Log.i(ContentValues.TAG, "passei 921 passei 921 passei 921" + buttonUmB.value)

                var pararRefUmDireita = db.collection("smartmodel").document("motores")

                var pauloUmDireita = true

                while (!buttonUmB.value && pauloUmDireita) {

                    mSmartDrive?.SmartDrive_Run_Unlimited(SmartDrive.SmartDrive_Motor_1, SmartDrive.SmartDrive_Direction_Reverse,100)

                    var taskUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()

                    var snapUmDireita: DocumentSnapshot = Tasks.await(taskUmDireita)

                    var xPararUmDireita = snapUmDireita.toObject(PararUm::class.java)?.PararUmDireita

                    Log.i(TAG, "antes, antes = 9429 9429 9429" + xPararUmDireita)

                    if (xPararUmDireita!!.equals(2)) {

                        var taskUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()

                        var snapUmDireita: DocumentSnapshot = Tasks.await(taskUmDireita)

                        var xPararUmDireita = snapUmDireita.toObject(PararUm::class.java)?.PararUmDireita

                        pauloUmDireita = false

//                        var DirecaoUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()
//
//                        var snapDirecaomDireita: DocumentSnapshot = Tasks.await(DirecaoUmDireita)
//
//                        var xPararDirecaoUmDireita = snapDirecaomDireita.toObject(PararUm::class.java)?.DirecaoUm

                        mSmartDrive?.command(CMD_R)

                        mSmartDrive?.SmartDrive_Stop(
                            SmartDrive_Motor_1,
                            SmartDrive_Next_Action_Brake
                        )

//                        var DirecaoUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()
//
//                        var snapDirecaomDireita: DocumentSnapshot = Tasks.await(DirecaoUmDireita)
//
//                        var xPararDirecaoUmDireita = snapDirecaomDireita.toObject(PararUm::class.java)?.DirecaoUm
//
//                        if (xPararDirecaoUmDireita!!.equals(1) or (xPararDirecaoUmDireita!!.equals(0)) ) {
//
//                            pauloUmDireita = false
//
//                        }

//                        var data2Direita=hashMapOf("PararUmDireita" to 0)
//
//                        db.collection("smartmodel").document("motores").set(data2Direita, SetOptions.merge())

                        Log.i(TAG, "parado, parado = 9420 9420 9420" + xPararUmDireita)
                    }

//                    var data2Direita=hashMapOf("PararUmDireita" to 0)
//
//                    db.collection("smartmodel").document("motores").set(data2Direita, SetOptions.merge())

                    Log.i(TAG, "girando, girando, =  9421 9421 9421" + xPararUmDireita)

                    Log.i(ContentValues.TAG,"nao entrei no if and else if 9422 9422 9422" + buttonUmB.value  )
                }
            }
            }).start()

            db.collection("smartmodel").document("motores").set(fcmotorUmBvalue0, SetOptions.merge())

            return true
        }
        override fun onGpioError(gpio: Gpio?, error: Int) = LOG.severe("$gpio Error event $error")
    }


    fun RodaMotorUmDireita(buttonUmB: Gpio){

        Thread(Runnable {

            Log.i(ContentValues.TAG, "GPIO changed, button 94201 94201 94201" + buttonUmB.value)

            var pararRefUmDireita = db.collection("smartmodel").document("motores")

            var taskUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()

            var snapUmDireita: DocumentSnapshot = Tasks.await(taskUmDireita)

            var xPararUmDireita = snapUmDireita.toObject(PararUm::class.java)?.PararUmDireita

            if (buttonUmB.value) {

                db.collection("smartmodel").document("motores").set(fcmotorUmBvalue1, SetOptions.merge())

                Log.i(ContentValues.TAG, "passei 23 passei 23 passei 23")

            } else if (!buttonUmB.value && xPararUmDireita!!.equals(0)) {

                db.collection("smartmodel").document("motores").set(fcmotorUmBvalue0,SetOptions.merge())

                Log.i(ContentValues.TAG, "passei 921 passei 921 passei 921" + buttonUmB.value)

                var pararRefUmDireita = db.collection("smartmodel").document("motores")

                var pauloUmDireita = true

                while (!buttonUmB.value && pauloUmDireita) {

                    mSmartDrive?.SmartDrive_Run_Unlimited(SmartDrive.SmartDrive_Motor_1, SmartDrive.SmartDrive_Direction_Reverse,100)

                    var taskUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()

                    var snapUmDireita: DocumentSnapshot = Tasks.await(taskUmDireita)

                    var xPararUmDireita = snapUmDireita.toObject(PararUm::class.java)?.PararUmDireita

                    Log.i(TAG, "antes, antes = 9429 9429 9429" + xPararUmDireita)

                    if (xPararUmDireita!!.equals(2)) {

                        var taskUmDireita: Task<DocumentSnapshot> = pararRefUmDireita.get()

                        var snapUmDireita: DocumentSnapshot = Tasks.await(taskUmDireita)

                        var xPararUmDireita = snapUmDireita.toObject(PararUm::class.java)?.PararUmDireita

                        pauloUmDireita = false

                        mSmartDrive?.command(CMD_R)

                        mSmartDrive?.SmartDrive_Stop(
                            SmartDrive_Motor_1,
                            SmartDrive_Next_Action_Brake
                        )

                        Log.i(TAG, "parado, parado = 9420 9420 9420" + xPararUmDireita)
                    }

                    Log.i(TAG, "girando, girando, =  9421 9421 9421" + xPararUmDireita)

                    Log.i(ContentValues.TAG,"nao entrei no if and else if 9422 9422 9422" + buttonUmB.value  )
                }
            }
        }).start()

    }

}