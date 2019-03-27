import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
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
    private JCheckBox ignoreCheckBox;
    private JLabel soloMergeLabel;
    private JCheckBox onlyMergeCheckBox;
    private JLabel pathOldAnnLabel;
    private JTextField pathOldAnnText;
    private JButton pathOldAnnButton;
    private JFileChooser fileChooser;

    /**
     * Main form
     */
    public MainForm() {
        super("HGMD Update");

        pathAdvSubButton.addActionListener(this);
        pathAdvSubButton.setActionCommand("Search_Adv_Sub");
        pathMicroLesionsButton.addActionListener(this);
        pathMicroLesionsButton.setActionCommand("Search_Micro_Lesions");
        pathOldAnnButton.addActionListener(this);
        pathOldAnnButton.setActionCommand("Search_Old_Ann");
        pathOutButton.addActionListener(this);
        pathOutButton.setActionCommand("Set_path_out");
        updateButton.addActionListener(this);
        updateButton.setActionCommand("Update");
        ignoreCheckBox.addActionListener(this);
        ignoreCheckBox.setActionCommand("Check_ignore");
        onlyMergeCheckBox.addActionListener(this);
        onlyMergeCheckBox.setActionCommand("Check_merge");

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

            case "Search_Micro_Lesions":
                //chooses Micro Lesions file
                pathMicroLesionsText.setText(getPath(false));
                break;

            case "Search_Old_Ann":
                //chooses old HGMD database file
                pathOldAnnText.setText(getPath(false));
                break;

            case "Set_path_out":
                //chooses result file
                pathOutText.setText(getPath(true));
                break;

            case "Check_ignore":
                //makes sure first field is disabled if mode is only merge
                pathAdvSubText.setEnabled(!pathAdvSubText.isEnabled());
                pathAdvSubButton.setEnabled(!pathAdvSubButton.isEnabled());
                break;

            case "Check_merge":
                //makes sure first field is disabled if mode is only merge
                pathAdvSubText.setEnabled(!pathAdvSubText.isEnabled());
                pathAdvSubButton.setEnabled(!pathAdvSubButton.isEnabled());
                ignoreCheckBox.setEnabled(!ignoreCheckBox.isEnabled());
                break;

            case "Update":
                //Starts the computation
                String pathAdvSub, pathMicroLesions, pathOldAnn, version, pathOut;

                pathAdvSub = ignoreCheckBox.isSelected() ? null : pathAdvSubText.getText();
                pathMicroLesions = pathMicroLesionsText.getText();
                pathOldAnn = pathOldAnnText.getText();
                version = versionText.getText();
                pathOut = pathOutText.getText() + File.separator;

                if (onlyMergeCheckBox.isSelected()) {
                    if (pathMicroLesions.equals("") || pathOldAnn.equals("") ||
                            version.equals("") || pathOut.equals(File.separator)) {
                        JOptionPane.showMessageDialog(this, "I campi non sono compilati correttamente",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Computation comp = new Computation(null, pathMicroLesions, pathOldAnn, version, pathOut);
                        comp.fileMerge();
                    }
                } else {
                    if ((!ignoreCheckBox.isSelected() && pathAdvSub.equals("")) ||
                            pathMicroLesions.equals("") || version.equals("") ||
                            pathOut.equals(File.separator)) {
                        JOptionPane.showMessageDialog(this, "I campi non sono compilati correttamente",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Computation comp = new Computation(pathAdvSub, pathMicroLesions, pathOldAnn, version, pathOut);
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
