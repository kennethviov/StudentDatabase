package com.violetfruit.studentdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StudentAdapter.OnItemActionListener{

    DBHelper dbHelper;
    RecyclerView recyclerView;
    StudentAdapter adapter;
    List<Student> students;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        intent = new Intent(MainActivity.this, MainActivity2.class);

        recyclerView = findViewById(R.id.recyclerView1);

        students = dbHelper.getAllStudents();
//        students.add(new Student("Alpha", "BSCS", R.drawable.baseline_person_24, null));
//        students.add(new Student("Bravo", "BSIT", R.drawable.baseline_person_24, null));
//        students.get(0).setId(1001);
//        students.get(1).setId(1002);

        adapter = new StudentAdapter(this, students, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ImageButton btnAdd = findViewById(R.id.btnAdd);
        SearchView searchView = findViewById(R.id.searchView);

        btnAdd.setOnClickListener(v -> {
            // Handle add button click
            startActivityForResult(intent, 1);
        });
    }

    @NonNull
    private ItemTouchHelper getItemTouchHelper() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAbsoluteAdapterPosition();
                int toPos = target.getAbsoluteAdapterPosition();
                adapter.moveItem(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.removeItem(position);
                Toast.makeText(getApplicationContext(), "Item removed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                    viewHolder.itemView.setAlpha(0.7f);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f);
            }
        };

        // Attach helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        return itemTouchHelper;
    }

    @Override
    public void onMenuClicked(View anchor, int position) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.actionEdit) {
                Toast.makeText(this, "Edit " + students.get(position).name, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();

                bundle.putInt("id", students.get(position).getId());
                bundle.putString("name", students.get(position).name);
                bundle.putString("course", students.get(position).course);
                bundle.putInt("imageRes", students.get(position).avatarResId);
                bundle.putString("imageUri", students.get(position).avatarUri);

                intent.putExtras(bundle);
                startActivityForResult(intent, 1);

                return true;
            } else if (item.getItemId() == R.id.actionDelete) {
                Toast.makeText(this, "Delete " + students.get(position).name, Toast.LENGTH_SHORT).show();

                ///  TODO: Implement delete logic

                students.remove(position);
                adapter.notifyItemRemoved(position);

                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onAcvityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            assert bundle != null;
            boolean isUpdate = bundle.getBoolean("isUpdate", false);
            int id = bundle.getInt("id");
            String name = bundle.getString("name");
            String course = bundle.getString("course");
            int imageRes = bundle.getInt("imageRes");
            String imageUri = bundle.getString("imageUri");

            ///  TODO: Add UI update logic

            if (isUpdate) {
                dbHelper.updateStudent(id, new Student(name, course, imageRes, imageUri));
            } else {
                dbHelper.addStudent(new Student(name, course, imageRes, imageUri));
            }
        }
    }

    public void onItemClicked(int position) {
        Toast.makeText(this, "Clicked " + students.get(position).name, Toast.LENGTH_SHORT).show();
    }
}