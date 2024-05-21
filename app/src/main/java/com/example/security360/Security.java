package com.example.security360;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Security extends AppCompatActivity {
    CardView siren, location, Settings, currentlocation, community, news, aboutUs, shareBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_security );

        Intent backgroundService = new Intent( getApplicationContext(), ScreenOnOffBackgroundService.class );
        this.startService( backgroundService );
        Log.d( ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Activity onCreate" );
        int permissionCheck = ContextCompat.checkSelfPermission (Security.this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission (Security.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission (Security.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {


            final AlertDialog.Builder alert = new AlertDialog.Builder(Security.this);
            View mView = getLayoutInflater().inflate(R.layout.custom_dialog_security,null);

            Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
            TextView heading=mView.findViewById (R.id.heading);
            heading.setText("Security360 needs access to");
            TextView sms=mView.findViewById (R.id.sms);
            sms.setText("Sending SMS:-");
            TextView textView=mView.findViewById (R.id.textFormodal);
            textView.setText ("Emergency messaging needs SEND SMS permission");
            TextView location=mView.findViewById (R.id.location);
            location.setText("Location:-");
            TextView locationText=mView.findViewById (R.id.textLocation);
            locationText.setText("Messaging embedded with live location needs Location permission");
            TextView call=mView.findViewById (R.id.call);
            call.setText("Phone Call:-");
            TextView callText=mView.findViewById (R.id.textCall);
            callText.setText("Emergency Calling needs CALL PHONE permission");
            TextView declaration=mView.findViewById (R.id.declaration);
            declaration.setText("Declaration");
            TextView declaratioText=mView.findViewById (R.id.textDeclaration);
            declaratioText.setText("All data related to this app is stored locally in your phone.");
            CheckBox checkbox = (CheckBox)mView.findViewById(R.id.checkBox);
            TextView checkBoxtext = (TextView)mView.findViewById(R.id.checkBoxText);
            checkbox.setVisibility (View.VISIBLE);
            checkBoxtext.setVisibility (View.VISIBLE);
            checkbox.setEnabled (true);
            checkBoxtext.setEnabled (true);

            checkbox.setText("");
            checkBoxtext.setText(Html.fromHtml("I accept the " +
                    "<a href='https://www.websitepolicies.com/policies/view/IaK4RjyB'>PRIVACY POLICY</a>"+" of the app"));
            checkBoxtext.setClickable(true);
            checkBoxtext.setMovementMethod(LinkMovementMethod.getInstance());
            alert.setView(mView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            btn_okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkbox.isChecked ()) {

                        alertDialog.dismiss ();
                        ActivityCompat.requestPermissions (Security.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE}, 0);
                    }else{
                        Toast.makeText (Security.this,"Please accept privacy policy",Toast.LENGTH_LONG).show ();

                    }
                }
            });
            alertDialog.show();

        }

        /*siren = findViewById( R.id.Siren );
        location = findViewById( R.id.send_Location );*/
        Settings = findViewById( R.id.Settings );
        currentlocation = findViewById( R.id.Currentlocation );
        /*news = findViewById( R.id.News );
        aboutUs = findViewById( R.id.about_us );*/
        // community=findViewById (R.id.community);
        /*siren.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), Flashing.class ) );
            }
        } );*/
        location.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), Instructions.class ) );
            }
        } );
        Settings.setOnClickListener( v -> startActivity( new Intent( getApplicationContext(), SmsActivity.class ) ) );
        currentlocation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), CurrentLocation.class ) );
            }
        } );

    }




}