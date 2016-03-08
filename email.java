import java.io.*;
import java.net.*;

class blue {
    public static void main(String args[]) throws Exception
    {
      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("10.137.2.122");
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      int state = 0;
      String sentence = "HELLO Blue";
      String response = "";
      DatagramPacket sendPacket = null;
      DatagramPacket receivePacket = null;

      while (state < 3){
        sendData = new byte[1024];
        receiveData = new byte[1024];
        switch (state){
          case 0: // send initial message to server and wait for response
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);
            receivePacket =  new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            response = new String(receivePacket.getData());
          if (response.substring(0,3).equals("100")) {
            System.out.println("From server: 100 Message Received. Waiting for second client.");
            state = 1; //You are first client. wait for second client to connect
          }
          else if (response.substring(0,3).equals("200")){
            System.out.println("From server: 200 Message Received. Please wait for message.");
            state = 2; //you are second client. Wait for message from first client
          }
          break;

          //State 1
          case 1: // Waiting for notification that the second client is ready
          System.out.println("Wait for second client to connect.");
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData());

          if (response.substring(0,3).equals("200")){
            System.out.print("200 Message recieved. Second client connected.\n Me: ");
            sentence = inFromUser.readLine();
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, 9876);
            clientSocket.send(sendPacket);
            state = 2; //other client connected
          }
          //state = 2; //transition to state 2: chat mode
          break;


          //State 2:  CHAT MODE, wait for response first then send message
          case 2:
          receivePacket = new DatagramPacket(receiveData, receiveData.length);
          clientSocket.receive(receivePacket);
          response = new String(receivePacket.getData());

          if (response.startsWith("Goodbye")){
            state = 3; //prepare to exit the while loop
            break;
          }
          //if not Goodbye, get next message from user and send it;
          System.out.println(response);

          System.out.print("Me: ");
          sentence = inFromUser.readLine();
          sendData = sentence.getBytes();
          sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, 9876);
          clientSocket.send(sendPacket);
          //stay in state 2
          break;
        } //end switch
      } // end while
      //close the socket


      clientSocket.close();
    }
}
