import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * This class hosts the main computation.
 */
public class Computation {
    /**
     * Column title for gene in metadata row
     */
    private static final String GENE_COL_NAME = "gene";
    /**
     * Column title for disease in metadata row
     */
    private static final String DISEASE_COL_NAME = "disease";

    /**
     * String for advancedSubstitution file
     */
    private String pathAdvSub;
    /**
     * String for micro lesions file
     */
    private String pathMicroLesions;
    /**
     * String for old gene-disease file
     */
    private String pathOldAnn;
    /**
     * String for version
     */
    private String version;
    /**
     * String for output path
     */
    private String pathOut;
    /**
     * HashSet of gene-disease pairs, because they need to be unique
     */
    private HashSet<GeneAnnotationPair> geneAnnotationSet;
    /**
     * List of gene-disease pairs, just to ensure order
     */
    private ArrayList<GeneAnnotationPair> geneAnnotationList;

    public Computation(String pathAdvSub, String pathMicroLesions, String pathOldAnn, String version, String pathOut) {
        this.pathAdvSub = pathAdvSub;
        this.pathMicroLesions = pathMicroLesions;
        this.pathOldAnn = pathOldAnn;
        this.version = version;
        this.pathOut = pathOut;
        this.geneAnnotationSet = new HashSet<>();
        this.geneAnnotationList = new ArrayList<>();
    }

    /**
     * Normal computation pipeline
     */
    public void normal() {
        try {
            if (this.pathAdvSub != null)
                readFile(this.pathAdvSub, this.geneAnnotationSet, 0);
            readFile(this.pathMicroLesions, this.geneAnnotationSet, 0);
            if (! this.pathOldAnn.equals(""))
                readFile(this.pathOldAnn, this.geneAnnotationSet, 1);

            ensureOrder();
            mergeGeneAnn();

            writeOut(this.geneAnnotationList);
            JOptionPane.showMessageDialog(null, "Tutto è andato secondo i piani.",
                    "Informazione", JOptionPane.INFORMATION_MESSAGE);
        } catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"Errore nella lettura dei file (Inesistenti?)",
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
            readFile(this.pathMicroLesions, this.geneAnnotationSet, 1);
            readFile(this.pathOldAnn, this.geneAnnotationSet, 1);

            ensureOrder();
            mergeGeneAnn();

            writeOut(this.geneAnnotationList);

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
     * Read data from raw file or db-like file
     * @param path file path
     * @param dest where to store data
     * @param mode 0 if raw file, 1 if db-like
     * @throws FileNotFoundException
     */
    private void readFile(String path, HashSet<GeneAnnotationPair> dest, int mode) throws FileNotFoundException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));

        String[] line;
        String regex = (mode == 0) ? ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)" : "\t";
        GeneAnnotationPair element;
        int gene_index = -1, disease_index = -1;

        //In mode 0, searches for columns we need in metadata line, throws an exception in the case they are not found
        if (mode == 0) {
            line = sc.nextLine().split(regex);
            for (int i = 0; i < line.length; i++) {
                if (line[i].equals(GENE_COL_NAME))
                    gene_index = i;
                if (line[i].equals(DISEASE_COL_NAME))
                    disease_index = i;
            }
            if (gene_index == -1 || disease_index == -1)
                throw new RuntimeException("Non sono state trovate le indicazioni relative alle colonne interessate.");
        } else {
            sc.nextLine(); //We don't care about the first line in other modes
        }

        //File parsing
        while(sc.hasNext()) {
            line = sc.nextLine().split(regex);

            if (mode == 0) {
                element = new GeneAnnotationPair(
                        line[gene_index].replace("\"","").toUpperCase().trim(),
                        line[disease_index].replace("\"","").trim()
                );
            } else {
                element = new GeneAnnotationPair(line[0], line[1]);
            }

            dest.add(element);
        }
        sc.close();
    }

    /**
     * Passes the set to a list and orders it
     */
    private void ensureOrder() {
        this.geneAnnotationList.addAll(this.geneAnnotationSet);

        this.geneAnnotationList.sort(GeneAnnotationPair::compareTo);
    }

    /**
     * Merges lines which contain the same gene but different annotations into a line with all unique annotations
     */
    private void mergeGeneAnn() {
        GeneAnnotationPair first, current;
        boolean sameGene;

        for (int i = 0; i < this.geneAnnotationList.size() - 1; i++) {
            //first element with this gene
            first = this.geneAnnotationList.get(i);

            do {
                //next element, if there is another line with the same gene but different annotation, it will be found
                //in this (or further next) element
                current = this.geneAnnotationList.get(i + 1);

                sameGene = current.getGene().equals(first.getGene());
                if (sameGene) {
                    //same gene in elements in sequence, must merge their annotations
                    first.setAnnotation(mergeAnnotation(first.getAnnotation(), current.getAnnotation()));
                    //get rid of the element whose information now lies in first
                    this.geneAnnotationList.remove(i + 1);
                }

            } while (sameGene && i + 1 < this.geneAnnotationList.size());
        }        
    }

    /**
     * merges 2 annotations strings into a single one which has only the unique annotations from the starting ones
     * @param newAnn new annotation
     * @param oldAnn old annotation
     * @return merged annotation
     */
    private String mergeAnnotation(String newAnn, String oldAnn) {
        String separator = "; ";
        HashSet<String> noDupAnnotations = new HashSet<>();

        //maintains only unique annotations
        Collections.addAll(noDupAnnotations, newAnn.split(separator));
        Collections.addAll(noDupAnnotations, oldAnn.split(separator));

        //ensures order
        ArrayList<String> annotationsList = new ArrayList<>(noDupAnnotations);
        annotationsList.sort(String::compareTo);

        String annotation = "";
        for (int i = 0; i < annotationsList.size() - 1; i++) {
            annotation += annotationsList.get(i) + separator;
        }
        annotation += annotationsList.get(annotationsList.size() - 1);

        return annotation;
    }

    /**
     * Writes computation results into the chosen file in the correct format
     */
    private void writeOut(Collection<GeneAnnotationPair> coll) {
        String fileName = "CustomGeneAnnotation_HGMD_" + this.version + ".txt";
        String separator = "\t";
        String newline = System.lineSeparator();

        try {
            FileWriter fw = new FileWriter(pathOut + fileName);
            //builds the metadata line
            String s = "gene" + separator + "annotation" + newline;
            fw.append(s);

            //adds all the lines
            for (GeneAnnotationPair geneAnnotationPair : coll) {
                fw.append(geneAnnotationPair.join(separator) + newline);
            }

            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Errore nella scrittura del file",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
