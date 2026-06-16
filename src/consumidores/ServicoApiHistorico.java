package consumidores;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;

public class ServicoApiHistorico {
	// Credenciais da Base de Dados Local
	private final static String DB_URL = "jdbc:postgresql://ep-aged-poetry-acxch3o5-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
	private final static String DB_USER = "postgres";
	private final static String DB_PASSWORD = "npg_DnRLdvP3Hfc1";

    public static void main(String[] args) throws Exception {
        // Cria um servidor HTTP nativo do Java na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/historico", new HistoricoHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("API de Histórico rodando na porta 8080. Endpoint: http://localhost:8080/api/historico");
    }

    static class HistoricoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Configuração de CORS para permitir que o HTML local acesse a API
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            
            List<Map<String, Object>> resultados = new ArrayList<>();
            // Busca as últimas 60 leituras para não sobrecarregar o gráfico
            String query = "SELECT tipo_sensor, valor, data_hora FROM historico_clima ORDER BY data_hora DESC LIMIT 60";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Map<String, Object> linha = new HashMap<>();
                    linha.put("tipoSensor", rs.getString("tipo_sensor"));
                    linha.put("valor", rs.getDouble("valor"));
                    linha.put("timestamp", rs.getTimestamp("data_hora").toString());
                    resultados.add(linha);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Inverte a lista para colocar em ordem cronológica (da esquerda para a direita no gráfico)
            Collections.reverse(resultados);

            Gson gson = new Gson();
            String response = gson.toJson(resultados);

            exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes("UTF-8"));
            os.close();
        }
    }
}