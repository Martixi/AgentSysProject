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