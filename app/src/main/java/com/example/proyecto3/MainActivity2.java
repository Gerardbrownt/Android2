package com.example.proyecto3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

        EditText etNombre, etEmail;
        Button btnGuardar, btnVerUsuarios;
        DBHelper dbHelper;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            etNombre = findViewById(R.id.etNombre);
            etEmail = findViewById(R.id.etEmail);
            btnGuardar = findViewById(R.id.btnGuardar);
            btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
            dbHelper = new DBHelper(this);

            btnGuardar.setOnClickListener(v -> {
                String nombre = etNombre.getText().toString();
                String email = etEmail.getText().toString();
                guardarUsuario(nombre, email);
            });

            btnVerUsuarios.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity2.this, MainActivity3.class));
            });

        }

        private void guardarUsuario(String nombre, String email) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("email", email);
            db.insert("usuarios", null, values);
            Toast.makeText(this, "Usuario guardado", Toast.LENGTH_SHORT).show();
            etNombre.setText("");
            etEmail.setText("");
    }
}
