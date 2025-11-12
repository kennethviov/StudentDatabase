package com.violetfruit.studentdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "students";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COURSE = "course";
    private static final String COLUMN_AVATAR_RES_ID = "avatar_res_id";
    private static final String COLUMN_AVATAR_URI = "avatar_uri";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_COURSE + " TEXT, " +
                COLUMN_AVATAR_RES_ID + " INTEGER, " +
                COLUMN_AVATAR_URI + " TEXT)";
        db.execSQL(createTableQuery);

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Kenneth");
        values.put(COLUMN_COURSE, "BSCS");
        values.put(COLUMN_AVATAR_RES_ID, R.drawable.baseline_person_24);
        db.insert(TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // create
    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, student.name);
        values.put(COLUMN_COURSE, student.course);
        values.put(COLUMN_AVATAR_RES_ID, student.avatarResId);
        values.put(COLUMN_AVATAR_URI, student.avatarUri);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // retrieve by id
    public Student getStudentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Student student = new Student(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_RES_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_URI))
            );
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));

            cursor.close();
            db.close();
            return student;
        }

        db.close();
        return null;
    }

    // retrieve all
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_RES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_URI))
                );
                student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                students.add(student);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return students;
    }

    // update
    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, student.name);
        values.put(COLUMN_COURSE, student.course);
        values.put(COLUMN_AVATAR_RES_ID, student.avatarResId);
        values.put(COLUMN_AVATAR_URI, student.avatarUri);

        int rows = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(student.getId())});
        db.close();
        return rows;
    }

    // delete
    public int deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}
