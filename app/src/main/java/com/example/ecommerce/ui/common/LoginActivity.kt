package com.example.ecommerce.ui.common

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityLoginBinding
import com.example.ecommerce.ui.component.LoadingHandler
import com.example.ecommerce.ui.seller.SellerMainActivity
import com.example.ecommerce.ui.user.UserMainActivity
import com.example.ecommerce.utils.PasswordVisibility
import com.example.ecommerce.utils.Validate
import com.example.ecommerce.viewmodel.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var loadingHandler: LoadingHandler
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingHandler = LoadingHandler(supportFragmentManager)
        setupTogglePassword()
        setupListeners()
        setupGoogleLogin()
        setupObservers()
    }

    private fun setupTogglePassword() {
        val passwordVisibility = PasswordVisibility(binding.etPassword, binding.ivTogglePassword)
        binding.ivTogglePassword.setOnClickListener { passwordVisibility.toggle() }
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val validate = Validate()
            if (validate.validateInput(email, password, binding.etEmail, binding.etPassword)) {
                loadingHandler.showLoading()
                authViewModel.loginWithEmail(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password feature coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun setupObservers() {
        authViewModel.loginSuccess.observe(this, Observer { user ->
            loadingHandler.hideLoading()
            if (user != null) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                when (user.role) {
                    "user" -> {
                        startActivity(Intent(this, UserMainActivity::class.java))
                        finish()
                    }
                    "seller" -> {
                        startActivity(Intent(this, SellerMainActivity::class.java))
                        finish()
                    }
                    else -> {
                        Toast.makeText(this, "Vai trò không xác định", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        authViewModel.loginError.observe(this, Observer { error ->
            loadingHandler.hideLoading()
            if (error != null) {
                Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleLogin.setOnClickListener {
            loadingHandler.showLoading()
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnSuccessListener { account ->
                val idToken = account.idToken ?: return@addOnSuccessListener
                authViewModel.signInWithGoogle(idToken)
            }.addOnFailureListener {
                loadingHandler.hideLoading()
                Toast.makeText(this, "Đăng nhập Google thất bại: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}