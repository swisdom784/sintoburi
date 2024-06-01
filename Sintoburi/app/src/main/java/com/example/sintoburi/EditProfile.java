package com.example.sintoburi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends Fragment {

    private EditText nicknameEditText, currentPasswordEditText, newPasswordEditText;
    private Button updateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);

        nicknameEditText = view.findViewById(R.id.nicknameEditText);
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        updateButton = view.findViewById(R.id.updateButton);

        // 사용자 정보 불러오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

        // 기존 닉네임 세팅
        String currentNickname = currentUser.getDisplayName();
        nicknameEditText.setText(currentNickname);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNickname = nicknameEditText.getText().toString().trim();
                String currentPassword = currentPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                // 기존 비밀번호를 맞추지 못할 경우 처리
                if (TextUtils.isEmpty(currentPassword)) {
                    currentPasswordEditText.setError("기존 비밀번호를 입력하세요.");
                    currentPasswordEditText.requestFocus();
                    return;
                }

                // 기존 비밀번호로 인증
                AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
                currentUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // 인증 성공, 닉네임 업데이트
                                    if (!TextUtils.isEmpty(newNickname)) {
                                        updateNickname(currentUser, newNickname);
                                    }

                                    // 비밀번호 업데이트
                                    if (!TextUtils.isEmpty(newPassword)) {
                                        updatePassword(currentUser, newPassword);
                                    }

                                } else {
                                    // 인증 실패
                                    Toast.makeText(getContext(), "기존 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }

    private void updateNickname(FirebaseUser user, String newNickname) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newNickname)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                            updateUserInDatabase(user.getUid(), newNickname);
                            // 이전 페이지로 돌아가기
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "프로필 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "비밀번호가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "비밀번호 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserInDatabase(String userId, String newNickname) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userId);

        userRef.child("nickname").setValue(newNickname)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "사용자 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "사용자 정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
