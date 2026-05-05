package com.campus.scems.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StudentSignupForm {

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Size(min = 8, max = 8, message = "University ID must be exactly 8 characters (VTU + 5 digits)")
    @Pattern(regexp = "^[Vv][Tt][Uu]\\d{5}$", message = "Use your Veltech ID: VTU followed by five digits")
    private String loginId;

    @NotBlank
    @Size(min = 8, max = 80)
    private String password;

    @NotBlank
    @Size(min = 8, max = 80)
    private String confirmPassword;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
