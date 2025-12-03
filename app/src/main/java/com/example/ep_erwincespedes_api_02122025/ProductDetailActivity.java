package com.example.ep_erwincespedes_api_02122025;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private TextView tvTitle, tvPrice, tvCategory, tvDescription, tvRating;
    private ProgressBar progressBar;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalle del Producto");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivProduct = findViewById(R.id.iv_product_detail);
        tvTitle = findViewById(R.id.tv_title_detail);
        tvPrice = findViewById(R.id.tv_price_detail);
        tvCategory = findViewById(R.id.tv_category_detail);
        tvDescription = findViewById(R.id.tv_description_detail);
        tvRating = findViewById(R.id.tv_rating_detail);
        progressBar = findViewById(R.id.progress_bar);

        productId = getIntent().getIntExtra("product_id", -1);

        if (productId != -1) {
            loadProductDetail(productId);
        } else {
            Toast.makeText(this, "ID de producto inválido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductDetail(int id) {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApiService().getProduct(id)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            Product product = response.body();
                            displayProduct(product);
                        } else {
                            Toast.makeText(ProductDetailActivity.this,
                                    "Error al cargar producto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProductDetailActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayProduct(Product product) {
        tvTitle.setText(product.getTitle());
        tvPrice.setText("$" + String.format("%.2f", product.getPrice()));
        tvCategory.setText("Categoría: " + product.getCategory());
        tvDescription.setText(product.getDescription());

        if (product.getRating() != null) {
            tvRating.setText(String.format("⭐ %.1f (%d reseñas)",
                    product.getRating().getRate(),
                    product.getRating().getCount()));
        } else {
            tvRating.setText("Sin calificaciones");
        }

        Glide.with(this)
                .load(product.getImage())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(ivProduct);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}