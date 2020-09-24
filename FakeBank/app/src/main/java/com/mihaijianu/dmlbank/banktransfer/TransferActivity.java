package com.mihaijianu.dmlbank.banktransfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mihaijianu.dmlbank.R;
import com.mihaijianu.dmlbank.entities.Account;
import com.spark.submitbutton.SubmitButton;

public class TransferActivity extends AppCompatActivity {

    // onClick listener for bTransfer
    private class onBTransferClickListener implements View.OnClickListener{

        // Callback for biometricPrompt used by onBTransferClick.onClick
        private class BiometricPromptCallback extends BiometricPrompt.AuthenticationCallback {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                Toast.makeText(TransferActivity.this, String.format("%s", errString), Toast.LENGTH_SHORT).show();
                Log.e(this.getClass().getName() + ".onAuthenticationError", String.format("%s (%d)", errString, errorCode));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                // does the transfer
                try {
                    BankTransferController.getReference().pay(Account.getReference(),
                            Integer.parseInt(TransferActivity
                                    .this
                                    .holder
                                    .textInputAmount
                                    .getEditText()
                                    .getText()
                                    .toString()
                            )
                    );
                    Toast.makeText(TransferActivity.this, R.string.TRANSFER_SUCCESS, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (InsufficentBalanceException e) {
                    Toast.makeText(TransferActivity.this, R.string.TRANSFER_FAILED_INSUFFICIENT_FUNDS, Toast.LENGTH_LONG).show();
                } catch (NullPointerException | NumberFormatException e) {
                    Toast.makeText(TransferActivity.this, R.string.TRANSFER_FAILED_CANT_READ_AMOUNT_FROM_GUI, Toast.LENGTH_LONG).show();
                    Log.e(this.getClass().getName() + ".onAuthenticationSucceded", getString(R.string.TRANSFER_FAILED_CANT_READ_AMOUNT_FROM_GUI), e);
                }
            }
        }

        private boolean isNonBiometricAuthAvailable = false;

        private void askForBiometricInput(){
            // asks for biometric input
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.BIOMETRIC_PROMPT_TITLE))
                    .setSubtitle(getString(R.string.BIOMETRIC_PROMPT_SUBTITLE))
                    .setDeviceCredentialAllowed(isNonBiometricAuthAvailable)   // enables user to authenticate using other auth methods (PIN, password, pattern, ...) if there are any enrolled. Please note that you can't call setNegativeButtonText along with this method
                    // .setNegativeButtonText(getString(R.string.BIOMETRIC_PROMPT_NEGATIVE_TEXT))
                    .build();
            BiometricPrompt biometricPrompt = new BiometricPrompt(TransferActivity.this,
                    ContextCompat.getMainExecutor(TransferActivity.this),
                    new onBTransferClickListener.BiometricPromptCallback());
            biometricPrompt.authenticate(promptInfo);
        }

        private void cancelTransfer(){
            (new AlertDialog.Builder(TransferActivity.this))
                    .setTitle(R.string.NEITHER_BIOMETRIC_NOR_NONBIOMETRIC_ALERT_TITLE)
                    .setMessage(R.string.NEITHER_BIOMETRIC_NOR_NONBIOMETRIC_ALERT_MSG)
                    .setPositiveButton(R.string.PROMPT_OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        
        private void askForBioOnlyIfNonBioIsAvailable(){
            // if non biometric input is available allows transfer, cancels it otherwise.
            if (isNonBiometricAuthAvailable){
                askForBiometricInput();
            } else {
                cancelTransfer();
            }
        }

        // onClick
        @Override
        public void onClick(View v) {

            // checks if device is secured via non-biometric authentication errors

            try {
                isNonBiometricAuthAvailable = ((KeyguardManager) TransferActivity.this.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceSecure();
            } catch (NullPointerException e){
                Toast.makeText(TransferActivity.this, R.string.FAILED_TO_CHECK_NON_BIOMETRIC_AUTH, Toast.LENGTH_LONG).show();
                Log.e(this.getClass().getName() + ".onClick", getString(R.string.FAILED_TO_CHECK_NON_BIOMETRIC_AUTH), e);
            }
            Log.d(this.getClass().getName() + ".onClick", String.format("isNonBiometricAuthAvailable: %s", isNonBiometricAuthAvailable));

            // checks if biometric input is available
            switch (TransferActivity.this.biometricManager.canAuthenticate()) {//userÃ² una costate per vedere le diverse possibilitÃ 
                case BiometricManager.BIOMETRIC_SUCCESS: {
                    askForBiometricInput();
                    break;
                }

                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: {
                    // Prompts user to enroll a biometric model
                    (new AlertDialog.Builder(TransferActivity.this))
                            .setTitle(R.string.ALERT_NO_BIOMETRIC_ENROLLED_TITLE)
                            .setMessage(R.string.ALERT_NO_BIOMETRIC_ENROLLED_MSG)
                            .setPositiveButton(R.string.ALERT_BIOMETRIC_NONE_ENROLLED_POSITIVE, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Starts activity for biometric model enrollment
                                    Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                                    startActivity(enrollIntent);
                                }
                            })
                            .setNegativeButton(R.string.ALERT_BIOMETRIC_NONE_ENROLLED_NEGATIVE, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   askForBioOnlyIfNonBioIsAvailable();
                                }
                            })
                            .create()
                            .show();
                    break;
                }
                // if biometric hw is not present or unavailable, payment should be enabled only if other methods of auth are available
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:{
                    askForBioOnlyIfNonBioIsAvailable();
                    break;
                }
            }


        }
    }

    // Holder
    private class Holder{
        private TextInputLayout textInputAmount;
        private SubmitButton bTransfer;

        private Holder() {
            this.textInputAmount = findViewById(R.id.text_input_amount);
            this.bTransfer = findViewById(R.id.transfer_button);
            this.bTransfer.setOnClickListener(new onBTransferClickListener());
        }

    }

    // activity
    public double amount = 0;
    private BiometricManager biometricManager;
    private Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // sets up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        this.biometricManager = BiometricManager.from(this);
        holder = new Holder();
    }


}