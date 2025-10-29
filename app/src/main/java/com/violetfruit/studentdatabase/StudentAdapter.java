package com.violetfruit.studentdatabase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class StudentAdapter  extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> students;
    private Context context;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onMenuClicked(View anchor, int position);

        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

        void onItemClicked(int position);

    }

    public StudentAdapter(Context context, List<Student> students, OnItemActionListener listener) {
        this.context = context;
        this.students = students;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtCourse;
        ImageButton btnMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }

        public void bind(Student student) {
            if (student.avatarUri != null && !student.avatarUri.isEmpty()) {
                try {
                    Uri uri = Uri.parse(student.avatarUri);
                    imgAvatar.setImageURI(uri);
                } catch (Exception e) {
                    imgAvatar.setImageResource(student.avatarResId);
                }
            } else {
                imgAvatar.setImageResource(student.avatarResId);
            }

            txtName.setText(student.name);
            txtCourse.setText(student.course);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Student s = students.get(position);
        holder.bind(s);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClicked(holder.getAdapterPosition());
        });

        holder.btnMenu.setOnClickListener(v -> {
            if (listener != null) listener.onMenuClicked(v, holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(students, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void removeItem(int position) {
        students.remove(position);
        notifyItemRemoved(position);
    }
}
