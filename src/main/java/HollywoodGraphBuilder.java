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

        while ((ligne = f.readLine()) != null) {
            JsonElement filmJson = JsonParser.parseString(ligne);
            Film film = gson.fromJson(filmJson, Film.class);

            if (film.cast != null) {
                List<String> noms = new ArrayList<>();
                for (String a : film.cast) {
                    String nom = a.replaceAll("\\[\\[|\\]\\]", "");
                    if (nom.contains("|")) {
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

    // 3.2
    public static Set<String> collaborateursCommuns(Graph<String, DefaultEdge> graphe, String acteur1, String acteur2) {
        Set<String> voisins1 = Graphs.neighborSetOf(graphe, acteur1);
        Set<String> voisins2 = Graphs.neighborSetOf(graphe, acteur2);

        Set<String> communs = new HashSet<>(voisins1);
        communs.retainAll(voisins2);
        return communs;
    }

    // 3.3
    /*
     * # SAE Exploration algorithmique d'un problème
     * 
     * Voici l'algorithme permettant d'obtenir l'ensemble des collaborateurs à
     * distance k d'un acteur ou d'une actrice. A vous de l'implémenter en JAVA
     * 
     * Algo collaborateurs_proches(G,u,k):
     * """Algorithme renvoyant l'ensemble des acteurs à distance au plus k de
     * l'acteur u dans le graphe G. La fonction renvoie None si u est absent du
     * graphe.
     * 
     * Parametres:
     * G: le graphe
     * u: le sommet de départ
     * k: la distance depuis u
     * """
     * si u n'est pas un sommet de G:
     * afficher u+"est un illustre inconnu"
     * fin de l'algorithme
     * collaborateurs = Ensemble vide
     * Ajouter u à l'ensemble des collaborateurs
     * pour tout i allant de 1 à k:
     * collaborateurs_directs = Ensemble Vide
     * Pour tout collaborateur c dans l'ensemble des collaborateurs
     * Pour tout voisin v de c:
     * si v n'est pas dans l'ensemble des collaborateurs:
     * Ajouter v à l'ensemble des collaborateurs_directs
     * Remplacer collaborateurs par l'union des collaborateurs et
     * collaborateurs_directs
     * Renvoyer l'ensemble collaborateurs
     * 
     * Vous pouvez par exemple utiliser la classe HashSet en java
     * (https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html) pour les
     * ensembles.
     */

    public static Set<String> collaborateur_proches(Graph<String, DefaultEdge> G, String u, int k) {
        if (!G.containsVertex(u)) {
            System.out.println((u + "est un acteur inconnu"));
            return null;
        }

        Set<String> collaborateurs = new HashSet<>();
        collaborateurs.add(u);
        for (int i = 1; i <= k; i++) {
            Set<String> collaborateurs_directs = new HashSet<>();
            for (String collab : collaborateurs) {
                for (String voisin : Graphs.neighborSetOf(G, collab)) {
                    if (!collaborateurs.contains(voisin)) {
                        collaborateurs_directs.add(voisin);
                    }
                }
            }
            collaborateurs.addAll(collaborateurs_directs);
        }
        return collaborateurs;
    }

    // 3.4

    public static int DistanceMaxCentralite(Graph<String, DefaultEdge> G, String u) {
        if (!G.containsVertex(u)) {
            System.out.println(u + " est un acteur inconnu");
            return -1;
        }
    
        Set<String> dejaObserver = new HashSet<>();
        dejaObserver.add(u);
    
        Set<String> courant = new HashSet<>();
        courant.add(u);
    
        int distance = 0;
    
        while (!courant.isEmpty()) {
            Set<String> prochain = new HashSet<>();
    
            for (String acteur : courant) {
                for (String voisin : Graphs.neighborListOf(G, acteur)) {
                    if (!dejaObserver.contains(voisin)) {
                        dejaObserver.add(voisin);
                        prochain.add(voisin);
                    }
                }
            }
    
            courant = prochain;
            if (!courant.isEmpty()) {
                distance++;
            }
        }
    
        return distance;
    }
    
    public static String Centre(Graph<String, DefaultEdge> G) {
        int minCentralite = Integer.MAX_VALUE;
        String acteurCentral = null;
    
        for (String acteur : G.vertexSet()) {
            int c = DistanceMaxCentralite(G, acteur);
    

            if (c >= 0 && c < minCentralite) {
                minCentralite = c;
                acteurCentral = acteur;
            }
        }
    
        return acteurCentral;
    }
    


        public static void main(String[] args) {
            try {
                // 3.1
                Graph<String, DefaultEdge> graphe = creerGraphe("data/data_2.txt");
                System.out.println("Nb acteurs: " + graphe.vertexSet().size());
                System.out.println("Nb liens: " + graphe.edgeSet().size());
        
                exportGraph(graphe, "graph.dot");
                System.out.println("fichier DOT fait");
        
                // 3.2
                Set<String> communs = collaborateursCommuns(graphe, "Tom Hanks", "Charles Durning");
                System.out.println("Collaborateurs communs entre Tom Hanks et Charles Durning:");
                if (communs.isEmpty()) {
                    System.out.println("Aucun collaborateur commun trouvé.");
                } else {
                    for (String nom : communs) {
                        System.out.println(" - " + nom);
                    }
                }
        
                // 3.3
                Set<String> proches = collaborateur_proches(graphe, "Brad Pitt", 1);
                if (proches != null) {
                    System.out.println("Collaborateurs proches de Brad Pitt en k=1 : ");
                    for (String nom : proches) {
                        if (!nom.equals("Brad Pitt")) {
                            System.out.println(" - " + nom);
                        }
                    }
                }
                System.out.println("Collaborateurs proches de Brad Pitt en k=2 : " + collaborateur_proches(graphe, "Brad Pitt", 2));
        
                // 3.4 - Centralité de Tom Hanks
                int centraliteTom = DistanceMaxCentralite(graphe, "Charles Durningd");
                System.out.println("Centralité de Tom Hanks : " + centraliteTom);
        
                // 3.4 - Acteur central
                String acteurCentral = Centre(graphe);
                System.out.println("Acteur central du graphe : " + acteurCentral);
        
            } catch (Exception e) {
                System.out.println("erreur : " + e);
            }
        }}
        