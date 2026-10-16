package com.nadota.ai

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.Locale
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var history: TextView
    private lateinit var entry: EditText
    private var style = 0

    private val blue = Color.rgb(0, 102, 255)
    private val cement = Color.rgb(224, 226, 229)

    private fun preguntarIA(mensaje: String) {
        thread {
            var conexion: HttpURLConnection? = null

            try {
                conexion = URL(
                    "https://nadota-ai-server.onrender.com/chat"
                ).openConnection() as HttpURLConnection

                conexion.requestMethod = "POST"
                conexion.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                )
                conexion.connectTimeout = 60000
                conexion.readTimeout = 60000
                conexion.doOutput = true

                val datos = JSONObject()
                datos.put("mensaje", mensaje)

                conexion.outputStream.use {
                    it.write(datos.toString().toByteArray(Charsets.UTF_8))
                }

                val codigo = conexion.responseCode

                val respuesta = if (codigo in 200..299) {
                    conexion.inputStream.bufferedReader().use {
                        it.readText()
                    }
                } else {
                    conexion.errorStream?.bufferedReader()?.use {
                        it.readText()
                    } ?: "Error HTTP $codigo"
                }

                if (codigo !in 200..299) {
                    throw Exception("Servidor: $codigo - $respuesta")
                }

                val json = JSONObject(respuesta)

                val texto = json.optString(
                    "respuesta",
                    json.optString(
                        "response",
                        json.optString("reply", "")
                    )
                )

                if (texto.isBlank()) {
                    throw Exception("El servidor no devolvió una respuesta")
                }

                runOnUiThread {
                    history.append("\nNADOTA: $texto\n")

                    if (::tts.isInitialized) {
                        tts.speak(
                            texto,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "nadota"
                        )
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    history.append(
                        "\nNADOTA: No pude conectar con la IA: " +
                        "${e.message}\n"
                    )
                }
            } finally {
                conexion?.disconnect()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 30, 20, 20)
            setBackgroundColor(cement)
        }

        val title = TextView(this).apply {
            text = "✦ NADOTA AI"
            textSize = 30f
            setTextColor(blue)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Tu asistente, a tu manera"
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 18)
        }

        root.addView(subtitle)

        val voices = Spinner(this)

        val options = arrayOf(
            "Voz NADOTA (mexicana)",
            "Estilo alegre (inspirado en Thalía)",
            "Estilo potente (inspirado en Christina Aguilera)"
        )

        voices.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            options
        )

        voices.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    style = position
                    configureVoice()
                }
            }

        root.addView(voices)

        history = TextView(this).apply {
            text = """
                ¡Qué onda! Soy NADOTA AI 💙

                Creada por Sizo Alonso.

                Escríbeme algo y vamos a platicar.
            """.trimIndent()

            textSize = 17f
            setTextColor(Color.rgb(32, 36, 44))
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.WHITE)
        }

        val scroll = ScrollView(this).apply {
            addView(history)
        }

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        entry = EditText(this).apply {
            hint = "Escríbele a NADOTA..."
            setSingleLine(false)
            minLines = 2
        }

        root.addView(entry)

        val speak = Button(this).apply {
            text = "💙 ENVIAR A NADOTA"
            setTextColor(Color.WHITE)

            backgroundTintList =
                android.content.res.ColorStateList.valueOf(blue)
        }

        speak.setOnClickListener {
            val msg = entry.text.toString().trim()

            if (msg.isNotEmpty()) {
                history.append("\nTú: $msg\n")
                preguntarIA(msg)
                entry.text.clear()
            }
        }

        root.addView(speak)

        setContentView(root)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            configureVoice()

            tts.speak(
                "¡Qué onda! Soy NADOTA AI, creada por Sizo Alonso. ¿Qué se arma hoy?",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "presentacion"
            )
        }
    }

    private fun configureVoice() {
        if (!::tts.isInitialized) return

        when (style) {
            0 -> {
                tts.language = Locale("es", "MX")
                tts.setPitch(1.0f)
                tts.setSpeechRate(1.0f)
            }

            1 -> {
                tts.language = Locale("es", "MX")
                tts.setPitch(1.2f)
                tts.setSpeechRate(1.08f)
            }

            2 -> {
                tts.language = Locale("es", "US")
                tts.setPitch(0.9f)
                tts.setSpeechRate(0.95f)
            }
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        super.onDestroy()
    }
}
