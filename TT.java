/*
Maksymalnie 8 punktow za klase TT

The TT class implements an agent which will be responsible for the transmission times
computation and the network reliability estimation based on the information obtained
from the SSVGenerator agent. The TT agent should behave as follows
 */

// import libraries
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * TT Agent: Responsible for computing transmission times and
 * estimating network reliability at level (d, T).
 */
public class TT extends Agent {
    // klasa TT ma przyjmowac te dwa paramtery - 1 punkt
    private double d; // units of flow
    private double T; // max transmission time

    @Override
    protected void setup() {
        // pokazuje wiadomosc, ze dziala
        System.out.println("Transmission times computing agent " + getAID().getName() + " is ready.");

        // 1. Get arguments (d and T)
        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            d = Double.parseDouble(args[0].toString());
            T = Double.parseDouble(args[1].toString());
            System.out.println("The aim is to estimate the probability of sending " + d + " units of flow within time " + T);
        }

        // 2. Register in DF (Directory Facilitator) so SSVGenerator can find it
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("transmission-time-computation");
        sd.setName("JADE-network-reliability");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) { fe.printStackTrace(); }

        // 3. Behaviour to handle incoming MFN data and SSVs
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        // Unpack the message
                        Object[] data = (Object[]) msg.getContentObject();
                        MFN mfn = (MFN) data[0];
                        String mpPath = (String) data[1];
                        double[][] randomSSVs = (double[][]) data[2];

                        // Determine output filename (SSV0.csv or SSV1.csv)
                        String outFileName = mpPath.contains("0") ? "SSV0.csv" : "SSV1.csv";

                        // 4. Write SSVs to CSV file
                        PrintWriter writer = new PrintWriter(new FileWriter(outFileName));
                        for (double[] ssv : randomSSVs) {
                            for (int i = 0; i < ssv.length; i++) {
                                writer.print((int)ssv[i] + (i == ssv.length - 1 ? "" : ","));
                            }
                            writer.println();
                        }
                        writer.close();

                        // 5. Reliability Estimation
                        // tu uzywamy fukncji z SSV zeby sprawdzic to reliability danej sieci
                        int countSuccess = 0;
                        int N = randomSSVs.length;

                        for (double[] X : randomSSVs) {
                            // Use Formula (8) from MFN class
                            double minTime = mfn.MinTransmissionTime(d, X);
                            if (minTime <= T) {
                                countSuccess++;
                            }
                        }

                        double estimatedReliability = (double) countSuccess / N;

                        // 6. Send result back to SSVGenerator
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(String.valueOf(estimatedReliability));
                        send(reply);

                    } catch (Exception e) { e.printStackTrace(); }
                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        try { DFService.deregister(this); } catch (FIPAException fe) { }
        System.out.println("TT-agent terminating.");
    }
}