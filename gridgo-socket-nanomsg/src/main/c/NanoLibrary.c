#include <stdio.h>
#include "NanoUtil.h"

#include "org_nanomsg_NanoLibrary.h"

static jclass    buffer_cls;
static jmethodID position_r_mid;
static jmethodID position_w_mid;
static jmethodID limit_r_mid;

static jclass pollfd_class;
static jfieldID pollfd_fd;
static jfieldID pollfd_events;
static jfieldID pollfd_revents;

typedef struct {
    const char* name;
    int val;
}symbol_t;

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_load_1symbols(JNIEnv* env,
                                                                  jobject obj,
                                                                  jobject map)
{
    jclass cmap = 0;
    jclass cint = 0;
    jmethodID mput = 0;
    jmethodID mnew = 0;
    jint count = 0;
    symbol_t symbols[140];
    int symi = 0;

    cmap = (*env)->GetObjectClass(env, map);
    NANO_ASSERT(cmap);

    mput = (*env)->GetMethodID(env, cmap,
                               "put",
                               "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    NANO_ASSERT(mput);

    cint = (*env)->FindClass(env, "java/lang/Integer");
    NANO_ASSERT(cint);

    mnew = (*env)->GetMethodID(env, cint,
                               "<init>",
                               "(I)V");
    NANO_ASSERT(mnew);

    for (count = 0; ; ++count) {
        const char* ckey;
        int cval;

        ckey = nn_symbol(count, &cval);
        if (ckey == 0)
            break;

        symbols[symi].name = ckey;
        symbols[symi].val = cval;
        symi++;
    }

    symbols[symi].name = "NN_POLLIN";
    symbols[symi].val = NN_POLLIN;
    symi++;


    symbols[symi].name = "NN_POLLOUT";
    symbols[symi].val = NN_POLLOUT;
    symi++;


    symbols[symi].name = "EACCESS";
    symbols[symi].val = EACCESS;
    symi++;


    symbols[symi].name = "EISCONN";
    symbols[symi].val = EISCONN;
    symi++;


    symbols[symi].name = "ESOCKTNOSUPPORT";
    symbols[symi].val = ESOCKTNOSUPPORT;
    symi++;

    for (count = 0; count < symi; ++count)
    {
        const char* ckey;
        int cval;
        jstring jkey =  0;
        jobject jval = 0;

        ckey = symbols[count].name;
        cval = symbols[count].val;
        // fprintf(stderr, "Got symbol #%d: [%s] -> %d\n", count, ckey, cval);

        jkey = (*env)->NewStringUTF(env, ckey);
        NANO_ASSERT(jkey);
        // fprintf(stderr, "Created Java String for [%s]\n", ckey);

        jval = (*env)->NewObject(env, cint, mnew, cval);
        NANO_ASSERT(jval);
        // fprintf(stderr, "Created Java Integer for [%d]\n", cval);

        (*env)->CallObjectMethod(env, map, mput, jkey, jval);
        // fprintf(stderr, "Inserted symbol in map: [%s] -> %d\n", ckey, cval);
    }

    buffer_cls = (*env)->FindClass(env, "java/nio/Buffer");
    position_r_mid = (*env)->GetMethodID(env, buffer_cls, "position", "()I");
    position_w_mid = (*env)->GetMethodID(env, buffer_cls, "position", "(I)Ljava/nio/Buffer;");
    limit_r_mid    = (*env)->GetMethodID(env, buffer_cls, "limit",    "()I");

    pollfd_class = (*env)->FindClass(env, "org/nanomsg/NNPollFD");
    pollfd_fd = (*env)->GetFieldID(env, pollfd_class, "fd", "I");
    pollfd_events = (*env)->GetFieldID(env, pollfd_class, "events", "I");
    pollfd_revents = (*env)->GetFieldID(env, pollfd_class, "revents", "I");

    return count;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1errno(JNIEnv* env,
                                                              jobject obj)
{
    return nn_errno();
}

JNIEXPORT jstring JNICALL Java_org_nanomsg_NanoLibrary_nn_1strerror(JNIEnv* env,
                                                                    jobject obj,
                                                                    jint errnum)
{
    const char* cerr = 0;
    jstring jerr = 0;

    cerr = nn_strerror(errnum);
    if (cerr == 0)
        cerr = "";

    jerr = (*env)->NewStringUTF(env, cerr);
    NANO_ASSERT(jerr);
    return jerr;
}

JNIEXPORT void JNICALL Java_org_nanomsg_NanoLibrary_nn_1term(JNIEnv* env,
                                                             jobject obj)
{
    nn_term();
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1socket(JNIEnv* env,
                                                               jobject obj,
                                                               jint domain,
                                                               jint protocol)
{
    return nn_socket(domain, protocol);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1close(JNIEnv* env,
                                                              jobject obj,
                                                              jint socket)
{
    return nn_close(socket);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1bind(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jstring address)
{
    const char* cadd = 0;

    cadd = (*env)->GetStringUTFChars(env, address, NULL);
    NANO_ASSERT(cadd);

    return nn_bind(socket, cadd);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1connect(JNIEnv* env,
                                                                jobject obj,
                                                                jint socket,
                                                                jstring address)
{
    const char* cadd = 0;

    cadd = (*env)->GetStringUTFChars(env, address, NULL);
    NANO_ASSERT(cadd);

    return nn_connect(socket, cadd);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1shutdown(JNIEnv* env,
                                                                 jobject obj,
                                                                 jint socket,
                                                                 jint how)
{
    return nn_shutdown(socket, how);
}

/*
 * Write up to remaining() bytes to the queue, where remaining() = limit() -
 * position().
 *
 * Suppose that a byte sequence of length n is written, where 0 <= n <= r.
 * This byte sequence will be transferred from the buffer starting at index p,
 * where p is the buffer's position at the moment this method is invoked; the
 * index of the last byte written will be p + n - 1. Upon return the buffer's
 * position will be equal to p + n; its limit will not have changed.
 *
 */
JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1send(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jobject buffer,
                                                             jint flags)
{
    jclass    cls;

    jint      position;
    jint      limit;
    jint      send_length;
    jbyte*    cbuf;
    jint      ret;
    jint      new_position;

    position     = (*env)->CallIntMethod(env, buffer, position_r_mid);
    limit        = (*env)->CallIntMethod(env, buffer, limit_r_mid);
    send_length  = limit - position;
    cbuf         = (jbyte*) (*env)->GetDirectBufferAddress(env, buffer);

    NANO_ASSERT(cbuf);
    ret          = nn_send(socket, cbuf + position, send_length, flags);
    new_position = ret <= 0? 0 : position + ret;

    (*env)->CallObjectMethod(env, buffer, position_w_mid, new_position);

    return ret;
}

/*
 * Read up to remaining() bytes from the queue, where remaining() = limit() -
 * position().
 *
 * Suppose that a byte sequence of length n is read, where 0 <= n <= r. This
 * byte sequence will be transferred into the buffer so that the first byte in
 * the sequence is at index p and the last byte is at index p + n - 1, where p
 * is the buffer's position at the moment this method is invoked. Upon return
 * the buffer's position will be equal to p + n; its limit will not have
 * changed.
 *
 */
JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1recv(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jobject buffer,
                                                             jint flags)
{
    jclass    cls;

    jint      position;
    jint      limit;
    jint      recv_length;
    jbyte*    cbuf;
    jint      ret;
    jint      new_position;

    position     = (*env)->CallIntMethod(env, buffer, position_r_mid);
    limit        = (*env)->CallIntMethod(env, buffer, limit_r_mid);
    recv_length  = position - limit;
    cbuf         = (jbyte*) (*env)->GetDirectBufferAddress(env, buffer);

    NANO_ASSERT(cbuf);
    ret          = nn_recv(socket, cbuf + position, limit - position, flags);
    new_position = ret <= 0? 0 : position + ret;

    (*env)->CallObjectMethod(env, buffer, position_w_mid, new_position);

    return ret;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1getsockopt_1int(JNIEnv* env,
                                                                        jobject obj,
                                                                        jint socket,
                                                                        jint level,
                                                                        jint optidx,
                                                                        jobject optval)
{
    jint ret = -1;
    int go = 0;

    switch (level) {
    case NN_SOL_SOCKET:
        switch (optidx) {
        case NN_DOMAIN:
        case NN_PROTOCOL:
        case NN_LINGER:
        case NN_SNDBUF:
        case NN_RCVBUF:
        case NN_SNDTIMEO:
        case NN_RCVTIMEO:
        case NN_RECONNECT_IVL:
        case NN_RECONNECT_IVL_MAX:
        case NN_SNDPRIO:
        case NN_SNDFD:
        case NN_RCVFD:
            go = 1;
            break;
        }
        break;

    case NN_TCP:
        switch (optidx) {
        case NN_TCP_NODELAY:
            go = 1;
            break;
        }
        break;
    }

    if (go) {
        int oval = 0;
        size_t olen = sizeof(oval);

        ret = nn_getsockopt(socket, level, optidx, &oval, &olen);
        if (ret >= 0) {
            jclass cval = 0;
            jfieldID ival = 0;

            ret = olen;

            cval = (*env)->GetObjectClass(env, optval);
            NANO_ASSERT(cval);

            ival = (*env)->GetFieldID(env, cval, "value", "I");
            NANO_ASSERT(ival);

            (*env)->SetIntField(env, optval, ival, oval);
        }
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1setsockopt_1int(JNIEnv* env,
                                                                        jobject obj,
                                                                        jint socket,
                                                                        jint level,
                                                                        jint optidx,
                                                                        jint optval)
{
    jint ret = -1;
    int go = 0;

    switch (level) {
    case NN_SOL_SOCKET:
        switch (optidx) {
        case NN_LINGER:
        case NN_SNDBUF:
        case NN_RCVBUF:
        case NN_SNDTIMEO:
        case NN_RCVTIMEO:
        case NN_RECONNECT_IVL:
        case NN_RECONNECT_IVL_MAX:
        case NN_SNDPRIO:
            go = 1;
            break;
        }
        break;

    case NN_TCP:
        switch (optidx) {
        case NN_TCP_NODELAY:
            go = 1;
            fprintf(stderr, "TCP_NODELAY\n");
            break;
        }
        break;
    }

    if (go) {
        int oval = optval;
        size_t olen = sizeof(oval);

        ret = nn_setsockopt(socket, level, optidx, &oval, olen);
        if (ret >= 0) {
            ret = olen;
        }
    }

    return ret;
}

JNIEXPORT jstring JNICALL Java_org_nanomsg_NanoLibrary_nn_1recvstr(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jint flags)
{
	void *buf;
	jstring retst;
	int ret = nn_recv(socket, &buf, NN_MSG, flags);
	if (ret < 0) return 0;
	retst = (*env)->NewStringUTF(env, buf);
	nn_freemsg(buf);
	return retst;
}

JNIEXPORT jbyteArray JNICALL Java_org_nanomsg_NanoLibrary_nn_1recvbyte(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jint flags)
{
	void *buf;
	jbyteArray result;
	int ret = nn_recv(socket, &buf, NN_MSG, flags);
	if (ret < 0) return 0;
	result = (*env)->NewByteArray(env, ret);
	(*env)->SetByteArrayRegion(env, result, 0, ret, (const jbyte*) buf);
	nn_freemsg(buf);
	return result;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1sendbyte(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jbyteArray array,
                                                             jint flags)
{
    jbyte* cbuf = 0;
    jint ret = 0;
    
    jsize length = (*env)->GetArrayLength(env, array);
    cbuf = (*env)->GetByteArrayElements(env, array, 0);
    NANO_ASSERT(cbuf);
    ret = nn_send(socket, cbuf, length, flags);
    (*env)->ReleaseByteArrayElements(env, array, cbuf, JNI_ABORT);
    
    return ret;
}



JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1sendstr(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jstring str,
                                                             jint flags)
{
    jint ret = 0;
    
    char *cbuf;
    cbuf = (*env)->GetStringUTFChars( env, str , NULL ) ;
    NANO_ASSERT(cbuf);
    ret = nn_send(socket, cbuf, strlen(cbuf) + 1, flags);
    (*env)->ReleaseStringUTFChars(env, str,cbuf);
    return ret;
}


JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1setsockopt_1str(JNIEnv* env,
                                                                        jobject obj,
                                                                        jint socket,
                                                                        jint level,
                                                                        jint optidx,
                                                                        jstring optval)
{
    jint ret = -1;
    int go = 0;
    
    switch (level) {
        case NN_SOL_SOCKET:
            switch (optidx) {
                case NN_SOCKET_NAME:
                    go = 1;
                    break;
            }
            break;
            
        case NN_TCP:
            switch (optidx) {
                case NN_TCP_NODELAY:
                    go = 1;
                    fprintf(stderr, "TCP_NODELAY\n");
                    break;
            }
            break;
            
        default:
            go = 1;
    }
    
    if (go) {
        char *cbuf;
        size_t olen;
        cbuf = (*env)->GetStringUTFChars( env, optval , NULL ) ;
        NANO_ASSERT(cbuf);

        olen = strlen(cbuf);
        ret = nn_setsockopt(socket, level, optidx, cbuf, olen);
        if (ret >= 0) {
            ret = olen;
        }
        (*env)->ReleaseStringUTFChars(env, optval,cbuf);
    }
    
    return ret;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1poll(JNIEnv* env,
                                                            jobject obj,
															jobjectArray pollfds,
															jint timeout)
{
    struct nn_pollfd *pfds;
    int i;
    int ret;
    jsize length = (*env)->GetArrayLength(env, pollfds);
    if (length <= 0) return -1;

	pfds =  (struct nn_pollfd *) malloc(sizeof(struct nn_pollfd) * length);
	NANO_ASSERT(pfds);

    
	for (i = 0; i < length; ++i)
	{
	    jobject pollfd = (*env)->GetObjectArrayElement(env, pollfds, i);
	    pfds[i].fd = (*env)->GetIntField(env, pollfd, pollfd_fd);
	    pfds[i].events = (*env)->GetIntField(env, pollfd, pollfd_events);
	}

	ret = nn_poll(pfds, length, timeout);

    if (ret > 0) {
        for (i = 0; i < length; ++i)
        {
            jobject pollfd = (*env)->GetObjectArrayElement(env, pollfds, i);
            (*env)->SetIntField(env, pollfd, pollfd_revents, pfds[i].revents);
            (*env)->SetObjectArrayElement(env, pollfds, i, pollfd);
        }
    }

    free(pfds);

	return ret;
}
