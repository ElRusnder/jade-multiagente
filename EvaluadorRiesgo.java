import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;

public class EvaluadorRiesgo extends Agent {

    private Double ultimoRiesgo = null; // <- dentro de la clase

    protected void setup() {
        registrar("riesgo");

        // 1) Cada 2s: pedir CLIMA? al Sensor y calcular riesgo
        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {
                AID sensor = buscar("clima");
                if (sensor == null) return;

                ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                req.addReceiver(sensor);
                req.setContent("CLIMA?");
                send(req);

                // Recibir solo INFORM (evita comerse otros mensajes como "RIESGO?")
                ACLMessage res = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 1500);
                if (res != null && res.getContent().startsWith("TEMP=")) {
                    String s = res.getContent().split("=")[1];
                    double t;
                    try {
                        t = Double.parseDouble(s); // llega con punto (Locale.US en el sensor)
                    } catch (Exception ex) {
                        System.err.println("Evaluador: no pude parsear TEMP='" + s + "' (" + ex.getMessage() + ")");
                        return;
                    }
                    double riesgo = Math.min(1.0, Math.max(0, (t - 20)/15.0));
                    ultimoRiesgo = riesgo; // <- guarda el último valor
                    System.out.println("Evaluador -> TEMP=" + t + " -> RIESGO=" + String.format(java.util.Locale.US, "%.2f", riesgo));
                }
            }
        });

        // 2) Responder a "RIESGO?" desde AlertaCiudadana
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg == null) { block(); return; }
                if ("RIESGO?".equalsIgnoreCase(msg.getContent())) {
                    ACLMessage rep = msg.createReply();
                    rep.setPerformative(ACLMessage.INFORM);
                    rep.setContent(ultimoRiesgo == null ? "R=NA"
                            : "R=" + String.format(java.util.Locale.US, "%.2f", ultimoRiesgo));
                    send(rep);
                } else {
                    block();
                }
            }
        });
    }

    private void registrar(String tipo){
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo); sd.setName(getLocalName());
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (FIPAException e) {}
    }

    private AID buscar(String tipo){
        try {
            ServiceDescription sd = new ServiceDescription(); sd.setType(tipo);
            DFAgentDescription t = new DFAgentDescription(); t.addServices(sd);
            DFAgentDescription[] r = DFService.search(this, t);
            return r.length>0 ? r[0].getName() : null;
        } catch (Exception e) { return null; }
    }
}
