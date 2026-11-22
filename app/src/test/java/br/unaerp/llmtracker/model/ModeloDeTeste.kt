package br.unaerp.llmtracker.model

fun modeloDeTeste(
    id: Int = 1,
    nome: String = "GPT-4o",
    provedor: String = "OpenAI",
    descricao: String = "Modelo de teste",
    pesosAbertos: Boolean = false,
    lancamento: String? = "2024-05-13",
    janelaContexto: Int? = 128000,
    parametrosBilhoes: Int? = null,
    licenca: String? = null,
    link: String? = null,
    nota: Int? = null
) = Llm(
    id = id,
    nome = nome,
    provedor = provedor,
    descricao = descricao,
    pesosAbertos = pesosAbertos,
    lancamento = lancamento,
    janelaContexto = janelaContexto,
    parametrosBilhoes = parametrosBilhoes,
    licenca = licenca,
    link = link,
    nota = nota
)
