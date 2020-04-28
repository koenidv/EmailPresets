package com.koenidv.presetmail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.ShortcutManagerCompat;

import static android.content.Context.MODE_PRIVATE;
import static com.koenidv.presetmail.MainActivity.mailList;

//  Created by koenidv on 28.04.2020.
public class EditBottomSheet extends BottomSheetDialogFragment {

    private Mail toEdit;
    private TextInputEditText recipientsText, subjectText, bodyText;
    private TextInputLayout recipientsLayout;
    private Button pinButton, testButton, saveButton, cancelButton;
    private boolean editmode;

    EditBottomSheet() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sheet_edit, container, false);

        recipientsText = view.findViewById(R.id.recipientsEditText);
        recipientsLayout = view.findViewById(R.id.recipientsInputLayout);
        subjectText = view.findViewById(R.id.subjectEditText);
        bodyText = view.findViewById(R.id.bodyEditText);
        pinButton = view.findViewById(R.id.pinButton);
        testButton = view.findViewById(R.id.testButton);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        if (getTag() != null && !getTag().equals("new")) {
            try {
                toEdit = mailList.get(Integer.parseInt(getTag()));

                recipientsText.setText(TextUtils.join(", ", toEdit.getRecipients()));
                subjectText.setText(toEdit.getSubject());
                bodyText.setText(toEdit.getBody());
                pinButton.setText(R.string.button_save);
                saveButton.setText(R.string.button_pin);
                editmode = true;
            } catch (NullPointerException | IndexOutOfBoundsException npe) {
                toEdit = new Mail();
            }
        } else {
            toEdit = new Mail();
        }

        checkInputEmpty();
        this.setCancelable(false);
        if (mailList.isEmpty())
            cancelButton.setEnabled(false);


        recipientsText.addTextChangedListener(new TextWatcher() {
            //@formatter:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            //@formatter:on

            @Override
            public void afterTextChanged(Editable s) {
                if (checkEmailValid(s.toString())) {
                    toEdit.setRecipients(s.toString().split(" | \n|; |; \n|;|;\n|, |, \n|,|,\n|\n"));
                    checkInputEmpty();
                    recipientsLayout.setErrorEnabled(false);
                } else {
                    setButtonsEnabled(false);
                    recipientsLayout.setErrorEnabled(true);
                    recipientsLayout.setError(getString(R.string.error_recipients));
                }
            }
        });

        subjectText.addTextChangedListener(new TextWatcher() {
            //@formatter:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            //@formatter:on

            @Override
            public void afterTextChanged(Editable s) {
                toEdit.setSubject(s.toString());
                checkInputEmpty();
            }
        });

        bodyText.addTextChangedListener(new TextWatcher() {
            //@formatter:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            //@formatter:on

            @Override
            public void afterTextChanged(Editable s) {
                toEdit.setBody(s.toString());
                checkInputEmpty();
            }
        });

        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                if (!editmode)
                    MainActivity.pin(toEdit, getActivity());
                dismiss();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.send(toEdit, Objects.requireNonNull(getActivity()));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                if (editmode)
                    MainActivity.pin(toEdit, getActivity());
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }

    private void checkInputEmpty() {
        setButtonsEnabled(!Objects.requireNonNull(recipientsText.getText()).toString().isEmpty()
                && !Objects.requireNonNull(subjectText.getText()).toString().isEmpty()
                && !Objects.requireNonNull(bodyText.getText()).toString().isEmpty());
    }

    private void setButtonsEnabled(boolean enabled) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(Objects.requireNonNull(getActivity()))) {
            pinButton.setEnabled(enabled);
        } else {
            pinButton.setEnabled(false);
        }
        testButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
    }

    private boolean checkEmailValid(String s) {
        if (!s.isEmpty()) {
            for (String s1 : s.split(" | \n|; |; \n|;|;\n|, |, \n|,|,\n|\n")) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s1).matches())
                    return false;
            }
        }
        return true;
    }

    private void save() {
        if (getTag() != null && !getTag().equals("new")) {
            mailList.set(Integer.parseInt(getTag()), toEdit);
            Objects.requireNonNull(MainActivity.presetsRecycler.getAdapter()).notifyItemChanged(Integer.parseInt(getTag()));
        } else {
            mailList.add(toEdit);
            Objects.requireNonNull(MainActivity.presetsRecycler.getAdapter()).notifyItemInserted(mailList.size());
        }

        final SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("preset", MODE_PRIVATE);
        prefs.edit().putString("presets", new Gson().toJson(mailList)).apply();
    }
}
