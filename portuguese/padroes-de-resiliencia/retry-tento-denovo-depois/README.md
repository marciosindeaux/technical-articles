# Retry + Backoff - Depois eu tento denovo 
![DarkSouls](./markdown-static/ds-dies-from-nothing.gif)

O Retry é um padrão simples e o nome é bem autoexplicativo: Tente novamente. Em certas situações um retry pode fazer total sentido. Um dos artigos feitos aqui fala sobre um padrão chamado Timelimiter/Timeout, e quando ele acontece, o retry pode entrar em cena, refazendo a requisição para ver se alguma outra instancia do serviço pode responder a tempo. 

Sendo objetivo , ***o retry*** é uma estrategia para tornar sistemas mais robustos evitando falhas temporarias. Ele ***tenta executar uma nova requisição caso a anterior/primeira falhe por algum motivo*** como consistencia eventual ,falha de rede, indispobibilidade temporaria ou sobrecarga passageira. 

### Backoff e Jitter - Seus maiores amigos

O Retry também conta com ferramentas que fazem com que ele não seja apenas uma retentativa. Re-tentar por retentar não muda nada a situação de uma sobrecarga momentanea de rede por exemplo. Imagine o seguinte cenário: X instancias de varios serviços diferentes chamando um segundo serviço que tem uma pool de conexão limitada. Diversas respostas serão erros e refazer essas tentativas só vai trazer o mesmo problema em uma escala ligeiramente menor. Tenha isso em mente enquanto entramos no primeiro subtópico:

#### Backoff

O Backoff é uma ferramenta muito usada junto com o Retry, ele é responsavel por atrasar as retentativas e dar um espaçamento de tempo para elas. Existem alguns tipos de backoff, como o Fixo, Linear, Exponencial, Incremental e Fibonacci, mas os mais comuns são o Fixo, Linear, e Exponencial e eu explico eles abaixo :

 * ***Fixo***: As re-tentativas acontecem entre um intervalo de tempo fixo. A cada X periodo de tempo por exemplo. O tempo de espera entre as retentativas pode se expresso pela seguinte equação abaixo: <br><br>

    $backoffWaitTime(attemptNumeber) = initialInterval $ <br><br>

 * ***Linear***: As re-tentativas acontecem entre o intervalo de tempo anterior + valor fixo colocado na configuração. O tempo de espera entre as retentativas pode se expresso pela seguinte equação abaixo: <br><br>

    $backoffWaitTime(attemptNumber) = initialInterval + (linearIncrement \cdot attemptNumber)$ <br><br>

 * ***Exponencial***: Dado o intervalo de espera inicial , as retentativas acontecem com o intervalo inicial sendo multiplicado por um fator de crescimento elevado ao numero da tentativa. O tempo de espera entre as retentativas pode se expresso pela seguinte equação abaixo: <br><br>

    $backoffWaitTime(attemptNumber) = initialInterval \cdot (growthFactor^{attemptNumber})$ <br><br>


Usar o backoff, mesmo que fixo é essencial em um cenário de congestionamento de rede, e ele pode salvar sua aplciações de tomar erros desnecessários causados por limitações, mas será que ele é o suficiente ? 

#### Jitter 

Caso voce não tenha percebido, agora apesar de termos requisições espalhadas temos um problema que pode voltar a te atormentar: A possivel formação de silos de requisições, Se todos os serviços forem configurados com backoffs iguais, pode acontecer de todos realizarem as mesmas requisições ao mesmo tempo. Para solucionar esse tipo de situação é necessário mais.

Aleatorizar os tempos de espera baseados nos tempos gerados pelo backoff é uma solução muito inteligente apesar de contraintuitiva, como apresentada no blog de arquitetura do AWS no artigo ***"Exponential Backoff And Jitter"*** do Marc Brooker[^1]. O Intuito aqui é criar um fator aleatorizador (ou semi-aleatorio) que faça com que mesm ocom um Backoff configurado igualmente para os serviços, cada um faça uma requisição em um tempo diferente, evitando assim uma enxurrada de requisições sincronizadas. 

Essa estratégia não só se encaixa bem para evitar concorrencia desenfreada no serviço requisitado, como ameniza de forma efetiva uma sobrecarga de rede sincronizada, Homogenizado assim também o processamento que deve ser feito. 

Assim como o Backoff, o Jitter também tem tipos específicos, cada um com a sua abordagem e cada um com suas vantagens e desvantagens. As estrategias são : 

 * ***Full Jitter (Aleatorizador absoluto)***: O tempo real de espera vai ser qualquer numero entre 0ms e o numero gerado pelo backoff. Ele pode ser considerado aleatorio, em contrapartida os tempo são desconexos e no pior dos casos ele pode ou executar todas as requisições sem intervalo, ou no intervalo maximo. Ele é representado pela seguinte formula: <br><br>

    $realWaitTime(backoffWaitTime) = random(0, backoffWaitTime)$ <br><br>

 * ***Equal Jitter (Aleatorizador Equalizado)***: Esse trabalha um pouco difernete.Ele parte do principio de para evitar sobrecargas é realmente necessário se ter um aleatorizador, mas ele deve seguir um tempo de espera minimo para que haja alguma auto-recuperação se necessário (no minimo metade do tempo estabelecido). Ele é representado da seuginte formula: <br><br>

    $realWaitTime(backoffWaitTime) = \frac{backoffWaitTime}{2} + random(0, \frac{backoffWaitTime}{2}) $ <br><br>    

 * ***Decorrelated Jitter***: Esse jitter é o mais difícil de entender, mas é o que traz melhores resultados. Uma das coisas que ele leva em consideração é o backoff gerado, mas, diferentemente das outras alternativas de jitter, ele não depende apenas disso. O principal destaque desse tipo de jitter é que ele utiliza o tempo de espera da requisição anterior em conjunto com um fator de crescimento (semelhante ao backoff exponencial) para gerar um incremento aleatório e sempre incremental. Outro ponto positivo é que ele torna os clientes dessincronizados em relação aos serviços que utilizam esse padrão, tornando muito rara a ocorrência de sobrecarga na rede. Atenção: ***não é recomendável combinar esse jitter com backoff exponencial tradicional***. Ele pode ser descrito na seguinte formula:  <br><br>

    $realWaitTime(backoffWaitTime) = min(backoffWaitTime, random(baseDaelay, growthFactor \cdot previousWaitTime))$ <br><br>   

[^1]: [AWS Archtecture Blog - Exponential Backoff And Jitter - B. Mark](https://aws.amazon.com/pt/blogs/architecture/exponential-backoff-and-jitter/)
