import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.util.*;

public class HollywoodGraphBuilder {

static class Film {
    String title;
    List<String> cast;
}

public static Graph<String, DefaultEdge> creerGraphe(String cheminFichier) throws IOException {
Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
BufferedReader f = new BufferedReader(new FileReader(cheminFichier));
String ligne;
Gson gson = new Gson();

while((ligne = f.readLine()) != null){
    JsonElement filmJson = JsonParser.parseString(ligne);
    Film film = gson.fromJson(filmJson, Film.class);

    if(film.cast != null){
        List<String> noms = new ArrayList<>();
        for(String a : film.cast){
            String nom = a.replaceAll("\\[\\[|\\]\\]", "");
            if(nom.contains("|")){
            nom = nom.split("\\|")[0];
            }
            nom = nom.trim();
            g.addVertex(nom);
            noms.add(nom);
        }

        // relier tous les acteurs entre eux
        for (int i = 0; i < noms.size(); i++) {
            for (int j = 0; j < noms.size(); j++) {
                if (!noms.get(i).equals(noms.get(j))) {
                    g.addEdge(noms.get(i), noms.get(j));
                }
            }
        }
    }
}
    f.close();
    return g;
}

// aider du TP1
public static void exportGraph(Graph<String, DefaultEdge> graph, String chemin) throws IOException {
		DOTExporter<String, DefaultEdge> exporter = new DOTExporter<String, DefaultEdge>();
		exporter.setVertexAttributeProvider((x) -> Map.of("label", new DefaultAttribute<>(x, AttributeType.STRING)));
		exporter.exportGraph(graph, new FileWriter("graph.dot"));
	}

public static Set<String> collaborateursCommuns(Graph<String, DefaultEdge> graphe, String acteur1, String acteur2) {
    Set<String> voisins1 = Graphs.neighborSetOf(graphe, acteur1);
    Set<String> voisins2 = Graphs.neighborSetOf(graphe, acteur2);

    Set<String> communs = new HashSet<>(voisins1);
    communs.retainAll(voisins2);
    return communs;
}

public static void main(String[] args) {
    try {
        Graph<String, DefaultEdge> graphe = creerGraphe("data/data_100.txt");
        System.out.println("Nb acteurs: " + graphe.vertexSet().size());
        System.out.println("Nb liens: " + graphe.edgeSet().size());

        exportGraph(graphe, "graph.dot");
        System.out.println("fichier DOT fait");

        Set<String> communs = collaborateursCommuns(graphe, "Tom Hanks", "Meg Ryan");

        System.out.println("Collaborateurs communs entre Tom Hanks et Meg Ryan :");
        if (communs.isEmpty()) {
            System.out.println("Aucun collaborateur commun trouvé.");
        } else {
            for (String nom : communs) {
                System.out.println(" - " + nom);
            }
        }

    } catch (Exception e) { 
        System.out.println("erreur : " + e);
    }
}


}
