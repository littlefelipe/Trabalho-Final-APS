package publishers;

public class SensorFactory {
    public static Sensor criarSensor(String tipo) {
        if (tipo.equalsIgnoreCase("TEMPERATURA")) {
            return new SensorTemperatura();
        } else if (tipo.equalsIgnoreCase("RUIDO")) {
            return new SensorPoluicaoSonora();
        } else if (tipo.equalsIgnoreCase("QUALIDADE")) {
            return new SensorQualidadeAr();
        } else if (tipo.equalsIgnoreCase("UMIDADE")) {
            return new SensorUmidade();
        } else if (tipo.equalsIgnoreCase("VELOCIDADE")) {
            return new SensorVelocidadeVento();
        } 
        throw new IllegalArgumentException("Tipo de sensor desconhecido.");
    }
}