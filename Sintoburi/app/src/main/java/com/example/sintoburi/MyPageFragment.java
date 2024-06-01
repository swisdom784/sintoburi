package com.example.sintoburi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPageFragment extends Fragment {
    private TextView usernameTextView, emailTextView;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);

        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        Button manageProductsButton = view.findViewById(R.id.manageProductsButton);
        Button transactionHistoryButton = view.findViewById(R.id.transactionHistoryButton);
        Button addProductButton = view.findViewById(R.id.addProductButton);
        Button wishlistButton = view.findViewById(R.id.wishlistButton);
        Button chatHistoryButton = view.findViewById(R.id.chatHistoryButton);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로필 수정 버튼 클릭 시 동작 구현
                Toast.makeText(getContext(), "프로필 수정 버튼 클릭", Toast.LENGTH_SHORT).show();
                // 프로필 수정 버튼 클릭 시 EditProfile 프래그먼트로 전환
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new EditProfile()); // 수정이 필요한 부분
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        manageProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상품 관리 버튼 클릭 시 동작 구현
                Toast.makeText(getContext(), "상품 관리 버튼 클릭", Toast.LENGTH_SHORT).show();
                // 상품 관리 버튼 클릭 시 MyProductsFragment로 전환
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MyProduct());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        transactionHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 거래 기록 버튼 클릭 시 동작 구현
                Toast.makeText(getContext(), "거래 기록 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상품 등록 버튼 클릭 시 동작 구현
                Toast.makeText(getContext(), "상품 등록", Toast.LENGTH_SHORT).show();
                // 상품 등록 버튼 클릭 시 AddProductFragment로 전환
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new AddProductFragment());

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 찜 버튼 클릭 시 MyWishFragment로 전환
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MyWish());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        chatHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅 기록 버튼 클릭 시 동작 구현
                Toast.makeText(getContext(), "채팅 기록 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // 사용자 정보 로드
            loadUserData(currentUser);
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            Toast.makeText(getContext(), "사용자 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadUserData(FirebaseUser user) {
        String uid = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                if (userAccount != null) {
                    // 유저 정보 설정
                    usernameTextView.setText(userAccount.getNickname());
                    emailTextView.setText(userAccount.getEmail());
                } else {
                    Toast.makeText(getContext(), "사용자 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터베이스에서 유저 정보를 가져오는 중 오류 발생 시 처리
                Toast.makeText(getContext(), "사용자 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

