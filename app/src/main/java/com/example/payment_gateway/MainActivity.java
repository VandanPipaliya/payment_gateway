package com.example.payment_gateway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
EditText amount,name,note,upiid;
Button pay;
final int UPI_PAYMENT=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        methodinitialize();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amounttxt=amount.getText().toString();
                String notetxt=note.getText().toString();
                String nametxt=name.getText().toString();
                String upitxt=upiid.getText().toString();
                payUsingupi(amount, upiid, name, note);
            }
        });
    }
    void methodinitialize() {
        pay=(Button)findViewById(R.id.button);
        name=(EditText) findViewById(R.id.name);
        amount=(EditText)findViewById(R.id.amount);
        note=(EditText)findViewById(R.id.note);
        upiid=(EditText)findViewById(R.id.upi_id);
    }
private void payUsingupi(EditText amounttxt, EditText notetxt, EditText nametxt, EditText upitxt){
        Uri uri= Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", String.valueOf(upiid))
                .appendQueryParameter("pn", String.valueOf(name))
                .appendQueryParameter("tn", String.valueOf(note))
                .appendQueryParameter("am", String.valueOf(amount))
                .appendQueryParameter("cu","INR")
                .build();
        Intent upi_payment=new Intent(Intent.ACTION_VIEW);
        upi_payment.setData(uri);
        Intent chooser=Intent.createChooser(upi_payment,"pay with");
        if(null!=chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser,UPI_PAYMENT);
        }
        else{
            Toast.makeText(this,"No Upi App Found,please install one to continue",Toast.LENGTH_SHORT).show();
        }
}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode || (resultCode == 11))) {
                    if (data != null) {
                        String txt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult:" + txt);
                        ArrayList<String> dataLst = new ArrayList<>();
                        dataLst.add(txt);
                        upipaymentdataoperation(dataLst);
                    } else {
                        Log.d("UPI", "onActivityResult:" + "Return data is null");
                        ArrayList<String> dataLst = new ArrayList<>();
                        dataLst.add("Nothing");
                        upipaymentdataoperation(dataLst);
                    }
                } else {
                    Log.d("UPI", "onActivityResult:" + "Return data is null");
                    ArrayList<String> dataLst = new ArrayList<>();
                    dataLst.add("Nothing");
                    upipaymentdataoperation(dataLst);
                }
                break;
        }
    }

    private void upipaymentdataoperation(ArrayList<String> data) {
        if(isConnectionAvaliable(MainActivity.this)){
            String str=data.get(0);
            Log.d("UPIPAY","upipaymentoperation:"+str);
            String paymentCancel="";
            if(str==null)str="discard";
            String status="";
            String approvalref="";
            String response[]=str.split("&");
            for (int i=0;i<response.length;i++){
                String equalStr[]=response[i].split("=");
                if(equalStr.length>=2){
                    if (equalStr[0].toLowerCase().equals("status".toLowerCase())){
                        status=equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("approval Ref".toLowerCase())||
                    equalStr[0].toLowerCase().equals("approval Ref".toLowerCase())){
                        approvalref=equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancel by user";
                }
            }
                    if (status.equals("success")){
                        Toast.makeText(this,"Transcation Success",Toast.LENGTH_SHORT).show();
                        Log.d("UPI","responsestr"+approvalref);
                    }
                    else if("payment cancel by user".equals(paymentCancel)){
                        Toast.makeText(this,"Payment cancel by user",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this,"Transcation failed.Please try again",Toast.LENGTH_SHORT).show();
                    }

        }else{
            Toast.makeText(this,"Internet connection is not available.Please check",Toast.LENGTH_SHORT).show();

        }
    }

    private static boolean isConnectionAvaliable(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()&& networkInfo.isConnectedOrConnecting()&& networkInfo.isAvailable()){
                return true;
            }

        }
        return false;
    }
}