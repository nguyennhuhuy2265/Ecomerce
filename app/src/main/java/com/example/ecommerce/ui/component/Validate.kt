package com.example.ecommerce.ui.component

import android.util.Patterns
import android.widget.EditText
import android.widget.Spinner

class Validate {
    fun validateInput(email: String, password: String, emailField: EditText, passwordField: EditText): Boolean {
        if (email.isEmpty()) {
            emailField.error = "Email không được để trống"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.error = "Email không đúng định dạng"
            return false
        }
        if (password.isEmpty()) {
            passwordField.error = "Mật khẩu không được để trống"
            return false
        }
        if (password.length < 6) {
            passwordField.error = "Mật khẩu phải có ít nhất 6 ký tự"
            return false
        }
        return true
    }

    fun validateRegister(
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        shopName: String,
        category: String?,
        emailField: EditText,
        passwordField: EditText,
        confirmPasswordField: EditText,
        shopNameField: EditText,
        categoryField: Spinner
    ): List<String> {
        val errors = mutableListOf<String>()

        // Validate cơ bản
        if (!validateInput(email, password, emailField, passwordField)) {
            errors.add("Vui lòng kiểm tra email và mật khẩu")
        }

        if (confirmPassword.isEmpty()) {
            errors.add("Vui lòng xác nhận mật khẩu")
            confirmPasswordField.error = "Vui lòng xác nhận mật khẩu"
        } else if (password != confirmPassword) {
            errors.add("Mật khẩu xác nhận không khớp")
            confirmPasswordField.error = "Mật khẩu xác nhận không khớp"
        }

        // Nếu là seller thì phải nhập tên shop và ngành hàng
        if (role == "seller") {
            if (shopName.isEmpty()) {
                errors.add("Tên cửa hàng không được để trống")
                shopNameField.error = "Tên cửa hàng không được để trống"
            }
            if (category.isNullOrEmpty()) {
                errors.add("Vui lòng chọn ngành hàng kinh doanh")
                categoryField.requestFocus()
            }
        }

        return errors
    }
}