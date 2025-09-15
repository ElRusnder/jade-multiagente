**Miembros del grupo**
- Daniel Vides Ames Camayo
- Wilfredo Guia Muñoz

# Sistema Multiagente con JADE

Este proyecto implementa un **sistema multiagente colaborativo** utilizando la plataforma **JADE (Java Agent DEvelopment Framework)**.  
Los agentes creados interactúan entre sí mediante mensajes ACL y servicios registrados en el **Directory Facilitator (DF)**, demostrando el uso de las "páginas amarillas" de JADE para el descubrimiento dinámico.

---

## 📌 Descripción del Proyecto

El sistema está compuesto por tres agentes principales:

1. **SensorClima**  
   - Genera valores de temperatura simulados.  
   - Responde a las solicitudes de otros agentes con mensajes `INFORM` del tipo `TEMP=valor`.

2. **EvaluadorRiesgo**  
   - Solicita periódicamente datos al `SensorClima`.  
   - Calcula un índice de riesgo en función de la temperatura recibida.  
   - Ofrece el resultado a otros agentes cuando se le consulta (`RIESGO?`).  

3. **AlertaCiudadana**  
   - Consulta al `EvaluadorRiesgo` el nivel de riesgo actual.  
   - Muestra los valores recibidos y actúa como un consumidor final de la información.  

---

## ⚙️ Configuración del Entorno

- **Lenguaje**: Java (JDK 8 o superior).  
- **Framework**: JADE 4.5.0  
- **IDE recomendado**: IntelliJ IDEA o Eclipse.  
- **Ejecución**:  
```powershell
# Compilar todos los agentes
javac -cp ".;jade.jar" Main.java SensorClima.java EvaluadorRiesgo.java AlertaCiudadana.java

# Ejecutar el sistema multiagente
java -cp ".;jade.jar" Main

