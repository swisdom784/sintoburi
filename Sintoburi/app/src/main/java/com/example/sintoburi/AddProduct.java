package com.example.sintoburi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AddProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText;
    private EditText priceEditText;
    private ImageView photoImageView;
    private ProgressBar progressBar;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_product);

        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        photoImageView = findViewById(R.id.photoImageView);
        progressBar = findViewById(R.id.progressBar);

        Button imageAddButton = findViewById(R.id.imageAddButton);
        Button submitButton = findViewById(R.id.submitButton);

        imageAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadItem();
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
            photoImageView.setImageURI(imageUri);
        }
    }

    private void uploadItem() {
        String title = titleEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("글 제목을 입력하세요");
            titleEditText.requestFocus();
            return;
        }

        if (price.isEmpty()) {
            priceEditText.setError("가격을 입력하세요");
            priceEditText.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "이미지를 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 여기에 등록 처리 로직을 추가할 수 있습니다.
        // 예를 들어, 이미지 업로드, 데이터베이스에 정보 저장 등을 수행합니다.

        // 업로드 과정을 시뮬레이션하기 위해 ProgressBar를 보여줍니다.
        progressBar.setVisibility(View.VISIBLE);

        // 업로드 완료 후 ProgressBar를 숨깁니다.
        progressBar.setVisibility(View.GONE);

        // 예시로 간단히 Toast 메시지를 띄워 사용자에게 완료를 알립니다.
        Toast.makeText(this, "아이템이 성공적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
    }
}
