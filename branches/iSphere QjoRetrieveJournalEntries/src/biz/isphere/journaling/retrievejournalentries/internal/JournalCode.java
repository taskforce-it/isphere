package biz.isphere.journaling.retrievejournalentries.internal;

/**
 * Enumeration, representing the System i journal codes.
 * 
 * @author Stanley, Thomas Raddatz
 */
public enum JournalCode {
    A ("A", "System accounting entry"),
    B ("B", "Integrated file system operation"),
    C ("C", "Commitment control operation"),
    D ("D", "Database file operation"),
    E ("E", "Data area operation"),
    F ("F", "Database file member operation"),
    I ("I", "Internal operation"),
    J ("J", "Journal or journal receiver operation"),
    L ("L", "License management"),
    M ("M", "Network management data"),
    P ("P", "Performance tuning entry"),
    Q ("Q", "Data queue operation"),
    R ("R", "Record level operation"),
    S ("S", "Distributed mail service for SNA distribution services (SNADS), network alerts, or mail server framework"),
    T ("T", "Audit trail entry"),
    U ("U", "User generated");

    private String key;

    private String description;

    private JournalCode(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return this.key;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%s, (%s)", getDescription(), getKey());
    }
}
