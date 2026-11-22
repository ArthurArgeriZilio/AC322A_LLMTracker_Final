package br.unaerp.llmtracker.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.unaerp.llmtracker.model.Llm
import br.unaerp.llmtracker.ui.texto.textoJanela

@Composable
fun LlmCard(
    modelo: Llm,
    favorito: Boolean,
    aoClicar: () -> Unit,
    aoAlternarFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = aoClicar,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = modelo.nome, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = modelo.provedor,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = aoAlternarFavorito) {
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
            Text(
                text = modelo.descricao,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = resumo(modelo),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun resumo(modelo: Llm): String {
    val pesos = if (modelo.pesosAbertos) "pesos abertos" else "pesos fechados"
    val janela = modelo.janelaContexto?.let { textoJanela(it) } ?: "contexto não informado"
    return "$pesos · $janela"
}
