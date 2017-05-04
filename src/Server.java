import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Server {



    //A special "wrapper" for ArrayList, which provides access to an array of different threads
    //connection - This is an array with all user connections
    //synchronizedList - Get synchronized collection objects
    private List<Connection> connections =
            Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket server;

    static Queue<String> queue = new CircularQueue<String>(new LinkedList<String>(), Const.maxCountLastMessage);


    public Server() {
        try {
            server = new ServerSocket(Const.PORT);
            System.out.println("Server started");

            while (true) {

                Socket socket = server.accept();

                Connection con = new Connection(socket);
                if (connections.size() < Const.maxCountUsers){

                    connections.add(con);

                    con.start();
                } else System.out.println("Exceeded max number of users");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }


    private void closeAll() {
        try {
            server.close();

            //Enumerate all Connection and call the close () method for each.
            // The synchronized {} block is necessary for correct access to one data of
            // their different threads
            synchronized(connections) {
                Iterator<Connection> iter = connections.iterator();
                while(iter.hasNext()) {
                    ((Connection) iter.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("The streams were not closed!");
        }
    }

    //Work with a specific user in a separate thread
    private class Connection extends Thread {
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;

        private String name;


        public Connection(Socket socket) {
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        // Query the user name and wait for messages from it.
        // When you receive each message, it is sent to everyone else along with the user name.
        @Override
        public void run() {
            try {
                name = in.readLine();
                //We send a message to all customers that a new user has logged in.
                synchronized(connections) {
                    Iterator<Connection> iter = connections.iterator();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("[YYYY.MM.dd][' at' HH:mm:ss]");
                    String dateTime = dateTimeFormatter.format(localDateTime);
                    while(iter.hasNext()) {
                        ((Connection) iter.next()).out.println(dateTime + " - " + name + " cames now");
                    }
                    Connection aa = connections.get(connections.size() - 1);
                    print(aa);

                }


                String str;
                while (true) {
                    //Reads the received data
                    str = in.readLine();
                    if(str.equals("exit")) break;

                    //Sending the next message to all clients
                    synchronized(connections) {
                        Iterator<Connection> iter = connections.iterator();

                        InetAddress addr = InetAddress.getLocalHost();
                        String myLANIP = addr.getHostAddress();

                        LocalDateTime localDateTime = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("[YYYY.MM.dd][' at' HH:mm:ss]");
                        String dateTime = dateTimeFormatter.format(localDateTime);

                        // checking whether any symbols have remained in the input stream
                        while(iter.hasNext()) {
                            ((Connection) iter.next()).out.println(name + " (" + dateTime + "): " + str + "{" + myLANIP + "}");
                       }
                        queue.add(name + " (" + dateTime + "): " + str);
                    }
                }

                synchronized(connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext()) {
                        ((Connection) iter.next()).out.println(name + " has left");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }



        public void close() {
            try {
                in.close();
                out.close();
                socket.close();

                //If there are no more connections left, we close everything that is and shut down the server
                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("The streams were not closed!");
            }
        }
    }


    private static void print(Connection conect) {
        for (String s : queue) {
            conect.out.print(s + " ");
            conect.out.println();
        }

    }


}