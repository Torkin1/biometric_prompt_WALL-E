package com.mihaijianu.dmlbank.banktransfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mihaijianu.dmlbank.R;
import com.mihaijianu.dmlbank.entities.Account;

public class TransferActivity extends AppCompatActivity {

    // onClick listener for bTransfer
    private class onBTransferClickListener implements View.OnClickListener{

        // Callback for biometricPrompt used by onBTransferClick.onClick
        private class BiometricPromptCallback extends BiometricPrompt.AuthenticationCallback {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                Toast.makeText(TransferActivity.this, String.format(getString(R.string.TRANSFER_FAILED), errString, errorCode), Toast.LENGTH_SHORT).show();
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
                }
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(TransferActivity.this, R.string.TRANSFER_FAILED_AUTH_FAILED, Toast.LENGTH_LONG).show();
            }
        }

        // onClick
        @Override
        public void onClick(View v) {

            // checks if device is secured via non-biometric authentication errors
            boolean isNonBiometricAuthAvailable;
            try {
                isNonBiometricAuthAvailable = ((KeyguardManager) TransferActivity.this.getSystemService(Context.KEYGUARD_SERVICE)).isDeviceSecure();
            } catch (NullPointerException e){
                Toast.makeText(TransferActivity.this, R.string.FAILED_TO_CHECK_NON_BIOMETRIC_AUTH, Toast.LENGTH_LONG).show();
                isNonBiometricAuthAvailable = false;
            }

            // checks if biometric input is available
            switch (TransferActivity.this.biometricManager.canAuthenticate()) {//userÃ² una costate per vedere le diverse possibilitÃ 
                case BiometricManager.BIOMETRIC_SUCCESS: {
                    break;
                }
                // if biometric hw is not present or unavailable, payment should be enabled only if other methods of auth are available
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:{
                    if (!isNonBiometricAuthAvailable) {
                        // No auth methods available, disable transfers
                        (new AlertDialog.Builder(TransferActivity.this))
                                .setTitle(R.string.NEITHER_BIOMETRIC_NOR_NONBIOMETRIC_ALERT_TITLE)
                                .setMessage(R.string.NEITHER_BIOMETRIC_NOR_NONBIOMETRIC_ALERT_MSG)
                                .create()
                                .show();
                        finish();
                    }
                    break;
                }
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: {
                    // Prompts user to enroll a biometric model
                    (new AlertDialog.Builder(TransferActivity.this))
                            .setTitle(R.string.NO_BIOMETRIC_ENROLLED_ALERT_TITLE)
                            .setMessage(R.string.NO_BIOMETRIC_ENROLLED_ALERT_MSG)
                            .create()
                            .show();
                    // Starts activity for biometric model enrollment
                    Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                    startActivity(enrollIntent);
                    break;
                }
            }

            // asks for biometric input
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.BIOMETRIC_PROMPT_TITLE))
                    .setSubtitle(getString(R.string.BIOMETRIC_PROMPT_SUBTITLE))
                    .setDeviceCredentialAllowed(isNonBiometricAuthAvailable)   // enables user to authenticate using other auth methods (PIN, password, pattern, ...) if there are any enrolled. Please note that you can't call setNegativeButtonText along with this method
                    .build();
            BiometricPrompt biometricPrompt = new BiometricPrompt(TransferActivity.this,
                    ContextCompat.getMainExecutor(TransferActivity.this),
                    new BiometricPromptCallback());
            biometricPrompt.authenticate(promptInfo);
        }
    }

    // Holder
    private class Holder{
        private TextInputLayout textInputAmount;
        private Button bTransfer;

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
        Holder holder = new Holder();
    }
}