package com.koenidv.presetmail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

public class MainActivity extends AppCompatActivity {

    private EditText recipientsText, subjectText, bodyText;
    private Button sendButton, addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = getSharedPreferences("preset", MODE_PRIVATE);

        recipientsText = findViewById(R.id.recipientsEditText);
        subjectText = findViewById(R.id.subjectEditText);
        bodyText = findViewById(R.id.bodyEditText);
        sendButton = findViewById(R.id.sendButton);
        addButton = findViewById(R.id.addButton);

        recipientsText.setText(prefs.getString("recipients", ""));
        subjectText.setText(prefs.getString("subject", ""));
        bodyText.setText(prefs.getString("body", ""));

        checkInputEmpty();

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(getApplicationContext()))
            addButton.setText(R.string.button_save);


        recipientsText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                prefs.edit().putString("recipients", s.toString()).apply();
                checkInputEmpty();
            }
        });

        subjectText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                prefs.edit().putString("subject", s.toString()).apply();
                checkInputEmpty();
            }
        });

        bodyText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                prefs.edit().putString("body", s.toString()).apply();
                checkInputEmpty();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send an email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:"))
                        .putExtra(Intent.EXTRA_EMAIL, prefs.getString("recipients", "").split("[,\n]"))
                        .putExtra(Intent.EXTRA_SUBJECT, prefs.getString("subject", ""))
                        .putExtra(Intent.EXTRA_TEXT, prefs.getString("body", ""));
                // Only open if email client is installed
                if (emailIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(emailIntent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(getApplicationContext(), prefs.getString("subject", ""))
                        .setShortLabel(prefs.getString("subject", ""))
                        .setLongLabel(prefs.getString("subject", ""))
                        .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_send_round))
                        .setIntent(new Intent(Intent.ACTION_SENDTO)
                                .setData(Uri.parse("mailto:"))
                                .putExtra(Intent.EXTRA_EMAIL, prefs.getString("recipients", "").split("[,\n]"))
                                .putExtra(Intent.EXTRA_SUBJECT, prefs.getString("subject", ""))
                                .putExtra(Intent.EXTRA_TEXT, prefs.getString("body", "")))
                        .build();

                ShortcutManagerCompat.addDynamicShortcuts(getApplicationContext(), Collections.singletonList(shortcut));

                if (ShortcutManagerCompat.isRequestPinShortcutSupported(getApplicationContext())) {
                    ShortcutManagerCompat.requestPinShortcut(getApplicationContext(), shortcut, null);
                }
            }
        });

    }

    private void checkInputEmpty() {
        if (!recipientsText.getText().toString().isEmpty()
                && !subjectText.getText().toString().isEmpty()
                && !bodyText.getText().toString().isEmpty()) {
            sendButton.setEnabled(true);
            addButton.setEnabled(true);
        } else {
            sendButton.setEnabled(false);
            addButton.setEnabled(false);
        }
    }
}
