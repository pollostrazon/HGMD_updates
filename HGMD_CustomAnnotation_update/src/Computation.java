import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Created by Paolo on 27/03/2017.
 */
public class Computation {

    private final int ADVSUBCOLS = 30;
    private final int MICLESCOLS = 24;

    String pathAdvSub, version, pathOut;
    ArrayList<String[]> coordinates;
    ArrayList<String> out;
    HashSet<String> nodup;

    public Computation(String pathAdvSub, String version, String pathOut) {
        this.pathAdvSub = pathAdvSub;
        this.version = version;
        this.pathOut = pathOut;
        this.coordinates = new ArrayList<>();
        this.nodup = new HashSet<>();
        this.out = new ArrayList<>();
    }

    public void start() {
        try {
            try {
                readAdvSub();
            } catch(FileNotFoundException e) {
                JOptionPane.showMessageDialog(null,"Errore nella lettura dei file (Inesistenti?)",
                        "Errore",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            Compute();
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
        sc.nextLine(); //skip the intestation
        String regex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        while(sc.hasNext()) {
            String[] element = new String[5];
            line = sc.nextLine().split(regex);
            element[0] = line[5];
            element[1] = line[5];
            element[2] = line[11];
            element[3] = line[11];
            if (!(element[0].equals("NULL") || element[1].equals("NULL"))) coordinates.add(element);
        }
        sc.close();
    }

    private void Compute() {
        int length = coordinates.size();
        String[] element;

        for (int i = 0; i < length; i++) {
            element = coordinates.get(i);
            element[0] = element[0].replaceAll("(chr)|:(.+)","");
            element[1] = element[1].replaceAll("(chr(\\d+|X|x|Y|y):)|(:[-+])","");
            element[2] = element[2].replaceAll("([AaGgTtCc]+\\[)|(\\/.+)","");
            element[3] = element[3].replaceAll("([AaGgTtCc]+\\[\\w\\/)|(\\].+)","");
            element[4] = "HGMD";
            coordinates.set(i, element);
        }

        for (String[] e : coordinates) {
            nodup.add(String.join("\t",e));
        }

        out.addAll(nodup);
        out.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    private void WriteOut() {
        String fileName = "CustomAnnotation_Variant_HGMD_" + this.version + ".txt";
        String separator = "\t";
        String newline = System.lineSeparator();
        String s;

        try {
            FileWriter fw = new FileWriter(pathOut + fileName);
            s = "chr" + separator + "position" + separator + "ref" + separator + "variant" + separator + "annotation" + newline;
            fw.append(s);

            for (String l : out) {
                fw.append(l + newline);
            }

            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Errore nella scrittura del file",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}
