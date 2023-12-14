package fi.utu.tech.telephonegame.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * This class represents the connection between two peers.
 * It sends and receives seralizable objects (messages) from a peer.
 * 
 * The class has two streams: one for sending and one for receiving serializable objects.
 * The class also has two queues: one for incoming messages and one for outgoing messages.
 * Objects to be sent are in a queue, which is initialized here. Objects are added to the queue by calling postMessage method in networkService.
 * Objects to be received are in a queue, which is initialized in the NetworkService class.
 */

public class PeerHandler extends Thread {

  Socket s;
  final ObjectInputStream in;
  final ObjectOutputStream out;
  LinkedBlockingQueue<Serializable> msgIn;
  LinkedBlockingQueue<Serializable> msgOut;

  public PeerHandler(Socket s, LinkedBlockingQueue<Serializable> msgIn) throws IOException {
    this.s = s;
    this.out = new ObjectOutputStream(s.getOutputStream());
    this.in = new ObjectInputStream(s.getInputStream());
    this.msgIn = msgIn;
    this.msgOut = new LinkedBlockingQueue<Serializable>();
    this.setName("PeerHandler Thread");
  }


  /**
   * Adds a message to the queue of messages to be sent to the peer.
   * 
   * @param msg The message to be sent
   * 
   * note: This method is called by the NetworkService class
   */
  public void postMessage(Serializable msg) {
    msgOut.add(msg);
  }

  @Override
  public void run() {
    System.out.println("Peer connected" + s.getInetAddress() + ":" + s.getPort());

    // We read the receiving messages in a separate thread, because the readObject method is blocking.
    new Thread(() -> {
      try (in) {
        while (true) {
          Object o = in.readObject();
          if (o != null) {
            try {
              msgIn.put((Serializable) o);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }).start();

    // Here we take the next message to be sent and send it to the peer
    try (out) {
      while (true) {
        try {
          Serializable msg = msgOut.take();
          out.writeObject(msg);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      System.out.println("Peer disconnected" + s.getInetAddress() + ":" + s.getPort());
      e.printStackTrace();
    }
  }

}

