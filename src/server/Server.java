package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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



        ObjectInputStream ois = null;

        try {
            final FileInputStream fichier = new FileInputStream("files/message.ser");
            ois = new ObjectInputStream(fichier);
            final Message m2 = (Message) ois.readObject();
            System.out.println("Message : ");

            System.out.println("tailleMidi : " + m2.getTailleMidi());
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }


        ServerSocket listener = null;
        String line;
        BufferedReader is;
        BufferedWriter os;
        Socket socketOfServer;

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
            /*ObjectInputStream in = null;
            ObjectOutputStream out = null;

            in = new ObjectInputStream(new BufferedInputStream(socketOfServer.getInputStream()));
            out = new ObjectOutputStream(new BufferedOutputStream(socketOfServer.getOutputStream()));

            out.writeObject(m);
            out.flush();
            System.out.println("Sent obj");

            //sendFile("files/message.ser", socketOfServer);*/

            // Open input and output streams
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
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
            }

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Sever stopped!");
    }
}