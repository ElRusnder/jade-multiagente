public class Main {
    public static void main(String[] args) {
        jade.Boot.main(new String[]{
                "-gui",
                "sensor:SensorClima;eval:EvaluadorRiesgo;alerta:AlertaCiudadana"
        });
    }
}
