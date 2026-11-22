package br.unaerp.llmtracker.model

enum class FiltroLista(val rotulo: String) {
    Todos("Todos"),
    Abertos("Pesos abertos"),
    Fechados("Pesos fechados")
}

fun Llm.passaNoFiltro(filtro: FiltroLista): Boolean = when (filtro) {
    FiltroLista.Todos -> true
    FiltroLista.Abertos -> pesosAbertos
    FiltroLista.Fechados -> !pesosAbertos
}

fun Llm.combinaComBusca(busca: String): Boolean {
    val texto = busca.trim()
    if (texto.isEmpty()) return true
    val campos = listOfNotNull(nome, provedor, descricao, licenca, lancamento)
    return campos.any { it.contains(texto, ignoreCase = true) }
}
