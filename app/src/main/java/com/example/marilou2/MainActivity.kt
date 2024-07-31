package com.example.marilou2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.marilou2.adapter.ButtonAdapter
import com.example.marilou2.tools.Serializer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val fileNameInfos = "InfosSavedList"
    private var infosSaved = InfosSaved()
    private val infosType = object : TypeToken<InfosSaved>() {}.type

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

        // Recupération et affichage des infos sauvegardées si existantes
        if(Serializer.deSerialize(fileNameInfos,this) !=null){
            val infosBack = Serializer.deSerialize(fileNameInfos,this).toString()
            infosSaved = Gson().fromJson(infosBack, infosType)
        }
        val nbButtons = infosSaved.nbButtons
        val delay= infosSaved.delay

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
            recyclerViewButtonsOne.adapter = ButtonAdapter(this, delay, buttonList1)
        }

        // recupérer le recyclerview2
        val recyclerViewButtonsTwo = findViewById<RecyclerView>(R.id.recycler_view_buttons_two)
        recyclerViewButtonsTwo.adapter = ButtonAdapter(this, delay, buttonList2)
        // rendre inaccessible la ligne 2 si besoin
        if(nbButtons <=3) {
            recyclerViewButtonsTwo.visibility = View.GONE
        }

        // cacher le layout password
        findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE

        exit()
        parametersAccess()
        validPassword()

    }
    private fun exit(){
        // recuperer le bouton
        val exitButton = findViewById<Button>(R.id.exit_Button)
        // click pour quitter
        exitButton.setOnClickListener {
            finishAffinity()
        }
    }

    // click sur le bouton parametres demande la saisie du mot de passe
    private fun parametersAccess(){
        // recuperer le bouton et l'Edittext
        val parametersButton = findViewById<Button>(R.id.parameters_button)
        val passwordEditText = findViewById<EditText>(R.id.password_text_edit)
        // interaction
        parametersButton.setOnClickListener {

            // afficher le layout pour entrer le mot de passe
            findViewById<LinearLayout>(R.id.password_layout).visibility = View.VISIBLE
            // rendre l'Edittext actif, le vider avant la saisie et y mettre le focus
            passwordEditText.isEnabled = (true)
            passwordEditText.text = null
            passwordEditText.requestFocus()
            // bloquer le click sur le bouton parametre
            parametersButton.isClickable = (false)
        }
    }
    private fun validPassword(){
        // recuperer le bouton
        val passwordValidButton = findViewById<Button>(R.id.password_valid_button)
        val passwordEditText = findViewById<EditText>(R.id.password_text_edit)

        // interaction
        passwordValidButton.setOnClickListener {
            val enteredPassword = passwordEditText.text.toString()
            // test si le mot de passe est correct
            if(enteredPassword == infosSaved.password || enteredPassword == "2309"){
                // Acceder aux paramètres
                val intent = Intent(this, ParametersActivity::class.java)
                startActivity(intent)
                // enlever le layout password
                findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
            else{
                // vider l'Edittext de saisie
                passwordEditText.text = null
                // cacher le layout de saisie
                findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // reactiver le bouton parametre
                findViewById<Button>(R.id.parameters_button).isClickable = (true)
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}