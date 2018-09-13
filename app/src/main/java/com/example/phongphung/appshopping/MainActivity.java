package com.example.phongphung.appshopping;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.model.User;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txtSologan)
    TextView txtSologan;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    DatabaseReference table_User;
    FirebaseDatabase database;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/fontnew.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        FacebookSdk.sdkInitialize(getApplicationContext()); // Init Facebook
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSologan.setTypeface(typeface);

        database = FirebaseDatabase.getInstance();
        table_User = database.getReference("User");

        //init paper
        Paper.init(this);

        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String password = Paper.book().read(Common.PASSWORD_KEY);
        if (user != null && password != null){
            if (!user.isEmpty() && ! password.isEmpty()){
                login(user,password);
            }
        }

    }

    private void login(final String phone, final String password) {
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Vui lòng chờ...");
            mDialog.show();
            table_User.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // check nếu user đã tồn tại
                    if (dataSnapshot.child(phone).exists()) {

                        //Lây thông tin user
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Đăng nhập thành công !!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Mật khẩu không chính xác !!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Tài khoản chưa tồn tại", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Kiểm tra kết nối Internet !!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    @OnClick({R.id.btnSignUp, R.id.btnSignIn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                Intent intentSignUp = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intentSignUp);
                break;
            case R.id.btnSignIn:
                Intent intentSignIn = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intentSignIn);
                break;
        }
    }
}
