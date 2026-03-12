# Demolição/Desmonolitização de projetos e distribuição de microsserviços

A transição de um sistema monolítico para uma arquitetura distribuída é, essencialmente, uma mudança de um modelo de chamadas de função em memória para comunicação via rede. O maior desafio não é apenas "quebrar o código", mas gerenciar a consistência eventual e a complexidade operacional.

# 1. Definições Fundamentais

## O Monolito: Unidade de Deployment e Runtime

Um monolito não é necessariamente um "código ruim". Tecnicamente, é um sistema onde todos os componentes funcionais (UI, regras de negócio, acesso a dados) são empacotados e implantados como uma única unidade de execução (um único processo no SO, um único .war ou .exe).

- **Vantagens Técnicas**: Baixa latência (chamadas em memória), transações ACID nativas e simplicidade de refatoração cross-module.
- **O Maior Problema** : O acoplamento temporal e de dados. Uma falha em um módulo simples pode derrubar o motor de pagamentos (falta de isolamento de falhas).

## Microsserviços: Autonomia e Contextos Delimitados

Microsserviços são uma abordagem de arquitetura distribuída onde o sistema é composto por serviços pequenos, independentes, que se comunicam via protocolos leves (HTTP/gRPC/Messaging).

- **A Realidade Técnica**: Cada serviço possui sua própria stack e, crucialmente, seu próprio esquema de banco de dados. O isolamento é o objetivo; a rede é o preço.

- **O Maior problema**: Gerenciar a consistência de dados sem transações distribuídas (2PC é raramente a resposta) e garantir a observabilidade em um ambiente fragmentado.

# 2.Estratégias de Segregação

## Padrão Strangler Fig (Estrangulamento)

Diferente de um replatform, o Strangler foca em coexistência. O segredo técnico aqui é o Request Routing Layer.

- **Definição Técnica**: Introdução de uma camada de indireção que atua como um roteador inteligente. Ela mapeia URIs para o legado ou para o novo serviço.
- **Sub-estratégia (Asset Trap)**: Identificar funcionalidades "folhas" na árvore de dependências do monolito. Começar por serviços que não possuem dependências internas pesadas.
- **Exemplo de Implementação**: Utilizar um Ingress Controller no Kubernetes ou um AWS Application Load Balancer com regras de roteamento baseadas em caminhos (/api/v2/\*).

## Decomposição por Capacidade de Negócio

Baseia-se no princípio de que o software deve espelhar os processos estáveis da organização.

- **Definição Técnica**: Agrupamento de módulos baseados no "Verbo" do negócio. Ex: Faturar, Entregar, Estocar.
- **Sub-estratégia** (Single Responsibility Principle a nível de Processo): Se um serviço de "Pedidos" começa a calcular impostos complexos, a capacidade de "Tributação" deve ser extraída para evitar o inchaço do serviço original.
- **Exemplo**: Em um banco, a capacidade de "Transferência" é isolada da "Consulta de Saldo" para permitir escalabilidade independente (o saldo é consultado $100\times$ mais do que transferências são feitas).

## Decomposição por Subdomínio (DDD)

Esta é a estratégia mais sofisticada, focando no Modelo de Domínio.

- **Definição Técnica**: Divisão baseada em Bounded Contexts. Cada contexto possui sua própria linguagem ubíqua e modelos de dados que não vazam para outros contextos.
- **Sub-estratégia (Context Mapping)**: Definir as relações entre os contextos. O contexto de "Vendas" é um Upstream para o contexto de "Envio" (Downstream).
- **Exemplo**: O objeto User no monolito contém password_hash e shipping_address. No DDD, o serviço de Identidade cuida do hash, e o serviço de Logística cuida do endereço. Eles são entidades distintas com o mesmo ID.

## Anti-Corruption Layer (ACL)

Essencial para evitar que o "débito técnico" do legado contamine o "greenfield".

- **Definição Técnica**: Um componente de tradução mediadora. Ele isola o modelo semântico do novo serviço das idiossincrasias do legado.
- **Sub-estratégia**: A ACL pode atuar como uma Facade (simplificando uma API complexa do monolito) ou como um Adapter (convertendo formatos como XML antigo para JSON moderno).
- **Exemplo**: O novo microsserviço usa eventos assíncronos (Kafka). A ACL escuta o banco de dados do monolito via CDC (Change Data Capture) e publica os eventos no formato esperado pelo novo serviço.

## Extração por "Zonas Quentes" (Hotspots)

Estratégia baseada em telemetria e análise de custo operacional.

- **Definição Técnica**: Identificação de módulos com alta contenção de recursos ou alta frequência de commits.
- **Sub-estratégia (Resource Isolation)**: Extrair módulos que exigem hardware específico (ex: processamento de imagem que se beneficia de GPUs) ou que possuem requisitos de conformidade diferentes (ex: módulo PCI de cartões).
- **Exemplo**: Se o módulo de "Relatórios" consome toda a memória do heap da JVM e causa Garbage Collection excessivo, afetando os usuários do sistema principal, ele deve ser o primeiro a ser isolado em seu próprio runtime.

# 3.O Desafio do Banco de Dados

O maior impedimento não é o código, mas a Base de Dados Compartilhada.

## Estratégia de Migração de Dados:

- Shared Database: Inicialmente, o microsserviço acessa as tabelas do monolito. (Perigoso, mas comum no início).
- Logical Separation: Criação de esquemas/views separados dentro do mesmo banco.
- Physical Separation: Migração para uma instância de banco dedicada.
- Sincronização Entre bancos: Utilizar Transactional Outbox Pattern para garantir que a atualização no banco do serviço e a notificação para o resto do ecossistema ocorram de forma atômica.

$$\text{Disponibilidade} \approx 1 - (1 - P)^n$$

(Onde $P$ é a probabilidade de falha de um serviço e $n$ é o número de serviços em uma chamada síncrona encadeada. Isso ilustra por que a comunicação assíncrona é vital para a resiliência em sistemas distribuídos).
