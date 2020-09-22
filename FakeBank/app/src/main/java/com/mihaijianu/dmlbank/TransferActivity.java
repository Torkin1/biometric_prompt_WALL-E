package com.mihaijianu.dmlbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;

public class TransferActivity extends AppCompatActivity {
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputSwiftCode;
    private TextInputLayout textInputIBAN;
    private TextInputLayout textInputAmount;
    public double amount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        textInputDescription = findViewById(R.id.text_input_description);
        textInputSwiftCode = findViewById(R.id.text_input_swiftCode);
        textInputIBAN = findViewById(R.id.text_input_IBAN);
        textInputAmount = findViewById(R.id.text_input_amount);



        //biometric mangaer, controllo se puÃ² usare o meno il biometric
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {//userÃ² una costate per vedere le diverse possibilitÃ 
            case BiometricManager.BIOMETRIC_SUCCESS: //successo, permesso fornito
                Toast.makeText(getApplicationContext(), "Biometric request approved, click again to access", Toast.LENGTH_LONG).show();
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
        final BiometricPrompt biometricPrompt = new BiometricPrompt(TransferActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override//chiamato quando ci sono problemi nell'autenticazione
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override//chiamato se ho un successo con l'auttenticazione
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "fingerprint accepted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }

            @Override//chiamato se se ho fallito l'autenticazione
            public void onAuthenticationFailed() {
                // TODO: payment fails, return to main menu
                super.onAuthenticationFailed();
            }
        });

        //Creo il biometric Dialog
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("PayConfirmation")
                .setDescription("Requestd fingerprint to acquire google, a total of 20 bilions")
                .setNegativeButtonText("Cancel")
                .build();

        findViewById(R.id.transfer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateDescription() | !validateSwiftCode() | !validateIBAN())
                    return;
                String temp = textInputAmount.getEditText().getText().toString();
                amount = Integer.parseInt(temp);
                biometricPrompt.authenticate(promptInfo);

            }
        });

    }

    private boolean validateDescription() {
        String descriptionInput = textInputDescription.getEditText().getText().toString().trim();
        if (descriptionInput.isEmpty()) {
            textInputDescription.setError("Field can't be empty");
            return false;
        } else if (descriptionInput.length() > 50) {
            textInputSwiftCode.setError("Description to long too long");
            return false;
        }
        else {
            textInputDescription.setError(null);
//            textInputEmail.setErrorEnabled(true);
            return true;
        }
    }

    private boolean validateSwiftCode() {
        String swiftCodeInput = textInputSwiftCode.getEditText().getText().toString().trim();
        if (swiftCodeInput.isEmpty()) {
            textInputSwiftCode.setError("Field can't be empty");
            return false;
        } else if (swiftCodeInput.length() > 12) {
            textInputSwiftCode.setError("SwiftCode to long too long");
            return false;
        } else {
            textInputSwiftCode.setError(null);
            return true;
        }

    }

    private boolean validateIBAN() {
        String IBANInput = textInputIBAN.getEditText().getText().toString().trim();
        if (IBANInput.isEmpty()) {
            textInputIBAN.setError("Field can't be empty");
            return false;
        } else if (IBANInput.length() > 27) {
            textInputIBAN.setError("Username too long");
            return false;
        } else {
            textInputIBAN.setError(null);
            return true;
        }

    }
}