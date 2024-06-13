package com.example.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsapp.databinding.ItemContatosBinding
import com.example.whatsapp.model.Usuario
import com.squareup.picasso.Picasso

class ContatosAdapter(
    private val onclick: (Usuario) -> Unit
): Adapter<ContatosAdapter.ContatosViewHolder>(){

    private var listaContatos = emptyList<Usuario>()
    fun adicionarLista(lista: List<Usuario>){
        listaContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(private val binding: ItemContatosBinding) : ViewHolder(binding.root){
    fun bind (usuario: Usuario) {
        binding.textContatoNome.text = usuario.nome
        if (!usuario.foto.isNullOrEmpty()) {
            Picasso.get().load(usuario.foto).into(binding.imgContatoFoto)
        }
        binding.clItemContato.setOnClickListener {
            onclick(usuario)
        }

    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = ItemContatosBinding.inflate(inflater, parent, false)
        return ContatosViewHolder(view)
    }
    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind(usuario)
    }
    override fun getItemCount(): Int {
        return listaContatos.size
    }
}