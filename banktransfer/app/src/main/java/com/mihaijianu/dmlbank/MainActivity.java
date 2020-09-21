package com.mihaijianu.dmlbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;

import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    TextView username, walletuser;
    int currentBudget = 98;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //costumizing font
//        username = findViewById(R.id.username);
//        walletuser = findViewById(R.id.walletuser);
//
//        //import font
//        Typeface Alien = Typeface.createFromAsset(getAssets(),"fonts/AlienWars-3V3M.ttf" );
//        Typeface AlienItalic = Typeface.createFromAsset(getAssets(),"fonts/AlienWarsItalic-xWDO.ttf" );
//
//        //usefont
//        username.setTypeface(AlienItalic);
//        walletuser.setTypeface(Alien);

        //creating the chart
        //Todo creare il grafico, è uno standard, vanno aggiunte le variabili
        PieChart pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> visitors = new ArrayList<>();

// creato un array che comporra i dati del grafico a torta, con dati passati e l'ultimo da aggiornare,
        //con le entrate mensili/giornaliere
        visitors.add(new PieEntry(67, "2016"));
        visitors.add(new PieEntry(75, "2017"));
        visitors.add(new PieEntry(90, "2018"));
        visitors.add(new PieEntry(96, "2019"));
        visitors.add(new PieEntry(currentBudget, "2020"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Bilions each Year");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(13f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Bilions each Year");
        pieChart.animate();

        //background animation
        ConstraintLayout constraintLayout = findViewById(R.id.mainlayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        //biometric mangaer, controllo se può usare o meno il biometric
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {//userò una costate per vedere le diverse possibilità
            case BiometricManager.BIOMETRIC_SUCCESS: //successo, permesso fornito
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE://no hardware
                Toast.makeText(getApplicationContext(), "No Hardware", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE://no hardware disponibile
                Toast.makeText(getApplicationContext(), "Hardware unvailable", Toast.LENGTH_LONG).show();
                //Todo bottone che varia
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED://no hardware
                Toast.makeText(getApplicationContext(), "No fingerPrints saved", Toast.LENGTH_LONG).show();
                break;
        }

        //biometric dialog box
        //creazione executer
        Executor executor = ContextCompat.getMainExecutor(this);

        //biometricPrompt
        final BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override//chiamato quando ci sono problemi nell'autenticazione
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override//chiamato se ho un successo con l'auttenticazione
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "fingerprint accepted", Toast.LENGTH_LONG).show();
//Todo aggiornare il grafico, esempio sotto
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(getIntent());
//                overridePendingTransition(0, 0);


                PieChart pieChart = findViewById(R.id.pieChart);
                ArrayList<PieEntry> visitors = new ArrayList<>();


                visitors.add(new PieEntry(67, "2016"));
                visitors.add(new PieEntry(75, "2017"));
                visitors.add(new PieEntry(90, "2018"));
                visitors.add(new PieEntry(96, "2019"));
                visitors.add(new PieEntry(78, "2020"));

                PieDataSet pieDataSet = new PieDataSet(visitors, "Bilions each Year");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextSize(13f);
                pieDataSet.setValueTextColor(Color.BLACK);

                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText("Bilions each Year");
                pieChart.animate();


            }

            @Override//chiamato se se ho fallito l'autenticazione
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        //Creo il biometric Dialog
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("PayConfirmation")
                .setDescription("Requestd fingerprint to acquire google, a total of 20 bilions")
                .setNegativeButtonText("Cancel")
                .build();

        findViewById(R.id.pay_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
                //Todo redirige con
            }
        });

    }
}