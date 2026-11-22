package br.unaerp.llmtracker.ui.detalhe

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.unaerp.llmtracker.ui.componentes.EstadoCarregando
import br.unaerp.llmtracker.ui.componentes.EstadoErro
import br.unaerp.llmtracker.ui.componentes.SecaoCard
import br.unaerp.llmtracker.ui.componentes.TitleAndValueRow
import br.unaerp.llmtracker.ui.texto.textoJanela
import br.unaerp.llmtracker.ui.texto.textoNota
import br.unaerp.llmtracker.ui.texto.textoOuNaoInformado
import br.unaerp.llmtracker.ui.texto.textoParametros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheTela(
    viewModel: DetalheViewModel,
    aoVoltar: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    val titulo = (estado as? DetalheUiState.Pronto)?.modelo?.nome ?: "Modelo"
    val favorito = (estado as? DetalheUiState.Pronto)?.modelo?.id?.let { it in favoritos } == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (estado is DetalheUiState.Pronto) {
                        IconButton(onClick = viewModel::alternarFavorito) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = if (favorito) {
                                    "Remover dos favoritos"
                                } else {
                                    "Adicionar aos favoritos"
                                },
                                tint = if (favorito) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val atual = estado) {
            DetalheUiState.Carregando -> EstadoCarregando(
                mensagem = "Carregando modelo...",
                modifier = Modifier.padding(padding)
            )

            is DetalheUiState.Erro -> EstadoErro(
                mensagem = atual.mensagem,
                aoTentarNovamente = viewModel::tentarNovamente,
                modifier = Modifier.padding(padding)
            )

            is DetalheUiState.Pronto -> {
                val modelo = atual.modelo
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = modelo.descricao,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    item {
                        SecaoCard(titulo = "Ficha") {
                            TitleAndValueRow("Provedor", modelo.provedor)
                            TitleAndValueRow("Lançamento", textoOuNaoInformado(modelo.lancamento))
                            TitleAndValueRow("Janela de contexto", textoJanela(modelo.janelaContexto))
                            TitleAndValueRow("Parâmetros", textoParametros(modelo.parametrosBilhoes))
                            TitleAndValueRow(
                                "Pesos abertos",
                                if (modelo.pesosAbertos) "Sim" else "Não"
                            )
                            TitleAndValueRow("Licença", textoOuNaoInformado(modelo.licenca))
                            TitleAndValueRow("Nota", textoNota(modelo.nota))
                            TitleAndValueRow("Link", textoOuNaoInformado(modelo.link))
                        }
                    }
                    val link = modelo.link
                    if (link != null) {
                        item {
                            Button(
                                onClick = { abrirLink(contexto, link) },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("Abrir página do modelo")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun abrirLink(contexto: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    try {
        contexto.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // Sem app para abrir o endereço. O link continua visível na ficha.
    }
}
