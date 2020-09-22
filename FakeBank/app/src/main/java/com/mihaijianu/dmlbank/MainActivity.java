package com.mihaijianu.dmlbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
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
import com.mihaijianu.dmlbank.entities.Account;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    TextView username, walletuser;

    Account account = Account.getReference();
    String user = account.getUsername();
    int currentBudget = account.getBalance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        username.setText(user);

        //creating the chart
        //Todo: creare il grafico, Ã¨ uno standard, vanno aggiunte le variabili
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


        findViewById(R.id.pay_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TransferActivity.class));
            }
        });

    }
}