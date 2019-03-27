/**
 * This class represents a gene-annotation pair
 */
public class GeneAnnotationPair implements Comparable<GeneAnnotationPair> {
    private String gene, annotation;

    public GeneAnnotationPair(String gene, String annotation) {
        this.gene = gene;
        this.annotation = annotation;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Joins gene and annotation through a chosen CharSequence
     * @param cs input CharSequence
     * @return joined string
     */
    public String join(CharSequence cs) {
        return this.gene + cs + this.annotation;
    }

    /**
     * Joins gene and annotation through a tab character
     * @return joined string
     */
    public String join() {
        return this.join("\t");
    }

    @Override
    public String toString() {
        return this.join();
    }

    @Override
    public int compareTo(GeneAnnotationPair geneAnnotationPair) {
        return this.gene.compareTo(geneAnnotationPair.gene);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GeneAnnotationPair) {
            return this.gene.equals(((GeneAnnotationPair) o).gene) &&
                    this.annotation.equals(((GeneAnnotationPair) o).annotation);
        } else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 3*hash + this.gene.hashCode();
        hash = 3*hash + this.annotation.hashCode();
        return hash;
    }
}
