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
public class Server extends Thread{
  // Attributes

  //Constructor
  public Server() {
    // Pull
  }

  @Override
  public void run() {
    //
  }
}
