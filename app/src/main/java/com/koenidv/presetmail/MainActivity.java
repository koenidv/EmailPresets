package com.koenidv.presetmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    static List<Mail> mailList;
    static RecyclerView presetsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = getSharedPreferences("preset", MODE_PRIVATE);

        mailList = new Gson().fromJson(prefs.getString("presets", ""), new TypeToken<ArrayList<Mail>>() {
        }.getType());
        if (mailList == null || mailList.isEmpty()) {
            mailList = new ArrayList<>();
            addMail();
        }

        findViewById(R.id.newButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMail();
            }
        });

        presetsRecycler = findViewById(R.id.presetsRecycler);
        presetsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        presetsRecycler.setAdapter(new PresetsAdapter(mailList));
    }

    void addMail() {
        EditBottomSheet sheet = new EditBottomSheet();
        sheet.show(getSupportFragmentManager(), "new");
    }

    public void edit(View view) {
        EditBottomSheet sheet = new EditBottomSheet();
        sheet.show(getSupportFragmentManager(), String.valueOf((int) view.getTag()));
    }

    public void remove(View view) {
        mailList.remove((int) view.getTag());
        Objects.requireNonNull(presetsRecycler.getAdapter()).notifyItemRemoved((int) view.getTag());
        presetsRecycler.getAdapter().notifyItemRangeChanged((int) view.getTag(), mailList.size());
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("preset", MODE_PRIVATE);
        prefs.edit().putString("presets", new Gson().toJson(mailList)).apply();
        ShortcutManagerCompat.removeAllDynamicShortcuts(getApplicationContext());
        if (mailList.isEmpty())
            addMail();
    }

    public void pin(View view) {
        pin(mailList.get((int) view.getTag()), getApplicationContext());
    }

    static void pin(Mail toEdit, Context context) {
        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, toEdit.getSubject())
                .setShortLabel(toEdit.getSubject())
                .setLongLabel(toEdit.getSubject())
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_send_round))
                .setIntent(new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:"))
                        .putExtra(Intent.EXTRA_EMAIL, toEdit.getRecipients())
                        .putExtra(Intent.EXTRA_SUBJECT, toEdit.getRecipients())
                        .putExtra(Intent.EXTRA_TEXT, toEdit.getRecipients()))
                .build();

        try {
            ShortcutManagerCompat.addDynamicShortcuts(context, Collections.singletonList(shortcut));
        } catch (IllegalArgumentException ignored) {
        }

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null);
        }
    }

    public void send(View view) {
        send(mailList.get((int) view.getTag()), getApplicationContext());
    }

    static void send(Mail toEdit, Context context) {
        // Send an email
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, toEdit.getRecipients())
                .putExtra(Intent.EXTRA_SUBJECT, toEdit.getSubject())
                .putExtra(Intent.EXTRA_TEXT, toEdit.getBody())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Only open if email client is installed
        if (emailIntent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(emailIntent);
    }
}
