package com.example.ep_erwincespedes_api_02122025;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditProductActivity extends AppCompatActivity {

    private EditText etTitle, etPrice, etDescription, etImage;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ProgressBar progressBar;

    private boolean isEditMode = false;
    private int productId = -1;
    private List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        etImage = findViewById(R.id.et_image);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);

        // Verificar si es modo edición
        if (getIntent().hasExtra("edit_mode")) {
            isEditMode = getIntent().getBooleanExtra("edit_mode", false);
            productId = getIntent().getIntExtra("product_id", -1);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Producto");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            if (productId != -1) {
                loadProductData(productId);
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nuevo Producto");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        loadCategories();

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void loadCategories() {
        RetrofitClient.getInstance().getApiService().getCategories()
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categories = response.body();
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    AddEditProductActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    categories
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategory.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Toast.makeText(AddEditProductActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProductData(int id) {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApiService().getProduct(id)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            Product product = response.body();
                            etTitle.setText(product.getTitle());
                            etPrice.setText(String.valueOf(product.getPrice()));
                            etDescription.setText(product.getDescription());
                            etImage.setText(product.getImage());

                            // Seleccionar la categoría en el spinner
                            if (categories != null) {
                                int position = categories.indexOf(product.getCategory());
                                if (position >= 0) {
                                    spinnerCategory.setSelection(position);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddEditProductActivity.this, "Error al cargar producto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProduct() {
        String title = etTitle.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String image = etImage.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = new Product(title, price, description, category, image);

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        if (isEditMode && productId != -1) {
            product.setId(productId);
            updateProduct(product);
        } else {
            createProduct(product);
        }
    }

    private void createProduct(Product product) {
        RetrofitClient.getInstance().getApiService().createProduct(product)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(AddEditProductActivity.this,
                                    "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEditProductActivity.this,
                                    "Error al crear producto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(AddEditProductActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProduct(Product product) {
        RetrofitClient.getInstance().getApiService().updateProduct(productId, product)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(AddEditProductActivity.this,
                                    "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddEditProductActivity.this,
                                    "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(AddEditProductActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}