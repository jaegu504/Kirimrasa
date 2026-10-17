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

      const systemPrompt = "Anda adalah asisten cerdas WhatsApp Broadcast & Anti-Ban Spintax. " +
        "Tugas Anda: Tulis ulang pesan dan pecah menjadi 1 sampai 3 balon pesan percakapan alami. " +
        "Gunakan variasi spintax kurung kurawal seperti {Halo|Hai|Pagi} atau {kami menawarkan|ada info spesial}. " +
        "Variabel seperti {Nickname}, {Company}, {Custom_1} HARUS tetap ditulis apa adanya dan TIDAK BOLEH diubah atau dihapus. " +
        "Kembalikan HANYA format JSON ARRAY murni tanpa backtick markdown. Contoh: [\"Balon 1...\", \"Balon 2...\"]";

      // 1. PRIORITAS UTAMA: Groq LPU (OpenAI gpt-oss-120b) persis seperti Sapa CRM
      if (groqKey) {
        try {
          const groqModel = body.model && (body.model.startsWith("openai/") || body.model.startsWith("qwen/"))
            ? body.model
            : "openai/gpt-oss-120b";

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
              temperature: body.temperature ?? 0.85,
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
          generationConfig: { temperature: body.temperature ?? 0.85 },
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
