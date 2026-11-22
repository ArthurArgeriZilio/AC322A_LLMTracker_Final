# LLM Tracker

Trabalho final de Programação Mobile I (AC322A, UNAERP).

O app é um catálogo de modelos de linguagem. Dá para buscar, filtrar por pesos abertos ou fechados, abrir o detalhe de um modelo e marcar favoritos. Os dados vêm de um JSON público. Se o download falhar, o app usa o mesmo arquivo que está em `app/src/main/assets/llms.json`.

## Pré-requisitos

- Android Studio estável com suporte ao Android Gradle Plugin 9.3
- JDK 17 ou mais novo para rodar o Gradle (o Android Studio já inclui um). O código compila para Java 11.
- Android SDK 37. Se a IDE pedir para baixar a plataforma ou o build-tools, aceite.
- minSdk 24, targetSdk 37, compileSdk 37

## Como compilar e rodar

1. Clone este repositório.
2. No Android Studio, abra a pasta raiz do projeto (a que contém `settings.gradle.kts`). Não abra só a pasta `app`.
3. Espere o Gradle sincronizar.
4. Crie um emulador com API 24 ou superior, ou conecte um celular com a depuração USB ligada.
5. Selecione a configuração **app** e aperte Run.

Não precisa criar `local.properties` na mão: o Android Studio gera esse arquivo com o caminho do SDK. Ele está no `.gitignore`.

No terminal, na raiz do projeto:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

## Arquitetura

Uma única `MainActivity` troca as três telas (lista, detalhe e favoritos) com Navigation 3. A lista e os favoritos usam o `CatalogoViewModel`. O detalhe usa o `DetalheViewModel`. Os dois guardam o estado da tela em `StateFlow` e reagem às ações do usuário. O `LlmRepositorioRemoto` baixa o JSON com coroutines, espera um instante de propósito para o loading aparecer, e desserializa com kotlinx.serialization. Se a rede falhar, lê o JSON de assets. Favorito fica só na memória, enquanto o processo estiver vivo.

## Segredos

Este projeto não usa chave de API, senha, token nem banco. Nada disso entra no código, no histórico do Git ou no Logcat. O app não escreve log. A permissão `INTERNET` existe só para baixar o JSON público deste repositório (`llms.json` no GitHub). Não há arquivo `secrets.properties` para preencher.
