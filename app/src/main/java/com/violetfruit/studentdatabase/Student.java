package com.violetfruit.studentdatabase;

public class Student {
    private int id;
    public String name;
    public String course;
    public int avatarResId = R.drawable.baseline_person_24;
    public String avatarUri;

    public Student(String name, String course, int avatarResId, String avatarUri) {
        this.name = name;
        this.course = course;

        if (avatarUri != null) {
            this.avatarUri = avatarUri;
        } else {
            this.avatarResId = avatarResId;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
