
#### Table of Content
```mermaid
graph LR
    A[Sistemas de Banco de Dados] --> B1[1- Propriedades Transacionais]
    A --> B2[2- Modelos de Consistência]
    A --> B3[3- Filosofias de Sistemas Distribuídos]
    A --> B4[4- Categorias de Bancos de Dados]
    A --> B5[5- Mecanismos Internos / Arquitetura]
```
##### Propriedades Transacionais

<details>
    <summary> Grafico de Conteudos </summary>

```mermaid
graph LR
    B1[1- Propriedades Transacionais] --> C1[ACID]
    C1 --> C1A[Atomicidade]
    C1 --> C1B[Consistência]
    C1 --> C1C[Isolamento]
    C1 --> C1D[Durabilidade]

    B1 --> C2[ACID 2.0]
    C2 --> C2A[Associativity]
    C2 --> C2B[Commutativity]
    C2 --> C2C[Idempotence]
    C2 --> C2D[Distribution]

    B1 --> C3[Níveis de Isolamento]
    C3 --> C3A[Read Uncommitted]
    C3 --> C3B[Read Committed]
    C3 --> C3C[Repeatable Read]
    C3 --> C3D[Snapshot Isolation]
    C3 --> C3E[Serializable]

    B1 --> C4[Anomalias Transacionais]
    C4 --> C4A[Dirty Read]
    C4 --> C4B[Non-Repeatable Read]
    C4 --> C4C[Phantom Read]
    C4 --> C4D[Write Skew]
```

</details>



##### Modelos de Consistencia


<details>
    <summary> Grafico de Conteudos </summary>

```mermaid
graph LR
    B2[2- Modelos de Consistência] --> D1[Consistência Forte]
    D1 --> D1A[Strict Consistency]
    D1 --> D1B[Linearizability]
    D1 --> D1C[Sequential Consistency]
    D1 --> D1D[Strict Serializability]

    B2 --> D2[Baseados em Snapshot]
    D2 --> D2A[Snapshot Isolation]
    D2 --> D2B[MVCC Read Consistency]

    B2 --> D3[Consistência Causal]
    D3 --> D3A[Causal]
    D3 --> D3B[Causal+]

    B2 --> D4[Consistência Fraca]
    D4 --> D4A[Eventual Consistency]
    D4 --> D4B[Weak Consistency]
    D4 --> D4C[Bounded Staleness]
    D4 --> D4D[Read-Your-Writes - Consistencia de Sesão ]
```

</details>


##### Filosofias de Sistemas Distribuídos

<details>
    <summary> Grafico de Conteudos </summary>

```mermaid
graph LR

    B3[3- Filosofias de Sistemas Distribuídos] --> E1[BASE]
    E1 --> E1A[Basically Available]
    E1 --> E1B[Soft State]
    E1 --> E1C[Eventual Consistency]

    B3 --> E2[CAP Theorem]
    E2 --> E2A[Consistency]
    E2 --> E2B[Availability]
    E2 --> E2C[Partition Tolerance]
```

</details>



##### Categorias de Bancos de Dados

<details>
    <summary> Grafico de Conteudos </summary>

```mermaid
graph LR
    B4[4- Categorias de Bancos de Dados] --> F1[SQL Tradicionais]
    F1 --> F1A[PostgreSQL]
    F1 --> F1B[MySQL]

    B4 --> F2[NoSQL]
    F2 --> F2A[Key-Value - Dynamo, Riak]
    F2 --> F2B[Document - MongoDB]
    F2 --> F2C[Column Family - Cassandra]
    F2 --> F2D[Graph - Neo4j]

    B4 --> F3[NewSQL]
    F3 --> F3A[CockroachDB]
    F3 --> F3B[Google Spanner]
    F3 --> F3C[TiDB]
    F3 --> F3D[VoltDB]
```
</details>



##### Mecanismos Internos e Arquiteturais
<details>
    <summary> Grafico de Conteudos </summary>

```mermaid
graph LR
    B5[5- Mecanismos Internos] --> G1[Controle de Concorrência]
    G1 --> G1A[2PL - Two-Phase Locking]
    G1 --> G1B[OCC - Optimistic Concurrency Control]
    G1 --> G1C[MVCC - Multiversion Concurrency Control]
    G1 --> G1D[SSI - Serializable Snapshot Isolation]

    B5 --> G2[Protocolos de Commit Distribuído]
    G2 --> G2A[2PCw - Two-Phase Commit]
    G2 --> G2B[3PC - Three-Phase Commit]
    G2 --> G2C[Paxos Commit]
    G2 --> G2D[Raft-based Commit]

    B5 --> G3[Replicação e Consenso]
    G3 --> G3A[Raft]
    G3 --> G3B[Paxos]
    G3 --> G3C[Gossip]
```
</details>


