package com.violetfruit.studentdatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StudentAdapter.OnItemActionListener{

    DBHelper dbHelper;
    RecyclerView recyclerView;
    StudentAdapter adapter;
    List<Student> students;
    Intent intent;
    private SearchView searchView;


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

        adapter = new StudentAdapter(this, students, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ImageButton btnAdd = findViewById(R.id.btnAdd);
        searchView = findViewById(R.id.searchView);

        btnAdd.setOnClickListener(v -> {
            startActivityForResult(new Intent(MainActivity.this, MainActivity2.class), 1);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (searchView.findFocus() != null) {
                Rect searchViewRect = new Rect();
                searchView.getGlobalVisibleRect(searchViewRect);
                if (!searchViewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    searchView.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void filter(String text) {
        List<Student> filteredList = new ArrayList<>();
        for (Student student : students) {
            if (student.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(student);
            }
        }
        adapter.setStudents(filteredList);
    }

    private void refreshStudentList() {
        students.clear();
        students.addAll(dbHelper.getAllStudents());
        adapter.notifyDataSetChanged();
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
                int position = viewHolder.getBindingAdapterPosition();

                Student removedStudent = students.get(position);

                adapter.removeItem(position);

                dbHelper.deleteStudent(removedStudent.getId());

                //Toast.makeText(getApplicationContext(), "Item removed!", Toast.LENGTH_SHORT).show();
                Snackbar.make(recyclerView, "Item removed!", Snackbar.LENGTH_SHORT).show();
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

        return new ItemTouchHelper(callback);
    }

    @Override
    public void onMenuClicked(View anchor, int position) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.context_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.actionEdit) {
                //Toast.makeText(this, "Edit " + students.get(position).name, Toast.LENGTH_SHORT).show();
                Snackbar.make(recyclerView, "Edit " + students.get(position).name, Snackbar.LENGTH_SHORT).show();

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
                // Toast.makeText(this, "Delete " + students.get(position).name, Toast.LENGTH_SHORT).show();
                Snackbar.make(recyclerView, "Delete " + students.get(position).name, Snackbar.LENGTH_LONG).show();

                int deleted = dbHelper.deleteStudent(students.get(position).getId());

                if (deleted < 0) {
                    Snackbar.make(recyclerView, "Error deleting student", Snackbar.LENGTH_SHORT).show();
                    return false;
                }

                adapter.removeItem(position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();

            boolean isUpdate = bundle.getBoolean("isUpdate", false);
            int id = bundle.getInt("id");
            String name = bundle.getString("name");
            String course = bundle.getString("course");
            int imageRes = bundle.getInt("imageRes");
            String imageUri = bundle.getString("imageUri");

            Student student = new Student(name, course, imageRes, imageUri);

            if (isUpdate) {
                student.setId(id);
                dbHelper.updateStudent(student);
                // Toast.makeText(this, "Updated " + student.name, Toast.LENGTH_SHORT).show();
                Snackbar.make(recyclerView, "Updated " + student.name, Snackbar.LENGTH_SHORT).show();
            } else {
                dbHelper.addStudent(student);
                // Toast.makeText(this, "Added " + student.name, Toast.LENGTH_SHORT).show();
                Snackbar.make(recyclerView, "Added " + student.name, Snackbar.LENGTH_SHORT).show();
            }
            refreshStudentList();
        }
    }

    public void onItemClicked(int position) {

    }
}
