package com.violetfruit.studentdatabase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean isUpdate;

        ImageView imgPicker = findViewById(R.id.imgPicker);
        EditText editName = findViewById(R.id.editName);
        Spinner spnrCourse = findViewById(R.id.spnrCourse);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        /*
        *
        * Image Picker
        *
        *
        * */
        imgPicker.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });


        /*
        *
        * Spinner
        *
        * */
        String[] courses = {"Choose course", "BSCS", "BSIT", "BSEMC", "BSIS", "BSHM"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courses
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrCourse.setAdapter(adapter);

        spnrCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCourse = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity2.this, "Selected: " + selectedCourse, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected
            }
        });



        /*
         *
         * IF EDIT / UPDATE
         *
         * */
        int toUpdateStudent;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            toUpdateStudent = bundle.getInt("id");
            String name = bundle.getString("name");
            String course = bundle.getString("course");
            int img = bundle.getInt("imageRes");
            String uri = bundle.getString("imageUri");

            editName.setText(name);
            spnrCourse.setSelection(adapter.getPosition(course));

            if (uri != null) {
                imgPicker.setImageURI(Uri.parse(uri));
            } else {
                imgPicker.setImageResource(img);
            }

            isUpdate = true;
        } else {
            toUpdateStudent = 0;
            isUpdate = false;
        }

        /*
        *
        * Save Button
        *
        * */
        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String course = spnrCourse.getSelectedItem().toString().trim();
            int avatarRes = imgPicker.getDrawable() != null ? R.drawable.baseline_person_24 : 0;
            String avatarUri = imgPicker.getDrawable() != null ? imgPicker.getDrawable().toString() : "";

            if (name.isEmpty()) {
                editName.setError("Name is required");
                editName.requestFocus();
                return;
            }
            if (spnrCourse.getSelectedItemPosition() == 0) {
                Toast.makeText(MainActivity2.this, "Please select a course", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent();
            Bundle bundle1 = new Bundle();

            assert bundle != null;
            bundle.putBoolean("isUpdate", isUpdate);
            bundle.putInt("id", toUpdateStudent);
            bundle.putString("name", name);
            bundle.putString("course", course);
            bundle.putInt("imageRes", avatarRes);
            bundle.putString("imageUri", avatarUri);

            intent.putExtras(bundle1);
            setResult(RESULT_OK, intent);
            finish();
        });

        /*
        *
        * Cancel Button
        *
        * */
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            ImageView imgPicker = findViewById(R.id.imgPicker);
            imgPicker.setImageURI(imageUri);
        }
    }
}