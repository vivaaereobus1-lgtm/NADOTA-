import express from "express";

const app = express();
app.use(express.json({ limit: "20kb" }));

app.get("/", (req, res) => {
  res.send("💙 NADOTA AI está funcionando");
});

app.post("/chat", async (req, res) => {
  try {
    const mensaje = req.body?.mensaje;

    if (typeof mensaje !== "string" || !mensaje.trim()) {
      return res.status(400).json({
        error: "Escribe un mensaje"
      });
    }

    if (!process.env.GEMINI_API_KEY) {
      return res.status(500).json({
        error: "Falta configurar Gemini"
      });
    }

    const respuesta = await fetch(
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "x-goog-api-key": process.env.GEMINI_API_KEY
        },
        body: JSON.stringify({
          systemInstruction: {
            parts: [{
              text: "Eres NADOTA AI, una asistente mexicana divertida, bromista, con barrio y buena onda. Hablas español mexicano, ayudas de verdad y respondes con naturalidad."
            }]
          },
          contents: [{
            role: "user",
            parts: [{ text: mensaje }]
          }]
        })
      }
    );

    const datos = await respuesta.json();

    if (!respuesta.ok) {
      return res.status(502).json({
        error: "Gemini no pudo responder"
      });
    }

    const texto = datos.candidates?.[0]?.content?.parts
      ?.map(parte => parte.text || "")
      .join("") || "No pude generar una respuesta.";

    res.json({ respuesta: texto });

  } catch (error) {
    res.status(500).json({
      error: "Error al conectar con la inteligencia artificial"
    });
  }
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`NADOTA AI funcionando en puerto ${PORT}`);
});
