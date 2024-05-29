package com.example.sintoburi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MyPage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mypage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("마이 페이지");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        // Dummy data for demonstration
        usernameTextView.setText("User123");
        emailTextView.setText("user123@example.com");
        phoneNumberTextView.setText("010-1234-5678");

        Button editProfileButton = findViewById(R.id.editProfileButton);
        Button manageProductsButton = findViewById(R.id.manageProductsButton);
        Button transactionHistoryButton = findViewById(R.id.transactionHistoryButton);
        Button addProductButton = findViewById(R.id.addProductButton);
        Button wishlistButton = findViewById(R.id.wishlistButton);
        Button chatHistoryButton = findViewById(R.id.chatHistoryButton);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit profile logic here
                Toast.makeText(MyPage.this, "Edit Profile", Toast.LENGTH_SHORT).show();
            }
        });

        manageProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manage products logic here
                Toast.makeText(MyPage.this, "Manage Products", Toast.LENGTH_SHORT).show();
            }
        });

        transactionHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Transaction history logic here
                Toast.makeText(MyPage.this, "Transaction History", Toast.LENGTH_SHORT).show();
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add product logic here
                Toast.makeText(MyPage.this, "Add Product", Toast.LENGTH_SHORT).show();
            }
        });

        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wishlist logic here
                Toast.makeText(MyPage.this, "Wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        chatHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chat history logic here
                Toast.makeText(MyPage.this, "Chat History", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Glide를 사용하여 원형 이미지로 로드
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
