# Sistema de Gestão de Estacionamentos

## Visão Geral

O sistema gerencia vagas de estacionamento, setores, entradas e saídas de veículos, além de calcular faturamento e aplicar regras de preço dinâmico conforme a lotação. A comunicação é feita via API REST e Webhooks.

## Tecnologias Usadas

- Java 22
- Spring Boot e Spring Data
- PostgreSQL
- Maven

# Estrutura de Pacotes

```
com.modasby.gestaoestacionamentos
|-- client.simulator    # Cliente do serviço Rest fornecido pela Estapar
|-- domain              # Entidades (persistência e regras de negócio especificas)
|   |-- garage          # Entidades relacionadas a garagem
|   |-- parking         # Entidades relacionadas ao parking
|   |-- spot            # Entidades relacionadas as vagas
|-- event               # Camada de eventos de webhook
|   |-- handler         # Handlers do webhook
|   |-- model           # POJOS com dados dos eventos
|-- repository          # Camada de repositórios
|-- service             # Camada de service(orquestração)
|-- web                 # Camada de transporte
|   |-- controller      # Controllers da aplicação
|   |-- dto             # Objetos de transporte de dados
|   |-- exception       # Mapeamento de exceções de domínio para respostas HTTP
```

# Executando com Docker
1. Configure o banco de dados no application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/estacionamento
spring.datasource.username=postgres
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
```

2. Faça build do projeto
```bash
docker build -t gestao-estacionamentos:latest
```

3. Inicie o container
```bash
docker run --network="host" gestao-estacionamentos:latest
```
4. Inicie o Simulador
```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

4. Acesse o endpoint `http://localhost:3003/garage` para iniciar o sistema

# Tradeoffs

- **Tecnologias:** Foi escolhido como tecnologia Java/Spring, pois é a que tenho mais afinidade
- **Arquitetura:** O projeto foi desenvolvido seguindo parcialmente conceitos de Clean Arch, DDD e SOLID para manter a simplicidade sem perder as vantagens de separação de responsabilidades, segregação de interfaces, modelo de domínios, etc.
- **Calculo de preço**: Na descrição do teste foi especificado que o cálculo do preço deve ser feito na entrada do veículo, mas o setor não é especificado no evento de ENTRY, foi implementada a seguinte solução:
  - No evento de ENTRY, a sessão é criada com status ENTERED
  - No evento PARKED, o sistema identifica o setor e calcula a lotação que existia no momento exato do ENTRY original.
  - A regra de preço dinâmico é então aplicada sobre essa lotação histórica, garantindo que o preço seja definido conforme as condições do momento da entrada, conforme o requisito.
  - O preço final é o valor do preço dinâmico multiplicado pela quantidade de horas que o veículo ficou estacionado

# Documentação da API

## Webhook
### Entrada na garagem

**WEBHOOK - POST**
```JSON
{
  "license_plate": "ZUL0001",
  "entry_time": "2025-01-01T12:00:00.000Z",
  "event_type": "ENTRY"
}
```

------------------

### Entrada na vaga

**WEBHOOK - POST**
```JSON
{
  "license_plate": "ZUL0001",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}
```

------------------

### Saida de garagem

**WEBHOOK - POST**
```JSON
{		
  "license_plate": "",
  "exit_time": "2025-01-01T12:00:00.000Z",
  "event_type": "EXIT"
}
```

### Garage config

**GET**
`/garage`

```JSON
{
  "garage": [
    {
      "sector": "A",
      "basePrice": 10.0,
      "max_capacity": 100,
      "open_hour": "08:00",
      "close_hour": "22:00",
      "duration_limit_minutes": 240
    },
    {
      "sector": "B",
      "basePrice": 4.0,
      "max_capacity": 72,
      "open_hour": "05:00",
      "close_hour": "18:00",
      "duration_limit_minutes": 120
    }
  ],
  "spots": [
    {
      "id": 1,
      "sector": "A",
      "lat": -23.561684,
      "lng": -46.655981
    },
    {
      "id": 2,
      "sector": "B",
      "lat": -23.561674,
      "lng": -46.655971
    }
  ]
}
```

### Consulta de Placa

**POST**
`/plate-status`
```JSON
{
  "license_plate": "ZUL0001"
}
```

Response
```JSON
{
  "license_plate": "ZUL0001",
  "price_until_now": 0.00,
  "entry_time": "2025-01-01T12:00:00.000Z", 
  "time_parked": "2025-01-01T12:00:00.000Z",
  "lat": -23.561684,
  "lng": -46.655981
}
```

------------------

### Consulta de Vaga

**POST**
`/spot-status`

Request
```JSON
{
  "lat": -23.561684,
  "lng": -46.655981
}
```

Response - 200
```JSON
{
  "ocupied": false,
  "license_plate": "",
  "price_until_now": 0.00,
  "entry_time": "2025-01-01T12:00:00.000Z",
  "time_parked": "2025-01-01T12:00:00.000Z"
}
```

### Consulta faturamento

**GET**
`/revenue`

Request
```JSON
{
  "date": "2025-01-01",
  "sector": "A"
}
```

Response
```JSON
{
  "amount": 0.00,
  "currency": "BRL",
  "timestamp": "2025-01-01T12:00:00.000Z"
}
```