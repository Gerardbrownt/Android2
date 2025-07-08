package com.example.proyecto3;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {

    ListView listUsuarios;
    DBHelper dbHelper;
    ArrayList<Usuario> listaUsuarios;
    ArrayAdapter<String> adapter;
    ArrayList<String> nombresMostrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listUsuarios = findViewById(R.id.listUsuarios);
        dbHelper = new DBHelper(this);

        mostrarUsuarios();

        listUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            Usuario seleccionado = listaUsuarios.get(position);
            mostrarDialogoEditarOEliminar(seleccionado);
        });
    }

    private void mostrarUsuarios() {
        listaUsuarios = new ArrayList<>();
        nombresMostrados = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM usuarios", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

                Usuario usuario = new Usuario(id, nombre, email);
                listaUsuarios.add(usuario);
                nombresMostrados.add(id + " - " + nombre + " (" + email + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombresMostrados);
        listUsuarios.setAdapter(adapter);
    }

    private void mostrarDialogoEditarOEliminar(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar usuario");

        // Campos de entrada
        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre");
        inputNombre.setText(usuario.getNombre());

        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setText(usuario.getEmail());

        // Layout vertical para mostrar ambos campos
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.addView(inputNombre);
        layout.addView(inputEmail);
        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = inputNombre.getText().toString();
            String nuevoEmail = inputEmail.getText().toString();
            actualizarUsuario(usuario.getId(), nuevoNombre, nuevoEmail);
        });

        builder.setNegativeButton("Cancelar", null);

        builder.setNeutralButton("Eliminar", (dialog, which) -> {
            confirmarEliminarUsuario(usuario.getId());
        });

        builder.show();
    }

    private void actualizarUsuario(int id, String nuevoNombre, String nuevoEmail) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nuevoNombre);
        values.put("email", nuevoEmail);

        int filas = db.update("usuarios", values, "id = ?", new String[]{String.valueOf(id)});
        if (filas > 0) {
            Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            mostrarUsuarios();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarUsuario(int id) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle("¿Eliminar usuario?");
        confirmDialog.setMessage("¿Estás seguro de que deseas eliminar este usuario?");
        confirmDialog.setPositiveButton("Sí, eliminar", (dialog, which) -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int filas = db.delete("usuarios", "id = ?", new String[]{String.valueOf(id)});
            if (filas > 0) {
                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                mostrarUsuarios();
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
        confirmDialog.setNegativeButton("Cancelar", null);
        confirmDialog.show();
    }
}
