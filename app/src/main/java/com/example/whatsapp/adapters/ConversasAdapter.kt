package com.example.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.whatsapp.databinding.ItemContatosBinding
import com.example.whatsapp.databinding.ItemConversasBinding
import com.example.whatsapp.model.Conversa
import com.example.whatsapp.model.Usuario
import com.squareup.picasso.Picasso

class ConversasAdapter(private val onclick: (Conversa) -> Unit): Adapter<ConversasAdapter.ConversasViewHolder>() {

    private var listaConversas = emptyList<Conversa>()
    fun adicionarLista(lista: List<Conversa>){
        listaConversas = lista
        notifyDataSetChanged()
    }
    inner class ConversasViewHolder(private val binding: ItemConversasBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (conversa: Conversa) {
            binding.textConversaNome.text = conversa.nome
            binding.textConversaMensagem.text = conversa.ultimaMensagem
            if (!conversa.foto.isNullOrEmpty()) {
                Picasso.get().load(conversa.foto).into(binding.imageConversaFoto)
            }
            binding.clItemConversa.setOnClickListener {
                onclick(conversa)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemConversasBinding.inflate(inflater, parent, false)
        return ConversasViewHolder(view)
    }



    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversas[position]
        holder.bind(conversa)
    }

    override fun getItemCount(): Int = listaConversas.size
}