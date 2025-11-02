# Lendo arquivos JSON no Java de forma simplificada

_*Artigo originalmente escrito em 2020*_

Em algum momento da nossa vida, teremos que ler um arquivo estático e tratá-lo em nosso sistema. Diversas APIs no mundo disponibilizam diferentes tipos de arquivos estáticos: CSVs, TXTs, JSONs e afins, para que o usuário possa fazer algum tipo de "ajuste" ou análise dos dados. É justamente essa parte que você foi escolhido para automatizar.

## Antes de tudo: Um alerta
Primeiramente, tenho que alertar: ler arquivos estáticos no sistema não é uma boa prática. Faça isso apenas quando não houver nenhuma outra opção. Mas, se porventura isso for realmente necessário, bem, não é o ideal, mas acontece.

<img src="./markdown-statics/nao_ideal.jpg">
<br>

## Iniciando o projeto
Para seguir com este projeto, é importante ter conhecimento sobre a linguagem Java e adicionar as dependências do Gson e do JUnit (afinal, como saberemos se deu certo sem testes, não é?) no seu POM. Sem essas bibliotecas, não será possível seguir com este artigo.

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.6.2</version>
    <scope>test</scope>
</dependency>
```

## Antes de continuar: Um pouco de Java
Bem... esta é a parte chata que os mais experientes podem pular, mas é sempre bom revisar. A biblioteca GSON tem muito poder de simplificação porque ela trabalha com `String`.

Se você é mais experiente, provavelmente já teve que ler algum arquivo estático e agora, na sua cabeça, já deve estar bolando algumas ideias para realizar essa ação. De cara, alguns já pensam em usar `FileReader` ou `InputStreamReader`, mas calma, não vamos precisar de tantas coisas assim.

<img src="./markdown-statics/Vale-a-pena.jpg">
<br>

Para você que é mais novo nesse mundo, vou simplificar e explicar o que cada método de cada classe faz. Você que já é mais experiente pode pular e ir direto para o código, mas é sempre bom revisar.

 * ``` Path.get(String dirPath) ``` 
  O método estático `get()` recebe uma `String`. Ele tenta se orientar a partir da pasta raiz do seu projeto (pasta onde está o pom.xml). Então, digamos que seu pom.xml esteja em `C:\Users\user\Documents\projeto` e você passe uma `String` com `./pasta`, o resultado disso será `C:\Users\user\Documents\projeto\pasta`. Ele retornará um objeto `Path` que representa ou um diretório ou um arquivo (caso passe um arquivo).
<br>

 * ``` Files.readAllLines(Path path, Charset charset) ```
  O método estático `readAllLines()` recebe 2 parâmetros: o `Path` do arquivo no qual está buscando (agora fica claro o porquê de usarmos o método explicado acima) e o `Charset` do arquivo. Ele retorna uma lista de `String`, onde cada item da lista é uma linha do arquivo. Bom, agora tudo já parece bem mais fácil, mas espere, tem mais.
<br>

 * ``` Strings.join(String delimitador, String ... itens) ```
  O método estático `join()` recebe o primeiro parâmetro como um "delimitador". O outro campo é um spread operator, que significa que podemos receber uma ou mais `String`, que serão juntadas. Se você já usou a função `split(String separator)`, saiba que ela faz o processo reverso. Ao invés de separar, ela junta os itens pelo delimitador. Sigamos o exemplo abaixo:
    ```java
    List<String> lista = Arrays.asList("Meu", "Nome", "é", "Marcio");
    String resultado = String.join(" ", lista);
    System.out.println(resultado);
    ```
    A saída do código acima no console é `Meu Nome é Marcio`.
    Ele também pode ser escrito desta forma:
    ```java
    String resultado = String.join(", ","Set", "Map", "List");
    System.out.println(resultado);
    ```
    A saída do código acima no console é `Set, Map, List`.

## Lendo o JSON e o transformando em Objeto

Ok, agora chegamos na parte que realmente interessa. Como dito no tópico anterior, a biblioteca GSON tem muito poder de simplificação porque ela trabalha com `String`. 

Agora vamos levar em consideração que você quer trabalhar com este JSON:

```json
{
  "id": 17,
  "sigla": "TO",
  "nome": "Tocantins",
  "regiao": {
    "id": 1,
    "sigla": "N",
    "nome": "Norte"
  }
}
```
_(JSON extraído da API de localidades do IBGE)_

**Leve em consideração que na pasta onde há o arquivo pom.xml existe uma pasta chamada static e dentro dela o arquivo que carrega este JSON se chama Tocantins.json.** 

### Lendo o JSON como String 

Usando todos os métodos que já foram mostrados acima, fica bem fácil deduzir um jeito de ler este JSON. Mesmo assim, vou mostrar a forma que eu fiz para auxiliar na compreensão do problema:

De certa forma, fazer código repetido é bem fácil. Então, eu criei uma classe chamada `AbstractReader`. Nela, criei um método estático chamado `readJson(String dirPath)`, que une toda a lógica de leitura em um único método. Ela ficou assim:

```java
public abstract class AbstracrReader {
    public static String readJson(String path) throws IOException {
            String json = String.join(" ",
                    Files.readAllLines(
                            Paths.get(path),
                            StandardCharsets.UTF_8)
            );
            return json;
    }
}
```
_([Este arquivo pode ser conferido neste projeto](https://github.com/marciosindeaux/leitura-json))_

### Criando a classe que representa o JSON 

É importante ressaltar neste tópico que, como o GSON se orienta pela `String` formada, **a classe no Java deve conter campos com os nomes idênticos aos do JSON e não há problema em ter campos sobressalentes**. Eles serão gerados com valor `null`, mas é importante que a classe tenha os nomes iguais aos fornecidos pelo JSON.

No caso do JSON acima, temos 2 classes logo de cara: a classe `Estado` e a classe `Regiao`. Veja como ficam as classes no Java:

```java
public class Regiao {
    private Integer id;
    private String sigla;
    private String nome;
    private List<Estado> estados = new ArrayList<>();

    // Getters e Setters omitidos
}
```
```java
public class Estado {
    private Integer id;
    private String sigla;
    private String nome;
    private Regiao regiao;

    // Getters e Setters omitidos
}
```

### Buscando o Tipo da classe no Java

Até agora, nós não usamos nada da biblioteca GSON, mas é aqui que isso muda. Vamos usá-la para conseguir fazer essa operação. 

Agora que já temos o JSON como `String`, precisamos dizer qual o tipo de classe que esse JSON é. Para isso, vamos nos utilizar de uma classe chamada `TypeToken` dessa biblioteca.

A classe `TypeToken` implementa a classe `Type` (das reflections) do Java. Segundo a documentação oficial, o Java não provê uma forma de representar tipos genéricos, então essa classe faz isso. Ela força o programador a criar uma classe anônima interna e assim pegar o tipo em tempo de execução.

Nosso código seria algo como isso: 

```java
Type type = new TypeToken<Estado>(){}.getType();
```

 > Agora fica a dúvida: se tivéssemos um JSON com uma lista de estados ao invés de apenas um estado, como isso ficaria? A resposta é simples:

```java
Type type = new TypeToken<List<Estado>>(){}.getType();
```

### Transformando o JSON em Objeto

Bom... Se você chegou neste ponto e seguiu os passos anteriores, saiba que a partir daqui a coisa é bem simples. 

Existe uma classe na biblioteca GSON chamada `Gson`, e existe um método nela chamado `fromJson(String jsonText, Type convertType)`. Esse método recebe o JSON como `String` e o tipo do JSON e retorna uma instância do tipo da classe com os dados encontrados na `String`. 

A implementação dessa ação fica assim:

```java
String jsonText = AbstracrReader.readJson("./static/Tocantins.json");
Type type = new TypeToken<Estado>(){}.getType();
Estado estado = new Gson().fromJson(jsonText, type);
```

Bem simples, não é?

## Sugestão de implementação

Caso você faça essa implementação para muitos JSONs diferentes, sugiro que, para cada classe, você crie uma classe `Reader` que herde de `AbstractReader`. Uma para cada JSON diferente.

Isso pode ficar grande? Claro que sim, mas cada código de cada classe estará isolado e as responsabilidades estarão divididas. Além disso, isso vai evitar códigos repetidos.

Para o caso citado acima, foi criado um `Reader` para a entidade `Estado`:

```java
public class EstadoReader extends AbstracrReader {
    public static Estado readOneFrom(String path) throws IOException {
        String jsonText = readJson(path);

        Type collectionType = new TypeToken<Estado>(){}.getType();
        return new Gson().fromJson(jsonText, collectionType);
    }

    public static List<Estado> readListFrom(String path) throws IOException {
        String jsonText = readJson(path);
        Type collectionType = new TypeToken<List<Estado>>(){}.getType();
        return new Gson().fromJson(jsonText, collectionType);

    }
}
```

É isso, espero que tenha ajudado. 
Até a próxima!