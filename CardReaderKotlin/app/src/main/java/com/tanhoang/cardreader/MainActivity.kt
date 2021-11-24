package com.tanhoang.cardreader

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.tanhoang.cardreader.util.APDUUtil
import com.tanhoang.cardreader.util.PreferenceProvider
import com.tanhoang.cardreader.util.TypeConvertor

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null

    lateinit var infoTv : TextView
    lateinit var cardBtn : Button
    var isReadCardFunc = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        infoTv = findViewById(R.id.infoTxt)
        cardBtn = findViewById(R.id.cardBtn)
        cardBtn.setOnClickListener {
            if (isReadCardFunc) {
                cardBtn.setText(R.string.switch_to_card_reader_mode)
                nfcAdapter?.disableReaderMode(this)
                infoTv.setText(R.string.card_emulator_mode_hint)
            }
            else {
                cardBtn.setText(R.string.switch_to_card_simulator_mode)
                enableNfcReaderMode()

                infoTv.setText(R.string.card_reader_mode_hint)
            }
            isReadCardFunc = !isReadCardFunc
        }
    }

    override fun onResume() {
        super.onResume()
        if (isReadCardFunc) {
            enableNfcReaderMode()
        }

    }

    override fun onPause() {
        super.onPause()
        if (isReadCardFunc)
            nfcAdapter?.disableReaderMode(this)
    }

    private fun enableNfcReaderMode() {
        nfcAdapter?.enableReaderMode(this, this,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()

        processAPDUCommands(isoDep)

        isoDep.close()
    }

    private fun processAPDUCommands(isoDep: IsoDep) {
        var result = ""
        val preferenceProvider = PreferenceProvider(applicationContext)
        for ((step, apduCommand) in APDUUtil.apduCommandMap) {
            val response = APDUUtil.executeApduCommand(apduCommand, isoDep)
            preferenceProvider.putString(step, TypeConvertor.toHex(response))
            result += "\n${step}: " + TypeConvertor.toHex(response)
        }
        runOnUiThread {
            infoTv.setText("Card Response: ${result}")
        }
    }
}