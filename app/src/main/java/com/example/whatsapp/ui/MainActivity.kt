package com.example.whatsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.example.whatsapp.R
import com.example.whatsapp.adapters.ViewPagerAdapter
import com.example.whatsapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarNavegaçãoAbas()
    }

    private fun inicializarNavegaçãoAbas() {

        val tabLayout = binding.tabLayoutPrincipal
        val viewPager = binding.viewPagerPrincipal
        val abas = listOf("Conversas", "Contatos")
        viewPager.adapter = ViewPagerAdapter(abas,supportFragmentManager, lifecycle)
        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = abas[position]
        }.attach()

    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeMainToolbar.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "TRAVA ZAP"
        }
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.item_perfil -> {
                            val intent = Intent(applicationContext, PerfilActivity::class.java)
                            startActivity(intent)
                        }
                        R.id.item_sair -> {
                            deslogarUsuario()
                        }
                    }
                    return true
                }

            }
        )
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setPositiveButton("Sim") { _, _ ->
                firebaseAuth.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}