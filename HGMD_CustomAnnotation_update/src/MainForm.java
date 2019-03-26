import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class contains the code related to the GUI.
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
    private JLabel pathOldAnnLabel;
    private JTextField pathOldAnnText;
    private JButton pathOldAnnButton;
    private JLabel soloMergeLabel;
    private JCheckBox onlyMergeCheckBox;
    private JFileChooser fileChooser;

    /**
     * Main form
     */
    public MainForm() {
        super("HGMD Update");

        //sets all listener
        pathAdvSubButton.addActionListener(this);
        pathAdvSubButton.setActionCommand("Search_Adv_Sub");
        pathOldAnnButton.addActionListener(this);
        pathOldAnnButton.setActionCommand("Search_Old_Ann");
        pathOutButton.addActionListener(this);
        pathOutButton.setActionCommand("Set_path_out");
        updateButton.addActionListener(this);
        updateButton.setActionCommand("Update");

    }

    /**
     * Performs an action based on which button was clicked
     * @param e ActionEvent generated from the click of a button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String choice = e.getActionCommand();

        switch (choice) {
            case "Search_Adv_Sub":
                //chooses Advanced Substitution file
                pathAdvSubText.setText(getPath(false));
                break;

            case "Search_Old_Ann":
                //chooses old HGMD database file
                pathOldAnnText.setText(getPath(false));
                break;

            case "Set_path_out":
                //chooses result file
                pathOutText.setText(getPath(true));
                break;

            case "Update":
                //Starts the computation
                String pathAdvSub, pathOldAnn, version, pathOut;

                pathAdvSub = pathAdvSubText.getText();
                pathOldAnn = pathOldAnnText.getText();
                version = versionText.getText();
                pathOut = pathOutText.getText() + File.separator;

                if (onlyMergeCheckBox.isSelected()) {
                    if (pathAdvSub.equals("") || pathOldAnn.equals("") ||
                            version.equals("") || pathOut.equals(File.separator)) {
                        JOptionPane.showMessageDialog(this, "I campi non sono compilati correttamente",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Computation comp = new Computation(pathAdvSub, pathOldAnn, version, pathOut);
                        comp.fileMerge();
                    }
                } else {
                    if (pathAdvSub.equals("") || version.equals("") || pathOut.equals(File.separator)) {
                        JOptionPane.showMessageDialog(this, "I campi non sono compilati correttamente",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Computation comp = new Computation(pathAdvSub, pathOldAnn, version, pathOut);
                        comp.normal();
                    }
                }
                break;
        }
    }

    /**
     * Shows a new file/dir-chooser window
     * @param dir true if dir, false if file
     * @return absolute path of the file/dir
     */
    public String getPath(boolean dir) {
        fileChooser = new JFileChooser();
        if (dir) fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retVal = fileChooser.showOpenDialog(this);
        String s = "";

        if (retVal == JFileChooser.APPROVE_OPTION) {
            s = fileChooser.getSelectedFile().getAbsolutePath();

        }

        return s;
    }

    /**
     * Main class
     * @param args
     */
    public static void main(String[] args) {
        MainForm mf = new MainForm();
        mf.setContentPane(mf.mainFormPanel);
        mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mf.pack();
        mf.setVisible(true);
    }

}
