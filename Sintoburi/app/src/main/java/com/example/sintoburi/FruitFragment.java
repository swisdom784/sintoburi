package com.example.sintoburi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FruitFragment extends Fragment {

    private DatabaseReference mDatabase;
    private List<Product> productList;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fruit, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Firebase Database 참조
        mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
        productList = new ArrayList<>();

        // 어댑터 설정
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(productAdapter);

        // 데이터 로드
        loadDataFromFirebase();

        // 검색어 변경 이벤트 리스너 설정
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 카테고리 버튼 클릭 이벤트 설정
        setLinearLayoutListener(view, R.id.fruitButton, "과일");
        setLinearLayoutListener(view, R.id.vegetableButton, "채소");
        setLinearLayoutListener(view, R.id.seafoodButton, "수산물");
        setLinearLayoutListener(view, R.id.meatButton, "육류");
        setLinearLayoutListener(view, R.id.sideDishButton, "반찬");
        setLinearLayoutListener(view, R.id.snackButton, "간식");
        setLinearLayoutListener(view, R.id.dailyNecessitiesButton, "생필품");
        setLinearLayoutListener(view, R.id.plantButton, "식물");
        setLinearLayoutListener(view, R.id.healthFoodButton, "건강식품");
        setLinearLayoutListener(view, R.id.otherButton, "기타");

        return view;
    }

    private void setLinearLayoutListener(View parentView, int layoutId, String category) {
        LinearLayout layout = parentView.findViewById(layoutId);
        layout.setOnClickListener(v -> filterByCategory(category));
    }

    private void loadDataFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }

    private void filterData(String keyword) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter = new ProductAdapter(getContext(), filteredList);
        recyclerView.setAdapter(productAdapter);
    }

    private void filterByCategory(String category) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategory().equals(category)) {
                filteredList.add(product);
            }
        }
        productAdapter = new ProductAdapter(getContext(), filteredList);
        recyclerView.setAdapter(productAdapter);
    }
}
