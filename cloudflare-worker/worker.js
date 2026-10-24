/**
 * Kirimrasa AI rewrite proxy (Cloudflare Worker).
 * - Primary: Groq (openai/gpt-oss-120b), fallback: Google Gemini.
 * - Keys come only from Worker secrets GROQ_API_KEY / GEMINI_API_KEY.
 * - Always answers {"bubbles": [...]} so the app gets one shape whatever the provider.
 */

const TONES = {
  friendly:
    "Warm and friendly, like texting someone you know well. Natural, kind wording.",
  heartfelt:
    "Heartfelt and touching. Let real feeling show: how much the reader means to the writer, that they are missed or remembered, how much their prayer or support would mean. Use gentle, emotive words a person would truly write. Moving, but never dramatic, preachy or cheesy.",
  casual:
    "Relaxed and casual, like chatting with a close friend. Spoken, everyday words and the light slang people really use (Indonesian: nih, dong, ya, kok; Turkish: ya, be, valla).",
  polite:
    "Polite and respectful, for elders, teachers or customers. Courteous standard language with no slang or chat fillers (Indonesian: no nih, aja, dong, gimana, ngabarin, yaa; use mohon, terima kasih, sampaikan, kabar). Kind and personal, never stiff.",
  shorter:
    "Brief. Cut filler and repetition so each bubble is short, but keep every fact and the warmth.",
  max_variance:
    "Warm and friendly, like texting someone you know well.",
};

function buildSystemPrompt(tone, maxBubbles) {
  const variation =
    tone === "max_variance"
      ? "Put alternatives in most sentences (2 to 4 options each) so no two recipients get the same text."
      : "Add 1 to 3 small alternatives in the whole message, on greetings and common phrases.";
  return [
    "You rewrite a WhatsApp message that one person sends to people they personally know (friends, family, customers).",
    `YOUR MAIN JOB is the requested style: ${TONES[tone] || TONES.friendly}`,
    "Rephrase and reorder freely so the style is clearly felt. Do not just copy the draft. It must read like that person typed it on their phone: natural and human, never like an advert, a bot or a template.",
    "",
    "Rules:",
    "1. LANGUAGE: write in the same language as the draft (Indonesian stays Indonesian, Turkish stays Turkish, English stays English). Never translate. Keep how the writer addresses the reader (sen/siz, kamu/Anda, abi/abla).",
    "2. FACTS: keep every fact (names, dates, days, times, places, numbers, prices, links) and every request in the draft. You may add feeling and warmth, but no new facts, plans, offers or promises.",
    "3. GREETINGS AND FAITH: keep religious and cultural phrases as written (Assalamualaikum, Selamün aleyküm, insyaAllah, inşallah, Allah nasip ederse, Barakallah). An opening greeting stays an opening greeting: never write a reply greeting such as Waalaikumsalam or Aleykümselam.",
    "4. FORMAT: short sentences, keep the writer's emojis and add at most one more. No hashtags, no ALL CAPS, no sales language.",
    `5. BUBBLES: split into 1 to ${maxBubbles} chat bubbles, the way a person sends a few short messages in a row (greeting and how-are-you, then the news, then the request or closing). Fewer for short drafts, never pad. Each bubble is one complete thought.`,
    `6. VARIATION (so it never looks like a mass message): ${variation} Write them as {option A|option B}; every option must be natural, mean the same, be in the same language and fit the sentence.`,
    "7. VARIABLES: keep {Nickname}, {Company} and {Custom_1} exactly as written wherever the draft uses them. Never translate, rename, remove or add them, never put them inside {..|..}, and never invent other {Words}. Every { is closed by }, never nested.",
    'Reply with JSON only: {"bubbles": ["bubble 1", "bubble 2"]}',
  ].join("\n");
}

const VARIABLE = /\{(Nickname|Company|Custom_1)\}/g;
const REPLY_GREETING = /wa['’]?\s?alaikum|aleyk[uü]m\s?selam/i;

/** Rejects output that would reach people broken: lost variables, unbalanced braces, or a reply-greeting the draft never had. */
function isUsable(draft, bubbles) {
  const text = bubbles.join("\n");
  for (const v of new Set(draft.match(VARIABLE) || [])) if (!text.includes(v)) return false;
  for (const b of bubbles) {
    let depth = 0;
    for (const c of b) {
      if (c === "{") depth++;
      else if (c === "}") depth--;
      if (depth < 0 || depth > 1) return false;
    }
    if (depth !== 0) return false;
  }
  return !(REPLY_GREETING.test(text) && !REPLY_GREETING.test(draft));
}

function extractBubbles(text) {
  if (!text) return null;
  const cleaned = String(text).replace(/^```(json)?\s*/m, "").replace(/```\s*$/m, "").trim();
  try {
    const parsed = JSON.parse(cleaned);
    const list = Array.isArray(parsed) ? parsed : parsed?.bubbles;
    if (!Array.isArray(list)) return null;
    const bubbles = list.map((b) => String(b).trim()).filter(Boolean);
    return bubbles.length ? bubbles : null;
  } catch (_) {
    return null;
  }
}

export default {
  async fetch(request, env) {
    const cors = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization",
    };
    const reply = (obj, status = 200) =>
      new Response(JSON.stringify(obj), { status, headers: { ...cors, "Content-Type": "application/json" } });

    if (request.method === "OPTIONS") return new Response(null, { headers: cors });
    if (request.method !== "POST") return reply({ error: "Gunakan method POST." }, 405);

    try {
      const body = await request.json();
      const draft = String(body.message || body.prompt || "").trim();
      if (!draft) return reply({ error: "Parameter 'message' wajib dikirim." }, 400);
      if (draft.length > 4000) return reply({ error: "Pesan terlalu panjang." }, 413);

      const tone = TONES[body.tone] ? body.tone : "friendly";
      const maxBubbles = Math.min(Math.max(parseInt(body.maxBubbles, 10) || 3, 1), 5);
      const systemPrompt = buildSystemPrompt(tone, maxBubbles);

      // 1. Groq (fast). Model fixed server-side so callers can't pick a pricier one.
      let groqStatus = env.GROQ_API_KEY ? "no-answer" : "no-key";
      if (env.GROQ_API_KEY) {
        try {
          const resp = await fetch("https://api.groq.com/openai/v1/chat/completions", {
            method: "POST",
            headers: { Authorization: `Bearer ${env.GROQ_API_KEY}`, "Content-Type": "application/json" },
            body: JSON.stringify({
              model: "openai/gpt-oss-120b",
              messages: [
                { role: "system", content: systemPrompt },
                { role: "user", content: `Draft:\n${draft}` },
              ],
              temperature: 0.7,
              response_format: { type: "json_object" },
            }),
          });
          groqStatus = String(resp.status);
          if (resp.ok) {
            const data = await resp.json();
            const bubbles = extractBubbles(data?.choices?.[0]?.message?.content);
            if (bubbles && isUsable(draft, bubbles)) return reply({ bubbles, provider: "groq" });
          }
        } catch (_) {
          // fall through to Gemini
        }
      }

      // 2. Gemini fallback
      if (env.GEMINI_API_KEY) {
        for (const model of ["gemini-3.6-flash", "gemini-3.6-flash", "gemini-3.5-flash-lite"]) {
          try {
            const resp = await fetch(
              `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${env.GEMINI_API_KEY}`,
              {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                  systemInstruction: { parts: [{ text: systemPrompt }] },
                  contents: [{ role: "user", parts: [{ text: `Draft:\n${draft}` }] }],
                  generationConfig: {
                    temperature: 0.7,
                    responseMimeType: "application/json",
                    responseSchema: {
                      type: "OBJECT",
                      properties: { bubbles: { type: "ARRAY", items: { type: "STRING" } } },
                      required: ["bubbles"],
                    },
                  },
                }),
              }
            );
            if (resp.ok) {
              const data = await resp.json();
              const bubbles = extractBubbles(data?.candidates?.[0]?.content?.parts?.[0]?.text);
              if (bubbles && isUsable(draft, bubbles)) return reply({ bubbles, provider: model, groq: groqStatus });
            }
          } catch (_) {}
        }
      }

      return reply({ error: "Layanan AI sedang sibuk. Silakan coba kembali." }, 503);
    } catch (err) {
      return reply({ error: err.message || "Internal Server Error" }, 500);
    }
  },
};
