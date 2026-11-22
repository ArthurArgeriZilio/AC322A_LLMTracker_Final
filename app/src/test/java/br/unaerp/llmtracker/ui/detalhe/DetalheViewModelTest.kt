package br.unaerp.llmtracker.ui.detalhe

import br.unaerp.llmtracker.data.RepositorioFalso
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
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalheViewModelTest {

    @Test
    fun encontraOModeloPeloId() = runVmTest {
        val repo = RepositorioFalso(
            listOf(
                modeloDeTeste(id = 2, nome = "Claude", licenca = null, janelaContexto = null)
            )
        )
        val vm = DetalheViewModel(modeloId = 2, repositorio = repo)
        advanceUntilIdle()

        val pronto = vm.estado.value as DetalheUiState.Pronto
        assertEquals("Claude", pronto.modelo.nome)
        assertEquals(null, pronto.modelo.licenca)
        assertEquals(null, pronto.modelo.janelaContexto)
    }

    @Test
    fun idInexistenteViraErroETentarNovamenteMantem() = runVmTest {
        val repo = RepositorioFalso(listOf(modeloDeTeste(id = 1)))
        val vm = DetalheViewModel(modeloId = 99, repositorio = repo)
        advanceUntilIdle()

        val erro = vm.estado.value as DetalheUiState.Erro
        assertEquals("Modelo não encontrado.", erro.mensagem)

        vm.tentarNovamente()
        advanceUntilIdle()
        assertTrue(vm.estado.value is DetalheUiState.Erro)
        assertTrue(repo.ultimaForcou)
    }

    @Test
    fun falhaDeCargaMostraErro() = runVmTest {
        val repo = RepositorioFalso(listOf(modeloDeTeste()), falhar = true)
        val vm = DetalheViewModel(modeloId = 1, repositorio = repo)
        advanceUntilIdle()

        val erro = vm.estado.value as DetalheUiState.Erro
        assertEquals("Não foi possível carregar o modelo.", erro.mensagem)
    }

    @Test
    fun alternarFavoritoNoDetalhe() = runVmTest {
        val repo = RepositorioFalso(listOf(modeloDeTeste(id = 6)))
        val vm = DetalheViewModel(modeloId = 6, repositorio = repo)
        advanceUntilIdle()

        vm.alternarFavorito()
        advanceUntilIdle()
        assertTrue(6 in vm.favoritos.value)
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
