package nhb.net;

import java.util.function.Consumer;

public interface Socket {

	boolean isAlive();

	/**
	 * Connect to uri
	 * 
	 * @param uri in format <b>protocol://endpoint_spec/path</b>.<br>
	 *            For example: <br>
	 *            <ul>
	 *            <li>tcp://localhost:8080</li>
	 *            <li>inproc://mysocket</li>
	 *            <li>ipc://my_unix_socket</li>
	 *            </ul>
	 */
	void connect(String uri);

	void close();

	void send(Payload payload);

	void setReceiver(Consumer<Payload> consumer);
}
