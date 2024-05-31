package com.example.sintoburi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class AddProduct extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText;
    private EditText priceEditText;
    private EditText descriptionEditText;
    private ImageView photoImageView;
    private ProgressBar progressBar;
    private Spinner tagSpinner;

    private Uri imageUri;

    private String[] tags = {"과일", "채소", "수산물", "육류", "간식",
            "생필품", "식물", "건강식품", "기타"};

    // Firebase Database 및 Storage
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Firebase 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        titleEditText = view.findViewById(R.id.titleEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        photoImageView = view.findViewById(R.id.photoImageView);
        progressBar = view.findViewById(R.id.progressBar);
        tagSpinner = view.findViewById(R.id.tagSpinner);

        // 태그 스피너에 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tags);
        tagSpinner.setAdapter(adapter);

        Button imageAddButton = view.findViewById(R.id.imageAddButton);
        Button submitButton = view.findViewById(R.id.submitButton);

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

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            photoImageView.setImageURI(imageUri);
        }
    }

    private void uploadItem() {
        final String title = titleEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String tag = tags[tagSpinner.getSelectedItemPosition()]; // 선택된 태그

        if (title.isEmpty()) {
            titleEditText.setError("상품 이름을 입력하세요");
            titleEditText.requestFocus();
            return;
        }

        if (price.isEmpty()) {
            priceEditText.setError("가격(원)을 입력하세요");
            priceEditText.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError("설명을 입력하세요");
            descriptionEditText.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(getContext(), "이미지를 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // 현재 시간 가져오기
            long currentTime = new Date().getTime();

            // 상품 객체 생성
            final Product product = new Product(title, price, null, description, tag, user.getEmail(), currentTime, new ArrayList<String>(), 0);

            // 업로드 과정을 시뮬레이션하기 위해 ProgressBar를 보여줍니다.
            progressBar.setVisibility(View.VISIBLE);

            // UUID를 사용하여 이미지 파일명 생성
            final String imageFileName = UUID.randomUUID().toString() + ".jpg";
            final StorageReference imageRef = mStorage.child("images/" + imageFileName);

            // 이미지를 Storage에 업로드
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // 이미지 업로드 성공 후, 다운로드 URL을 가져와 Product 객체에 설정
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // 이미지 URL 설정
                                    product.setImageUrl(uri.toString());
                                    Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_SHORT).show();

                                    // 상품 정보 데이터베이스에 저장
                                    mDatabase.child("Products").child(mDatabase.push().getKey()).setValue(product)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // 상품 정보 저장 성공
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "상품이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                                    getFragmentManager().popBackStack(); // 이전 프래그먼트로 돌아가기
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // 상품 정보 저장 실패
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "상품 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                    Log.e("Firebase", "상품 정보 저장 실패: " + e.getMessage());
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 이미지 URL 가져오기 실패
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "이미지 URL을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "이미지 URL 가져오기 실패: " + e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 이미지 업로드 실패
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "이미지 업로드 실패: " + e.getMessage());
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(getContext(), "사용자 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
