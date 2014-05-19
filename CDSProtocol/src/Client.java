import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Client {

	public static void main(String args[]) throws Exception {   
		int myPort = 8887;
		int sendPort = 8888;
/*
		BufferedReader inFromUser =          
				new BufferedReader(new InputStreamReader(System.in));       
		DatagramSocket clientSocket = new DatagramSocket();       
		InetAddress IPAddress = InetAddress.getByName("localhost");       
		byte[] sendData = new byte[1024];       
		byte[] receiveData = new byte[1024];       
		//String sentence = inFromUser.readLine();       
		String sentence = "here is the message!";
		sendData = sentence.getBytes();       
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, sendPort);       
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);       
		String modifiedSentence = new String(receivePacket.getData());       
		System.out.println("FROM SERVER:" + modifiedSentence);       
		clientSocket.close();
*/
		Channel c = new UDPChannel(myPort);
		InetAddress IPAddress = InetAddress.getByName("localhost"); 
		if (c.connect(new InetSocketAddress(IPAddress, sendPort))) {
			ByteBuffer bb = ByteBuffer.allocate(1024);
			bb.put("here is the message!".getBytes());
			bb.flip();
			c.write(bb);
			                                                                                     
			bb.clear();
			System.out.println(c.read(bb));
			bb.flip();
			byte[] rst = new byte[15];
			bb.get(rst);
			System.out.println(new String(rst));
		}
	}
}