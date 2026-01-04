# Escolhendo bancos de dados da forma correta

Se você é um desenvolvedor no início de carreira, poucas vezes deve ter se deparado com essa decisão. Ela parece simples, mas esconde uma complexidade muito maior. Uma decisão ruim pode significar mais trabalho no futuro, situações indesejadas, problemas de performance para o usuário e, em cenários mais extremos, migrações podem vir a acontecer.

Dito isso, vamos começar com algo não tão polêmico: **Não existe bala de prata**. A seleção de um banco de dados é um exercício de escolhas entre trade-offs. O objetivo não é encontrar a ferramenta perfeita, mas sim a ferramenta cujas limitações sejam aceitáveis para o contexto atual e futuro.

Não existe um banco de dados que resolva tudo. Eu sei que existem muitos amantes de MySQL, SQLServer, Oracle e MongoDB, mas cada caso é um caso. Então é sempre bom ter a mente aberta para entender que a decisão que você mais gosta nem sempre será a que mais se adapta.

Dito isso, este artigo também não é uma bala de prata, apenas uma forma de analisar situações envolvendo esse tipo de decisão. Se você tem outra abordagem que se adequa melhor à sua realidade, sinta-se livre para ler apenas por curiosidade.

## 1 - Não ignore requisitos não funcionais

É muito comum que desenvolvedores, em geral, tenham um olhar mais forte para o que deve ser feito. Se você já trabalhou em alguma fábrica de software, provavelmente chegam para você apenas as atividades que devem ser feitas, funções que devem ser implementadas, mas o mundo é mais amplo que isso.

Durante muito tempo achei que _"requisito não funcional"_ fosse coisa de literatura, mas depois de passar por situações onde disponibilidade e escalabilidade são importantes, percebi o mundo de decisões que estava ignorando por me limitar a não olhar para os impactos das coisas que eu fazia.

Para tomar esse tipo de decisão, você precisa entender tanto os requisitos funcionais quanto os não funcionais. O primeiro deles é o que deve ser feito, o segundo é como este deve performar [^1]. É importante olhar para a segunda parte e entender como essa decisão pode impactar no futuro.

## 2 - Use e abuse do teorema CAP[^2]

**_Para que possa ser entendido esse ponto, primeiro é necessário entender o que é um nó (e consequentemente o que é um cluster)_**. Um nó é (em resumo) uma unidade física ou lógica que faz parte de um cluster. Existem diversos tipos de nós, como os de armazenamento, coordenação e aplicação, e quando falamos de um cluster estamos falando sobre um conjunto de 2 ou mais nós de tipos diferentes operando de forma ordenada para que pareça ser "uma coisa só".

Caso você não conheça o teorema CAP, não o culpo, ele está muito mais próximo do design system do que do desenvolvimento em si. Esse teorema aborda 3 principais pilares de sistemas distribuídos, sendo eles:

### Consistência

Esse conceito é simples de entender, significa que todas as leituras buscando por um recurso retornam sempre o mesmo resultado, a não ser que uma ação modificadora do recurso seja explicitamente chamada para tal e essa ação seja explicitamente concluída. Essa definição anda lado a lado com a definição de Modelo de Consistência e Linearizabilidade[^3] em situações de acesso concorrente.

Consistência tem seus ônus e bônus. Tempos de escrita podem ser mais longos e performances de leitura podem ser prejudicadas. Em contrapartida, consistência evita casos de leituras de dados desatualizados, cenários de escritas conflitantes ou mesmo problemas de idempotência.

Em resumo, **_a consistência prega que todos os nós do banco de dados veem os mesmos dados ao mesmo tempo para a escrita mais recente_**. Isso garante que as respostas após essa escrita devem ter o dado mais atual.

### Disponibilidade (Availability)

Esse conceito pode ser um pouco enganoso. Quando pensamos em disponibilidade, normalmente pensamos que algo nunca deve estar fora do ar, mas aqui a aplicação é olhando para o recurso buscado. Essencialmente, cada requisição feita sobre o recurso deve retornar uma resposta, mesmo que ela não seja a mais recente.

Disponibilidade de recurso traz muitos benefícios que podem ser essenciais para seus sistemas, como alta responsividade e consistência eventual. Em contrapartida, para equilibrar, os ônus são visíveis: podem haver casos de inconsistência temporária visível ao usuário e até problemas envolvendo reconciliação de dados.

A disponibilidade pode ser resumida a uma única frase: **_Responda a todo custo, mesmo que não seja mais a resposta atual_**

### Particionamento Tolerável

Um sistema particionável é excelente quando sua aplicação tem uma atuação geográfica grande ou é mundial. Ele pode ter atuação em várias regiões e evitar algumas dores de cabeça envolvendo a queda da região escolhida para sua cloud [^4], aumentar o desempenho em locais distintos e ter uma escalabilidade muito mais flexível. Em contrapartida, ele tem não só uma complexidade maior como uma sobrecarga de rede e até conflitos de escrita em casos mais complexos.

Se eu fosse resumir em uma frase como fiz nos tópicos anteriores, eu diria que particionamento é uma das maiores aplicações do **_dividir para conquistar_**.

### Pensamentos sobre sua decisão

Olhando para as 3 letras do CAP, fica evidente que é impossível ter 100% dos 3. Se você escolher Consistência e Disponibilidade (CA), fica evidente que não dá para ter particionamento 100% efetivo sem alguma inconsistência ou conflito de escrita. Se escolher entre particionamento e disponibilidade (AP), num cenário com réplicas, é impossível que haja uma consistência de escrita em todas as réplicas. Caso escolha consistência e particionamento (CP), em algum momento fica propício acontecer um conflito com uma condição de corrida que impossibilite a disponibilidade.

Veja que eu falei que é impossível ter 100% dos 3, mas isso é realmente necessário? Até que ponto seu sistema é crítico o suficiente para necessitar de 100% dos 3? Essas são as perguntas que você terá que responder na hora de escolher. Um caso de estudo que pode te ajudar a entender a necessidade real do limite teórico e do prático para seu caso de uso é o caso do Google Spanner[^5][^6], um sistema CP que tem 99% de disponibilidade. Esse é um excelente caso de estudo e eu recomendo fortemente a leitura das referências bibliográficas.

Em 2010 foi proposto um princípio que complementasse o CAP, o princípio PACELC[^7]. Esse princípio, além de levar em consideração o CAP, também adiciona uma camada extra: análise de latências. Também é um caso que pode ser analisado caso o seu sistema demande.

## 3 - Caso ainda haja dúvidas: Analise a Consistência

Note que esta consistência é diferente da consistência do teorema CAP, apesar do mesmo nome.

### Consistência forte centralizada: o modelo _ACID_ [^8]

O modelo ACID é o modelo mais conhecido. Se você já estudou Ciência da Computação, Engenharia da Computação ou cursos tangentes, provavelmente um professor da faculdade já te explicou sobre ele, mas vamos relembrar.

A definição de **_Atomicidade_** é que uma transação de escrita no banco deve ser concluída inteiramente ou falhar inteiramente, nunca um meio termo. Isso implica numa definição simples: **_Ou o dado está lá ou não está_**

Já a **_Consistência_** diz que **_uma transação só pode ser executada se ela for válida de acordo com as regras estabelecidas_** para a ocorrência. Violações como regra de chave primária, estrangeira, composta ou validações de inserção impedirão com certeza que o dado seja inserido. Tente inserir um dado null em um campo NOT_NULL e falhe.

O **_Isolamento_** é uma regra fundamental. **_Cada transação deve executar de forma independente e linear_**. Isso indica que transações concorrentes devem ser tratadas de forma isolada e seguindo uma regra de ordem de chegada, é como se tudo acontecesse em série.

**_Durabilidade_** é outro pilar. Ele diz que uma vez que ocorra uma transação bem-sucedida, **_as alterações sobreviverão a qualquer falha sistêmica_**. Os registros depois de commitados e inseridos podem ser recuperados, e mesmo que haja uma corrupção o banco pode se reestruturar a partir de um WAL ou um Backup diferencial. (Por favor, ativem seus backups).

Bancos relacionais clássicos como Postgre, MySQL, Oracle e SQL Server estão nesse grupo.

### Consistência Forte Distribuída: _NewSQL_ [^9]

Esse modelo é um pouco mais recente, mas devido à criação de sistemas distribuídos e orientados a eventos, ele tem tomado um espaço considerável. Ele busca trazer as vantagens do ACID, mas traz ferramentas extras para adaptar para um contexto mais distribuído e múltiplos nós:

**_Consenso Distribuído (Paxos)_** é implementado para que haja consistência entre os nós. Essencialmente, o nó coordenador orienta os nós de armazenamento a realizarem a transação e o consenso é atingido quando a maioria dos nós realiza a transação.

**_Failover Automático_** para nós coordenadores é essencial para que o consenso distribuído possa acontecer. Neste caso, sempre que um nó coordenador falhar em alguma parte do processo, outro nó assume esse papel. Se todos os nós falharem, significa que o consenso não foi atingido.

**_Replicação síncrona_** é implementada para todos os nós que estão dentro da rede. Sempre que houver uma mudança principal em um nó, essa mudança só é de fato commitada no nó coordenador se for atingido um estado de consenso.

**_Controle de concorrência Multi-versão (MVCC)_** é implementado para garantir que os dados inseridos ou consultados nunca tenham indisponibilidade alta. Nunca há de fato uma atualização dos dados, e sim uma sobreposição, mas versões antigas continuam existindo para que não haja perda significativa de disponibilidade quando se tenta atingir uma consistência relativamente forte.

**_Transações distribuídas_** acontecem, pois cada nó tem um trecho significativo do contexto que será alterado.

Sistemas como FaunaDB, Google Spanner e Zookeeper estão aqui.

### Consistência Causal

Apesar de se usar pouco, esse é talvez o modelo que mais esteja presente no nosso dia a dia nas redes sociais. Ele preza que relações de causa e efeito devem ser vistas na ordem correta, mas se dois eventos não têm relação causal, a ordem deles pouco importa. Ele é o modelo mais útil quando a performance e a disponibilidade são as mais importantes, em contrapartida ele é extremamente difícil de se implementar. Por isso ele usa ferramentas como:

**_Rastreamento de dependências_** para garantir que os efeitos sejam aplicados apenas depois que a causa for criada. Para isso existem estratégias de versionamento temporário e dependência explícita. Assim, um nó só aplica a operação quando todas as dependências estiverem explicitamente presentes.

**_Propagação controlada_** para que, caso ações em lote cheguem, as operações sejam aplicadas na ordem correta (Controle de filhos órfãos). Em determinados sistemas é comum encontrar um buffering completo da operação.

**_Merge e resolução de conflitos_** são aplicados porque, devido à concorrência de acessos, podem surgir dados vindos de diferentes origens com relações válidas de causa-efeito no dado inserido.

Bancos como ChainReaction e Datomic estão aqui, mas outros bancos aplicam, como Redis e MongoDB.

### Consistência Variável

Boa parte dos bancos não relacionais pode ter configurações de consistência variável. Um sistema com consistência variável tenta, essencialmente, equilibrar o teorema CAP de forma tal que não necessariamente você terá 100% dos três, mas terá uma porcentagem suficiente que não prejudique a aplicação usuária.

A consistência variável funciona da seguinte maneira. Imagine uma aplicação distribuída com 8 Nós (Vamos chamar esse valor de **_N_**). Definimos a partir disso quantos desses nós devem responder quando uma escrita for criada (Vamos chamar esse valor de **_E_**). Depois disso também determinamos quantos desses nós devem responder a uma solicitação de leitura de dados (Vamos chamar de **_L_**). Note que podem haver nós que respondem pelos dois.

- Um banco terá uma consistência forte quando <br>

  $E + L > N$

Caso contrário, o banco de dados distribuído pode ser considerado de consistência eventual ou fraca.

Alguns bancos de dados distribuídos bem famosos estão deste lado da consistência variável, sendo eles Cassandra, Dynamo e Mongo.

### Consistência Eventual

Aqui boa parte dos bancos distribuidos se encontram. Inclusive bancos que são de consistencia variavel podem ser configurados com uma consistencia eventual (Fraca). Aqui o Tradeoff é claro: se tiramos a consistencia do teorema CAP, sobra disponibilidade e particionamento toleravel. Mas o que isso significa ?

Bancos de dados de consistencia eventual normalmente são usados em sistemas que não precisam ter uma ancoragem forte no dado mais recente. Redes sociais, notificações, sistemas de milhas, carrinhos de compra e até o processamento da multa de velocidade do seu carro. As principais características de sistemas de consistencia eventual são :

**_Tolerancia a Latencia_**: Como a disoponibilidade e o particionamento toleravel são os prinicpais fatores, sistemas de consistencia eventual costuman ser extremamente responsivos pra inserção de dados e busca. Pois ao inserir , voce garante que o dado foi inserido apenas naquele nó de rede, e na busca o primeiro nó disponivel mais proximo pode responder.

**_Propagação Assincrona_**: Como um dado é inserido apenas um nó por vez, uma propagação e validação é feito de forma assincrona em segundo plano. A propagação vai acontecer de fato depois da resposta ou da inserção, mas outros nós podem demorar o tempo que for para equalizar o dado, formando assim um:

**_Periodo de Inconsistencia_**: Existe um periodo de tempo (chamado de janela de inconsistencia) que é um periodo onde diferentes nós de rede podem ter diferentes respostas para o mesmo registro ou mesma chave.

Tendo um sistema de consistencia eventual, podemos não ter a resposta mais atual, mas agora nosso sistema pode ser distribuido geograficamente e as respostas acontecem no primeiro nó chamado, diminuindo drasticamente a latencia das respostas.

## 4 - BASE

BASE é um acrônimo. Ele representa as características priorizadas em sistemas distribuídos com alta escalabilidade. Normalmente ele é visto como o oposto do ACID, apesar de não necessariamente estarem na mesma caixinha comparativa. Suas letras significam:

### Basicamente disponível (Basically Available)

O sistema deve permanecer 100% operacional e responsivo para a maioria das requisições, mesmo quando ocorrem falhas externas, como falhas de rede, nós ou partições. A principal motivação é que, mesmo em períodos de indisponibilidade local por alguma falha crítica de infraestrutura, os usuários consigam acessar os dados (mesmo que não sejam os mais atuais).

A disponibilidade aqui é garantida por uma série de estratégias que atuam sobre a premissa de que ocasionalmente erros podem acontecer, e que esses erros devem ser tratados e ter suas devidas soluções de contorno. Dentre as estratégias estão:

**_Request Collapsing_**: O nome é um pouco autoexplicativo para quem conhece, mas essa estratégia visa aglomerar uma série de requisições para reduzir a pressão de I/O do sistema. Se todas as requisições forem iguais, mas vindas de origens diferentes, faz sentido fazer N requisições ou apenas uma e devolver o valor N vezes?

**_Load Shedding_**: Requisições são baseadas pelo seu nível de criticidade; existe uma triagem para cada requisição em seus momentos de entrada ou dependendo da origem. O sistema, ao atingir um nível alto de sobrecarga (um sinal), começa a descartar as requisições menos prioritárias a fim de salvar recursos para que o sistema possa ter uma margem para processar as requisições já existentes.

### Estado Suave/Flexível (Soft State)

O sistema pode mudar o estado de alguns dados com o tempo, mesmo que não haja de fato uma entrada ou escrita externa. Como o estado não pode ser garantido em todos os nós simultaneamente, durante o período de sincronização, dados podem convergir de forma que não necessariamente uma solicitação de escrita tenha sido realizada. Um cenário prático é, por exemplo, um nó de rede no Polo Sul ou réplicas read-only. Para garantir que essas mudanças aconteçam em nós que não foram solicitados, duas estratégias podem ser usadas (normalmente uma em detrimento da outra):

**_Broadcast_**: Um nó envia para todos os outros uma réplica do que foi de fato alterado (seja de uma forma diferencial ou integral), e os nós ficam responsáveis por alterarem a si mesmos. Quando um não consegue, essa requisição é repetida até que todos entrem em um estado de consenso. Normalmente se organiza em uma estrutura de árvore.

**_Fofoquinha (Gossip/Epidemic Protocol)_**: Ao invés do broadcast, aqui cada nó escolhe aleatoriamente um pequeno subconjunto de outros nós para trocar informações através de mecanismos de push (enviar novas alterações), pull (saber das novas alterações) ou push-pull (trocar diffs). Protocolos de fofoca são usados, por exemplo, no Apache Cassandra e no DynamoDB, e o motivo é simples: com esses "micro conjuntos" não definidos e flexíveis, a redundância de propagação é tão alta que é estatisticamente improvável que um nó não receba a alteração.

### Consistência Eventual (Eventual Consistency)

Esse é o ponto onde toda a parte da complexidade sistêmica entra. Por mais que "o que" a consistência eventual seja tenha sido explicado ali no tópico 3, o "como" com certeza pairou um pouco pela sua cabeça. Para resolver os problemas, as estratégias mais usadas são:

**_Mecanismos de Rastreamento Causal_**: São utilizados vetores de versionamento de alteração, baseados em relógios ou em versões em seus respectivos pods (Vector Clocks e Version Vectors), para que haja uma causalidade entre os eventos. Às vezes, para resolver conflitos entre as mesmas versões ou situações de escrita ao mesmo tempo, são utilizados merges semânticos.

**_Última Escrita Vence (LWW)_**: Uma estratégia de resolução de conflito baseada no timestamp da escrita. Normalmente sacrifica a causalidade em favor da simplicidade operacional, mas aumenta a entropia do sistema.

Como foi visto acima, existe uma troca clara entre se ter os dados mais recentes e se ter menos latência e mais disponibilidade. Foi a partir dessa ideia que sistemas PACELC começaram a surgir.

ACID e BASE são duas ideias completamente opostas, mas que também tratam de consistências. Então, por que BASE não é considerado um modelo de consistência? A resposta é mais complexa do que você pensa. Enquanto ACID é uma ideia voltada à aplicação de bancos de dados e tem seu foco em tratar um modelo transacional, BASE é uma filosofia arquitetural ou princípio de design. É só analisar o escopo: no ACID, o desenvolvedor delega sempre a complexidade ao banco, enquanto BASE sempre se dirige a sistemas distribuídos e inconsistentes, aceitando a natureza caótica dos sistemas distribuídos voltados a eventos e projetando o sistema para ser tolerante a dados obsoletos e apto a lidar com reconciliações.

## 5 - Filtro financeiro e operacional

Se tem duas coisas que podem te impedir de trabalhar com o banco de dados escolhido, essas coisas são o filtro financeiro (o custo total da propriedade) e o filtro operacional.

Custo Financeiro - Custo total da propriedade (TCO)
Para se analisar o quanto de fato um banco de dados vai custar e se realmente é o que é preciso para atender às necessidades financeiras, é preciso analisar:

- **_Custo da licença_**: Bancos de dados proprietários costumam ter licenças objetivamente caras. Dê valor aos bancos open source.
- **_Custo de escalabilidade_**: Avalie se a aplicação terá um crescimento linear ou exponencial de dados. Sistemas concentrados costumam se tornar muito caros quando escalam.
- **_Custo operacional_**: É necessário gastar com pessoas especialistas naquele assunto? É necessário fazer cursos sobre isso? Qual a curva de aprendizado? Qual o SLA para atendimento?
- **_Custo local (se houver)_**: Custos como energia, espaço físico, segurança e outros.
- **_Custo de cloud (se houver)_**: Existem modelos de cobrança diferentes caso se escolha um banco de dados relacional ou não relacional.
- **_Vendor Lock-in_**: Existe o perigo de você ficar tão dependente desse banco a ponto de não conseguir migrar para outro na hora que a carteira apertar?

[^1]: [IEEE Std 830-1998 - Recommended Practice for Software Requirements Specifications](https://ieeexplore.ieee.org/document/720574)
[^2]: [Designing Data-Intensive Applications, Ch. 9 - Kleppmann, Martin](https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/)
[^3]: [Linearizability: a correctness condition for concurrent objects - Herlihy, Maurice P.; Wing, Jeannette M. (1990).](https://dl.acm.org/doi/10.1145/78969.78972)
[^4]: [The Guardian: Amazon reveals cause of AWS outage that took everything from banks to smart beds offline](https://www.theguardian.com/technology/2025/oct/24/amazon-reveals-cause-of-aws-outage)
[^5]: [Inside Cloud Spanner and the CAP Theorem - Brewer, Eric.](https://cloud.google.com/blog/products/databases/inside-cloud-spanner-and-the-cap-theorem)
[^6]: [Spanner, TrueTime & The CAP Theorem - Brewer, Eric.](https://static.googleusercontent.com/media/research.google.com/pt-BR//pubs/archive/45855.pdf)
[^7]: [Consistency Tradeoffs in Modern Distributed Database System Design: CAP is Only Part of the Story - Abadi, Daniel J.](https://dl.acm.org/doi/10.1109/MC.2012.33)
[^8]: [Fundamentals of Database Systems - Elmasri, R. & Navathe, S.](https://www.pearson.com/en-us/subject-catalog/p/fundamentals-of-database-systems/P200000003546/9780137502523)
[^9]: [NewSQL: Towards Next-Generation Scalable RDBMS for Online Transaction Processing (OLTP) for Big Data Management - A. B. M. Moniruzzaman](https://arxiv.org/abs/1411.7343?utm_source=chatgpt.com)
