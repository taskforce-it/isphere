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

}
