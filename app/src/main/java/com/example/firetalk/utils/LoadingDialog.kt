package com.example.firetalk.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.firetalk.R

class LoadingDialog(context: Context) : Dialog(context) {
    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.view_dialog_loading)
    }
}