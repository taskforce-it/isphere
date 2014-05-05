package biz.isphere.journaling.retrievejournalentries.internal;

/**
 * Enumeration, representing the System i journal entry types.<br>
 * (only Journal Entry Types for Journal Code "R" are included)
 * 
 * @author Stanley, Thomas Raddatz
 */
public enum JournalEntryType {
    BR ("BR", "Before-image of record updated for rollback"),
    DL ("DL", "Record deleted from physical file member"),
    DR ("DR", "Record deleted for rollback"),
    IL ("IL", "Increment record limit"),
    PT ("PT", "Record added to physical file member"),
    PX ("PX", "Record added directly to physical file member"),
    UB ("UB", "Before-image of record updated in physical file member"),
    UP ("UP", "After-image of record updated in physical file member"),
    UR ("UR", "After-image of record updated for rollback");

    private String key;

    private String description;

    private JournalEntryType(String key, String description) {
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
