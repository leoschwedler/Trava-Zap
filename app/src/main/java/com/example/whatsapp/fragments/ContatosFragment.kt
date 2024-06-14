package com.example.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.R
import com.example.whatsapp.adapters.ContatosAdapter
import com.example.whatsapp.databinding.ActivityCadastroBinding
import com.example.whatsapp.databinding.FragmentContatosBinding
import com.example.whatsapp.model.Usuario
import com.example.whatsapp.ui.MensagensActivity
import com.example.whatsapp.util.Constantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class ContatosFragment : Fragment() {

    private lateinit var binding : FragmentContatosBinding
    private lateinit var eventoSnapshot : ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contatosAdapter = ContatosAdapter{
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDestinatario", it)
            intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding = FragmentContatosBinding.inflate(inflater, container, false)
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        eventoSnapshot = firestore.collection(Constantes.USUARIOS)
            .addSnapshotListener { querySnapshot, error ->
                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents
                documentos?.forEach {
                    val idUsuarioLogado = firebaseAuth.currentUser?.uid
                    val usuario = it.toObject(Usuario::class.java)
                    if (usuario != null && idUsuarioLogado != null){
                        if (idUsuarioLogado != null)
                        if (idUsuarioLogado != usuario.id){
                            listaContatos.add(usuario)
                        }
                    }
                }
                if (listaContatos.isNotEmpty()) {
                    contatosAdapter.adicionarLista(listaContatos)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }


}