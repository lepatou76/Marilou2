package com.example.marilou2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.example.marilou2.ButtonsRepository.Singleton.buttonList
import com.google.android.material.internal.ViewUtils
import com.google.android.material.internal.ViewUtils.hideKeyboard

class ParametersActivity : AppCompatActivity() {

    private val fileNameInfos = "InfosSavedList"
    private var infosSaved = InfosSaved()
    private val infosType = object : TypeToken<InfosSaved>() {}.type

       @SuppressLint("RestrictedApi")
       override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_parameters)


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
            resetButton.setOnClickListener { resetApp() }

            // recuperer bouton mode d'emploi
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
           // Vide le champ du délai bouton quand on est dessus
           findViewById<EditText>(R.id.editTextDelay).onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
               if (hasFocus) {
                   (findViewById<EditText>(R.id.editTextDelay)).text.clear()
               }}

           // Ne pas laisser le champ délai vide par erreur
           findViewById<EditText>(R.id.editTextDelay).setOnEditorActionListener { v, actionId, event ->
               val delay = findViewById<EditText>(R.id.editTextDelay).text.toString()

               if (actionId == EditorInfo.IME_ACTION_DONE){
                   if(delay.isEmpty()) {findViewById<EditText>(R.id.editTextDelay).setText("3")}
                   hideKeyboard(findViewById<EditText>(R.id.editTextDelay))
                   return@setOnEditorActionListener true
               }
               false

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
    private fun resetApp() {
        // faire apparaitre le layout demandant de confirmer la réinitialisation avec avertissement
        findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.VISIBLE
        // récupérer les boutons OUI et NON
        val yesButton = findViewById<Button>(R.id.yes_reset_button)
        val noButton = findViewById<Button>(R.id.no_reset_button)
        // choix de ne pas réinitialiser
        noButton.setOnClickListener {
            // disparition de la fenêtre
            findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.GONE
        }
        // choix de réinitialiser
        yesButton.setOnClickListener {
            resetButtons() {
            // disparition de la fenêtre
            findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.GONE
            // retourner à l'écran principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }}

    }
    private fun resetButtons(callback: () -> Unit) {
        val repo = ButtonsRepository()

        // recréer les six boutons d'origine grace au stockage de FireBase et le mettre à jour dans la bdd
        val button1 = ButtonModel(1, "Manger", "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/faim.jpg?alt=media&token=677a503f-f096-4e23-a741-4a920183e74c",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/faim.mp3?alt=media&token=4d9e4d43-f196-4b83-935d-848e279f101a")
        repo.updateButton(button1)
        val button2 = ButtonModel(2,"Boire","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/soif.jpg?alt=media&token=6f035b45-bd08-4b21-bc41-895f1865bb15",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/soif.mp3?alt=media&token=23fcaa3b-17ac-4402-b2ff-d6fccadc8244")
        repo.updateButton(button2)
        val button3 = ButtonModel(3, "Toilettes","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/toilettes.jpg?alt=media&token=7d0dd5d3-3d31-4f7b-b415-6b49149511e3",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/toilettes.mp3?alt=media&token=6e82ff50-cf13-4b4a-9e37-6e893e971ba5")
        repo.updateButton(button3)
        val button4 = ButtonModel(4,"Dormir","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/sommeil.jpg?alt=media&token=4b3f2158-19b7-46f7-be97-950df361386c",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/sommeil.mp3?alt=media&token=4944cce5-1305-4ab3-a849-8295c48b145f")
        repo.updateButton(button4)
        val button5 = ButtonModel(5,"Jouer","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/jouer.jpg?alt=media&token=e24e73e3-e1c2-42bd-86ed-100b24189fa8",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/jouer.mp3?alt=media&token=2d0b571c-eaa2-4cd8-bbe4-e9928d854feb")
        repo.updateButton(button5)
        val button6 = ButtonModel(6,"Se promener","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/se_promener.jpg?alt=media&token=babc7c2b-7fcf-4b5d-9723-ce955b770e67",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/se_promener.mp3?alt=media&token=a6136713-8a4f-4716-a1fb-435328764468")
        repo.updateButton(button6)

        val infos = InfosSaved()
        val jsonString = Gson().toJson(infos)
        Serializer.serialize(fileNameInfos, jsonString, this)

        callback()
    }

}