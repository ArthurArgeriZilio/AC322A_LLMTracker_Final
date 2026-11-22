# Como o app cobre a avaliação

Arquitetura, em poucas linhas: a `MainActivity` é a única activity. O `AppNavegacao` guarda a pilha da Navigation 3 e mostra a tela do topo. O `CatalogoViewModel` e o `DetalheViewModel` expõem `StateFlow`. O `LlmRepositorioRemoto` faz a parte assíncrona (rede, atraso e JSON). A interface só observa o estado e chama funções do ViewModel.

## Itens da rubrica

1. **Três telas em Compose, sem layout XML.** `ListaTela`, `DetalheTela` e `FavoritosTela`. Não existe pasta `res/layout`. O XML que sobrou é manifesto, tema da janela, string do nome e o ícone.
2. **LazyColumn e LazyRow.** `ListaTela` usa `LazyColumn` para os modelos e `LazyRow` para os filtros. `FavoritosTela` e `DetalheTela` também usam `LazyColumn`.
3. **ViewModel e StateFlow.** `CatalogoViewModel.uiState` e as ações `atualizarBusca`, `atualizarFiltro`, `alternarFavorito`, `tentarNovamente`. `DetalheViewModel.estado` e `DetalheViewModel.favoritos`.
4. **Coroutine.** `LlmRepositorioRemoto.carregar` roda em `Dispatchers.IO`, chama `delay` e lê o JSON. A URL é o arquivo público do repositório. Se o download falhar, `lerCatalogo` abre `assets/llms.json`.
5. **Estados da tela.** `EstadoCarregando`, `EstadoErro` (botão com o texto "Tentar novamente") e `EstadoVazio`. A lista usa os três. Busca sem resultado cai em `CatalogoUiState.buscaSemResultado`. Favoritos vazios também usam `EstadoVazio`. O detalhe tem carregando, erro e conteúdo.
6. **Navigation 3, uma activity.** `MainActivity` chama `AppNavegacao`. As chaves são `Rota.Lista`, `Rota.Favoritos` e `Rota.Detalhe`. A pilha é `rememberNavBackStack` e o conteúdo sai no `NavDisplay`.
7. **Nulos.** Em `Llm`, `lancamento`, `licenca` e `link` são `String?`. `janelaContexto`, `parametrosBilhoes` e `nota` são `Int?`. A ficha usa `textoOuNaoInformado`, `textoJanela`, `textoParametros` e `textoNota`. O botão de link só aparece se `modelo.link` não for nulo. O Grok, no JSON, está com esses campos nulos.
8. **kotlinx.serialization.** `Llm` tem `@Serializable`. `LlmRepositorioRemoto.lerCatalogo` chama `Json.decodeFromString`. As rotas da Navigation 3 também são `@Serializable`.

## Obrigatório

Não há chave, senha ou token no projeto. `local.properties` e `secrets.properties` estão no `.gitignore`. Nenhum `Log` é chamado, então o Logcat não recebe dado do app.
