package com.example.sintoburi;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase 초기화
        FirebaseApp.initializeApp(this);

        // 초기 프래그먼트 표시
        displayFragment(new FruitFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int b = item.getItemId();
                if(b == R.id.navigation_fruit)
                    fragment = new FruitFragment();
                else if(b == R.id.navigation_add_product)
                    fragment = new AddProductFragment();
                else if(b == R.id.navigation_my_page)
                    fragment = new MyPageFragment();
                else if(b == R.id.navigation_chatting)
                    fragment = new ChattingFragment();
                else fragment = null;

                /*switch (item.getItemId()) {
                    case R.id.navigation_fruit:
                        fragment = new FruitFragment();
                        break;
                    case R.id.navigation_add_product:
                        fragment = new AddProductFragment();
                        break;
                    case R.id.navigation_my_page:
                        fragment = new MyPageFragment();
                        break;
                    default:
                        return false;
                }*/
                displayFragment(fragment);
                return true;
            }
        });
    }

    private void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
