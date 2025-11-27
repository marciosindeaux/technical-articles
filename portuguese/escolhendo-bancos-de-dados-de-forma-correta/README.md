# Escolhendo bancos de dados da forma correta

Se você é um desenvolvedor no início de carreira, poucas vezes deve ter se deparado com essa decisão. Ela parece simples, mas esconde uma complexidade muito maior. Uma decisão ruim pode significar mais trabalho no futuro, situações indesejadas, problemas de performance para o usuário e, em cenários mais extremos, migrações podem vir a acontecer.

Dito isso, vamos começar com algo não tão polêmico: **Não existe bala de prata**

Não existe um banco de dados que resolva tudo. Eu sei que existem muitos amantes de MySQL, SQLServer, Oracle e MongoDB, mas cada caso é um caso. Então é sempre bom ter a mente aberta para entender que a decisão que você mais gosta nem sempre será a que mais se adapta.

Dito isso, este artigo também não é uma bala de prata, apenas uma forma de analisar situações envolvendo esse tipo de decisão. Se você tem outra abordagem que se adequa melhor à sua realidade, sinta-se livre para ler apenas por curiosidade.

## 1 - Não ignore requisitos não funcionais

É muito comum que desenvolvedores, em geral, tenham um olhar mais forte para o que deve ser feito. Se você já trabalhou em alguma fábrica de software, provavelmente chegam para você apenas as atividades que devem ser feitas, funções que devem ser implementadas, mas o mundo é mais amplo que isso.

Durante muito tempo achei que *"requisito não funcional"* fosse coisa de literatura, mas depois de passar por situações onde disponibilidade e escalabilidade são importantes, percebi o mundo de decisões que estava ignorando por me limitar a não olhar para os impactos das coisas que eu fazia.

Para tomar esse tipo de decisão, você precisa entender tanto os requisitos funcionais quanto os não funcionais. O primeiro deles é o que deve ser feito, o segundo é como este deve performar [^1]. É importante olhar para a segunda parte e entender como essa decisão pode impactar no futuro.

## 2 - Use e abuse do teorema CAP[^2]

***Para que possa ser entendido esse ponto, primeiro é necessário entender o que é um nó (e consequentemente o que é um cluster)***. Um nó é (em resumo) uma unidade física ou lógica que faz parte de um cluster. Existem diversos tipos de nós, como os de armazenamento, coordenação e aplicação, e quando falamos de um cluster estamos falando sobre um conjunto de 2 ou mais nós de tipos diferentes operando de forma ordenada para que pareça ser "uma coisa só".

Caso você não conheça o teorema CAP, não o culpo, ele está muito mais próximo do design system do que do desenvolvimento em si. Esse teorema aborda 3 principais pilares de sistemas distribuídos, sendo eles:

### Consistência

Esse conceito é simples de entender, significa que todas as leituras buscando por um recurso retornam sempre o mesmo resultado, a não ser que uma ação modificadora do recurso seja explicitamente chamada para tal e essa ação seja explicitamente concluída. Essa definição anda lado a lado com a definição de Modelo de Consistência e Linearizabilidade[^3] em situações de acesso concorrente.

Consistência tem seus ônus e bônus. Tempos de escrita podem ser mais longos e performances de leitura podem ser prejudicadas. Em contrapartida, consistência evita casos de leituras de dados desatualizados, cenários de escritas conflitantes ou mesmo problemas de idempotência.

Em resumo, ***a consistência prega que todos os nós do banco de dados veem os mesmos dados ao mesmo tempo para a escrita mais recente***. Isso garante que as respostas após essa escrita devem ter o dado mais atual.

### Disponibilidade (Availability)

Esse conceito pode ser um pouco enganoso. Quando pensamos em disponibilidade, normalmente pensamos que algo nunca deve estar fora do ar, mas aqui a aplicação é olhando para o recurso buscado. Essencialmente, cada requisição feita sobre o recurso deve retornar uma resposta, mesmo que ela não seja a mais recente.

Disponibilidade de recurso traz muitos benefícios que podem ser essenciais para seus sistemas, como alta responsividade e consistência eventual. Em contrapartida, para equilibrar, os ônus são visíveis: podem haver casos de inconsistência temporária visível ao usuário e até problemas envolvendo reconciliação de dados.

A disponibilidade pode ser resumida a uma única frase: ***Responda a todo custo, mesmo que não seja mais a resposta atual***

### Particionamento Tolerável

Um sistema particionável é excelente quando sua aplicação tem uma atuação geográfica grande ou é mundial. Ele pode ter atuação em várias regiões e evitar algumas dores de cabeça envolvendo a queda da região escolhida para sua cloud [^4], aumentar o desempenho em locais distintos e ter uma escalabilidade muito mais flexível. Em contrapartida, ele tem não só uma complexidade maior como uma sobrecarga de rede e até conflitos de escrita em casos mais complexos.

Se eu fosse resumir em uma frase como fiz nos tópicos anteriores, eu diria que particionamento é uma das maiores aplicações do ***dividir para conquistar***.

### Pensamentos sobre sua decisão

Olhando para as 3 letras do CAP, fica evidente que é impossível ter 100% dos 3. Se você escolher Consistência e Disponibilidade (CA), fica evidente que não dá para ter particionamento 100% efetivo sem alguma inconsistência ou conflito de escrita. Se escolher entre particionamento e disponibilidade (AP), num cenário com réplicas, é impossível que haja uma consistência de escrita em todas as réplicas. Caso escolha consistência e particionamento (CP), em algum momento fica propício acontecer um conflito com uma condição de corrida que impossibilite a disponibilidade.

Veja que eu falei que é impossível ter 100% dos 3, mas isso é realmente necessário? Até que ponto seu sistema é crítico o suficiente para necessitar de 100% dos 3? Essas são as perguntas que você terá que responder na hora de escolher. Um caso de estudo que pode te ajudar a entender a necessidade real do limite teórico e do prático para seu caso de uso é o caso do Google Spanner[^5][^6], um sistema CP que tem 99% de disponibilidade. Esse é um excelente caso de estudo e eu recomendo fortemente a leitura das referências bibliográficas.

Em 2010 foi proposto um princípio que complementasse o CAP, o princípio PACELC[^7]. Esse princípio, além de levar em consideração o CAP, também adiciona uma camada extra: análise de latências. Também é um caso que pode ser analisado caso o seu sistema demande.

## 3 - Caso ainda haja dúvidas: Analise a Consistência

Note que esta consistência é diferente da consistência do teorema CAP, apesar do mesmo nome.

### Consistência forte centralizada: o modelo *ACID* [^8]
O modelo ACID é o modelo mais conhecido. Se você já estudou Ciência da Computação, Engenharia da Computação ou cursos tangentes, provavelmente um professor da faculdade já te explicou sobre ele, mas vamos relembrar.

A definição de ***Atomicidade*** é que uma transação de escrita no banco deve ser concluída inteiramente ou falhar inteiramente, nunca um meio termo. Isso implica numa definição simples: ***Ou o dado está lá ou não está***

Já a ***Consistência*** diz que ***uma transação só pode ser executada se ela for válida de acordo com as regras estabelecidas*** para a ocorrência. Violações como regra de chave primária, estrangeira, composta ou validações de inserção impedirão com certeza que o dado seja inserido. Tente inserir um dado null em um campo NOT_NULL e falhe.

O ***Isolamento*** é uma regra fundamental. ***Cada transação deve executar de forma independente e linear***. Isso indica que transações concorrentes devem ser tratadas de forma isolada e seguindo uma regra de ordem de chegada, é como se tudo acontecesse em série.

***Durabilidade*** é outro pilar. Ele diz que uma vez que ocorra uma transação bem-sucedida, ***as alterações sobreviverão a qualquer falha sistêmica***. Os registros depois de commitados e inseridos podem ser recuperados, e mesmo que haja uma corrupção o banco pode se reestruturar a partir de um WAL ou um Backup diferencial. (Por favor, ativem seus backups).

Bancos relacionais clássicos como Postgre, MySQL, Oracle e SQL Server estão nesse grupo.

### Consistência Forte Distribuída: *NewSQL* [^9]

Esse modelo é um pouco mais recente, mas devido à criação de sistemas distribuídos e orientados a eventos, ele tem tomado um espaço considerável. Ele busca trazer as vantagens do ACID, mas traz ferramentas extras para adaptar para um contexto mais distribuído e múltiplos nós:

***Consenso Distribuído (Paxos)*** é implementado para que haja consistência entre os nós. Essencialmente, o nó coordenador orienta os nós de armazenamento a realizarem a transação e o consenso é atingido quando a maioria dos nós realiza a transação.

***Failover Automático*** para nós coordenadores é essencial para que o consenso distribuído possa acontecer. Neste caso, sempre que um nó coordenador falhar em alguma parte do processo, outro nó assume esse papel. Se todos os nós falharem, significa que o consenso não foi atingido.

***Replicação síncrona*** é implementada para todos os nós que estão dentro da rede. Sempre que houver uma mudança principal em um nó, essa mudança só é de fato commitada no nó coordenador se for atingido um estado de consenso.

***Controle de concorrência Multi-versão (MVCC)*** é implementado para garantir que os dados inseridos ou consultados nunca tenham indisponibilidade alta. Nunca há de fato uma atualização dos dados, e sim uma sobreposição, mas versões antigas continuam existindo para que não haja perda significativa de disponibilidade quando se tenta atingir uma consistência relativamente forte.

***Transações distribuídas*** acontecem, pois cada nó tem um trecho significativo do contexto que será alterado.

Sistemas como FaunaDB, Google Spanner e Zookeeper estão aqui.

### Consistência Causal

Apesar de se usar pouco, esse é talvez o modelo que mais esteja presente no nosso dia a dia nas redes sociais. Ele preza que relações de causa e efeito devem ser vistas na ordem correta, mas se dois eventos não têm relação causal, a ordem deles pouco importa. Ele é o modelo mais útil quando a performance e a disponibilidade são as mais importantes, em contrapartida ele é extremamente difícil de se implementar. Por isso ele usa ferramentas como:

***Rastreamento de dependências*** para garantir que os efeitos sejam aplicados apenas depois que a causa for criada. Para isso existem estratégias de versionamento temporário e dependência explícita. Assim, um nó só aplica a operação quando todas as dependências estiverem explicitamente presentes.

***Propagação controlada*** para que, caso ações em lote cheguem, as operações sejam aplicadas na ordem correta (Controle de filhos órfãos). Em determinados sistemas é comum encontrar um buffering completo da operação.

***Merge e resolução de conflitos*** são aplicados porque, devido à concorrência de acessos, podem surgir dados vindos de diferentes origens com relações válidas de causa-efeito no dado inserido.

Bancos como ChainReaction e Datomic estão aqui, mas outros bancos aplicam, como Redis e MongoDB.

### Consistência Variável

Boa parte dos bancos não relacionais pode ter configurações de consistência variável. Um sistema com consistência variável tenta, essencialmente, equilibrar o teorema CAP de forma tal que não necessariamente você terá 100% dos três, mas terá uma porcentagem suficiente que não prejudique a aplicação usuária.

A consistência variável funciona da seguinte maneira. Imagine uma aplicação distribuída com 8 Nós (Vamos chamar esse valor de ***N***). Definimos a partir disso quantos desses nós devem responder quando uma escrita for criada (Vamos chamar esse valor de ***E***). Depois disso também determinamos quantos desses nós devem responder a uma solicitação de leitura de dados (Vamos chamar de ***L***). Note que podem haver nós que respondem pelos dois.

* Um banco terá uma consistência forte quando <br>
  $E + L > N$

Caso contrário, o banco de dados distribuído pode ser considerado de consistência eventual ou fraca.

Alguns bancos de dados distribuídos bem famosos estão deste lado da consistência variável, sendo eles Cassandra, Dynamo e Mongo.

___
**Tópicos futuros:**

### Consistência Eventual
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