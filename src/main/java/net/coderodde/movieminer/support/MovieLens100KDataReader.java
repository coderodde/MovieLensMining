package net.coderodde.movieminer.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import net.coderodde.movieminer.DataReader;
import net.coderodde.movieminer.Movie;

/**
 * This class implements the data reader for MovieLens 100K data package.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Apr 11, 2016)
 */
public class MovieLens100KDataReader implements DataReader {

    private static final String MOVIE_DATA_FILE_NAME = "u.item";
    private static final String USER_DATA_FILE_NAME  = "u.data";
    
    private final String path;
    
    public MovieLens100KDataReader(String path) {
        this.path = Objects.requireNonNull(path, 
                                           "The path to the package is null.");
    }
    
    @Override
    public List<Set<Movie>> readData() throws FileNotFoundException {
        String directoryName = path + File.separator;
        File movieDataFile = new File(directoryName + MOVIE_DATA_FILE_NAME);
        File userDataFile  = new File(directoryName + USER_DATA_FILE_NAME);
        
        List<Movie> movieList = readMovieList(movieDataFile);
        Map<Integer, Movie> movieIndex = computeMovieIndex(movieList);
        
        Map<Integer, Set<Movie>> movieMap = readMovieMap(userDataFile,
                                                         movieIndex);
        return getItemsetList(movieMap);
    }
    
    private List<Movie> readMovieList(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(file));
        List<Movie> movieList = new ArrayList<>();
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Movie movie = parseMovie(line);
            
            if (movie != null) {
                movieList.add(movie);
            }
        }
        
        return movieList;
    }
    
    private Movie parseMovie(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        
        String[] tokens = line.split("\\s*\\|\\s*");
        
        if (tokens.length < 2) {
            return null;
        }
        
        int id;
        
        try {
            id = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException ex) {
            return null;
        }
            
        return new Movie(id, tokens[1]);
    }
    
    private Map<Integer, Movie> computeMovieIndex(List<Movie> movieList) {
        Map<Integer, Movie> index = new HashMap<>(movieList.size());
        
        movieList.stream()
                 .forEach((movie) -> { index.put(movie.getId(), movie); });
        
        return index;
    }
    
    private Map<Integer, Set<Movie>> 
        readMovieMap(File userDataFile, Map<Integer, Movie> movieIndex) 
        throws FileNotFoundException {
        Map<Integer, Set<Movie>> map = new HashMap<>();
        Scanner scanner = new Scanner(new FileInputStream(userDataFile));
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            
            if (line == null || line.isEmpty()) {
                continue;
            }
            
            String[] parts = line.split("\\s+");
            
            if (parts.length < 2) {
                continue;
            }
            
            int userId;
            int movieId;
            
            try {
                userId  = Integer.parseInt(parts[0]);
                movieId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                continue;
            }
            
            map.putIfAbsent(userId, new HashSet<>());
            map.get(userId).add(movieIndex.get(movieId));
        }
        
        return map;
    }
        
    private List<Set<Movie>> getItemsetList(Map<Integer, Set<Movie>> map) {
        List<Set<Movie>> itemsetList = new ArrayList<>(map.size());
        map.values().forEach((itemset) -> { itemsetList.add(itemset); });
        return itemsetList;
    }
}
