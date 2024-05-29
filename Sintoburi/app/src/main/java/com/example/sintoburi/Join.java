package com.example.sintoburi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Join extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    DatabaseReference mDatabaseRef; // 실시간 데이터 베이스
    EditText mNickname, mEmail, mPassword;
    Button mSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mNickname = findViewById(R.id.nickname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mSignup = findViewById(R.id.signup);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strNickname = mNickname.getText().toString();
                String strEmail = mEmail.getText().toString();
                String strPassword = mPassword.getText().toString();

                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(Join.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount userAccount = new UserAccount();
                            userAccount.setNickname(strNickname);
                            userAccount.setEmail(firebaseUser.getEmail());
                            userAccount.setPassword(strPassword);

                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);
                            Toast.makeText(Join.this, "회원가입에 성공했습니다",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(Join.this, "회원가입에 실패했습니다",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}