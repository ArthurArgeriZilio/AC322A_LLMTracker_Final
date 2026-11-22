package br.unaerp.llmtracker.data

import br.unaerp.llmtracker.model.Llm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

class RepositorioFalso(
    private val modelos: List<Llm>,
    var falhar: Boolean = false
) : LlmRepositorio {

    var chamadas = 0
        private set
    var ultimaForcou = false
        private set

    private val idsFavoritos = MutableStateFlow<Set<Int>>(emptySet())

    override fun favoritos(): StateFlow<Set<Int>> = idsFavoritos.asStateFlow()

    override suspend fun carregar(forcar: Boolean): List<Llm> {
        chamadas++
        ultimaForcou = forcar
        if (falhar) throw IOException("falha de teste")
        return modelos
    }

    override fun alternarFavorito(id: Int) {
        val atual = idsFavoritos.value
        idsFavoritos.value = if (id in atual) atual - id else atual + id
    }
}
