package com.campus.scems.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Name is required")
    @Size(max = 120)
    private String studentName;

    @NotBlank(message = "Roll number is required")
    @Size(max = 40)
    private String studentRoll;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }
}
