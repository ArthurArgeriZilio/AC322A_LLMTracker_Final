package br.unaerp.llmtracker.ui.detalhe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.unaerp.llmtracker.data.LlmRepositorio
import br.unaerp.llmtracker.model.Llm
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetalheUiState {
    data object Carregando : DetalheUiState
    data class Pronto(val modelo: Llm) : DetalheUiState
    data class Erro(val mensagem: String) : DetalheUiState
}

class DetalheViewModel(
    private val modeloId: Int,
    private val repositorio: LlmRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow<DetalheUiState>(DetalheUiState.Carregando)
    val estado: StateFlow<DetalheUiState> = _estado.asStateFlow()

    val favoritos: StateFlow<Set<Int>> = repositorio.favoritos()

    init {
        carregar()
    }

    fun carregar(forcar: Boolean = false) {
        viewModelScope.launch {
            _estado.value = DetalheUiState.Carregando
            try {
                val lista = repositorio.carregar(forcar)
                val modelo = lista.find { it.id == modeloId }
                _estado.value = if (modelo == null) {
                    DetalheUiState.Erro("Modelo não encontrado.")
                } else {
                    DetalheUiState.Pronto(modelo)
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                _estado.value = DetalheUiState.Erro("Não foi possível carregar o modelo.")
            }
        }
    }

    fun tentarNovamente() = carregar(forcar = true)

    fun alternarFavorito() {
        repositorio.alternarFavorito(modeloId)
    }

    companion object {
        fun factory(modeloId: Int, repositorio: LlmRepositorio): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DetalheViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return DetalheViewModel(modeloId, repositorio) as T
                    }
                    throw IllegalArgumentException("ViewModel não suportado")
                }
            }
    }
}
