package com.example.agenda

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)

        findViewById<Button>(R.id.botonAgregar).setOnClickListener {
            agregarContacto()
        }

        findViewById<Button>(R.id.botonConsultar).setOnClickListener {
            consultarContactos()
        }

        findViewById<Button>(R.id.botonBorrar).setOnClickListener {
            borrarContacto()
        }

        findViewById<Button>(R.id.botonBorrarTodos).setOnClickListener {
            borrarTodosLosContactos()
        }
    }

    private fun agregarContacto() {
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val direccion = findViewById<EditText>(R.id.editTextDireccion).text.toString()
        val telefono = findViewById<EditText>(R.id.editTextTelefono).text.toString()
        val correo = findViewById<EditText>(R.id.editTextCorreo).text.toString()

        if (nombre.isNotEmpty() && direccion.isNotEmpty() && telefono.isNotEmpty() && correo.isNotEmpty()) {
            val valores = ContentValues()
            valores.put("nombre", nombre)
            valores.put("direccion", direccion)
            valores.put("telefono", telefono)
            valores.put("correo", correo)

            dbHelper.writableDatabase.insert("contactos", null, valores)
            Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show()

            // Aqui limpia los campos
            findViewById<EditText>(R.id.editTextNombre).setText("")
            findViewById<EditText>(R.id.editTextDireccion).setText("")
            findViewById<EditText>(R.id.editTextTelefono).setText("")
            findViewById<EditText>(R.id.editTextCorreo).setText("")
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun consultarContactos() {
        val cursor: Cursor = dbHelper.readableDatabase.query(
            "contactos",
            null,
            null,
            null,
            null,
            null,
            null
        )

        val stringBuilder = StringBuilder()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            val direccion = cursor.getString(cursor.getColumnIndex("direccion"))
            val telefono = cursor.getString(cursor.getColumnIndex("telefono"))
            val correo = cursor.getString(cursor.getColumnIndex("correo"))
            stringBuilder.append("ID: $id\nNombre: $nombre\nDirección: $direccion\nTeléfono: $telefono\nCorreo: $correo\n\n")
        }
        cursor.close()


        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Lista de Contactos")
        dialog.setMessage(stringBuilder.toString())
        dialog.setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
        dialog.show()
    }

    private fun borrarContacto() {

        val db = dbHelper.writableDatabase
        val cursor = db.query("contactos", arrayOf("id"), null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            db.delete("contactos", "id=?", arrayOf(id.toString()))
            Toast.makeText(this, "Contacto borrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No hay contactos para borrar", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    private fun borrarTodosLosContactos() {
        dbHelper.writableDatabase.delete("contactos", null, null)
        Toast.makeText(this, "Todos los contactos han sido borrados", Toast.LENGTH_SHORT).show()
    }
}
