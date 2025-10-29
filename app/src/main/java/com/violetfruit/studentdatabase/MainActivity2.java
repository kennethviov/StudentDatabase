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

import com.google.android.material.snackbar.Snackbar;

public class MainActivity2 extends AppCompatActivity {

    private Uri selectedImageUri;

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

        imgPicker.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, 100);
        });

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                selectedImageUri = Uri.parse(uri);
                imgPicker.setImageURI(selectedImageUri);
            } else {
                imgPicker.setImageResource(img);
            }

            isUpdate = true;
        } else {
            toUpdateStudent = 0;
            isUpdate = false;
        }

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String course = spnrCourse.getSelectedItem().toString().trim();

            if (name.isEmpty()) {
                editName.setError("Name is required");
                editName.requestFocus();
                return;
            }
            if (spnrCourse.getSelectedItemPosition() == 0) {
//                Toast.makeText(MainActivity2.this, "Please select a course", Toast.LENGTH_SHORT).show();
                Snackbar.make(v, "Please select a course", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("isUpdate", isUpdate);
            resultIntent.putExtra("id", toUpdateStudent);
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("course", course);

            if (selectedImageUri != null) {
                resultIntent.putExtra("imageUri", selectedImageUri.toString());
            } else {
                resultIntent.putExtra("imageRes", R.drawable.baseline_person_24);
            }

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                final int takeFlags = data.getFlags() &
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    // handle or log; fallback is to copy the file into app storage
                    e.printStackTrace();
                }

                selectedImageUri = uri;
                ImageView imgPicker = findViewById(R.id.imgPicker);
                imgPicker.setImageURI(selectedImageUri);
            }
        }
    }
}