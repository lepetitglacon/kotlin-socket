import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket

class SocketHandler {
    lateinit var socket: Socket
    lateinit var objectOutputStream: ObjectOutputStream
    lateinit var objectInputStream: ObjectInputStream

    constructor(socket: Socket) {
        println("starting client handler")

        this.socket = socket
        objectOutputStream = ObjectOutputStream(this.socket.getOutputStream())
        Thread {
            objectInputStream = ObjectInputStream(socket.getInputStream())
        }.start()
        println("initialized client handler")
    }

    fun sendObject(obj: Any) {
        objectOutputStream.writeObject(obj)
        objectOutputStream.flush()
    }

    fun receiveObject(): Any {
        return objectInputStream.readObject()
    }

    fun close() {
        objectOutputStream.close()
        objectInputStream.close()
        socket.close()
    }
}