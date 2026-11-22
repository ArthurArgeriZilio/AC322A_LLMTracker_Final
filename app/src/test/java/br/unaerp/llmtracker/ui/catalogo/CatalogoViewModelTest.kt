package br.unaerp.llmtracker.ui.catalogo

import br.unaerp.llmtracker.data.RepositorioFalso
import br.unaerp.llmtracker.model.FiltroLista
import br.unaerp.llmtracker.model.modeloDeTeste
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest {

    @Test
    fun carregaCatalogoESaiDoLoading() = runVmTest {
        val repo = RepositorioFalso(listOf(modeloDeTeste()))
        val vm = CatalogoViewModel(repo)

        assertTrue(vm.uiState.value.carregando)
        advanceUntilIdle()

        val estado = vm.uiState.value
        assertFalse(estado.carregando)
        assertNull(estado.erro)
        assertEquals("GPT-4o", estado.modelos.single().nome)
        assertEquals(1, repo.chamadas)
    }

    @Test
    fun erroMostraMensagemETentarNovamenteRecupera() = runVmTest {
        val repo = RepositorioFalso(listOf(modeloDeTeste(nome = "Llama")), falhar = true)
        val vm = CatalogoViewModel(repo)
        advanceUntilIdle()

        assertEquals("Não foi possível carregar o catálogo.", vm.uiState.value.erro)
        assertTrue(vm.uiState.value.modelos.isEmpty())

        repo.falhar = false
        vm.tentarNovamente()
        advanceUntilIdle()

        assertNull(vm.uiState.value.erro)
        assertEquals("Llama", vm.uiState.value.modelos.single().nome)
        assertTrue(repo.ultimaForcou)
    }

    @Test
    fun buscaSemResultadoDeixaListaVazia() = runVmTest {
        val vm = CatalogoViewModel(RepositorioFalso(listOf(modeloDeTeste(nome = "Claude"))))
        advanceUntilIdle()

        vm.atualizarBusca("não existe")

        assertTrue(vm.uiState.value.buscaSemResultado)
        assertTrue(vm.uiState.value.listaFiltrada.isEmpty())
    }

    @Test
    fun filtroDePesosAbertos() = runVmTest {
        val aberta = modeloDeTeste(id = 1, nome = "Llama", pesosAbertos = true)
        val fechada = modeloDeTeste(id = 2, nome = "GPT-4o", pesosAbertos = false)
        val vm = CatalogoViewModel(RepositorioFalso(listOf(aberta, fechada)))
        advanceUntilIdle()

        vm.atualizarFiltro(FiltroLista.Abertos)

        assertEquals(listOf("Llama"), vm.uiState.value.listaFiltrada.map { it.nome })
    }

    @Test
    fun alternarFavoritoAtualizaOEstado() = runVmTest {
        val vm = CatalogoViewModel(RepositorioFalso(listOf(modeloDeTeste(id = 4))))
        advanceUntilIdle()

        vm.alternarFavorito(4)
        advanceUntilIdle()
        assertEquals(setOf(4), vm.uiState.value.idsFavoritos)
        assertEquals(1, vm.uiState.value.favoritos.size)

        vm.alternarFavorito(4)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.favoritos.isEmpty())
    }

    private fun runVmTest(bloco: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            bloco()
        } finally {
            Dispatchers.resetMain()
        }
    }
}
