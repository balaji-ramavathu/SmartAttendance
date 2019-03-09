package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Wifi_fun extends AppCompatActivity {
    ClipboardManager clipboard;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_fun);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        text = findViewById(R.id.text_input);
    }

    public void copyToClipboard(View view) {
        ClipData clip = ClipData.newPlainText("name", text.getText());
        clipboard.setPrimaryClip(clip);
        Log.d("clipboard", "message copied is "+getClipboardText());
    }

    public String getClipboardText() {
        String pasteData = "";
        if (clipboard.getText() != null) {
            pasteData = String.valueOf(clipboard.getText());
        }
        return pasteData;
    }

    public void settingsPage(View view) {
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
    }
}
