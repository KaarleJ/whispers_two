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
 * uusi peerhandler=clienthandler olio server kun alkaa kuuntelemaan uutta asiakasta while loopilla
 * 
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
      while(true){
        Socket s = serverSocket.accept();
        PeerHandler ph = new PeerHandler(s, messagesIn);
        peers.add(ph);
        ph.start();
      }
    } catch(IOException e){
      e.printStackTrace();
    }
  }
}
