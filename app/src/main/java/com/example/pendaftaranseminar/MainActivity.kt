package com.example.pendaftaranseminar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etWa: EditText
    private lateinit var etJudulMakalah: EditText
    private lateinit var rgKategori: RadioGroup
    private lateinit var cbAi: CheckBox
    private lateinit var cbIot: CheckBox
    private lateinit var cbCloud: CheckBox
    private lateinit var spFakultas: Spinner
    private lateinit var spJumlahPeserta: Spinner
    private lateinit var swSetuju: SwitchCompat
    private lateinit var btnDaftar: Button
    private lateinit var tvMetode: TextView

    private var metodeTerpilih = "Belum dipilih"

    private val pilihBayarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { hasil ->
            if (hasil.resultCode == RESULT_OK) {
                metodeTerpilih = hasil.data?.getStringExtra(BayarActivity.EXTRA_METODE) ?: "Belum dipilih"
                tvMetode.text = "Metode pembayaran: $metodeTerpilih"
            } else {
                Toast.makeText(this, "Pemilihan dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etWa = findViewById(R.id.etWa)
        etJudulMakalah = findViewById(R.id.etJudulMakalah)
        rgKategori = findViewById(R.id.rgKategori)
        cbAi = findViewById(R.id.cbAi)
        cbIot = findViewById(R.id.cbIot)
        cbCloud = findViewById(R.id.cbCloud)
        spFakultas = findViewById(R.id.spFakultas)
        spJumlahPeserta = findViewById(R.id.spJumlahPeserta)
        swSetuju = findViewById(R.id.swSetuju)
        btnDaftar = findViewById(R.id.btnDaftar)
        tvMetode = findViewById(R.id.tvMetode)

        // Spinner Fakultas
        ArrayAdapter.createFromResource(
            this, R.array.fakultas_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spFakultas.adapter = adapter
        }

        // Spinner Jumlah Peserta
        ArrayAdapter.createFromResource(
            this, R.array.jumlah_peserta_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spJumlahPeserta.adapter = adapter
        }

        // Switch mengaktifkan tombol
        swSetuju.setOnCheckedChangeListener { _, isChecked ->
            btnDaftar.isEnabled = isChecked
        }

        // Tombol Daftar
        btnDaftar.setOnClickListener {
            if (!formValid()) return@setOnClickListener

            val pindah = Intent(this, RingkasanActivity::class.java).apply {
                putExtra(RingkasanActivity.EXTRA_NAMA, etNama.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_EMAIL, etEmail.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_WA, etWa.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_JUDUL, etJudulMakalah.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_KATEGORI, kategoriTerpilih())
                putExtra(RingkasanActivity.EXTRA_SESI, sesiTerpilih().joinToString(", "))
                putExtra(RingkasanActivity.EXTRA_FAKULTAS, spFakultas.selectedItem.toString())
                putExtra(RingkasanActivity.EXTRA_JUMLAH, spJumlahPeserta.selectedItem.toString())
                putExtra(RingkasanActivity.EXTRA_METODE, metodeTerpilih)
            }
            startActivity(pindah)
        }

        // Tombol pilih metode pembayaran
        findViewById<Button>(R.id.btnBayar).setOnClickListener {
            pilihBayarLauncher.launch(Intent(this, BayarActivity::class.java))
        }

        // Tombol Hubungi Panitia (ACTION_DIAL)
        findViewById<Button>(R.id.btnHubungi).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:081234567890")
            }
            startActivity(intent)
        }

        // Tombol Reset
        findViewById<Button>(R.id.btnReset).setOnClickListener { resetForm() }
    }

    private fun kategoriTerpilih(): String = when (rgKategori.checkedRadioButtonId) {
        R.id.rbMahasiswa -> "Mahasiswa"
        R.id.rbDosen     -> "Dosen"
        R.id.rbUmum      -> "Umum"
        else             -> "-"
    }

    private fun sesiTerpilih(): List<String> {
        val daftar = mutableListOf<String>()
        if (cbAi.isChecked)    daftar.add("AI & Machine Learning")
        if (cbIot.isChecked)   daftar.add("Internet of Things")
        if (cbCloud.isChecked) daftar.add("Cloud Computing")
        return daftar
    }

    private fun formValid(): Boolean {
        etNama.error = null
        etEmail.error = null
        etWa.error = null
        etJudulMakalah.error = null

        if (etNama.text.toString().trim().isEmpty()) {
            etNama.error = getString(R.string.err_nama)
            etNama.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim()).matches()) {
            etEmail.error = getString(R.string.err_email)
            etEmail.requestFocus()
            return false
        }
        if (etWa.text.toString().trim().length < 10) {
            etWa.error = getString(R.string.err_wa)
            etWa.requestFocus()
            return false
        }
        if (etJudulMakalah.text.toString().trim().length < 5) {
            etJudulMakalah.error = getString(R.string.err_judul_makalah)
            etJudulMakalah.requestFocus()
            return false
        }
        if (sesiTerpilih().isEmpty()) {
            Toast.makeText(this, R.string.err_sesi, Toast.LENGTH_SHORT).show()
            return false
        }
        if (!swSetuju.isChecked) {
            Toast.makeText(this, R.string.err_setuju, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun resetForm() {
        etNama.text.clear()
        etEmail.text.clear()
        etWa.text.clear()
        etJudulMakalah.text.clear()
        rgKategori.check(R.id.rbMahasiswa)
        cbAi.isChecked = false
        cbIot.isChecked = false
        cbCloud.isChecked = false
        spFakultas.setSelection(0)
        spJumlahPeserta.setSelection(0)
        swSetuju.isChecked = false
        btnDaftar.isEnabled = false
        metodeTerpilih = "Belum dipilih"
        tvMetode.text = "Metode pembayaran: -"
        etNama.requestFocus()
        Toast.makeText(this, "Form dibersihkan", Toast.LENGTH_SHORT).show()
    }
}