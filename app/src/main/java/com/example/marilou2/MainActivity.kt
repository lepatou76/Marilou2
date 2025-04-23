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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.marilou2.ButtonsRepository.Singleton.buttonList

class MainActivity : AppCompatActivity() {

    private val fileNameInfos = "InfosSavedList"
    private var infosSaved = InfosSaved()
    private val infosType = object : TypeToken<InfosSaved>() {}.type

    var nbButtons1 = 0
    var nbButtons2 = 0
    var buttonList1 = arrayListOf<ButtonModel>()
    var buttonList2 = arrayListOf<ButtonModel>()

    // charger notre ButtonRepository
    private val repo = ButtonsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recupération et affichage des infos sauvegardées si existantes
        if(Serializer.deSerialize(fileNameInfos,this) !=null){
            val infosBack = Serializer.deSerialize(fileNameInfos,this).toString()
            infosSaved = Gson().fromJson(infosBack, infosType)
        }
        val nbButtons = infosSaved.nbButtons
        val delay= infosSaved.delay

        // mettre à jour la liste de boutons avant de les organiser
        repo.updateData {

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

        }

        // cacher le layout password
        findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE

        exit()
        parametersAccess()
        validPassword()
        val passwordLayout = findViewById<LinearLayout>(R.id.password_layout)
        passwordLayout.setOnClickListener {
            removePasswordLayout()
        }
    }

    private fun exit(){
        // recuperer le bouton
        val exitButton = findViewById<FloatingActionButton>(R.id.exit_Button)
        // click pour quitter
        exitButton.setOnClickListener {
            finishAffinity()
        }
    }

    // click sur le bouton parametres demande la saisie du mot de passe
    private fun parametersAccess(){
        // recuperer le bouton et l'Edittext
        val parametersButton = findViewById<FloatingActionButton>(R.id.parameters_button)
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
    // Pour tester le mot de passe et ouvrir les paramètres
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
                removePasswordLayout()
            }
        }
    }

    // pour cacher la boite si elle a été ouverte par erreur
    fun removePasswordLayout(){
        val passwordEditText = findViewById<EditText>(R.id.password_text_edit)

        // vider l'Edittext de saisie
        passwordEditText.text = null
        // cacher le layout de saisie
        findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
        // reactiver le bouton parametre
        findViewById<FloatingActionButton>(R.id.parameters_button).isClickable = (true)
        // forcer fermeture clavier
        passwordEditText.isEnabled = (false)
    }

    // Boucler le retour arriere depuis la page d'accueil
    // afin d'éviter un accès non autorisé aux paramètres
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}