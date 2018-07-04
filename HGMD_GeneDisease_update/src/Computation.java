import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Created by Paolo on 27/03/2017.
 * This class hosts the main computation.
 */
public class Computation {

    private String pathAdvSub, pathMicroLesions, version, pathOut;
    private ArrayList<Pair> table;

    public Computation(String pathAdvSub, String pathMicroLesions, String version, String pathOut) {
        this.pathAdvSub = pathAdvSub;
        this.pathMicroLesions = pathMicroLesions;
        this.version = version;
        this.pathOut = pathOut;
        this.table = new ArrayList<>();
    }

    public void start() {
        try {
            try {
                if (pathAdvSub != null) readAdvSub();
                readMicroLesions();
            } catch(FileNotFoundException e) {
                JOptionPane.showMessageDialog(null,"Errore nella lettura dei file (Inesistenti?)",
                        "Errore",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            this.table.sort(new Comparator<Pair>() {
                @Override
                public int compare(Pair p1, Pair p2) {
                    return p1.compareTo(p2);
                }
            });

            WriteOut();
            JOptionPane.showMessageDialog(null,"Tutto è andato secondo i piani.",
                    "Informazione", JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null,t.getMessage(),
                    "Errore",JOptionPane.ERROR_MESSAGE);
            t.printStackTrace();
        }

    }

    private void readAdvSub() throws FileNotFoundException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(this.pathAdvSub)));
        System.out.println(sc.delimiter());
        String[] line;
        String gene;
        String disease;

        while(sc.hasNext()) {
            line = sc.nextLine().split(",");
            gene = line[8].replace("\"","").toUpperCase().trim();
            disease = line[9].replace("\"","").trim();
            Pair pair = new Pair(gene, disease);
            if (table.contains(pair)) {
                continue;
            }
            table.add(pair);
        }
        sc.close();
    }

    private void readMicroLesions() throws FileNotFoundException{
        Scanner sc = new Scanner(new BufferedReader(new FileReader(this.pathMicroLesions)));
        String[] line;
        String gene;
        String disease;
        String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        //Throwing intestation row away
        if (sc.hasNext()) sc.nextLine();

        while(sc.hasNext()) {
            line = sc.nextLine().split(regex);
            gene = line[2].replace("\"","").toUpperCase().trim();
            disease = line[3].replace("\"","").trim();
            Pair pair = new Pair(gene, disease);
            if (table.contains(pair)) {
                continue;
            }
            table.add(pair);
        }
        sc.close();
    }

    private void WriteOut() {
        String fileName = "CustomGeneAnnotation_HGMD_" + this.version + ".txt";
        String separator = "\t";
        String newline = "\r\n";
        String gene = "";

        try {
            FileWriter fw = new FileWriter(pathOut + fileName);

            fw.append("gene" + separator + "annotation");
            for (Pair p: table) {
                if (p.getGene().equals(gene)) {
                    fw.append("; " + p.getDisease());
                } else {
                    gene = p.getGene();
                    fw.append(newline + p.getGene() + separator + p.getDisease());
                }
            }
            fw.append(newline);

            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Errore nella scrittura del file",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    class Pair implements Comparable<Pair>{
        private String gene;
        private String disease;

        public Pair(String gene, String disease) {
            this.gene = gene;
            this.disease = disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }

        public String getGene() {
            return this.gene;
        }

        public String getDisease() {
            return this.disease;
        }

        @Override
        public String toString() {
            return "Gene: " + this.getGene() +
                    ", Disease: " + this.getDisease();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                if (this.getGene().equals(((Pair) obj).getGene()) && this.getDisease().equals(((Pair) obj).getDisease())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public boolean equalsGene(String g) {
            if (this.getGene().equals(g)) return true;
            else return false;
        }

        @Override
        public int compareTo(Pair p) {
            return this.getGene().compareTo(p.getGene());
        }
    }
}
