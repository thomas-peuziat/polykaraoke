package server;

public class ServerMain {
    public static void main(String[] args) {

        String unprocessedPath = "./files/server/unprocessed/";
        String availablePath = "./files/server/available/";
        String statsPath = "./files/server/stats.txt";
        int port = 9999;

        Server serverKaraoke = new Server(unprocessedPath, availablePath, statsPath, port);

        // Pour chaque sous dossiers de unprocessedPath, on va créer un fichier .ser
        serverKaraoke.processMusics(serverKaraoke.getSubdirectoriesUnprocessedPath());

        // On lance la boucle du server, il va attendre les clients, répondre à leurs requêtes,
        // puis se remettre en attente
        serverKaraoke.communicationLoop();

    }
}