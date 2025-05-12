import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HollywoodGraphBuilder {

    static class Movie {
        String title;
        List<String> cast;
    }

    public static Graph<String, DefaultEdge> genererGraphe(String cheminFichier) throws IOException {
        Graph<String, DefaultEdge> graphe = new SimpleGraph<>(DefaultEdge.class);
        Gson gson = new Gson();

        BufferedReader reader = new BufferedReader(new FileReader(cheminFichier));
        String ligne;

        while ((ligne = reader.readLine()) != null) {
            JsonElement elem = JsonParser.parseString(ligne);
            Movie film = gson.fromJson(elem, Movie.class);

            if (film.cast != null) {
                List<String> noms = new ArrayList<>();

                for (String brut : film.cast) {
                    String nom = brut.replaceAll("\\[\\[|\\]\\]", "");
                    if (nom.contains("|")) {
                        nom = nom.split("\\|")[0];
                    }
                    nom = nom.trim();
                    graphe.addVertex(nom);
                    noms.add(nom);
                }

                for (String a1 : noms) {
                    for (String a2 : noms) {
                        if (!a1.equals(a2)) {
                            graphe.addEdge(a1, a2);
                        }
                    }
                }
            }
        }

        reader.close();
        return graphe;
    }

    public static void main(String[] args) {
        try {
            Graph<String, DefaultEdge> graph = genererGraphe("data/data_100.txt");
            System.out.println("Nombre d'acteurs : " + graph.vertexSet().size());
            System.out.println("Nombre de collaborations : " + graph.edgeSet().size());
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }
}