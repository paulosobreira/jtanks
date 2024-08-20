# JTanks

Jogo multijogador de combate de tanks. Inspirado num antigo jogo do Atari 2600

- Pode ser jogado multijogador local ou remoto(Rede local)
- Para se jogar de 2 na mesma maquina é preciso abrir 2 instancias do jogo onde uma delas é o Servidor/Cliente e a outra apenas cliente.

    1. Multijogador > Iniciar Servidor (Normalemnete porta 8080 ou outra disponivel)
    2. no console é exibido "Ok eu sou NomeDoPc/127.0.1.1 ouvindo na porta 8080"
    3. Multijogador > conectar e entre com nome:ip:porta informa na mensagem(sobreira:127.0.1.1:8080)
    4. Multijogador > Iniciar Jogo

## Requerimentos

- Para executar o jogo é necessário Java 11
- Para rodar uma instância do jogo no Windows execute run.bat
- Para rodar uma instância do jogo no linux execute run.sh

## Informação técnica

- Construção Maven
    - mvn clean package
