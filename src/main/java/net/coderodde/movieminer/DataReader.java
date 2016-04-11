package net.coderodde.movieminer;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

/**
 * This interface defines the API for reading the data sets.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Apr 11, 2016)
 */
public interface DataReader {
    
    /**
     * Reads the entire data and returns the list of movie itemsets.
     * 
     * @return the list of movie itemsets.
     */
    public List<Set<Movie>> readData() throws FileNotFoundException;
}
