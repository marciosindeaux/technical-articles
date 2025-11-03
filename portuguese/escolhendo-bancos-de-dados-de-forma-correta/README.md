# Escolhendo bancos de dados da forma correta. 

Se voce é um desenvolvedor no seu inicio de carreira, poucas vezes voce deve ter se deparado com essa decisão. Ela parece simples, mas esconde uma complexidade muito maior. Uma decisão ruim pode significar mais trabalho no futuro, situações indesejadas, problemas de performance para o usuario e em cenários mais extremos migrações podem vir a acontecer. 

Dito isso vamos começar com algo não tão polemico: **Não existe bala de prata**

Não existe um banco de dados que resolva tudo. Eu sei que existem muitos amantes de MySQL , SQLServer, Oracle e MongoDB, mas cada caso é um caso. Então é sempre bom ter a mente aberta para entender que a decisão que voce mais gosta nem sempre vai ser a decisão que mais se adapta. 

Dito isso, este artigo também não é uma bala de prata, apenas uma forma de se analizar situações envolvendo esse tipo de decisão. Se voce tem uma outra forma que se adequa melhor a sua realidade, sinta-se livre para ler apenas por curiosidade. 

## 1 - Não ignore Requisitos não funcionais

É muito comum que desenvolvedores em geral tenha um olhar mais forte para o que deve ser feito. Se voce ja trabalhou em alguma fabrica de software, provavelmente chegam pra voce apenas as atividades que devem ser feitas, funções que devem ser implementadas, mas o mundo é mais amplo que isso.

Durante muito tempo achei que *"requisito não funcional"* fosse coisa de literatura, mas depois de passar por situações onde disponibilidade e escalabilidade são importante, percebi o mundo de decisões que estava ignorando por me limitar a não olhar para os impactos das coisas que eu fazia. 

Pra tomar esse tipo de decisão, voce precisa entender tanto os requisitos funcionais, como os não funcionais. O primeiro deles é o que deve ser feito, o segundo é como este deve performar [^1]. É importante olhar para a segunda parte e entender como essa decisão pode impactar no futuro.


## Use e abuse do teorema CAP

Caso voce não conheça esse termo, não o culpo, ele está muito mais proximo do design system do que do desenvolvimento em si. Esse teorema aborda 3 principais pilares de sistemas distribuidos sendo eles: 

### Consistencia 

Esse conceito é simples de entender, significa que todas as leituras buscando por um recurso retornam sempre o mesmo resultado a não ser que uma ação modificadora do recurso seja explicitamente chamada para tal e essa ação seja explicitamente concluida. Essa definição anda lado a lado com a definição de Modelo de Consistencia e Linearizabilidade[^2] em situações de acesso concorrente. 

Consistencia tem seus onus e bonus. Tempos de escrita podem ser mais longos e performances de leitura podem ser prejudicadas. Em contrapartida, consistencia evita casos de leituras de dados desatualizados, cenários de escritas conflitantes ou mesmo problemas de idempotencia. 

Em resumo, ***a consistencia prega que todos os nós do banco de dados veem os mesmos dados ao mesmo tempo para a escrita mais recente***. Isso garante que as respostas após essa escrita devem ter o dado mais atual. 

### Disponibilidade (Availability)

Esse conceito pode ser um pouco enganoso. Quando pensamos em disponibilidade normalmente pensamos que algo nunca deve estar fora do ar, mas aqui a aplicação é olhando para o recurso buscado. Essencialmente cada requisição feita sobre o recurso deve retornar uma resposta, mesmo que ela não seja a mais recente. 

Disponibilidade de recurso traz muitos beneficios que podem ser essenciais para seu sistemas, como um alta responsividade e consistencia eventual. Em contrapartida, para equilibrar, os onus são visiveis: Podem haver casos de incosistencia temporaria visivel ao usuario e até problemas envolvendo reconciliação de dados.

A disponibilidade pode ser resumida a uma unica frase: ***Responda a todo custo, mesmo que não seja mais a resposta atual***


[^1]: IEEE Std 830-1998
[^2]: Linearizability: a correctness condition for concurrent objects - Herlihy, Maurice P.; Wing, Jeannette M. (1990). 