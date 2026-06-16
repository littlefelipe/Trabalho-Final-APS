# 🌍 Sistema de Monitoramento Ambiental Reativo (EDA)

Este projeto implementa uma **Arquitetura Orientada a Eventos (EDA)** baseada no modelo *Publisher/Subscriber* para monitoramento de dados climáticos em tempo real. O sistema captura, processa, armazena e exibe dados de sensores ambientais, aplicando conceitos avançados de engenharia de software, mensageria e processamento de eventos complexos.

## 🛠️ Tecnologias e Padrões Utilizados

* **Linguagem:** Java 
* **Mensageria:** RabbitMQ (CloudAMQP) com roteamento `fanout`
* **Banco de Dados:** PostgreSQL (Local)
* **Comunicação Web:** WebSockets (Java-WebSocket) e API REST nativa (`com.sun.net.httpserver`)
* **Frontend:** HTML5, CSS3, JavaScript Vanilla e Chart.js
* **Padrões de Projeto (GoF):**
    * **Factory Method:** Utilizado para a criação escalável de diferentes tipos de sensores.
    * **Strategy:** Implementado no Serviço de Alerta para avaliar diferentes limiares de risco (Insolação, Tempestade, Ruído, etc.) com base no estado do ambiente.

## ⚙️ Pré-requisitos

Para executar este projeto localmente, você precisará ter instalado:
* Java Development Kit (JDK)
* Eclipse IDE (ou similar)
* PostgreSQL rodando localmente (porta 5432)
* Navegador Web atualizado

## 🗄️ Configuração do Banco de Dados

1. Abra o seu gerenciador de banco de dados (DBeaver, pgAdmin, etc.).
2. Conecte-se ao seu PostgreSQL local (usuário `postgres`, senha `12345`).
3. Crie um banco de dados e execute o seguinte script SQL para criar a tabela de histórico:

```sql
CREATE TABLE historico_clima (
    id SERIAL PRIMARY KEY,
    id_evento VARCHAR(100) NOT NULL,
    tipo_sensor VARCHAR(50) NOT NULL,
    valor NUMERIC(10, 2) NOT NULL,
    unidade VARCHAR(10) NOT NULL,
    data_hora TIMESTAMP NOT NULL
);
```

Nota: Certifique-se de que as credenciais no código (ServicoHistorico.java e ServicoApiHistorico.java) correspondem ao seu ambiente local.

🚀 Como Executar o Projeto
Em arquiteturas orientadas a eventos, a ordem de execução é fundamental. Os consumidores devem ser iniciados antes dos produtores para garantir que as filas sejam criadas e acopladas aos roteadores (exchanges) antes que as mensagens comecem a chegar.

No Eclipse, execute as classes Java na seguinte ordem:

Passo 1: Iniciar os Consumidores e APIs (Backend)
ServicoHistorico.java: Conecta-se ao RabbitMQ e ao PostgreSQL para persistir todas as leituras geradas.

ServicoAlerta.java: Avalia os eventos em tempo real usando as Estratégias e publica notificações de risco no roteador alertas_ambientais.

ServicoApiHistorico.java: Inicia a API REST local na porta 8080 para fornecer o histórico inicial ao gráfico.

ServicoPainel.java: Inicia o servidor WebSocket na porta 8887 e escuta os roteadores de eventos e alertas para realizar o broadcast para o frontend.

Passo 2: Iniciar os Produtores (Sensores)
SimuladorApp.java: Inicia as threads independentes que simulam os sensores (Temperatura, Umidade, Ruído, Vento, Qualidade do Ar) publicando JSONs no RabbitMQ.

Passo 3: Abrir o Painel de Visualização (Frontend)
Abra o arquivo index.html no seu navegador web.

O painel consumirá imediatamente a API REST (http://localhost:8080/api/historico) para desenhar o gráfico com os dados passados.

Em seguida, a conexão WebSocket (ws://localhost:8887) assumirá o controle, atualizando os cards, o gráfico e a central de alertas dinamicamente em tempo real.

🏗️ Estrutura Arquitetural
Produtores (/produtores): Geram dados e enviam para o exchange eventos_ambientais.

Consumidores (/consumidores): Escutam ativamente as filas vinculadas aos roteadores.

Alertas (/alertas): Contém as regras de negócio para análise de Processamento de Eventos Complexos (CEP), mantendo o histórico de estado em memória.




