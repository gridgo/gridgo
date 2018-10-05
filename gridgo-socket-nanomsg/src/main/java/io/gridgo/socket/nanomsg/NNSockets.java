package io.gridgo.socket.nanomsg;

public class NNSockets {

	public static NNSocket createSocket(int type, String address) {
		return new NNSocket(type, address);
	}

	public static NNSocket createPushSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_PUSH, address);
	}

	public static NNSocket createPullSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_PULL, address);
	}

	public static NNSocket createPairSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_PAIR, address);
	}

	public static NNSocket createPubSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_PUB, address);
	}

	public static NNSocket createSubSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_SUB, address);
	}

	public static NNSocket createBusSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_BUS, address);
	}

	public static NNSocket createReqSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_REQ, address);
	}

	public static NNSocket createRepSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_REP, address);
	}

	public static NNSocket createSurveyorSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_SURVEYOR, address);
	}

	public static NNSocket createRespondentSocket(String address) {
		return createSocket(NNSocket.nanomsg.NN_RESPONDENT, address);
	}
}
