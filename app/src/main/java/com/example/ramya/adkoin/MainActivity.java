package com.example.ramya.adkoin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE=999;

    Button mobile,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printKeyHash();

        mobile=(Button)findViewById(R.id.mobile);
        email=(Button)findViewById(R.id.email);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage(LoginType.EMAIL);
            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage(LoginType.PHONE);
            }
        });
    }

    private void startLoginPage(LoginType loginType) {

        if (loginType==LoginType.EMAIL){

            Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.EMAIL,
                            AccountKitActivity.ResponseType.TOKEN);

            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());

            startActivityForResult(intent,REQUEST_CODE);
        }
        else if (loginType==LoginType.PHONE){
            Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                            AccountKitActivity.ResponseType.TOKEN);

            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());

            startActivityForResult(intent,REQUEST_CODE);

        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError()!= null){
                Toast.makeText(this,""+result.getError().getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                return;
            }
            else if (result.wasCancelled()){
                Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show();
                return;

            }
            else {

                if (result.getAccessToken()!=null)
                Toast.makeText(this,"Success! + %s"+result.getAccessToken().getAccountId(),Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Success! +%s"+result.getAuthorizationCode().substring(0,10),Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this,Success.class));
            }
        }

    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.ramya.adkoin",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature: info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
