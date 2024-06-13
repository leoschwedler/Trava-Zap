package com.example.whatsapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.whatsapp.databinding.ActivityPerfilBinding
import com.example.whatsapp.util.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPerfilBinding.inflate(layoutInflater) }
    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val gerenciadorGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                binding.imagePerfil.setImageURI(it)
                uploadImagemStorage(it)
            } else {
                exibirMensagem("Nenhuma imagem selecionada")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()


    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuario()
    }

    private fun recuperarDadosIniciaisUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            firestore.collection("usuarios").document(idUsuario).get().addOnSuccessListener {
                val dadosUsuario = it.data
                if (dadosUsuario != null) {
                    val nome = dadosUsuario["nome"] as String
                    val foto = dadosUsuario["foto"] as String

                    binding.editNomePerfil.setText(nome)
                    if (foto.isNotEmpty()) {
                        Picasso.get().load(foto).into(binding.imagePerfil)
                    }
                }
            }
        }
    }

    private fun uploadImagemStorage(it: Uri) {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            storage.getReference("fotos").child("usuarios").child(idUsuario).child("perfil.jpg")
                .putFile(it).addOnSuccessListener {
                    exibirMensagem("Sucesso ao fazer o upload da imagem")
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        val dados = mapOf("foto" to it.toString())
                        atualizarDadosPerfil(dados, idUsuario)

                    }?.addOnFailureListener {
                        exibirMensagem("Falha ao fazer o upload da imagem")
                    }
                }
        }

    }

    private fun atualizarDadosPerfil(dados: Map<String, String>, idUsuario: String) {
        firestore.collection("usuarios").document(idUsuario).update(dados).addOnSuccessListener {
            exibirMensagem("Sucesso ao atualizar o perfil")
        }.addOnFailureListener {
            exibirMensagem("Erro ao atualizar o perfil")
        }
    }

    private fun inicializarEventosClique() {
        binding.fabSelecionar.setOnClickListener {
            if (temPermissaoGaleria) {
                gerenciadorGaleria.launch("image/*")
            } else {
                exibirMensagem("Não tem permissão para acessar a galeria")
                solicitarPermissoes()
            }
        }
        binding.btnAtualizar.setOnClickListener {
            val nome = binding.editNomePerfil.text.toString()
            if (nome.isNotEmpty()) {
                val idUsuario = firebaseAuth.currentUser?.uid
                if (idUsuario != null) {
                    val dados = mapOf("nome" to nome)
                    atualizarDadosPerfil(dados, idUsuario)
                }
            } else {

            }
        }
    }

    private fun solicitarPermissoes() {
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val listaPermissoesNegadas = mutableListOf<String>()
        if (!temPermissaoCamera) {
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        }
        if (!temPermissaoGaleria) {
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (listaPermissoesNegadas.isNotEmpty()) {
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {
                temPermissaoCamera = it[Manifest.permission.CAMERA] ?: temPermissaoCamera
                temPermissaoGaleria =
                    it[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria
            }
            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolBarProfile.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}