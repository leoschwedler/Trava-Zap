package com.example.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapp.databinding.ActivityLoginBinding
import com.example.whatsapp.util.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var email: String
    private lateinit var senha: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        inicializarEventosClique()

    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuario = firebaseAuth.currentUser
        if (usuario != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
        binding.btnLogar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
            exibirMensagem("Login realizado com sucesso")
            startActivity(Intent(this, MainActivity::class.java))
        }.addOnFailureListener {
            try {
                throw it
            } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("E-mail não cadastrado")
            } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem("E-mail ou senha estao incorretos")

            }
        }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()
        if (email.isNotEmpty()) {
            binding.textInputLayoutEmail.error = null
            if (senha.isNotEmpty()) {
                binding.textInputLayoutSenha.error = null
                return true
            } else {
                binding.textInputLayoutSenha.error = "Por favor, informe sua senha"
                return false
            }
        } else {
            binding.textInputLayoutEmail.error = "Por favor, informe seu e-mail"
            return false
        }
    }
}