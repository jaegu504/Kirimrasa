/**
 * WhatsApp Bulk Broadcast AI Bridge - Cloudflare Worker Proxy
 * Menggunakan model yang persis sama dengan Sapa CRM:
 * - Prioritas 1: Groq LPU (openai/gpt-oss-120b - ultra cepat ~1 detik)
 * - Prioritas 2 (Cadangan): Google Gemini (gemini-3.5-flash-lite, gemini-3.6-flash)
 */
export default {
  async fetch(request, env) {
    const corsHeaders = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization",
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: corsHeaders });
    }

    if (request.method !== "POST") {
      return new Response(JSON.stringify({ error: "Gunakan method POST." }), {
        status: 405,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    try {
      const groqKey = env.GROQ_API_KEY || "";
      const geminiKey = env.GEMINI_API_KEY || "";

      const body = await request.json();
      const prompt = body.prompt || body.message;

      if (!prompt) {
        return new Response(JSON.stringify({ error: "Parameter 'prompt' atau 'message' wajib dikirim." }), {
          status: 400,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        });
      }

      // Honour what the app sends: output language, tone and bubble count (were ignored before)
      const language = String(body.language || "ID").toUpperCase() === "TR" ? "Turkish (Türkçe)" : "Indonesian (Bahasa Indonesia)";
      const toneGuide = {
        friendly: "warm, friendly and casual, like texting someone you know",
        shorter: "short and to the point; cut filler, keep the meaning",
        max_variance: "friendly, with as many spintax alternatives as reads naturally",
      }[body.tone] || "warm, friendly and casual";
      const maxBubbles = Math.min(Math.max(parseInt(body.maxBubbles, 10) || 3, 1), 3);

      const systemPrompt =
        "You rewrite WhatsApp messages that a person sends to people they know. " +
        `Write in ${language}. Tone: ${toneGuide}. ` +
        `Split the message into 1 to ${maxBubbles} short chat bubbles that read naturally one after another. ` +
        "Add spintax alternatives in curly braces separated by |, e.g. {Halo|Hai}, so each recipient gets slightly different wording. " +
        "Keep these variables exactly as written and never translate, rename or remove them: {Nickname}, {Company}, {Custom_1}. " +
        "Do not invent any other {Variable}. Every { must be closed by }. Never nest braces. " +
        'Reply with JSON only, shaped {"bubbles": ["bubble 1", "bubble 2"]}.';

      // 1. PRIORITAS UTAMA: Groq LPU (OpenAI gpt-oss-120b) persis seperti Sapa CRM
      if (groqKey) {
        try {
          // Model is fixed server-side; callers must not pick (and pay for) other models
          const groqModel = "openai/gpt-oss-120b";

          const groqResp = await fetch("https://api.groq.com/openai/v1/chat/completions", {
            method: "POST",
            headers: {
              "Authorization": `Bearer ${groqKey}`,
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              model: groqModel,
              messages: [
                {
                  role: "system",
                  content: systemPrompt,
                },
                {
                  role: "user",
                  content: prompt,
                },
              ],
              temperature: 0.85,
              response_format: { type: "json_object" },
            }),
          });

          if (groqResp.ok) {
            const data = await groqResp.json();
            if (data?.choices?.[0]?.message?.content) {
              return new Response(JSON.stringify(data), {
                status: 200,
                headers: { ...corsHeaders, "Content-Type": "application/json" },
              });
            }
          }
        } catch (_groqErr) {
          // Lanjut ke fallback Gemini jika Groq bermasalah
        }
      }

      // 2. CADANGAN OTOMATIS: Google Gemini (gemini-3.5-flash-lite, gemini-3.6-flash) persis seperti Sapa CRM
      if (geminiKey) {
        const geminiModels = ["gemini-3.5-flash-lite", "gemini-3.6-flash"];
        const geminiPayload = {
          contents: [
            {
              role: "user",
              parts: [{ text: `${systemPrompt}\n\nDraf pesan:\n${prompt}` }],
            },
          ],
          generationConfig: {
            temperature: 0.85,
            responseMimeType: "application/json",
            responseSchema: {
              type: "OBJECT",
              properties: { bubbles: { type: "ARRAY", items: { type: "STRING" } } },
              required: ["bubbles"],
            },
          },
        };

        for (const m of geminiModels) {
          try {
            const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/${m}:generateContent?key=${geminiKey}`;
            const geminiResp = await fetch(geminiUrl, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(geminiPayload),
            });
            if (geminiResp.ok) {
              const data = await geminiResp.json();
              if (data?.candidates?.[0]?.content?.parts?.[0]?.text) {
                return new Response(JSON.stringify(data), {
                  status: 200,
                  headers: { ...corsHeaders, "Content-Type": "application/json" },
                });
              }
            }
          } catch (_e) {}
        }
      }

      return new Response(
        JSON.stringify({ error: "Layanan AI sedang sibuk. Silakan coba kembali." }),
        { status: 503, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    } catch (err) {
      return new Response(JSON.stringify({ error: err.message || "Internal Server Error" }), {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }
  },
};
