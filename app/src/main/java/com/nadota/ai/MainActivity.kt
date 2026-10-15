package com.nadota.aipackage com.nadota.ai

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
    private fun preguntarIA(mensaje: String) {
    thread {
        try {
            val conexion = URL(
                "https://nadota-ai-server.onrender.com/chat"
            ).openConnection() as HttpURLConnection

            conexion.requestMethod = "POST"
            conexion.setRequestProperty(
                "Content-Type", "application/json"
            )
            conexion.connectTimeout = 30000
            conexion.readTimeout = 30000
            conexion.doOutput = true

            val datos = JSONObject()
            datos.put("mensaje", mensaje)

            conexion.outputStream.use {
                it.write(datos.toString().toByteArray())
            }

            val respuesta = conexion.inputStream
                .bufferedReader().use { it.readText() }

            conexion.disconnect()

            val texto = JSONObject(respuesta)
                .getString("respuesta")

            runOnUiThread {
                history.append("\nNADOTA: $texto\n")
                tts.speak(
                    texto,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "nadota"
                )
            }
        } catch (e: Exception) {
            runOnUiThread {
                history.append(
                    "\nNADOTA: No pude conectar con la IA.\n"
                )
            }
        }
    }
    }

    private val blue = Color.rgb(0, 102, 255)
    private val cement = Color.rgb(224, 226, 229)

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
            "Voz Dulce (mexicana)",
            "Voz Potente (internacional)"
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
                ) {}

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
                ¡Hola! Soy NADOTA 💙

                Esta es mi primera versión.

                Ya puedo leer mensajes en voz alta.

                Próximamente tendré inteligencia
                artificial local.
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
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        entry = EditText(this).apply {
            hint = "Escribe algo para escuchar..."
            setSingleLine(false)
            minLines = 2
        }

        root.addView(entry)

        val speak = Button(this).apply {
            text = "🔊 HABLAR"
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

        tts.language =
            if (style == 0)
                Locale("es", "MX")
            else
                Locale("es", "US")

        tts.setPitch(
            if (style == 0) 1.15f else 0.9f
        )

        tts.setSpeechRate(
            if (style == 0) 1.05f else 0.92f
        )
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

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

        tts.language =
            if (style == 0)
                Locale("es", "MX")
            else
                Locale("es", "US")

        tts.setPitch(
            if (style == 0) 1.15f else 0.9f
        )

        tts.setSpeechRate(
            if (style == 0) 1.05f else 0.92f
        )
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
