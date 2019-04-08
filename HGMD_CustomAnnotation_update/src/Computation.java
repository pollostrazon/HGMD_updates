import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * This class hosts the main computation.
 */
public class Computation {
    /**
     * Column title for coordinates in metadata row
     */
    private static final String COORDINATES_COL_NAME = "genomic_coordinates_hg19";
    /**
     * Column title for reference and variant in metadata row
     */
    private static final String VARIANT_COL_NAME = "sequence_context_hg19";

    /**
     * String for advanced substitution file
     */
    private String pathAdvSub;
    /**
     * String for old custom annotation file
     */
    private String pathOldAnn;
    /**
     * String for output path
     */
    private String pathOut;
    /**
     * String for version
     */
    private String version;
    /**
     * HashSet of custom annotations, because they need to be unique
     */
    private HashSet<CustomAnnotationVariant> custAnnSet;
    /**
     * List of custom annotations, just to ensure order
     */
    private ArrayList<CustomAnnotationVariant> custAnnList;

    public Computation(String pathAdvSub, String pathOldAnn, String version, String pathOut) {
        this.pathAdvSub = pathAdvSub;
        this.pathOldAnn = pathOldAnn;
        this.version = version;
        this.pathOut = pathOut;
        this.custAnnSet = new HashSet<>();
        this.custAnnList = new ArrayList<>();
    }

    /**
     * Normal computation pipeline
     */
    public void normal() {
        try {
            readFile(this.pathAdvSub, this.custAnnSet, 0);
            if (! this.pathOldAnn.equals(""))
                readFile(this.pathOldAnn, this.custAnnSet, 1);

            ensureOrder();

            writeOut(this.custAnnList);

            JOptionPane.showMessageDialog(null, "Tutto è andato secondo i piani.",
                    "Informazione", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File non esistente!",
                    "Errore",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null,t.getMessage(),
                    "Errore",JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }

    }

    /**
     * Db-like file merging only
     */
    public void fileMerge() {
        try {
            readFile(this.pathAdvSub, this.custAnnSet, 1);
            readFile(this.pathOldAnn, this.custAnnSet, 1);

            ensureOrder();

            writeOut(this.custAnnList);

            JOptionPane.showMessageDialog(null, "Tutto è andato secondo i piani.",
                    "Informazione", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File non esistente!",
                    "Errore",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null,t.getMessage(),
                    "Errore",JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    /**
     * Read data from raw file or db-like file into dest in the correct format
     * @param path file path
     * @param dest where to store data
     * @param mode 0 if raw file, 1 if db-like
     * @throws FileNotFoundException
     */
    private void readFile(String path, Collection<CustomAnnotationVariant> dest, int mode) throws FileNotFoundException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));

        String[] line;

        String regex = (mode == 0) ? ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)" : "\t";
        CustomAnnotationVariant element;
        int coordinates_index = -1, variant_index = -1;

        //In mode 0, searches for columns we need in metadata line, throws an exception in the case they are not found
        if (mode == 0) {
            line = sc.nextLine().split(regex);
            for (int i = 0; i < line.length; i++) {
                if (line[i].equals(COORDINATES_COL_NAME))
                    coordinates_index = i;
                if (line[i].equals(VARIANT_COL_NAME))
                    variant_index = i;
            }
            if (coordinates_index == -1 || variant_index == -1)
                throw new RuntimeException("Non sono state trovate le indicazioni relative alle colonne interessate.");
        } else {
            sc.nextLine(); //We don't care about the first line in other modes
        }

        //File parsing
        while(sc.hasNext()) {
            line = sc.nextLine().split(regex);

            if (mode == 0) {
                //needs to be transformed
                element = new CustomAnnotationVariant(
                        line[coordinates_index],
                        line[coordinates_index],
                        line[variant_index],
                        line[variant_index]
                );
                element.transform();
            } else
                element = new CustomAnnotationVariant(line[0], line[1], line[2], line[3]);

            if (!(element.getChr().equals("NULL") || element.getChr().equals("")
                    || element.getPos().equals("NULL") || element.getPos().equals("")))
                dest.add(element);
        }
        sc.close();
    }

    /**
     * Passes the set to a list and orders it
     */
    private void ensureOrder() {
        this.custAnnList.addAll(this.custAnnSet);

        this.custAnnList.sort(CustomAnnotationVariant::compareTo);
    }

    /**
     * Writes computation results into the chosen file in the correct format
     */
    private void writeOut(Collection<CustomAnnotationVariant> coll) {
        //params set
        String fileName = "CustomAnnotation_Variant_HGMD_" + this.version + ".txt";
        String separator = "\t";
        String newline = System.lineSeparator();
        String s;

        try {
            FileWriter fw = new FileWriter(pathOut + fileName);
            //builds the metadata line
            s = "chr" + separator +
                    "position" + separator +
                    "ref" + separator +
                    "variant" + separator +
                    "annotation" + newline;
            fw.append(s);

            //adds all the lines
            for (CustomAnnotationVariant customAnnotationVariant : coll) {
                fw.append(customAnnotationVariant.join(separator) + newline);
            }

            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Errore nella scrittura del file",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}
