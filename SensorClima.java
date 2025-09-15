import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import java.util.Locale;

public class SensorClima extends Agent {
    protected void setup() {
        registrar("clima");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg == null) { block(); return; }
                if ("CLIMA?".equalsIgnoreCase(msg.getContent())) {
                    double temp = 18 + Math.random()*16;
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("TEMP=" + String.format(Locale.US, "%.1f", temp)); // <-- usa punto
                    send(reply);
                }
            }
        });
    }

    private void registrar(String tipo){
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo); sd.setName(getLocalName());
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (FIPAException e) { e.printStackTrace(); }
    }
}
