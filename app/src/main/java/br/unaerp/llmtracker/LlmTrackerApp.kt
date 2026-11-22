package br.unaerp.llmtracker

import android.app.Application
import br.unaerp.llmtracker.data.LlmRepositorio
import br.unaerp.llmtracker.data.LlmRepositorioRemoto

class LlmTrackerApp : Application() {
    lateinit var repositorio: LlmRepositorio
        private set

    override fun onCreate() {
        super.onCreate()
        repositorio = LlmRepositorioRemoto(applicationContext)
    }
}
