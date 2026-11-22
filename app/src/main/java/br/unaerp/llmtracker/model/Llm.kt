package br.unaerp.llmtracker.model

import kotlinx.serialization.Serializable

/**
 * Modelo do catálogo. Os campos opcionais ficam nulos quando a fonte
 * não publica data, tamanho, licença, nota ou link.
 */
@Serializable
data class Llm(
    val id: Int,
    val nome: String,
    val provedor: String,
    val descricao: String,
    val pesosAbertos: Boolean,
    val lancamento: String? = null,
    val janelaContexto: Int? = null,
    val parametrosBilhoes: Int? = null,
    val licenca: String? = null,
    val link: String? = null,
    val nota: Int? = null
)
