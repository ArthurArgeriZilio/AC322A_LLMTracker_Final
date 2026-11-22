package br.unaerp.llmtracker.ui.texto

import java.text.NumberFormat
import java.util.Locale

fun textoOuNaoInformado(valor: String?): String =
    valor?.takeIf { it.isNotBlank() } ?: "Não informado"

fun textoJanela(tokens: Int?): String {
    if (tokens == null) return "Não informado"
    val numero = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR")).format(tokens)
    return "$numero tokens"
}

fun textoParametros(bilhoes: Int?): String {
    if (bilhoes == null) return "Não informado"
    return "$bilhoes bilhões"
}

fun textoNota(nota: Int?): String {
    if (nota == null) return "Sem nota"
    return "$nota/5"
}
