package com.example.sintoburi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;

public class MyPage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ShapeableImageView profileImageView;
    private Button editProfileButton;
    private Button manageProductsButton;
    private Button transactionHistoryButton;
    private Button addProductButton;
    private Button wishlistButton;
    private Button chatHistoryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mypage);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바에 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 뷰 초기화
        profileImageView = findViewById(R.id.profileImageView);
        editProfileButton = findViewById(R.id.editProfileButton);
        manageProductsButton = findViewById(R.id.manageProductsButton);
        transactionHistoryButton = findViewById(R.id.transactionHistoryButton);
        addProductButton = findViewById(R.id.addProductButton);
        wishlistButton = findViewById(R.id.wishlistButton);
        chatHistoryButton = findViewById(R.id.chatHistoryButton);

        // 프로필 사진 클릭 시 갤러리 열기
        profileImageView.setOnClickListener(v -> openGallery());

        // 클릭 리스너 설정
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPage.this, "내 정보 수정 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });

        manageProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPage.this, "내 상품 관리 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });

        transactionHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPage.this, "내 거래 기록 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPage.this, "상품 등록 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });

        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPage.this, "찜 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });

        chatHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPage.this, "채팅 기록 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // 갤러리에서 선택한 이미지 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                // 선택한 이미지를 프로필 사진으로 설정
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}