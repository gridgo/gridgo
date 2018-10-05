package org.nanomsg;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class NanoLibrary {
	static {
		ensureNativeCode();
	}

	public NanoLibrary() {
		symbols = new HashMap<String, Integer>();
		load_symbols(symbols);

		symbols.putIfAbsent("NN_VERSION_MAJOR", -1);
		symbols.putIfAbsent("NN_VERSION_MINOR", -1);
		symbols.putIfAbsent("NN_VERSION_PATCH", -1);
		symbols.putIfAbsent("NN_VERSION", this.get_version());

		symbols.putIfAbsent("NN_SOURCE", -1);
		symbols.putIfAbsent("NN_SINK", -1);

		AF_SP = get_symbol("AF_SP");
		AF_SP_RAW = get_symbol("AF_SP_RAW");

		NN_SOL_SOCKET = get_symbol("NN_SOL_SOCKET");

		NN_INPROC = get_symbol("NN_INPROC");
		NN_IPC = get_symbol("NN_IPC");
		NN_TCP = get_symbol("NN_TCP");

		NN_DOMAIN = get_symbol("NN_DOMAIN");
		NN_PROTOCOL = get_symbol("NN_PROTOCOL");
		NN_LINGER = get_symbol("NN_LINGER");
		NN_SNDBUF = get_symbol("NN_SNDBUF");
		NN_RCVBUF = get_symbol("NN_RCVBUF");
		NN_SNDTIMEO = get_symbol("NN_SNDTIMEO");
		NN_RCVTIMEO = get_symbol("NN_RCVTIMEO");
		NN_RECONNECT_IVL = get_symbol("NN_RECONNECT_IVL");
		NN_RECONNECT_IVL_MAX = get_symbol("NN_RECONNECT_IVL_MAX");
		NN_SNDPRIO = get_symbol("NN_SNDPRIO");
		NN_SNDFD = get_symbol("NN_SNDFD");
		NN_RCVFD = get_symbol("NN_RCVFD");

		NN_TCP_NODELAY = get_symbol("NN_TCP_NODELAY");

		NN_SUB_SUBSCRIBE = get_symbol("NN_SUB_SUBSCRIBE");
		NN_SUB_UNSUBSCRIBE = get_symbol("NN_SUB_UNSUBSCRIBE");

		NN_REQ_RESEND_IVL = get_symbol("NN_REQ_RESEND_IVL");

		NN_SURVEYOR_DEADLINE = get_symbol("NN_SURVEYOR_DEADLINE");

		NN_DONTWAIT = get_symbol("NN_DONTWAIT");

		NN_PAIR = get_symbol("NN_PAIR");
		NN_PUB = get_symbol("NN_PUB");
		NN_SUB = get_symbol("NN_SUB");
		NN_REP = get_symbol("NN_REP");
		NN_REQ = get_symbol("NN_REQ");
		NN_PUSH = get_symbol("NN_PUSH");
		NN_PULL = get_symbol("NN_PULL");
		NN_SURVEYOR = get_symbol("NN_SURVEYOR");
		NN_RESPONDENT = get_symbol("NN_RESPONDENT");
		NN_BUS = get_symbol("NN_BUS");

		NN_POLLIN = get_symbol("NN_POLLIN");
		NN_POLLOUT = get_symbol("NN_POLLOUT");

		ENOTSUP = get_symbol("ENOTSUP");
		EPROTONOSUPPORT = get_symbol("EPROTONOSUPPORT");
		ENOBUFS = get_symbol("ENOBUFS");
		ENETDOWN = get_symbol("ENETDOWN");
		EADDRINUSE = get_symbol("EADDRINUSE");
		EADDRNOTAVAIL = get_symbol("EADDRNOTAVAIL");
		ECONNREFUSED = get_symbol("ECONNREFUSED");
		EINPROGRESS = get_symbol("EINPROGRESS");
		ENOTSOCK = get_symbol("ENOTSOCK");
		EAFNOSUPPORT = get_symbol("EAFNOSUPPORT");
		EPROTO = get_symbol("EPROTO");
		EAGAIN = get_symbol("EAGAIN");
		EBADF = get_symbol("EBADF");
		EINVAL = get_symbol("EINVAL");
		EMFILE = get_symbol("EMFILE");
		EFAULT = get_symbol("EFAULT");
		EACCES = get_symbol("EACCES");
		EACCESS = get_symbol("EACCESS");
		ENETRESET = get_symbol("ENETRESET");
		ENETUNREACH = get_symbol("ENETUNREACH");
		EHOSTUNREACH = get_symbol("EHOSTUNREACH");
		ENOTCONN = get_symbol("ENOTCONN");
		EMSGSIZE = get_symbol("EMSGSIZE");
		ETIMEDOUT = get_symbol("ETIMEDOUT");
		ECONNABORTED = get_symbol("ECONNABORTED");
		ECONNRESET = get_symbol("ECONNRESET");
		ENOPROTOOPT = get_symbol("ENOPROTOOPT");
		EISCONN = get_symbol("EISCONN");
		ESOCKTNOSUPPORT = get_symbol("ESOCKTNOSUPPORT");
		ETERM = get_symbol("ETERM");
		EFSM = get_symbol("EFSM");
	}

	public native void nn_term();

	public native int nn_errno();

	public native String nn_strerror(int errnum);

	public native int nn_socket(int domain, int protocol);

	public native int nn_close(int socket);

	public native int nn_bind(int socket, String address);

	public native int nn_connect(int socket, String address);

	public native int nn_shutdown(int socket, int how);

	public native int nn_send(int socket, ByteBuffer buffer, int flags);

	public native int nn_recv(int socket, ByteBuffer buffer, int flags);

	public native int nn_sendstr(int socket, String str, int flags);

	public native int nn_sendbyte(int socket, byte[] str, int flags);

	public native String nn_recvstr(int socket, int flags);

	public native byte[] nn_recvbyte(int socket, int flags);

	public native int nn_getsockopt_int(int socket, int level, int optidx, Integer optval);

	public native int nn_setsockopt_int(int socket, int level, int optidx, int optval);

	public native int nn_setsockopt_str(int socket, int level, int optidx, String optval);

	public native int nn_poll(NNPollFD[] pollFDs, int timeout);

	public int get_version() {
		int maj, min, pat, ver;
		maj = get_symbol("NN_VERSION_MAJOR");
		min = get_symbol("NN_VERSION_MINOR");
		pat = get_symbol("NN_VERSION_PATCH");

		if (maj == -1 || min == -1 || pat == -1) {
			maj = get_symbol("NN_VERSION_CURRENT");
			min = get_symbol("NN_VERSION_REVISION");
			pat = get_symbol("NN_VERSION_AGE");
		}

		ver = maj * 10000 + min * 100 + pat;
		System.out.println("maj: " + maj + " min: " + min + " pat: " + pat + " ver: " + ver);

		return ver;
	}

	public int get_symbol_count() {
		return symbols.size();
	}

	public int get_symbol(String name) {
		Integer value = symbols.get(name);
		if (value == null)
			throw new IllegalArgumentException("Argument " + name + " not found in symbols.");
		return value.intValue();
	}

	public int AF_SP = -1;
	public int AF_SP_RAW = -1;

	public int NN_SOL_SOCKET = -1;

	public int NN_INPROC = -1;
	public int NN_IPC = -1;
	public int NN_TCP = -1;

	public int NN_DOMAIN = -1;
	public int NN_PROTOCOL = -1;
	public int NN_LINGER = -1;
	public int NN_SNDBUF = -1;
	public int NN_RCVBUF = -1;
	public int NN_SNDTIMEO = -1;
	public int NN_RCVTIMEO = -1;
	public int NN_RECONNECT_IVL = -1;
	public int NN_RECONNECT_IVL_MAX = -1;
	public int NN_SNDPRIO = -1;
	public int NN_SNDFD = -1;
	public int NN_RCVFD = -1;

	public int NN_TCP_NODELAY = -1;

	public int NN_SUB_SUBSCRIBE = -1;
	public int NN_SUB_UNSUBSCRIBE = -1;

	public int NN_REQ_RESEND_IVL = -1;

	public int NN_SURVEYOR_DEADLINE = -1;

	public int NN_DONTWAIT = -1;

	public int NN_PAIR = -1;
	public int NN_PUB = -1;
	public int NN_SUB = -1;
	public int NN_REP = -1;
	public int NN_REQ = -1;
	public int NN_PUSH = -1;
	public int NN_PULL = -1;
	public int NN_SURVEYOR = -1;
	public int NN_RESPONDENT = -1;
	public int NN_BUS = -1;

	public int NN_POLLIN = -1;
	public int NN_POLLOUT = -1;

	public int ENOTSUP = -1;
	public int EPROTONOSUPPORT = -1;
	public int ENOBUFS = -1;
	public int ENETDOWN = -1;
	public int EADDRINUSE = -1;
	public int EADDRNOTAVAIL = -1;
	public int ECONNREFUSED = -1;
	public int EINPROGRESS = -1;
	public int ENOTSOCK = -1;
	public int EAFNOSUPPORT = -1;
	public int EPROTO = -1;
	public int EAGAIN = -1;
	public int EBADF = -1;
	public int EINVAL = -1;
	public int EMFILE = -1;
	public int EFAULT = -1;
	public int EACCES = -1;
	public int EACCESS = -1;
	public int ENETRESET = -1;
	public int ENETUNREACH = -1;
	public int EHOSTUNREACH = -1;
	public int ENOTCONN = -1;
	public int EMSGSIZE = -1;
	public int ETIMEDOUT = -1;
	public int ECONNABORTED = -1;
	public int ECONNRESET = -1;
	public int ENOPROTOOPT = -1;
	public int EISCONN = -1;
	public int ESOCKTNOSUPPORT = -1;
	public int ETERM = -1;
	public int EFSM = -1;

	private static void ensureNativeCode() {
		if (EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY) {
			System.out.println("Using embedded native jnano lib");
		} else {
			System.out.println("Try to load native library from system shared libs");
			System.loadLibrary("jnano");
		}
	}

	private native int load_symbols(Map<String, Integer> map);

	private Map<String, Integer> symbols;

}
