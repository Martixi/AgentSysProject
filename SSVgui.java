/*
Maksymalnie do zdobycia 17 pkt razem z SSV generator
The SSVGenerator class should implement an agent performing the following actions:
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GUI for SSVGenerator to input MFN parameters and CSV file path.
 */
public class SSVGeneratorGui extends JFrame {
    private SSVGenerator myAgent;
    private JTextField filePathField, mField, wField, cField, lField, rField, rhoField;

    public SSVGeneratorGui(SSVGenerator a) {
        myAgent = a;
        setTitle("SSV Generator GUI");
        setLayout(new GridLayout(8, 2));

        add(new JLabel("Path to MPs (.csv):"));
        filePathField = new JTextField("MPs0.csv");
        add(filePathField);

        add(new JLabel("m:"));
        mField = new JTextField("5");
        add(mField);

        add(new JLabel("W (comma separated):"));
        wField = new JTextField("4,3,2,3,2");
        add(wField);

        add(new JLabel("C (comma separated):"));
        cField = new JTextField("10,15,25,15,20");
        add(cField);

        add(new JLabel("L (comma separated):"));
        lField = new JTextField("5,7,6,5,8");
        add(lField);

        add(new JLabel("R (comma separated):"));
        rField = new JTextField("0.7,0.65,0.67,0.71,0.75");
        add(rField);

        add(new JLabel("rho (comma separated):"));
        rhoField = new JTextField("0.1,0.3,0.5,0.7,0.9");
        add(rhoField);

        JButton sendBtn = new JButton("Send Data");
        sendBtn.addActionListener(e -> {
            try {
                int m = Integer.parseInt(mField.getText());
                int[] W = parseToIntArray(wField.getText());
                double[] C = parseToDoubleArray(cField.getText());
                int[] L = parseToIntArray(lField.getText());
                double[] R = parseToDoubleArray(rField.getText());
                double[] rho = parseToDoubleArray(rhoField.getText());

                myAgent.processData(m, W, C, L, R, rho, filePathField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage());
            }
        });
        add(sendBtn);
        pack();
    }

    private int[] parseToIntArray(String s) {
        String[] parts = s.split(",");
        int[] res = new int[parts.length];
        for(int i=0; i<parts.length; i++) res[i] = Integer.parseInt(parts[i].trim());
        return res;
    }

    private double[] parseToDoubleArray(String s) {
        String[] parts = s.split(",");
        double[] res = new double[parts.length];
        for(int i=0; i<parts.length; i++) res[i] = Double.parseDouble(parts[i].trim());
        return res;
    }
}