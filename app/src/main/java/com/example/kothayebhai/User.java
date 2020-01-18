package com.example.kothayebhai;

public class User {
    public String name, email, phone, education, current_job, future_job;

    public User(){

    }

    public User(String name, String email, String phone, String education, String current_job, String future_job) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.education = education;
        this.current_job = current_job;
        this.future_job = future_job;
    }
}
