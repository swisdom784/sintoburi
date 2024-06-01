package com.example.sintoburi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddProductFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

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

        Button galleryButton = view.findViewById(R.id.galleryButton);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        Button submitButton = view.findViewById(R.id.submitButton);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
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

    private void openCamera() {
        Log.d("AddProduct", "openCamera: Checking permissions");
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, CAMERA_PERMISSION_REQUEST_CODE);
        }
        Log.d("AddProduct", "openCamera: Permissions granted, opening camera");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            Log.e("AddProduct", "openCamera: No camera app found");
            Toast.makeText(getContext(), "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // 이미지 파일명 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return imageFile;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("AddProduct", "onRequestPermissionsResult: Permission granted, opening camera");
                openCamera();
            } else {
                Log.e("AddProduct", "onRequestPermissionsResult: Permission denied");
                Toast.makeText(getContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("AddProduct", "onActivityResult: requestCode " + requestCode + ", resultCode " + resultCode);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Log.d("AddProduct", "onActivityResult: Image captured from camera");
                // 이미지를 비트맵으로 디코딩하지 않고 바로 이미지뷰에 설정
                photoImageView.setImageURI(imageUri);
            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Log.d("AddProduct", "onActivityResult: Image picked from gallery");
                imageUri = data.getData();
                photoImageView.setImageURI(imageUri);
            } else {
                Log.e("AddProduct", "onActivityResult: Unknown requestCode or data is null");
            }
        } else {
            Log.e("AddProduct", "onActivityResult: Result not OK");
        }
    }

    private void uploadItem() {
        final String title = titleEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String tag = tags[tagSpinner.getSelectedItemPosition()]; // 선택된 태그

        // 입력값 검증
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
            final Product product = new Product(title, price, null, description, tag, user.getEmail(), currentTime, 0);

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
                                    String productId = mDatabase.child("Products").push().getKey();
                                    mDatabase.child("Products").child(productId).setValue(product)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // 상품 정보 저장 성공
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "상품이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();

                                                    // 유저의 상품 목록에 상품 ID 추가
                                                    mDatabase.child("UserAccount").child(user.getUid()).child("uploadedProducts").child(productId).setValue(true)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // 유저의 상품 목록에 추가 성공
                                                                    getFragmentManager().popBackStack(); // 이전 프래그먼트로 돌아가기
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // 유저의 상품 목록에 추가 실패
                                                                    Toast.makeText(getContext(), "유저 상품 목록 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                                    Log.e("Firebase", "유저 상품 목록 업데이트 실패: " + e.getMessage());
                                                                }
                                                            });
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