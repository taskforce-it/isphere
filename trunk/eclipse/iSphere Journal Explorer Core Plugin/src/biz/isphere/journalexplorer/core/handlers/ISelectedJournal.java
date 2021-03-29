package biz.isphere.journalexplorer.core.handlers;

public interface ISelectedJournal {

    /**
     * Returns the name of the host connection.
     * 
     * @return connection name
     */
    public String getConnectionName();

    /**
     * Returns the library where the object is stored.
     * 
     * @return library name
     */
    public String getLibrary();

    /**
     * Returns the name of the journal.
     * 
     * @return journal name
     */
    public String getName();

    /**
     * Returns the qualified name of the journal, e.g. library/journal
     * <p>
     * The qualified journal name is used for labels and tooltips.
     * 
     * @return qualified journal name
     */
    public String getQualifiedName();

}
