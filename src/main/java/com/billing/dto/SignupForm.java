package com.billing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** The sign-up form. */
public class SignupForm {

    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name can be at most 100 characters.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 120, message = "Email can be at most 120 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters.")
    private String password;

    @NotBlank(message = "Please confirm the password.")
    private String confirmPassword;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
