import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class ChatMulticast {

	static String nome;
	static boolean fim = false;
	static String ipGrupo = "224.255.255.255";
	static int porta = 6789;

	static Scanner in = new Scanner(System.in);

	public static void adicionarUsuario() throws IOException {

		System.out.println("Digite seu nome:");
		nome = in.nextLine();
		InetAddress groupIp = InetAddress.getByName(ipGrupo);

		MulticastSocket mSocket = new MulticastSocket(porta);
		mSocket.setTimeToLive(0);
		mSocket.joinGroup(groupIp);
		Thread th = new Thread(new ExecutaThread(mSocket, groupIp, porta));
		th.start();

    System.out.println();
		System.out.println("Ola " + nome );
    System.out.println("Bem vindo ao chat Multicast");
    System.out.println("Para deixar o chat digite 'sair' ");
     System.out.println();
    System.out.println("Digite sua mensagem:");

		while (true) {
			String msg = in.nextLine();

			if (msg.equals("sair")) {
				fim = true;
				mSocket.leaveGroup(groupIp);
				mSocket.close();
				break;
			}
			msg = "[" + nome + "]" + ": " + msg;
			byte[] buffer = new byte[1000];
			buffer = msg.getBytes();

			DatagramPacket enviaMsg = new DatagramPacket(buffer, buffer.length, groupIp, porta);
			mSocket.send(enviaMsg);
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("");
		System.out.println("CHAT MULTICAST JAVA");
		System.out.println("");

		adicionarUsuario();

	}
}

class ExecutaThread implements Runnable {

	MulticastSocket mSocket;
	InetAddress gropoIp;
	int porta;
	
	public ExecutaThread(MulticastSocket mSocket, InetAddress gropoIp, int porta) {
		super();
		this.mSocket = mSocket;
		this.gropoIp = gropoIp;
		this.porta = porta;
	} 
	
	@Override
	public void run () {
		while (!ChatMulticast.fim) {
			byte [] buffer = new byte[1000];
			DatagramPacket recebeMsg = new DatagramPacket (buffer, buffer.length);
			try {
				mSocket.receive(recebeMsg);
				String msgGrupo = new String(recebeMsg.getData());
				System.out.println(msgGrupo);
			} catch (IOException e) {
				System.out.println("Voce saiu do chat.");
			}
		}
	}
	
}
