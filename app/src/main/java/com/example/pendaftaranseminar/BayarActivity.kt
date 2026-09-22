package com.example.pendaftaranseminar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class BayarActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_METODE = "EXTRA_METODE_TERPILIH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayar)

        val rgBayar = findViewById<RadioGroup>(R.id.rgBayar)

        findViewById<Button>(R.id.btnKonfirmasi).setOnClickListener {
            val metode = when (rgBayar.checkedRadioButtonId) {
                R.id.rbTransfer -> "Transfer Bank"
                R.id.rbVa       -> "Virtual Account"
                R.id.rbQris     -> "QRIS"
                else            -> "E-Wallet"
            }

            val hasil = Intent().apply {
                putExtra(EXTRA_METODE, metode)
            }

            setResult(RESULT_OK, hasil)
            finish()
        }

        findViewById<Button>(R.id.btnBatalBayar).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}