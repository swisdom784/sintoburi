package com.example.sintoburi;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImageView = findViewById(R.id.productImageView);
        TextView productNameTextView = findViewById(R.id.productNameTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);

        // Intent로 전달된 Product 객체 가져오기
        Product product = (Product) getIntent().getSerializableExtra("product");

        // Product 정보를 UI에 표시
        productNameTextView.setText(product.getName());
        priceTextView.setText(product.getPrice());
        descriptionTextView.setText(product.getDescription());
        Glide.with(this).load(product.getImageUrl()).into(productImageView);
    }
}
