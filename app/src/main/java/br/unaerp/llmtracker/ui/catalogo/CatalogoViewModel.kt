package br.unaerp.llmtracker.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.unaerp.llmtracker.data.LlmRepositorio
import br.unaerp.llmtracker.model.FiltroLista
import br.unaerp.llmtracker.model.Llm
import br.unaerp.llmtracker.model.combinaComBusca
import br.unaerp.llmtracker.model.passaNoFiltro
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val carregando: Boolean = true,
    val erro: String? = null,
    val modelos: List<Llm> = emptyList(),
    val busca: String = "",
    val filtro: FiltroLista = FiltroLista.Todos,
    val idsFavoritos: Set<Int> = emptySet()
) {
    val listaFiltrada: List<Llm>
        get() = modelos.filter { it.passaNoFiltro(filtro) && it.combinaComBusca(busca) }

    val favoritos: List<Llm>
        get() = modelos.filter { it.id in idsFavoritos }

    val catalogoVazio: Boolean
        get() = !carregando && erro == null && modelos.isEmpty()

    val buscaSemResultado: Boolean
        get() = !carregando && erro == null && modelos.isNotEmpty() && listaFiltrada.isEmpty()
}

class CatalogoViewModel(
    private val repositorio: LlmRepositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.favoritos().collect { ids ->
                _uiState.update { it.copy(idsFavoritos = ids) }
            }
        }
        carregar()
    }

    fun carregar(forcar: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(carregando = true, erro = null) }
            try {
                val lista = repositorio.carregar(forcar)
                _uiState.update {
                    it.copy(carregando = false, erro = null, modelos = lista)
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        carregando = false,
                        erro = "Não foi possível carregar o catálogo."
                    )
                }
            }
        }
    }

    fun tentarNovamente() = carregar(forcar = true)

    fun atualizarBusca(texto: String) {
        _uiState.update { it.copy(busca = texto) }
    }

    fun atualizarFiltro(filtro: FiltroLista) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun alternarFavorito(id: Int) {
        repositorio.alternarFavorito(id)
    }

    companion object {
        fun factory(repositorio: LlmRepositorio): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CatalogoViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return CatalogoViewModel(repositorio) as T
                    }
                    throw IllegalArgumentException("ViewModel não suportado")
                }
            }
    }
}
