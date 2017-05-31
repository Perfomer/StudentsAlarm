package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import volkovmedia.perfo.studentsalarm.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Intent mStartMainActivity;

    private EditText etLogin, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setResult(RESULT_OK);
                    LoginActivity.this.onBackPressed();
                }
            }
        };

        mStartMainActivity = new Intent(this, MainActivity.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        startActivity(mStartMainActivity);
    }


    public void onLoginButtonClick(View v) {
        String fields[] = checkFields();
        if (fields == null) return;

        mAuth.signInWithEmailAndPassword(fields[0], fields[1])
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) Toast.makeText(LoginActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                        else {
                            setResult(RESULT_OK);
                            LoginActivity.this.finish();
                        }
                    }
                });
    }

    public String[] checkFields() {
        String login = etLogin.getText().toString(),
                password = etPassword.getText().toString();

        boolean error = false;

        if (login.isEmpty()) {
            etLogin.setError("Не введён логин");
            error = true;
        }
        if (password.isEmpty()) {
            etPassword.setError("Не введён пароль");
            error = true;
        }
        else if (password.length() < 8) {
            etPassword.setError("Пароль должен быть не менее 8 символов");
            error = true;
        }

        if (error) return null;
        return new String[]{login, password};
    }

    public void onSignUpButtonClick(View v) {
        String fields[] = checkFields();
        if (fields == null) return;

        mAuth.createUserWithEmailAndPassword(fields[0], fields[1])
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Пользователь успешно зарегистрирован", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            LoginActivity.this.finish();
                        }
                    }
                });
    }

}
