package com.example.marilou2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.marilou2.R
import com.example.marilou2.adapter.ButtonAdapter

class MainActivity : AppCompatActivity() {

    val nbButtons = 3
    val buttonList = arrayListOf<ButtonModel>()
    val button1 = ButtonModel(1, "Manger", R.drawable.faim, R.raw.faim)
    val button2 = ButtonModel(2,"Boire", R.drawable.soif, R.raw.soif)
    val button3 = ButtonModel(3, "Toilettes", R.drawable.toilettes, R.raw.envie_pipi)
    val button4 = ButtonModel(4,"Dormir", R.drawable.sommeil, R.raw.dormir)
    val button5 = ButtonModel(5,"Jouer", R.drawable.jouer, R.raw.jouer)
    val button6 = ButtonModel(6,"Se promener", R.drawable.promener, R.raw.promener)

    var nbButtons1 = 0
    var nbButtons2 = 0
    var buttonList1 = arrayListOf<ButtonModel>()
    var buttonList2 = arrayListOf<ButtonModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonList.add(button1)
        buttonList.add(button2)
        buttonList.add(button3)
        buttonList.add(button4)
        buttonList.add(button5)
        buttonList.add(button6)

        // adapter position et images des boutons suivant le nombre choisi
        if(nbButtons <= 3){
            nbButtons1 = nbButtons
            buttonList1.add(buttonList[0])
            if(nbButtons1 == 2){
                buttonList1.add(buttonList[1])
            }
            if(nbButtons1 == 3){
                buttonList1.add(buttonList[1])
                buttonList1.add(buttonList[2])
            }
        }
        else{
            if(nbButtons == 4){
                nbButtons1 = 2
                nbButtons2 = 2
                buttonList1.add(buttonList[0])
                buttonList1.add(buttonList[1])
                buttonList2.add(buttonList[2])
                buttonList2.add(buttonList[3])
            }
            else{
                nbButtons1 = 3
                nbButtons2 = nbButtons - 3
                buttonList1.add(buttonList[0])
                buttonList1.add(buttonList[1])
                buttonList1.add(buttonList[2])
                buttonList2.add(buttonList[3])
                buttonList2.add(buttonList[4])

                if(nbButtons == 6){
                    buttonList2.add(buttonList[5])
                }
            }
        }

        // recuperer le recyclerview1
        val recyclerViewButtonsOne = findViewById<RecyclerView>(R.id.recycler_view_buttons_one)
        if (recyclerViewButtonsOne != null) {
            recyclerViewButtonsOne.adapter = ButtonAdapter(this, buttonList1)
        }

        // recupérer le recyclerview2
        val recyclerViewButtonsTwo = findViewById<RecyclerView>(R.id.recycler_view_buttons_two)
        recyclerViewButtonsTwo.adapter = ButtonAdapter(this, buttonList2)
        // rendre inaccessible la ligne 2 si besoin
        if(nbButtons <=3) {
            recyclerViewButtonsTwo.visibility = View.GONE
        }

        // cacher le layout password
        findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE



    }


}