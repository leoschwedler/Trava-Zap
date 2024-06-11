package com.example.whatsapp.util

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensagem(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_LONG
    ).show()
}