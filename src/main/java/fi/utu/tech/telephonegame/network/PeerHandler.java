package fi.utu.tech.telephonegame.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * This class represents the connection between two peers.
 * It sends and receives seralizable objects (messages) from a peer.
 * 
 * The class has two streams: one for sending and one for receiving serializable objects.
 * The class also has two queues: one for incoming messages and one for outgoing messages.
 * Objects to be sent are in a queue. Objects are added to the queue by calling postMessage method.
 * Objects to be received are in a queue, which is initialized in the NetworkService class.
 */

public class PeerHandler extends Thread {

  Socket s;
  final ObjectInputStream in;
  final ObjectOutputStream out;
  ConcurrentLinkedQueue<Serializable> msgIn;
  ConcurrentLinkedQueue<Serializable> msgOut;

  public PeerHandler(Socket s, ConcurrentLinkedQueue<Serializable> msgIn) throws IOException {
    System.out.println("Initializing peer handler");
    this.s = s;
    this.in = new ObjectInputStream(s.getInputStream());
    this.out = new ObjectOutputStream(s.getOutputStream());
    this.msgIn = msgIn;
    this.msgOut = new ConcurrentLinkedQueue<Serializable>();
    this.setName("PeerHandler Thread");
  }

  public void postMessage(Serializable msg) {
    msgOut.add(msg);
  }

  @Override
  public void run() {
    System.out.println("Peer connected" + s.getInetAddress() + ":" + s.getPort());

    try (in;out) {
      while (true) {
        Object o = in.readObject();
        msgIn.add((Serializable) o);
        Serializable msg = msgOut.poll();
        if (msg != null) {
          out.writeObject(msg);
        }
      }
    } catch (IOException e) {
      System.out.println("Peer disconnected" + s.getInetAddress() + ":" + s.getPort());
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

}
