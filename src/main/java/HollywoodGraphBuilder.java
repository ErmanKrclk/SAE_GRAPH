import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.util.*;

public class HollywoodGraphBuilder {

    public static Graph<String, DefaultEdge> genererGraphe(String cheminFichier) throws IOException {
        Graph<String, DefaultEdge> graphe = new SimpleGraph<>(DefaultEdge.class);
        BufferedReader reader = new BufferedReader(new FileReader(cheminFichier));
        String ligne;

        while ((ligne = reader.readLine()) != null) {
            JsonElement film = JsonParser.parseString(ligne);

            if (film.getAsJsonObject().has("cast")) {
                List<String> noms = new ArrayList<>();

                for (JsonElement acteur : film.getAsJsonObject().getAsJsonArray("cast")) {
                    String brut = acteur.getAsString();
                    String nom = brut.replaceAll("\\[\\[|\\]\\]", "");
                    if (nom.contains("|")) {
                        nom = nom.split("\\|")[1];
                    }
                    nom = nom.trim();

                    graphe.addVertex(nom);
                    noms.add(nom);
                }

                for (int i = 0; i < noms.size(); i++) {
                    for (int j = i + 1; j < noms.size(); j++) {
                        if (!noms.get(i).equals(noms.get(j))) {
                            graphe.addEdge(noms.get(i), noms.get(j));
                        }
                    }
                }
                
            }
        }

        reader.close();
        return graphe;
    }

    public static void exporterDot(Graph<String, DefaultEdge> graph, String chemin) throws IOException {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider(nom ->
                Map.of("label", new DefaultAttribute<>(nom, AttributeType.STRING)));

        try (Writer writer = new FileWriter(chemin)) {
            exporter.exportGraph(graph, writer);
        }
    }

    public static void main(String[] args) {
        try {
            Graph<String, DefaultEdge> graph = genererGraphe("data/data_1.txt");
            System.out.println("Nombre d'acteurs : " + graph.vertexSet().size());
            System.out.println("Nombre de collaborations : " + graph.edgeSet().size());

            exporterDot(graph, "graph.dot");
            System.out.println("Graphe exporté avec succès dans graph.dot");
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
