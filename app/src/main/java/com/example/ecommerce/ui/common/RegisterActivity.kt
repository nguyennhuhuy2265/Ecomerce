package com.example.ecommerce.ui.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityRegisterBinding
import com.example.ecommerce.model.common.Category
import com.example.ecommerce.model.common.User
import com.example.ecommerce.ui.component.LoadingHandler
import com.example.ecommerce.ui.user.UserMainActivity
import com.example.ecommerce.utils.PasswordVisibility
import com.example.ecommerce.utils.Validate
import com.example.ecommerce.viewmodel.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var loadingHandler: LoadingHandler
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingHandler = LoadingHandler(supportFragmentManager)
        setupTogglePassword()
        setupListeners()
        setupGoogleLogin()
        setupObservers()
    }

    private fun setupCategorySpinner(categories: List<Category>) {
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapter
    }

    private fun setupTogglePassword() {
        val passwordVisibility = PasswordVisibility(binding.etPassword, binding.ivTogglePassword)
        binding.ivTogglePassword.setOnClickListener { passwordVisibility.toggle() }

        val confirmPasswordVisibility = PasswordVisibility(binding.etConfirmPassword, binding.ivToggleCofirmPassword)
        binding.ivToggleCofirmPassword.setOnClickListener { confirmPasswordVisibility.toggle() }
    }

    private fun setupListeners() {
        binding.roleGroup.setOnCheckedChangeListener { _, checkedId ->
            val isSeller = checkedId == binding.rbSeller.id
            binding.sellerInfoLayout.visibility = if (isSeller) View.VISIBLE else View.GONE
            binding.llSocialRegister.visibility = if (!isSeller) View.VISIBLE else View.GONE
            binding.tvOr.visibility = if (!isSeller) View.VISIBLE else View.GONE
        }

        binding.btnSignUp.setOnClickListener { handleSignUp() }
        binding.tvSignIn.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
    }

    private fun setupObservers() {
        authViewModel.categories.observe(this, Observer { categories ->
            if (categories.isNotEmpty()) {
                setupCategorySpinner(categories)
            } else {
                Toast.makeText(this, "Không có danh mục nào để hiển thị", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.categoriesError.observe(this, Observer { error ->
            if (error != null) {
                Toast.makeText(this, "Lỗi khi lấy danh mục: $error", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.registerSuccess.observe(this, Observer { user ->
            loadingHandler.hideLoading()
            if (user != null) {
                Toast.makeText(this, "Đăng ký thành công với vai trò ${user.role}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })

        authViewModel.registerError.observe(this, Observer { error ->
            loadingHandler.hideLoading()
            if (error != null) {
                Toast.makeText(this, "Đăng ký thất bại: $error", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.loginSuccess.observe(this, Observer { user ->
            loadingHandler.hideLoading()
            if (user != null) {
                Toast.makeText(this, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserMainActivity::class.java))
                finish()
            }
        })

        authViewModel.loginError.observe(this, Observer { error ->
            loadingHandler.hideLoading()
            if (error != null) {
                Toast.makeText(this, "Đăng nhập Google thất bại: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val role = if (binding.rbSeller.isChecked) "seller" else "user"
        val shopName = binding.etShopName.text.toString().trim()
        val category = binding.spCategory.selectedItem?.toString()

        val validate = Validate()
        val errorMessages = validate.validateRegister(
            email, password, confirmPassword, role, shopName, category,
            binding.etEmail, binding.etPassword, binding.etConfirmPassword,
            binding.etShopName, binding.spCategory
        )

        if (errorMessages.isNotEmpty()) {
            Toast.makeText(this, errorMessages.joinToString("\n"), Toast.LENGTH_LONG).show()
        } else {
            loadingHandler.showLoading()
            authViewModel.registerWithEmail(email, password, role, if (role == "seller") shopName else null, if (role == "seller") category else null)
        }
    }

    private fun setupGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleRegister.setOnClickListener {
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