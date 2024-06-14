package com.example.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.adapters.ContatosAdapter
import com.example.whatsapp.adapters.ConversasAdapter
import com.example.whatsapp.databinding.FragmentConversasBinding
import com.example.whatsapp.model.Conversa
import com.example.whatsapp.model.Usuario
import com.example.whatsapp.ui.MensagensActivity
import com.example.whatsapp.util.Constantes
import com.example.whatsapp.util.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var conversasAdapter: ConversasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversasBinding.inflate(inflater, container, false)
        conversasAdapter = ConversasAdapter {
            val intent = Intent(context, MensagensActivity::class.java)
            val usuario = Usuario(
                id = it.idUsuarioDestinatario,
                nome = it.nome,
                foto = it.foto
            )
            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", Constantes.ORIGEM_CONVERSA)
            startActivity(intent)
        }
        binding.rvConversas.adapter = conversasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

    private fun adicionarListenerConversas() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if (idUsuarioRemetente != null) {
            eventoSnapshot = firestore.collection(Constantes.CONVERSAS).document(idUsuarioRemetente)
                .collection(Constantes.ULTIMAS_CONVERSAS)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        activity?.exibirMensagem("Erro ao recuperar conversas")
                    }
                    val listaConversas = mutableListOf<Conversa>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach {
                        val conversa = it.toObject(Conversa::class.java)
                        if (conversa != null) {
                            listaConversas.add(conversa)
                            Log.i(
                                "exibicao_conversas",
                                "${conversa.nome} - ${conversa.ultimaMensagem}"
                            )
                        }
                    }
                    if (listaConversas.isNotEmpty()) {
                        conversasAdapter.adicionarLista(listaConversas)
                    }
                }
        }
    }
}
