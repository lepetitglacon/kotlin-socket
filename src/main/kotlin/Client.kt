import java.io.*
import java.net.Socket

fun main() {
    Client("localhost", 8080).start()
}

class Client {
    private val socket: Socket

    constructor(host: String, port: Int) {
        socket = Socket(host, port)
        println("Connected to server: ${socket.inetAddress.hostAddress}")
    }

    fun start() {

        val output = ObjectOutputStream(socket.getOutputStream())

        val inputReader = BufferedReader(InputStreamReader(System.`in`))

        Thread {
            val input = ObjectInputStream(socket.getInputStream())
            while (true) {
                println(input.readObject())
            }
        }.start()

        while (true) {
            println("reading console")
            val request = inputReader.readLine() ?: break
            println(request)
            output.writeObject(request)
        }
    }
}