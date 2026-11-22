package br.unaerp.llmtracker.data

import br.unaerp.llmtracker.model.Llm
import kotlinx.coroutines.flow.StateFlow

interface LlmRepositorio {
    suspend fun carregar(forcar: Boolean = false): List<Llm>
    fun favoritos(): StateFlow<Set<Int>>
    fun alternarFavorito(id: Int)
}
