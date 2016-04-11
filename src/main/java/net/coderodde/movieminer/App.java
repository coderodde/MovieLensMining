package net.coderodde.movieminer;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import net.coderodde.mining.associationrules.AprioriFrequentItemsetGenerator;
import net.coderodde.mining.associationrules.AssociationRule;
import net.coderodde.mining.associationrules.AssociationRuleGenerator;
import net.coderodde.mining.associationrules.FrequentItemsetData;
import net.coderodde.movieminer.support.MovieLens100KDataReader;

/**
 * This class implements the command line program for mining movie association 
 * rules.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Apr 11, 2016)
 */
public class App {
   
    public static void main(String[] args) {
        if (args.length < 3) {
            printUsageInfo();
            return;
        }
        
        double minimumSupport    = Double.NaN;
        double minimumConfidence = Double.NaN; 
        
        try {
            minimumSupport = Double.parseDouble(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("Bad minimum support string: " + args[0]);
            System.exit(1);
        }
        
        try {
            minimumConfidence = Double.parseDouble(args[1]);
        } catch (NumberFormatException ex) {
            System.err.println("Bad minimum confidence string: " + args[1]);
        }
        
        System.out.println("Minimum support:    " + minimumSupport);
        System.out.println("Minimum confidence: " + minimumConfidence);
        
        List<Set<Movie>> data = null;
        
        try {
            DataReader dataReader = new MovieLens100KDataReader(args[2]);
            data = dataReader.readData();
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.exit(1);
        }
            
        AprioriFrequentItemsetGenerator<Movie> itemsetGenerator =
                new AprioriFrequentItemsetGenerator<>();

        long startTime = System.nanoTime();
        FrequentItemsetData<Movie> frequentItemsets
                = itemsetGenerator.generate(data, minimumSupport);
        long endTime = System.nanoTime();
        
        System.out.printf("Mined the frequent itemsets in %d milliseconds.\n",
                          (endTime - startTime) / 1_000_000);
        
        AssociationRuleGenerator<Movie> associationRuleGenerator = 
                new AssociationRuleGenerator<>();

        startTime = System.nanoTime();
        List<AssociationRule<Movie>> associationRules = 
                associationRuleGenerator.mineAssociationRules(
                    frequentItemsets,
                    minimumConfidence);
        endTime = System.nanoTime();
        
        System.out.printf("Mined the association rules in %d milliseconds.\n",
                         (endTime - startTime) / 1_000_000);
        
        System.out.println("Found " + associationRules.size() +
                           " association rules.");
        
        System.out.println("=== Association rules ===");
        associationRules.stream()
                        .forEach((rule) -> { System.out.println(rule); });
    }
    
    private static void printUsageInfo() {
        System.out.println("Usage: java -jar This.jar " +
                           "<min_support> <min_confidence> <dir>");
        System.out.println("Where: <min_support>    is the minimum support");
        System.out.println("       <min_confidence> is the minimum confidence");
        System.out.println("       <dir>            is the directory " + 
                           "containing the data");
    }
}
