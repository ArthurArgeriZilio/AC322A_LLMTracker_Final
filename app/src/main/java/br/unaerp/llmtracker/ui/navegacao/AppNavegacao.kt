package br.unaerp.llmtracker.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import br.unaerp.llmtracker.LlmTrackerApp
import br.unaerp.llmtracker.ui.catalogo.CatalogoViewModel
import br.unaerp.llmtracker.ui.detalhe.DetalheTela
import br.unaerp.llmtracker.ui.detalhe.DetalheViewModel
import br.unaerp.llmtracker.ui.favoritos.FavoritosTela
import br.unaerp.llmtracker.ui.lista.ListaTela

@Composable
fun AppNavegacao() {
    val contexto = LocalContext.current
    val repositorio = (contexto.applicationContext as LlmTrackerApp).repositorio
    val catalogo: CatalogoViewModel = viewModel(
        factory = CatalogoViewModel.factory(repositorio)
    )
    val backStack = rememberNavBackStack(Rota.Lista)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Rota.Lista> {
                ListaTela(
                    viewModel = catalogo,
                    aoAbrirModelo = { id -> backStack.add(Rota.Detalhe(id)) },
                    aoAbrirFavoritos = {
                        if (backStack.lastOrNull() !is Rota.Favoritos) {
                            backStack.add(Rota.Favoritos)
                        }
                    }
                )
            }
            entry<Rota.Favoritos> {
                FavoritosTela(
                    viewModel = catalogo,
                    aoAbrirModelo = { id -> backStack.add(Rota.Detalhe(id)) },
                    aoVoltar = { backStack.removeLastOrNull() }
                )
            }
            entry<Rota.Detalhe> { rota ->
                val detalhe: DetalheViewModel = viewModel(
                    factory = DetalheViewModel.factory(rota.id, repositorio)
                )
                DetalheTela(
                    viewModel = detalhe,
                    aoVoltar = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
