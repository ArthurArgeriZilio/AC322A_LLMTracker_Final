package br.unaerp.llmtracker.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.unaerp.llmtracker.model.FiltroLista
import br.unaerp.llmtracker.ui.catalogo.CatalogoViewModel
import br.unaerp.llmtracker.ui.componentes.EstadoCarregando
import br.unaerp.llmtracker.ui.componentes.EstadoErro
import br.unaerp.llmtracker.ui.componentes.EstadoVazio
import br.unaerp.llmtracker.ui.componentes.LlmCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTela(
    viewModel: CatalogoViewModel,
    aoAbrirModelo: (Int) -> Unit,
    aoAbrirFavoritos: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LLM Tracker") },
                actions = {
                    IconButton(onClick = aoAbrirFavoritos) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favoritos"
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

            estado.catalogoVazio -> EstadoVazio(
                mensagem = "Nenhum modelo no catálogo.",
                modifier = Modifier.padding(padding)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                OutlinedTextField(
                    value = estado.busca,
                    onValueChange = viewModel::atualizarBusca,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar modelo ou provedor") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    singleLine = true
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FiltroLista.entries) { filtro ->
                        FilterChip(
                            selected = filtro == estado.filtro,
                            onClick = { viewModel.atualizarFiltro(filtro) },
                            label = { Text(filtro.rotulo) }
                        )
                    }
                }
                if (estado.buscaSemResultado) {
                    EstadoVazio(
                        mensagem = "Nenhum modelo encontrado para \"${estado.busca.trim()}\".",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estado.listaFiltrada, key = { it.id }) { modelo ->
                            LlmCard(
                                modelo = modelo,
                                favorito = modelo.id in estado.idsFavoritos,
                                aoClicar = { aoAbrirModelo(modelo.id) },
                                aoAlternarFavorito = { viewModel.alternarFavorito(modelo.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
