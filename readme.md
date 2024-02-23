# Topwar

## Topwar por Paulo Sobreira
-Topwar Simulador de combate

## Controles:
-XXXXXXXXXXX

## Construção Maven e Docker

- mvn clean package
- mvn war:war
- docker build -f topwar.dockerfile . -t sowbreira/topwar
- docker push sowbreira/topwar

## Como testar no Play with Docker

Pode ser executado no [Play with Docker](https://labs.play-with-docker.com/)

>Baixar o aqruivo do docker compose
```
curl -LfO 'https://raw.githubusercontent.com/paulosobreira/mesa11/master/docker-compose.yaml'
```

>Iniciar containers do Mysql,PhpMyAdmin e FlMane
```
docker compose up
```

>Url de acesso:

link_gerado_playwithdocker/**topwar/html5/index.html**
