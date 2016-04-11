package net.coderodde.movieminer;

/**
 * This class describes a movie.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Apr 11, 2016)
 */
public class Movie implements Comparable<Movie> {
    
    private final int id;
    private final String name;
    
    public Movie(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        return id == ((Movie) o).id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Movie o) {
        return id - o.id;
    }
}
