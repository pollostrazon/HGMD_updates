import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Paolo on 27/03/2017.
 */
public class MainForm extends JFrame implements ActionListener{

    private JLabel pathAdvSubLabel;
    private JTextField pathAdvSubText;
    private JButton pathAdvSubButton;
    private JPanel mainFormPanel;
    private JButton updateButton;
    private JPanel pathPanel;
    private JLabel versionLabel;
    private JTextField versionText;
    private JLabel pathOutLabel;
    private JTextField pathOutText;
    private JButton pathOutButton;
    private JFileChooser fileChooser;

    public MainForm() {
        super("HGMD Update");

        pathAdvSubButton.addActionListener(this);
        pathAdvSubButton.setActionCommand("Search_Adv_Sub");
        pathOutButton.addActionListener(this);
        pathOutButton.setActionCommand("Set_path_out");
        updateButton.addActionListener(this);
        updateButton.setActionCommand("Update");

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String choice = e.getActionCommand();

        if (choice.equals("Search_Adv_Sub")) {
            fileChooser = new JFileChooser();
            int retVal = fileChooser.showOpenDialog(this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                String s = fileChooser.getSelectedFile().getAbsolutePath();

                if (choice.equals("Search_Adv_Sub")) {
                    pathAdvSubText.setText(s);
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
            version = versionText.getText();
            pathOut = pathOutText.getText() + "\\";
            if (pathAdvSub.equals("") || versionText.equals("") || pathOutText.equals("")) {
                JOptionPane.showMessageDialog(this,"I campi non sono compilati correttamente",
                        "Errore",JOptionPane.ERROR_MESSAGE);
            } else {
                Computation comp = new Computation(pathAdvSub, version, pathOut);
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
