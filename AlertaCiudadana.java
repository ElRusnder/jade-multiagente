import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;

public class AlertaCiudadana extends Agent {
    protected void setup() {
        addBehaviour(new TickerBehaviour(this, 3000) {
            protected void onTick() {
                AID eval = buscar("riesgo");
                if (eval == null) {
                    System.out.println("Alerta: sin evaluador");
                    return;
                }
                ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                req.addReceiver(eval);
                req.setContent("RIESGO?");
                send(req);

                ACLMessage res = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 1200);
                if (res != null && res.getContent().startsWith("R=")) {
                    System.out.println("Alerta <- " + res.getContent()); // p.ej. "R=0.41"
                } else {
                    System.out.println("Alerta: no recibió riesgo (timeout)");
                }
            }
        });
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
