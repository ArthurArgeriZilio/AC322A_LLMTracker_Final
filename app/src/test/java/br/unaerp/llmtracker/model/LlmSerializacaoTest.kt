package br.unaerp.llmtracker.model

import br.unaerp.llmtracker.ui.texto.textoJanela
import br.unaerp.llmtracker.ui.texto.textoNota
import br.unaerp.llmtracker.ui.texto.textoOuNaoInformado
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LlmSerializacaoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun campoAusenteViraNulo() {
        val bruto = """
            {"id":1,"nome":"X","provedor":"Y","descricao":"Z","pesosAbertos":true}
        """.trimIndent()
        val llm = json.decodeFromString<Llm>(bruto)

        assertEquals("X", llm.nome)
        assertNull(llm.lancamento)
        assertNull(llm.janelaContexto)
        assertNull(llm.parametrosBilhoes)
        assertNull(llm.licenca)
        assertNull(llm.link)
        assertNull(llm.nota)
    }

    @Test
    fun catalogoDeAssetsTemNulosEDesserializa() {
        val arquivo = listOf(
            File("src/main/assets/llms.json"),
            File("app/src/main/assets/llms.json")
        ).firstOrNull { it.exists() }
        assertNotNull("llms.json não encontrado", arquivo)

        val lista = json.decodeFromString<List<Llm>>(arquivo!!.readText())
        assertTrue(lista.size >= 8)
        assertTrue(lista.any { it.janelaContexto == null })
        assertTrue(lista.any { it.parametrosBilhoes == null })
        assertTrue(lista.any { it.lancamento == null })
        assertTrue(lista.any { it.licenca == null })
        assertTrue(lista.any { it.link == null })
        assertTrue(lista.any { it.nota == null })
    }

    @Test
    fun buscaNaoQuebraComLicencaNula() {
        val grok = modeloDeTeste(nome = "Grok", licenca = null, lancamento = null)
        assertTrue(grok.combinaComBusca("grok"))
        assertTrue(!grok.combinaComBusca("apache"))
    }

    @Test
    fun textoNuloViraNaoInformado() {
        assertEquals("Não informado", textoJanela(null))
        assertEquals("Não informado", textoOuNaoInformado(null))
        assertEquals("Não informado", textoOuNaoInformado("   "))
        assertEquals("Sem nota", textoNota(null))
        assertEquals("128.000 tokens", textoJanela(128000))
    }
}
