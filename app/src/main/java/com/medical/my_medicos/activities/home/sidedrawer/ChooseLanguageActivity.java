package com.medical.my_medicos.activities.home.sidedrawer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.medical.my_medicos.R;

public class ChooseLanguageActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button selectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        Toolbar toolbar = findViewById(R.id.selectlangtoolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrowbackforappbar);
        }

        radioGroup = findViewById(R.id.languageradio);
        selectButton = findViewById(R.id.button2);

        // Set English as the default selected language
        RadioButton defaultLanguage = findViewById(R.id.selectenglish);
        defaultLanguage.setChecked(true);

        // Set listeners for RadioGroup and Select Button
        setRadioGroupListener();
        setSelectButtonListener();
    }

    private void setRadioGroupListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);

                if (selectedRadioButton.getId() != R.id.selectenglish) {
                    // If a language other than English is selected, show a Toast and disable the button
                    Toast.makeText(ChooseLanguageActivity.this, "Not available right now", Toast.LENGTH_SHORT).show();
                    selectButton.setEnabled(false);
                } else {
                    // Enable the button for English language
                    selectButton.setEnabled(true);
                }

                // Update the text color of the selected RadioButton
                updateRadioButtonTextColors(group, checkedId);
            }
        });
    }

    private void setSelectButtonListener() {
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the "Select" button
                Toast.makeText(ChooseLanguageActivity.this, "Language selected", Toast.LENGTH_SHORT).show();
                // Add your logic for handling the selection
            }
        });
    }

    private void updateRadioButtonTextColors(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof RadioButton && group.getChildAt(i).getId() != checkedId) {
                ((RadioButton) group.getChildAt(i)).setTextColor(getResources().getColor(R.color.black));
            }
        }
        RadioButton selectedRadioButton = findViewById(checkedId);
        selectedRadioButton.setTextColor(getResources().getColor(R.color.green));
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
