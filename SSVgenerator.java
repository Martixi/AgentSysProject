/*
Maksymalnie do zdobycia 17 pkt razem z SSV generator gui
The SSVGenerator class should implement an agent performing the following actions:
 */

/*
During activation of the agent in cmd you should pass as its arguments values 𝜖
and 𝛿 in order to determine the number N of SSVs based on formula (12b) from
[2]. The agent should check whether these values are between 0 and 1 (if not, the
agent should terminate). (2)
 */

/*
After activation, the agent should show the GUI of SSVGeneratorGui in which you
should be able to choose a .csv file with MPs and specify the parameters of an MFN
(5)
 */

/*
After clicking the “Send Data” button, the agent should create an object mfn of the
MFN class and display the parameters of this MFN, such as W, C, L, R, and rho. (1)
 Next, based on the randomSSV method, the agent should generate N random
SSVs. (0.5)
 */

/*
Next, the agent should look for the TT agent responsible for the computation of
transmission times and network reliability estimation. (0.5)
 */

/*
Next, the agent should send to the TT agent a message containing the MFN
parameters, the path to the .csv file with MPS, and the generated random SSVs. (6)
 */

/*
Finally, the agent should receive a message from the TT agent containing the
network reliability estimated by the TT agent, display this message, and terminate.
(2)
 */

// import libraries
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.io.Serializable;

public class SSVGenerator extends Agent {
    private double epsilon, delta;
    private int N;
    private MFN mfn;
    private SSVGeneratorGui gui;

    @Override
    protected void setup() {
        System.out.println("SSVGenerator agent " + getAID().getName() + " is ready.");

        // 1. Get arguments (epsilon and delta) from command line
        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            try {
                epsilon = Double.parseDouble(args[0].toString());
                delta = Double.parseDouble(args[1].toString());

                // Check constraints
                if (epsilon <= 0 || epsilon >= 1 || delta <= 0 || delta >= 1) {
                    System.err.println("Epsilon and Delta must be between 0 and 1.");
                    doDelete();
                    return;
                }

                // 2. Determine N based on Fishman's formula (12b)
                // N = ceil( (normalICDF(1 - delta/2) / (2 * epsilon))^2 )
                double z = MFN.normalICDF(1 - delta / 2.0);
                N = (int) Math.ceil(Math.pow(z / (2 * epsilon), 2));
                System.out.println("The minimum number of iterations is equal to " + N);

                // 3. Show GUI
                gui = new SSVGeneratorGui(this);
                gui.setVisible(true);

            } catch (Exception e) {
                System.err.println("Invalid arguments.");
                doDelete();
            }
        } else {
            System.err.println("Please provide epsilon and delta as arguments.");
            doDelete();
        }

        // Behavior to receive the final estimation from TT Agent
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("Estimated network reliability is equal to " + msg.getContent());
                    System.out.println("SSVGenerator agent " + getAID().getName() + " terminating.");
                    gui.dispose();
                    doDelete();
                } else {
                    block();
                }
            }
        });
    }

    /**
     * Called by GUI when "Send Data" is clicked. -- to do sprawdzenia bo idk czy dziala
     */
    public void processData(int m, int[] W, double[] C, int[] L, double[] R, double[] rho, String mpPath) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                // 1. Create MFN object
                mfn = new MFN(m, W, C, L, R, rho);
                mfn.getMPs(mpPath);

                System.out.println("It has been created the MFN with parameters:");
                System.out.println("W=" + java.util.Arrays.toString(W));
                // ... (Print other parameters as per example output)

                // 2. Generate N random SSVs
                double[][] pmf = new double[m][];
                for(int i=0; i<m; i++) {
                    pmf[i] = new double[W[i]+1];
                    for(int k=0; k<=W[i]; k++) pmf[i][k] = mfn.Pr(i, k);
                }
                double[][] cdf = mfn.CDF(pmf);
                double[][] generatedSSVs = mfn.randomSSV(N, cdf);
                System.out.println(N + " random SSVs have been generated!");

                // 3. Look for TT Agent
                AID ttAgent = null;
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("transmission-time-computation");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if (result.length > 0) {
                        ttAgent = result[0].getName();
                        System.out.println("Found the following transmission times computing agent: " + ttAgent.getName());
                    }
                } catch (FIPAException fe) { fe.printStackTrace(); }

                // 4. Send message to TT
                if (ttAgent != null) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(ttAgent);
                    // Sending data as a wrapped object
                    try {
                        Object[] data = {mfn, mpPath, generatedSSVs};
                        msg.setContentObject(data);
                        send(msg);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        });
    }
}