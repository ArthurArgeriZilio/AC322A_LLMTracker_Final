package br.unaerp.llmtracker.data

import android.content.Context
import br.unaerp.llmtracker.model.Llm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/**
 * Baixa o catálogo de um JSON público. Se a rede falhar, usa o arquivo
 * em assets. Não há chave nem token: a URL é só o arquivo deste repositório.
 */
class LlmRepositorioRemoto(
    private val contexto: Context
) : LlmRepositorio {

    private val json = Json { ignoreUnknownKeys = true }
    private var cache: List<Llm>? = null
    private val idsFavoritos = MutableStateFlow<Set<Int>>(emptySet())

    override fun favoritos(): StateFlow<Set<Int>> = idsFavoritos.asStateFlow()

    override fun alternarFavorito(id: Int) {
        val atual = idsFavoritos.value
        idsFavoritos.value = if (id in atual) atual - id else atual + id
    }

    override suspend fun carregar(forcar: Boolean): List<Llm> = withContext(Dispatchers.IO) {
        if (!forcar) {
            cache?.let { return@withContext it }
        }
        delay(ATRASO_MS)
        val lista = lerCatalogo()
        cache = lista
        lista
    }

    private fun lerCatalogo(): List<Llm> {
        val remoto = runCatching { baixar(URL_CATALOGO) }.getOrNull()
        if (remoto != null) {
            val lista = runCatching { json.decodeFromString<List<Llm>>(remoto) }.getOrNull()
            if (lista != null) return lista
        }
        val local = contexto.assets.open(ARQUIVO_ASSETS)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        return json.decodeFromString(local)
    }

    private fun baixar(endereco: String): String {
        val conexao = (URL(endereco).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "LLMTracker")
        }
        try {
            if (conexao.responseCode !in 200..299) {
                throw java.io.IOException("Falha ao baixar o catálogo")
            }
            return conexao.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            conexao.disconnect()
        }
    }

    private companion object {
        const val ATRASO_MS = 800L
        const val ARQUIVO_ASSETS = "llms.json"
        const val URL_CATALOGO =
            "https://raw.githubusercontent.com/ArthurArgeriZilio/AC322A_LLMTracker_Final/main/app/src/main/assets/llms.json"
    }
}
