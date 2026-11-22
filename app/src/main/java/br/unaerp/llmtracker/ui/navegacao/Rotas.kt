package br.unaerp.llmtracker.ui.navegacao

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Rota : NavKey {
    @Serializable
    data object Lista : Rota

    @Serializable
    data object Favoritos : Rota

    @Serializable
    data class Detalhe(val id: Int) : Rota
}
