package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import message.Message;

public class Server {

    public static void main(String[] args) {
        final Message m = new Message();
        m.createTabBytes("files/medley.mid", "files/texte.txt", "files/total");
        System.out.println("Taille :"+ m.getTailleMidi());

        ObjectOutputStream oos = null;

        try {
            final FileOutputStream fichier = new FileOutputStream("files/message.ser");
            oos = new ObjectOutputStream(fichier);
            oos.writeObject(m);
            oos.flush();
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        ServerSocket listener = null;
        Socket socketOfServer;
        ObjectInputStream in;
        ObjectOutputStream out;

        // Try to open a server socket on port 9999
        // Note that we can't choose a port less than 1023 if we are not
        // privileged users (root)

        try {
            listener = new ServerSocket(9999);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        try {
            System.out.println("Server is waiting to accept user...");
            // Accept client connection request
            // Get new Socket at Server.
            socketOfServer = listener.accept();
            System.out.println("Accept a client!");

            out = new ObjectOutputStream(socketOfServer.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socketOfServer.getInputStream());

            System.out.println("Serveur a cree les flux");

            out.writeObject(m);
            out.flush();

            System.out.println("Serveur: donnees emises");

            out.writeObject("Liste titres");
            out.flush();

            System.out.println("Serveur: liste emise");

            Object objetRecu = in.readObject();
            int[] tableauRecu = (int[]) objetRecu;

            System.out.println("Serveur recoit: " + Arrays.toString(tableauRecu));

            in.close();
            out.close();
            socketOfServer.close();

            // Open input and output streams
            /*is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));


            while (true) {
                // Read data to the server (sent from client).
                line = is.readLine();

                // Write to socket of Server
                // (Send to client)
                os.write(">> " + line);
                // End of line
                os.newLine();
                // Flush data.
                os.flush();


                // If users send QUIT (To end conversation).
                if (line.equals("QUIT")) {
                    os.write(">> OK");
                    os.newLine();
                    os.flush();
                    break;
                }
            }*/

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        }
        System.out.println("Sever stopped!");
    }
}