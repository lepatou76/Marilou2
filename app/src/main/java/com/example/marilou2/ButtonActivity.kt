package com.example.marilou2

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.marilou2.ButtonsRepository.Singleton.buttonList
import com.example.marilou2.ButtonsRepository
import com.example.marilou2.R

class ButtonActivity() : AppCompatActivity() {

    private var sound: MediaPlayer? = null
    private var fileImage: Uri? = null
    private var fileSound: Uri? = null
    private var uploadedImage: ImageView? = null
    private var imageChange = 0
    private var soundChange = 0
    private var buttonID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)

        // récupérer le bouton et ses infos par son ID

        buttonID = intent.getIntExtra("selected_button", -1)

        val button = buttonList[buttonID]

        // rendre impossible le changement de position du bouton
        findViewById<EditText>(R.id.editText_position).isEnabled = (false)
        // cacher l'attente enregistrement
        findViewById<Button>(R.id.waiting_upload).visibility = View.GONE

        // recupérer et afficher les infos du boutons choisi
        findViewById<EditText>(R.id.editText_action).text = SpannableStringBuilder(button.name)
        findViewById<EditText>(R.id.editText_position).text = SpannableStringBuilder((button.position).toString())
        Glide.with(this).load(Uri.parse(button.imageUrl)).into(findViewById(R.id.imageView_button))
        sound = MediaPlayer.create(this, Uri.parse(button.sonUrl))
        sound?.start()

        listenButton()
        changeAction()
        validButton()

        // initialisation de l'image dans les parametres
        uploadedImage = findViewById(R.id.imageView_button)
        // recuperer le bouton pour charger l'image
        val pickupImageButton = findViewById<Button>(R.id.upload_image_button)
        // clique dessus ouvre les images du telephone
        pickupImageButton.setOnClickListener { pickupImage() }
        // recuperer le bouton pour charger le son
        val pickupSoundButton = findViewById<Button>(R.id.upload_sound_button)
        // clique pour ouvrir les fichiers du telephone
        pickupSoundButton.setOnClickListener { pickupSound() }
        // récuperer le bouton pour aller sur le site internet
        val internetLinkButton = findViewById<Button>(R.id.internet_link_button)
        // clique pour aller sur le site de creation de son
        internetLinkButton.setOnClickListener {
            // adresse du site
            val url = "https://magicrec.com/fr/text-to-speech"
            // activité pour y aller
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        // recuperer le bouton pour sauvegarder la nouvelle image
        val saveNewImage = findViewById<Button>(R.id.save_new_image)
        // clique pour sauver les changements
        saveNewImage.setOnClickListener {
            // message attente visible 3s
            findViewById<Button>(R.id.waiting_upload).visibility = View.VISIBLE
            Handler().postDelayed({
                findViewById<Button>(R.id.waiting_upload).visibility = View.GONE
            }, 2000)
            saveImage()}
        // recuperer le bouton pour sauvegarder le nouveau son
        val saveNewSound = findViewById<Button>(R.id.save_new_sound)
        // clique pour sauver les changements
        saveNewSound.setOnClickListener {
            // message attente visible 3s
            findViewById<Button>(R.id.waiting_upload).visibility = View.VISIBLE
            Handler().postDelayed({
                findViewById<Button>(R.id.waiting_upload).visibility = View.GONE
            }, 2000)
            saveSound()}

    }
    // pour aller chercher une image dans l'appareil
    private fun pickupImage() {
        imageChange += 1
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 33)
    }

    // pour aller chercher un son dans l'appareil
    private fun pickupSound() {
        soundChange += 1
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Sound"), 44)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 33 && resultCode == Activity.RESULT_OK){
            // verifier si les donnees sont nulles
            if(data == null || data.data == null) return
            // recuperer l'image
            fileImage = data.data
            // mettre à jour l'aperçu de l'image
            uploadedImage?.setImageURI(fileImage)

        }
        if(requestCode == 44 && resultCode == Activity.RESULT_OK){
            // verifier si les donnees sont nulles
            if(data == null || data.data == null) return
            // recuperer le son
            fileSound = data.data
            // mettre à jour le son
            sound = (MediaPlayer.create(this, fileSound))
        }
    }

    // Pour sauvegarder l'image dans Firebase si on est allé en selectionner un nouveau
    private fun saveImage() {
        val repo = ButtonsRepository()

        if(imageChange > 0){
            repo.uploadImage(fileImage!!){
                val position = findViewById<EditText>(R.id.editText_position).text.toString().toInt()
                val action = findViewById<EditText>(R.id.editText_action).text.toString()
                val soundUrl = buttonList[position - 1].sonUrl
                val downloadImageUrl = ButtonsRepository.Singleton.downloadImageUri.toString()
                // créer le nouveau bouton
                val newButton = ButtonModel(
                    position,
                    action,
                    downloadImageUrl,
                    soundUrl)
                // mettre à jour dans la bdd
                repo.updateButton(newButton)
                imageChange = 0
            }
        }
    }

    // Pour sauvegarder le son dans Firebase si on est allé en selectionner une nouvelle
    private fun saveSound() {
        val repo = ButtonsRepository()

        if(soundChange > 0){
            repo.uploadSound(fileSound!!){
                val position = findViewById<EditText>(R.id.editText_position).text.toString().toInt()
                val action = findViewById<EditText>(R.id.editText_action).text.toString()
                val imageUrl = buttonList[position - 1].imageUrl
                val downloadSoundUrl = ButtonsRepository.Singleton.downloadSoundUri.toString()
                // créer le nouveau bouton
                val newButton = ButtonModel(
                    position,
                    action,
                    imageUrl,
                    downloadSoundUrl)
                // mettre à jour dans la bdd
                repo.updateButton(newButton)
                soundChange = 0
            }
        }
    }

    // pour ecouter le son actuel
    private fun listenButton(){
        val listenButton = findViewById<Button>(R.id.listen_button)
        listenButton.setOnClickListener {
            sound?.start()
        }
    }

    // place le curseur a la fin du text
    private fun changeAction(){
        val changeAction = findViewById<EditText>(R.id.editText_action)
        changeAction.setOnClickListener {
            changeAction.setSelection(changeAction.text.length)
        }
    }

    private fun validButton() {
        // recuperer le bouton
        val validButton = findViewById<Button>(R.id.valid_button2)
        // interaction
        validButton?.setOnClickListener {
            // recuperation et sauvegarde de l'action (name) du bouton au cas ou changement
            val newAction = findViewById<EditText>(R.id.editText_action).text.toString()
            val position = findViewById<EditText>(R.id.editText_position).text.toString()
            ButtonsRepository.Singleton.databaseRef.child("button" + position).child("name").setValue(newAction)
            // retourner à l'écran principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}