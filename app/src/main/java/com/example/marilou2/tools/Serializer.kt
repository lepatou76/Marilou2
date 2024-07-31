package com.example.marilou2.tools

import android.content.Context
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.StreamCorruptedException

/**
 * Classe qui permet de serialiser et deserialiser des objets
 * @author Emds
 */
class Serializer {

    companion object {
        /**
         * Serialisation d'un objet
         * @param filename
         * @param object
         */
        fun serialize(filename: String?, `object`: Any?, context: Context) {
            try {
                val file = context.openFileOutput(filename, Context.MODE_PRIVATE)
                val oos: ObjectOutputStream
                try {
                    oos = ObjectOutputStream(file)
                    oos.writeObject(`object`)
                    oos.flush()
                    oos.close()
                } catch (e: IOException) {
                    // erreur de serialisation
                    e.printStackTrace()
                }
            } catch (e: FileNotFoundException) {
                // fichier non trouve
                e.printStackTrace()
            }
        }

        /**
         * Deserialisation d'un objet
         * @param filename
         * @param context
         * @return
         */
        fun deSerialize(filename: String?, context: Context): Any? {
            try {
                val file = context.openFileInput(filename)
                val ois: ObjectInputStream
                try {
                    ois = ObjectInputStream(file)
                    try {
                        val `object` = ois.readObject()
                        ois.close()
                        return `object`
                    } catch (e: ClassNotFoundException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                } catch (e: StreamCorruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            } catch (e: FileNotFoundException) {
                // fichier non trouve
                e.printStackTrace()
            }
            return null
        }
    }
}