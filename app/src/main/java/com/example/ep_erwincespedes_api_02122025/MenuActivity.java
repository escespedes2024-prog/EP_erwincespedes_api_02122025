package com.example.ep_erwincespedes_api_02122025;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        CardView cardProducts = findViewById(R.id.card_products);
        CardView cardAbout = findViewById(R.id.card_about);
        Button btnExit = findViewById(R.id.btn_exit);

        // Ir a la lista de productos (CRUD)
        cardProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Acerca de (puedes implementar una actividad de información)
        cardAbout.setOnClickListener(v -> {
            // Mostrar información de la app
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Acerca de")
                    .setMessage("Aplicación CRUD de Productos\n\nDesarrollada por: Erwin Céspedes\nFecha: 02/12/2025\n\nUtiliza FakeStore API para gestionar productos.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Salir de la aplicación
        btnExit.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
    }
}