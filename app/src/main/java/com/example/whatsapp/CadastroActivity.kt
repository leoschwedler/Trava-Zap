package com.example.whatsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapp.databinding.ActivityCadastroBinding
import com.example.whatsapp.model.Usuario
import com.example.whatsapp.util.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCadastroBinding.inflate(layoutInflater) }
    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarEventosClique()


    }

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful) {
                exibirMensagem("Usuário criado com sucesso")
                val idUsuario = it.result.user?.uid
                if (idUsuario != null) {
                    val usuario = Usuario(idUsuario, nome, email)
                    salvarUsuarioFirestore(usuario)
                }

            }
        }.addOnFailureListener {
            try {
                throw it
            } catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                exibirMensagem("Senha muito fraca, digite outra senha")
                erroSenhaFraca.printStackTrace()
            } catch (erroUsuarioExiste: FirebaseAuthUserCollisionException) {
                exibirMensagem("Email já pertence a um usuário, digite outro e-mail")
                erroUsuarioExiste.printStackTrace()
            } catch (erroCredenciaisinvalida: FirebaseAuthInvalidCredentialsException) {
                exibirMensagem("Email inválido, digite um outro e-mail")
                erroCredenciaisinvalida.printStackTrace()
            }
        }

    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firestore.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Usuário criado com sucesso")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener {
                exibirMensagem("Erro ao criar o usuário")
            }

    }

    private fun validarCampos(): Boolean {

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if (nome.isNotEmpty()) {
            binding.editNome.error = null
            if (email.isNotEmpty()) {
                binding.editEmail.error = null
                if (senha.isNotEmpty()) {
                    binding.editSenha.error = null
                    return true
                } else {
                    binding.editSenha.error = "Por favor, informe sua senha"
                    return false
                }
            } else {
                binding.editEmail.error = "Por favor, informe seu e-mail"
                return false
            }
        } else {
            binding.editNome.error = "Por favor, informe seu nome"
            return false
        }

    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}