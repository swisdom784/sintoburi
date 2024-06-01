package com.example.sintoburi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyWish extends Fragment {
    private RecyclerView recyclerView;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private TextView noProductTextView;
    private DatabaseReference productsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myproduct, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(productAdapter);
        noProductTextView = view.findViewById(R.id.noProductTextView);

        // 현재 사용자 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // 사용자의 등록 상품 가져오기
        productsRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(currentUser.getUid()).child("wishlistProducts");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear(); // 기존 상품 제거

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String productId = ds.getKey();
                        if (productId != null) {
                            loadProduct(productId);
                        }
                    }
                    noProductTextView.setVisibility(View.GONE); // 상품이 있으면 메시지 숨기기
                } else {
                    productList.clear();
                    productAdapter.notifyDataSetChanged();
                    showNoProductsMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "상품 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadProduct(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                        productAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "상품 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoProductsMessage() {
        noProductTextView.setVisibility(View.VISIBLE); // 상품이 없을 때 메시지 보이기
    }
}


