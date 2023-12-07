package fi.utu.tech.telephonegame.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * This class represents a server that listens for incoming connections.
 * When a new peer connects, a new PeerHandler instance is created for it.
 */
public class Server extends Thread {
  private ServerSocket serverSocket;
  private int port;
  private ConcurrentLinkedQueue<Serializable> messagesIn;
  private CopyOnWriteArrayList<PeerHandler> peers;

  public Server(int port, ServerSocket serverSocket, ConcurrentLinkedQueue<Serializable> messagesIn,
      CopyOnWriteArrayList<PeerHandler> peers) {
    this.setName("Server Thread");
    this.serverSocket = serverSocket;
    this.port = port;
    this.peers = peers;
    this.messagesIn = messagesIn;
  }

  @Override
  public void run() {
    try {
      // Here we create a server socket and start listening for incoming connections
      serverSocket = new ServerSocket(port);
      while (true) {
        // When a new peer connects, we create a new socket and start a new PeerHandler
        // instance for it
        System.out.println("Waiting for peer to connect");
        Socket socket = serverSocket.accept();
        System.out.println("Peer connected:" + socket.getInetAddress() + ":" + socket.getPort());
        PeerHandler peerHandler = new PeerHandler(socket, messagesIn);
        peers.add(peerHandler);
        peerHandler.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
