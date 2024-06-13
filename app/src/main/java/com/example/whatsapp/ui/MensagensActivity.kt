package com.example.whatsapp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapp.databinding.ActivityMensagensBinding
import com.example.whatsapp.model.Usuario
import com.example.whatsapp.util.Constantes
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUsuarioDestinario()
        inicializarToolbar()

    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null){
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get().load(dadosDestinatario!!.foto).into(binding.imgFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUsuarioDestinario() {
        val extras = intent.extras
        if (extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO) {

                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                }else{
                    extras.getParcelable("dadosDestinatario")
                }

            } else if (origem == Constantes.ORIGEM_CONVERSA) {

            }
        }
    }
}