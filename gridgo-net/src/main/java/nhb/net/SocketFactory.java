package nhb.net;

public interface SocketFactory {

	Socket createSocket(CommunicationType communicationType);
}
