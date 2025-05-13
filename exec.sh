
dot -T pdf graph.dot -o graph.pdf
javac -cp "lib/*" -d bin src/*.java
java -cp "bin:lib/*" HollywoodGraphBuilder
