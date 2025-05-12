import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HollywoodGraphBuilder {
    
    // Classe pour représenter un film
    static class Movie {
        String title;
        List<String> cast;
    }
    
    public static Graph<String, DefaultEdge> buildGraphFromJson(String filePath) throws IOException {
        // Création du graphe
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        
        // Lecture du fichier JSON
        FileReader fileReader = new FileReader(filePath);
        JsonElement jsonElement = JsonParser.parseReader(fileReader);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        
        // Création du parser Gson
        Gson gson = new Gson();
        
        // Parcours de chaque élément du tableau JSON
        for (JsonElement element : jsonArray) {
            // Conversion de l'élément en objet Movie
            Movie movie = gson.fromJson(element, Movie.class);
            
            // Si le film a un casting
            if (movie.cast != null) {
                // Ajout des acteurs comme sommets
                for (String actorName : movie.cast) {
                    // Nettoyage du nom de l'acteur
                    String cleanName = cleanActorName(actorName);
                    
                    // Ajout de l'acteur comme sommet s'il n'existe pas déjà
                    if (!cleanName.isEmpty()) {
                        graph.addVertex(cleanName);
                    }
                }
                
                // Création des liens entre les acteurs
                for (int i = 0; i < movie.cast.size(); i++) {
                    String actor1 = cleanActorName(movie.cast.get(i));
                    
                    // On ne traite pas les noms vides
                    if (actor1.isEmpty()) {
                        continue;
                    }
                    
                    for (int j = 0; j < movie.cast.size(); j++) {
                        // On évite de créer un lien entre un acteur et lui-même
                        if (i == j) {
                            continue;
                        }
                        
                        String actor2 = cleanActorName(movie.cast.get(j));
                        
                        // On ne traite pas les noms vides
                        if (actor2.isEmpty()) {
                            continue;
                        }
                        
                        // Ajout de l'arête si elle n'existe pas déjà
                        if (!graph.containsEdge(actor1, actor2)) {
                            graph.addEdge(actor1, actor2);
                        }
                    }
                }
            }
        }
        
        // Fermeture du fichier
        fileReader.close();
        
        return graph;
    }
    
    // Nettoyage du nom de l'acteur
    private static String cleanActorName(String rawName) {
        // Suppression des [[ et ]] et récupération du nom avant le |
        String cleanName = rawName.replaceAll("\\[\\[|\\]\\]", "");
        if (cleanName.contains("|")) {
            cleanName = cleanName.split("\\|")[0];
        }
        return cleanName.trim();
    }
    
    public static void main(String[] args) {
        try {
            // Création du graphe à partir du fichier JSON
            Graph<String, DefaultEdge> graph = buildGraphFromJson("data/data_100.txt");
            
            // Affichage des statistiques
            System.out.println("Nombre d'acteurs : " + graph.vertexSet().size());
            System.out.println("Nombre de collaborations : " + graph.edgeSet().size());
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }
}