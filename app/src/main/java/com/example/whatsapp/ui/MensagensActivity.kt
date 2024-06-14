package com.example.whatsapp.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.adapters.MensagensAdapter
import com.example.whatsapp.databinding.ActivityMensagensBinding
import com.example.whatsapp.model.Conversa
import com.example.whatsapp.model.Mensagem
import com.example.whatsapp.model.Usuario
import com.example.whatsapp.util.Constantes
import com.example.whatsapp.util.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var mensagensAdapter: MensagensAdapter
    private var dadosDestinatario: Usuario? = null
    private var dadosUsuarioRemetente: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUsuarios()
        inicializarToolbar()
        inicializarEventosClique()
        inicializarRecyclerView()
        inicializarListeners()
    }

    private fun inicializarRecyclerView() {
        with(binding) {
            mensagensAdapter = MensagensAdapter()
            rvMensagens.adapter = mensagensAdapter
            rvMensagens.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }

    private fun inicializarListeners() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
            listenerRegistration = firestore.collection(Constantes.BD_MENSAGENS).document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario).orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        exibirMensagem("erro ao recuperar mensagens")
                        return@addSnapshotListener
                    }
                    val listaMensagem = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents
                    documentos?.forEach {
                        val mensagem = it.toObject(Mensagem::class.java)
                        if (mensagem != null) {
                            listaMensagem.add(mensagem)
                            Log.i("exibicao_mensagens", mensagem.mensagem)
                        }
                    }
                    if (listaMensagem.isNotEmpty()) {
                        mensagensAdapter.adicionarLista(listaMensagem)
                    }
                }
        }
    }

    private fun inicializarEventosClique() {
        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textoMensagem: String) {
        if (textoMensagem.isNotEmpty()) {
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
                val mensagem = Mensagem(idUsuarioRemetente, textoMensagem)
                val conversaRemetente = Conversa(
                    idUsuarioRemetente,
                    idUsuarioDestinatario,
                    dadosDestinatario!!.foto,
                    dadosDestinatario!!.nome,
                    textoMensagem
                )
                salvarConversaFirestore(conversaRemetente)
                salvarMensagemFireStore(idUsuarioRemetente, idUsuarioDestinatario, mensagem)
                salvarMensagemFireStore(idUsuarioDestinatario, idUsuarioRemetente, mensagem)
                val conversaDestinatario = Conversa(
                    idUsuarioDestinatario,
                    idUsuarioRemetente,
                    dadosUsuarioRemetente!!.foto,
                    dadosDestinatario!!.nome,
                    textoMensagem
                )
                salvarConversaFirestore(conversaDestinatario)
                binding.editMensagem.setText("")
            }
        }
    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore.collection(Constantes.CONVERSAS).document(conversa.idUsuarioRemetente)
            .collection(Constantes.ULTIMAS_CONVERSAS).document(conversa.idUsuarioDestinatario)
            .set(conversa).addOnFailureListener {
                exibirMensagem("Erro ao salvar conversa")
            }
    }

    private fun salvarMensagemFireStore(idUsuarioRemetente: String, idUsuarioDestinatario: String, mensagem: Mensagem) {
        firestore.collection(Constantes.BD_MENSAGENS).document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario).add(mensagem).addOnFailureListener {
                exibirMensagem("erro ao enviar mensagem")
            }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null) {
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get().load(dadosDestinatario!!.foto).into(binding.imgFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUsuarios() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if (idUsuarioRemetente != null) {
            firestore.collection(Constantes.USUARIOS).document(idUsuarioRemetente).get()
                .addOnSuccessListener {
                    val usuario = it.toObject(Usuario::class.java)
                    if (dadosUsuarioRemetente == null) {
                        dadosUsuarioRemetente = usuario
                    }
                }
        }
        val extras = intent.extras
        if (extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO) {
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                } else {
                    extras.getParcelable("dadosDestinatario")
                }
            } else if (origem == Constantes.ORIGEM_CONVERSA) {
                // Lógica para recuperar dados da conversa
            }
        }
    }
}
