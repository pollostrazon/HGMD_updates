/**
 * This class represents a custom annotation
 */
public class CustomAnnotationVariant implements Comparable<CustomAnnotationVariant> {
    private String chr, pos, ref, variant, annotation;

    public CustomAnnotationVariant(String chr, String pos, String ref, String variant, String annotation) {
        this.chr = chr;
        this.pos = pos;
        this.ref = ref;
        this.variant = variant;
        this.annotation = annotation;
    }

    public CustomAnnotationVariant(String chr, String pos, String ref, String variant) {
        this(chr, pos, ref, variant, "HGMD");
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Transforms current fields into db-like fields
     */
    public void transform() {
        this.setChr(this.getChr().replaceAll("(chr)|:(.+)","")); //gets the chromosome number
        this.setPos(this.getPos().replaceAll("(chr(\\d+|X|x|Y|y):)|(:[-+])","")); //gets the coordinate
        this.setRef(this.getRef().replaceAll("([AaGgTtCc]+\\[)|(\\/.+)","")); //gets the expected protein
        this.setVariant(this.getVariant().replaceAll("([AaGgTtCc]+\\[\\w\\/)|(\\].+)","")); //gets the mutation
    }

    /**
     * Joins gene and annotation through a chosen CharSequence
     * @param cs input CharSequence
     * @return joined string
     */
    public String join(CharSequence cs) {
        return this.chr + cs +
                this.pos + cs +
                this.ref + cs +
                this.variant + cs +
                this.annotation;
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
    public int compareTo(CustomAnnotationVariant customAnnotationVariant) {
        int cmp;
        cmp = this.chr.compareTo(customAnnotationVariant.chr);

        if (cmp == 0) {
            cmp = Integer.compare(Integer.parseInt(this.pos),
                    Integer.parseInt(customAnnotationVariant.pos));

            if (cmp == 0) {
                cmp = String.join(this.ref, this.variant, this.annotation)
                        .compareTo(
                        String.join(customAnnotationVariant.ref,
                                customAnnotationVariant.variant,
                                customAnnotationVariant.annotation));
            }
        }

        return cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CustomAnnotationVariant) {
            return this.compareTo((CustomAnnotationVariant) o) == 0;
        } else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 3*hash + this.chr.hashCode();
        hash = 3*hash + this.pos.hashCode();
        hash = 3*hash + this.ref.hashCode();
        hash = 3*hash + this.variant.hashCode();
        hash = 3*hash + this.annotation.hashCode();
        return hash;
    }
}
