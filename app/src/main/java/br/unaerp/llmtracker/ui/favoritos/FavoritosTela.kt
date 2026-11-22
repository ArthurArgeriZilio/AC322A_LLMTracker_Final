package br.unaerp.llmtracker.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.unaerp.llmtracker.ui.catalogo.CatalogoViewModel
import br.unaerp.llmtracker.ui.componentes.EstadoCarregando
import br.unaerp.llmtracker.ui.componentes.EstadoErro
import br.unaerp.llmtracker.ui.componentes.EstadoVazio
import br.unaerp.llmtracker.ui.componentes.LlmCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosTela(
    viewModel: CatalogoViewModel,
    aoAbrirModelo: (Int) -> Unit,
    aoVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        val erro = estado.erro
        when {
            estado.carregando -> EstadoCarregando(
                mensagem = "Carregando catálogo...",
                modifier = Modifier.padding(padding)
            )

            erro != null -> EstadoErro(
                mensagem = erro,
                aoTentarNovamente = viewModel::tentarNovamente,
                modifier = Modifier.padding(padding)
            )

            estado.favoritos.isEmpty() -> EstadoVazio(
                mensagem = "Nenhum favorito ainda. Marque a estrela em um modelo.",
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.favoritos, key = { it.id }) { modelo ->
                    LlmCard(
                        modelo = modelo,
                        favorito = true,
                        aoClicar = { aoAbrirModelo(modelo.id) },
                        aoAlternarFavorito = { viewModel.alternarFavorito(modelo.id) }
                    )
                }
            }
        }
    }
}
