package com.example.phongphung.appshopping;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtPhone)
    EditText edtPhone;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    DatabaseReference table_User;
    FirebaseDatabase database;
    @BindView(R.id.edtSecureCode)
    EditText edtSecureCode;

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

        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        table_User = database.getReference("User");
    }

    @OnClick(R.id.btnSignUp)
    public void onViewClicked() {
        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            table_User.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                        mDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        mDialog.dismiss();
                        User user = new User(edtPhone.getText().toString(),
                                edtName.getText().toString(), edtPassword.getText().toString(), edtSecureCode.getText().toString());
                        user.setPhone(edtPhone.getText().toString());
                        table_User.child(edtPhone.getText().toString()).setValue(user);
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        Intent intentSigIn = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentSigIn);
                        finish();
                    }
                    table_User.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Vui lòng kiểm tra kết nối Internet !!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
