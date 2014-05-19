import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Server {    
	
	public static void main(String args[]) throws Exception       {  
		int myPort = 8888;
		int sendPort = 8887;
		/*
		DatagramSocket serverSocket = new DatagramSocket(myPort);             
		byte[] receiveData = new byte[1024];             
		byte[] sendData = new byte[1024];             
		while(true)	{                   
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   
			serverSocket.receive(receivePacket);                   
			String sentence = new String( receivePacket.getData());                   
			System.out.println("RECEIVED: " + sentence);                   
			InetAddress IPAddress = receivePacket.getAddress();                   
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();                   
			sendData = capitalizedSentence.getBytes(); 
			
			DatagramChannel channel = DatagramChannel.open();
			channel.socket().bind(new InetSocketAddress(myPort+1));
			
			ByteBuffer bb = ByteBuffer.allocate(1024);
			bb.put(sendData);
			bb.flip();
			
			channel.connect(new InetSocketAddress(IPAddress, port));
			channel.write(bb);               
		}
		*/
		
		Channel c = new UDPChannel(myPort);
		Channel x = c.accept();
		if (x != null) {System.out.println("Connection accepeted");}
		ByteBuffer bb = ByteBuffer.allocate(1024);
		x.read(bb);
		bb.flip();
		System.out.println(new String(bb.array()));
		bb.clear();
		bb.put("reply message!".getBytes());
		bb.flip();
		x.write(bb);
		x.close();		
	} 
}
