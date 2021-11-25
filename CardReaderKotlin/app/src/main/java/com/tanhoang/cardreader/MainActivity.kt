package com.tanhoang.cardreader

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.tanhoang.cardreader.model.Card
import com.tanhoang.cardreader.util.APDUUtil
import com.tanhoang.cardreader.util.PreferenceProvider
import com.tanhoang.cardreader.util.TypeConvertor
import com.tanhoang.cardreader.util.parser.ResponseUtils

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null

    lateinit var infoTv: TextView
    lateinit var cardBtn: Button
    var isReadCardFunc = true
    lateinit var cardInfo: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardInfo = findViewById(R.id.cardInfo) as CardView

        if (!isNfcAvailable(this)) {
            Toast.makeText(this, "NFC not avalaible", Toast.LENGTH_LONG).show()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        infoTv = findViewById(R.id.infoTxt)
        cardBtn = findViewById(R.id.cardBtn)
        cardBtn.setOnClickListener {
            if (isReadCardFunc) {
                cardBtn.setText(R.string.switch_to_card_reader_mode)
                nfcAdapter?.disableReaderMode(this)
                infoTv.setText(R.string.card_emulator_mode_hint)
            } else {
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
        nfcAdapter?.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
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

            if (apduCommand == APDUUtil.apduCommandMap.get("GET_PROCESSING_OPTIONAL")) {
                val card = ResponseUtils.parseProcessingOptionResponse(isoDep, response)
                result += "track2: ${card?.track2} \n pan: ${card?.pan}\n exprire-date: ${card?.expireDate}"
                card?.let { fillData(it) }
            }

            preferenceProvider.putString(step, TypeConvertor.toHexString(response))
            //result += "\n$step: " + TypeConvertor.toHex(response)
        }
        runOnUiThread {
            //infoTv.setText("Card Response: $result")
        }
    }

    fun isNfcAvailable(context: Context): Boolean {
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager
        return nfcManager.defaultAdapter != null && nfcManager.defaultAdapter.isEnabled
    }

    fun fillData(card: Card) {
        val tvPan = findViewById(R.id.tv_card_number) as TextView
        val tvExpireDate = findViewById(R.id.tv_expired_date) as TextView
        tvPan.text = card?.pan
        tvExpireDate.text = card?.expireDate
    }
}
