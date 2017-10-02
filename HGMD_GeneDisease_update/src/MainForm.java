import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Paolo on 27/03/2017.
 * This class contains the code related to the GUI.
 */
public class MainForm extends JFrame implements ActionListener{

    private JTextField pathAdvSubText;
    private JButton pathAdvSubButton;
    private JTextField pathMicroLesionsText;
    private JButton pathMicroLesionsButton;
    private JPanel mainFormPanel;
    private JButton updateButton;
    private JTextField versionText;
    private JTextField pathOutText;
    private JButton pathOutButton;
    private JPanel pathPanel;
    private JLabel pathAdvSubLabel;
    private JLabel pathMicroLesionLabel;
    private JLabel versionLabel;
    private JLabel pathOutLabel;

    public MainForm() {
        super("HGMD Update");

        pathAdvSubButton.addActionListener(this);
        pathAdvSubButton.setActionCommand("Search_Adv_Sub");
        pathMicroLesionsButton.addActionListener(this);
        pathMicroLesionsButton.setActionCommand("Search_Micro_Lesions");
        pathOutButton.addActionListener(this);
        pathOutButton.setActionCommand("Set_path_out");
        updateButton.addActionListener(this);
        updateButton.setActionCommand("Update");

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String choice = e.getActionCommand();

        JFileChooser fileChooser;
        if (choice.equals("Search_Adv_Sub") || choice.equals("Search_Micro_Lesions")) {
            fileChooser = new JFileChooser();
            int retVal = fileChooser.showOpenDialog(this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                String s = fileChooser.getSelectedFile().getAbsolutePath();

                if (choice.equals("Search_Adv_Sub")) {
                    pathAdvSubText.setText(s);
                } else if (choice.equals("Search_Micro_Lesions")) {
                    pathMicroLesionsText.setText(s);
                }
            }
        } else if (choice.equals("Set_path_out")){
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int retVal = fileChooser.showOpenDialog(this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                String s = fileChooser.getSelectedFile().getAbsolutePath();
                pathOutText.setText(s);
            }
        } else if (choice.equals("Update")) {
            String pathAdvSub, pathMicroLesions, version, pathOut;

            pathAdvSub = pathAdvSubText.getText();
            pathMicroLesions = pathMicroLesionsText.getText();
            version = versionText.getText();
            pathOut = pathOutText.getText() + "\\";
            if (pathAdvSub.equals("") || pathMicroLesions.equals("") || versionText.equals("") || pathOutText.equals("")) {
                JOptionPane.showMessageDialog(this,"I campi non sono compilati correttamente",
                        "Errore",JOptionPane.ERROR_MESSAGE);
            } else {
                Computation comp = new Computation(pathAdvSub, pathMicroLesions, version, pathOut);
                comp.start();
            }
        }
    }

    public static void main(String[] args) {
        MainForm mf = new MainForm();
        mf.setContentPane(mf.mainFormPanel);
        mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mf.pack();
        mf.setVisible(true);
    }

}
