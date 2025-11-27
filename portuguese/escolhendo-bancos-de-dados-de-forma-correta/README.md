# Escolhendo bancos de dados da forma correta. 

Se voce é um desenvolvedor no seu inicio de carreira, poucas vezes voce deve ter se deparado com essa decisão. Ela parece simples, mas esconde uma complexidade muito maior. Uma decisão ruim pode significar mais trabalho no futuro, situações indesejadas, problemas de performance para o usuario e em cenários mais extremos migrações podem vir a acontecer. 

Dito isso vamos começar com algo não tão polemico: **Não existe bala de prata**

Não existe um banco de dados que resolva tudo. Eu sei que existem muitos amantes de MySQL , SQLServer, Oracle e MongoDB, mas cada caso é um caso. Então é sempre bom ter a mente aberta para entender que a decisão que voce mais gosta nem sempre vai ser a decisão que mais se adapta. 

Dito isso, este artigo também não é uma bala de prata, apenas uma forma de se analizar situações envolvendo esse tipo de decisão. Se voce tem uma outra forma que se adequa melhor a sua realidade, sinta-se livre para ler apenas por curiosidade. 

## 1 - Não ignore Requisitos não funcionais

É muito comum que desenvolvedores em geral tenha um olhar mais forte para o que deve ser feito. Se voce ja trabalhou em alguma fabrica de software, provavelmente chegam pra voce apenas as atividades que devem ser feitas, funções que devem ser implementadas, mas o mundo é mais amplo que isso.

Durante muito tempo achei que *"requisito não funcional"* fosse coisa de literatura, mas depois de passar por situações onde disponibilidade e escalabilidade são importante, percebi o mundo de decisões que estava ignorando por me limitar a não olhar para os impactos das coisas que eu fazia. 

Pra tomar esse tipo de decisão, voce precisa entender tanto os requisitos funcionais, como os não funcionais. O primeiro deles é o que deve ser feito, o segundo é como este deve performar [^1]. É importante olhar para a segunda parte e entender como essa decisão pode impactar no futuro.


## 2 - Use e abuse do teorema CAP[^2]

***Para que possa ser entendido esse ponto, primeiro é necessário entender o que é um nó (e consequentemente o que é um cluster)***. Um nó é (em resumo) uma unidade fisica ou logica que faz parte de um cluster. Existem diversos tipos de nós, como os de armazenemto, coordenação e aplicação, e quanto falamos de um cluster estamos falando sobre um conjunto de 2 ou mais nós de tipos diferentes operando de forma ordenada para que pareça ser "uma coisa só".


Caso voce não conheça o teorema CAP, não o culpo, ele está muito mais proximo do design system do que do desenvolvimento em si. Esse teorema aborda 3 principais pilares de sistemas distribuidos sendo eles: 

### Consistencia 

Esse conceito é simples de entender, significa que todas as leituras buscando por um recurso retornam sempre o mesmo resultado a não ser que uma ação modificadora do recurso seja explicitamente chamada para tal e essa ação seja explicitamente concluida. Essa definição anda lado a lado com a definição de Modelo de Consistencia e Linearizabilidade[^3] em situações de acesso concorrente. 

Consistencia tem seus onus e bonus. Tempos de escrita podem ser mais longos e performances de leitura podem ser prejudicadas. Em contrapartida, consistencia evita casos de leituras de dados desatualizados, cenários de escritas conflitantes ou mesmo problemas de idempotencia. 

Em resumo, ***a consistencia prega que todos os nós do banco de dados veem os mesmos dados ao mesmo tempo para a escrita mais recente***. Isso garante que as respostas após essa escrita devem ter o dado mais atual. 

### Disponibilidade (Availability)

Esse conceito pode ser um pouco enganoso. Quando pensamos em disponibilidade normalmente pensamos que algo nunca deve estar fora do ar, mas aqui a aplicação é olhando para o recurso buscado. Essencialmente cada requisição feita sobre o recurso deve retornar uma resposta, mesmo que ela não seja a mais recente. 

Disponibilidade de recurso traz muitos beneficios que podem ser essenciais para seu sistemas, como um alta responsividade e consistencia eventual. Em contrapartida, para equilibrar, os onus são visiveis: Podem haver casos de incosistencia temporaria visivel ao usuario e até problemas envolvendo reconciliação de dados.

A disponibilidade pode ser resumida a uma unica frase: ***Responda a todo custo, mesmo que não seja mais a resposta atual***

### Particionamento Toleravel

Um sistema particionavel é excelente quando seu sistema tem uma atuação geografica grande ou é mundial, ele pode ter atuação em varias regiões e pode evitar algumas dores de cabeça envolvendo a queda da região escolhida para sua cloud [^4], aumentar o desempenho em locais distintos e ter uma escalabilidade muito mais flexivel. Em contrapartida ele tem não só uma complexidade maior como uma sobrecarga de rede e até conflitos de escrita em casos mais complexos.

Se eu fosse resumir em uma frase como fiz nos topicos anteriores eu diria que particionamento é uma das maiores aplicaçẽos do ***dividir para conquistar***.

### Pensamentos sobre sua decição.

Olhando para as 3 letras do CAP, fica evidente que é impossivel ter 100% dos 3. Se voce escolher Consistencia e Disponibilidade (CA) fica evidente que não da pra ter particionamento 100% efetivo sem alguma inconsistencia ou conflito de escrita. Se escolher entre particionamento e disponibilidade (AP), num cenário com replicas é impossivel que haja uma consistencia de escrita em todas as replicas. Caso escolha consistencia e particionamento (CP), em algum momento fica propicio de acontecer um conflito com uma condição de corrida que impossibilite a disponiobilidade. 

Veja que eu falei que é impossivel ter 100% dos 3, mas isso é realmente necessário ? Até que ponto seu sistema é critico o suficiente pra necessitar de 100% dos 3 ? Essas são as perguntas que voce terá que responder na hora de escolher. Um caso de estudo que pode te ajudar a entender a necessidade real do limite teorico e do pratico para seu caso de uso é o caso do Google Spanner[^5][^6], um sistema CP que tem 99% de disponibilidade. Esse é um excelente caso de estudo e eu recomendo fortemente a leitura das referencias bibliograficas.

Em 2010 foi proposto um principio que complementasse o CAP, o principio PACELC[^7]. Esse principio além de levar em consideração o CAP , também adiciona uma camada extra: Analise de latencias. Também é um caso que pode ser analidado caso o seu sistema demande.

## 3 - Caso ainda haja duvidas: Analise a Consistencia 

Note que esta consistencia é diferente da consistencia do teorema CAP, apesar do mesmo nome.

### Consistencia forte centralizada: o modelo *ACID* [^8]
O modelo ACID é o modelo mais conhecido. Se voce ja estudou Ciencia da computação, Engenharia da computação ou cursos tangentes, provavelmente um professor da faculdade ja te explicou sobre ele, mas vamos relembrar.

A definição de ***Atomicidade*** é que uma transação de escrita no banco deve ser concluida inteiramente ou falhar inteiramente, nunca um meio termo. Isso implica numa definição simples: ***Ou o dado está lá ou não está***

Já a ***Consistencia*** diz que ***uma transação só pode ser executada se ela for valida de acordo com as regras estabelecias*** para a ocorrencia. Violações de como regra de chave primaria, estrangeira, composta ou validações de inserção impedirão com certeza que o dado seja inserido. Tente inserir um dado null em um campo NOT_NULL e falhe.

O ***Isolamento*** uma regra fundamental. ***Cada transação deve executar de forma independente e linear***. Isso indica que transições concorrentes devem ser tratadas de forma isolada e seguindo uma regra de ordem de chegada, é como se tudo acontecesse em serie. 

***Durabilidade*** é outro pilar. Ele diz que uma vez que ocorra uma transação bem sucedida, ***as alterações sobreviverão a qualquer falha sistemica***. Os registros depois de commitados e inseridos podem ser recuperados, e mesmo que haja uma corrupção o banco pode se re-estruturar apartir de um WAL ou um Backup diferencial. (Por favor, ativem seus backups).

Bancos relacionais classicos como Postgre, MySQL, Oracle e SQL Server estão nesse grupo

### Consistencia Forte Distribuida : *NewSQL* [^9]

Esse modelo é um pouco mais recente, mas devido a criação de sistemas distribuidos e orientados a eventos ele tem tomado um espaço consideravel. Ele busca trazer as vantagens do ACID, mas trás ferramentas extras para adaptar para om contexto mais distribuido e multiplos nós : 


***Consenso Distribuido (Paxos)*** é implementado para que haja consistencia entre os nós. Essencialmente o nó coordenador orienta os nós de armazenamento a realizarem a transação e o consenso é atingido quando a maioria dos nós realiza a transação.

***Failover Automatico*** para nós coordenadores são essenciais para que o consenso distribuido possa acontecer. Neste caso , sempre que um nó coordenador falhar em alguma parte do processo, outro nó assume esse papel. Se todos os nós falharem significa que o consenso não foi atingido. 

***Replicação sincrona*** é implementada para todos os nós que estão dentro da rede. Sempre que houver uma mudança principal em um nó, essa mudança só é de fato commitada no nó coordenador se for atingido um estado de consenso

***Controle de concorrencia Multi-versão (MVCC)*** é implementado para garantir que os dados inseridos ou consultados nunca tenham indisponibilidade alta. Nunca há de fato uma atualização dos dados, e sim uma sobreposição, mas versões antigas continuam existindo para que não haja perda de significativa de disbonibilidade quando se tenta atingir uma consistencia relativiamente forte. 

***Transações distribuidas*** acontecem, pois cada nó tem um trecho significativo do contexto que será alterado. 

Sistemas como FaunaDB, Google Spanner e Zookeeper estão aqui.

### Consistencia Causal 

Apesar de se usar pouco , esse é talvez o modelo que mais esteja presente no nosso dia-a-dia nas redes sociais. Ele preza que relações de causa e efeito devem ser vistas na ordem correta, mas se dois eventos não tem relação causal, a ordem deles pouco importa. Ele é o modelo mais util quando a performance e a dispobilibilidade são as mais importantes, em contrapartida ele é extremamente dificil de se implementar. Por isso ele usa ferramentas como :

***Rastreamento de dependencias***  para garantir que os efeitos sejam aplicados apenas depois que a causa for criada. Pra isso existem estrategias de versionamento temporario e dependencia explicita. Assim um nó só aplica a operação quando todas as dependencias estiverem explicitamente presentes. 

***Propagação controlada*** Para que caso ações em lote cheguem e as operações sejam aplicadas na ordem correta (Controle de filhos orfãos). Em determinados sistemas é comum encontrar um buffering completo da operação

***Merge e resolução de conflitos*** são aplicados porque devido a concorrencia de acessos, podem se ter dados vindos de diferentes origens diferentes com relações validas de causa-efeito no dado inserido.


Bancos como ChainReaction e Datomic estão aqui , mas outros bancos aplicam, como Redis e Mongo DB


### Consistencia Variavel

Boa parte dos bancos não relacionais podem ter configurçaões de consistencia variavel. Um sistenma com consistencia variavel tenta, essencialmente, equilibrar o teorema CAP de forma tal que não necessariamente voce vai ter 100% dos tres, mas vai ter uma % suficiente que não necessáriamente prejudique a aplicação usuária. 

A consistencia Variavel funciona da seguinte maneira. Imagine uma aplicação distribuida com 8 Nós (Vamos chamar esse valor de ***N***). Definimos apartir disso quantos desses nós devem responder quando uma escrita for criada (Vamos chamar esse valor de ***E***).  Depois disso também determinamos quantos desses nós devem responder a uma solicitação de leitura de dados (Vamos chamar de ***L***). Note que podem haver nós que respondem pelos dois 


* Um banco terá uma consistencia forte quando <br>
  $E + L > N$

Caso contrario, o banco de dados distribuido pode ser considerado de consistencia eventual ou fraca.

Alguns bancos de dados distribuidos bem famosos estão deste lado da consistencia variavel, sendo eles Cassandra, Dynamo e Mongo. 

___
<b>Topicos futuros:</b>

### Consistencia Eventual
## 4 - BASE
## 5 - Filtro financeiro e operacional.
## 6 - Conclusão.




[^1]: [IEEE Std 830-1998 - Recommended Practice for Software Requirements Specifications](https://ieeexplore.ieee.org/document/720574)
[^2]: [Designing Data-Intensive Applications, Ch. 9 - Kleppmann, Martin](https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/)
[^3]: [Linearizability: a correctness condition for concurrent objects - Herlihy, Maurice P.; Wing, Jeannette M. (1990).](https://dl.acm.org/doi/10.1145/78969.78972)
[^4]: [The Guardian: Amazon reveals cause of AWS outage that took everything from banks to smart beds offline](https://www.theguardian.com/technology/2025/oct/24/amazon-reveals-cause-of-aws-outage)
[^5]: [Inside Cloud Spanner and the CAP Theorem - Brewer, Eric.](https://cloud.google.com/blog/products/databases/inside-cloud-spanner-and-the-cap-theorem)
[^6]: [Spanner, TrueTime & The CAP Theorem - Brewer, Eric.](https://static.googleusercontent.com/media/research.google.com/pt-BR//pubs/archive/45855.pdf)
[^7]: [Consistency Tradeoffs in Modern Distributed Database System Design: CAP is Only Part of the Story - Abadi, Daniel J.](https://dl.acm.org/doi/10.1109/MC.2012.33)
[^8]: [Fundamentals of Database Systems - Elmasri, R. & Navathe, S.](https://www.pearson.com/en-us/subject-catalog/p/fundamentals-of-database-systems/P200000003546/9780137502523)
[^9]: [NewSQL: Towards Next-Generation Scalable RDBMS for Online
Transaction Processing (OLTP) for Big Data Management - A. B. M. Moniruzzaman](https://arxiv.org/abs/1411.7343?utm_source=chatgpt.com)