package com.example.marilou2

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.marilou2.tools.Serializer
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.marilou2.ButtonModel
import com.example.marilou2.MainActivity
import com.example.marilou2.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ParametersActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)

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

        // selection du spinner sur le nombre de boutons actuel
        findViewById<Spinner>(R.id.button_spinner_input).setSelection(infosSaved.nbButtons - 1)
        // recuperation du mot de passe actuel
        findViewById<EditText>(R.id.password_change_text_edit).setText(infosSaved.password)
        // recuperation du delai actuel
        findViewById<EditText>(R.id.editTextDelay).setText(infosSaved.delay.toString())
        // cacher le layout de modification du mot de passe
        findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.GONE
        // cacher le layout de confirmation réinitialisation
        findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.GONE

        selectButton()
        changePassword()
        validParameters()

        // recuperer le bouton de réinitialisation
        val resetButton = findViewById<Button>(R.id.reset_app)
        // clique pour lancer la procédure
        resetButton.setOnClickListener { } // recuperer bouton mode d'emploi
        val noticeButton = findViewById<Button>(R.id.read_notice)
        // clique pour accéder à la notice
        noticeButton.setOnClickListener {
            // adresse du lien de la notice
            val url = "https://1c85e57a-7f4a-421b-955a-fba11744e35f.filesusr.com/ugd/1ea87c_aebc5fa11c0f4274bb196196f87f2384.pdf"
            // activité pour la récupérer et l'afficher
            val intent = Intent(Intent.ACTION_QUICK_VIEW)
            intent.setDataAndType(Uri.parse(url), "application/pdf")
           startActivity(intent)
        }
    }

    // pour ouvrir le fragment Button avec le bouton selectionné
    private fun selectButton(){
        val uploadSelectedButton = findViewById<Button>(R.id.upload_button_selected)
        uploadSelectedButton.setOnClickListener {
            val position =
                findViewById<Spinner>(R.id.button_spinner_modif).selectedItem.toString().toInt() - 1

            val intent = Intent(this, ButtonActivity::class.java)
            intent.putExtra("selected_button", position)
            startActivity(intent)
        }
    }
    // pour modifier le mot de passe
    private fun changePassword(){
        val changePassword = findViewById<Button>(R.id.password_change_button)
        val textPassword = findViewById<EditText>(R.id.password_change_text_edit)
        changePassword.setOnClickListener {
            textPassword.isEnabled = (true)
            findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.VISIBLE
            findViewById<EditText>(R.id.password_change_text_edit).requestFocus()
            textPassword.setSelection(textPassword.text.length)
        }
        val validChange = findViewById<Button>(R.id.password_change_valid_button)
        validChange.setOnClickListener {
            findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.GONE
            textPassword.isEnabled = (false)
        }
    }

    private fun validParameters(){
        // recuperer le bouton
        val validButton = findViewById<Button>(R.id.valid_button1)
        // sauvegarde des informations au clic sur VALIDER
        validButton?.setOnClickListener {
            // recupération et sauvegarde du nombre de boutons, du mot de passe et du délai choisis
            val newNbButtons =
                findViewById<Spinner>(R.id.button_spinner_input)?.selectedItem.toString().toInt()
            val newPassword = findViewById<EditText>(R.id.password_change_text_edit)?.text.toString()
            val newDelay = findViewById<EditText>(R.id.editTextDelay)?.text.toString().toInt()

            infosSaved = InfosSaved(newDelay, newNbButtons, newPassword)

            val jsonString = Gson().toJson(infosSaved)
            Serializer.serialize(fileNameInfos, jsonString, this)

            // retourner à l'écran principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}