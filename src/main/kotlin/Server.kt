import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random

fun main(args: Array<String>) {
    Server(8080).start()
}

class Server {
    private val serverSocket: ServerSocket

    val clients = mutableListOf<HandleClient>()

    constructor(port: Int) {
        serverSocket = ServerSocket(port)
        println("Server started on port $port")
    }

    fun start() {
        while (true) {
            val clientSocket = serverSocket.accept()
            println("New client connected: ${clientSocket.inetAddress.hostAddress}")
            val hc = HandleClient(clientSocket)
            clients.add(hc)

            Thread {
                hc.start()
//                HandleClient(clientSocket).start()
            }.start()
        }
    }

    inner class HandleClient(val clientSocket: Socket) {

        val output = ObjectOutputStream(clientSocket.getOutputStream())

        val name = Random.nextBytes(8).toString()

        fun start() {
            var request: String = ""

            Thread {
                val input = ObjectInputStream(clientSocket.getInputStream())
                println("reading stream")
                while (true) {
//                    synchronized(request) {
//                        request = input.readObject() as String
//                    }
                    request = input.readObject() as String
                    println("Received from client: $request")
                    println(clients)
                    clients.forEach {
                        println(it)
                        println(it.output)
                        it.output.writeObject("${clients.find { it.clientSocket == clientSocket }?.name}: $request")
                    }
                }
            }.start()

            while (true) {

            }
        }
    }

    private fun handleClient(clientSocket: Socket) {
        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val output = PrintWriter(clientSocket.getOutputStream(), true)

        while (true) {
            val request = input.readLine() ?: break
            println("Received from client: $request")
            clients.forEach {  }
//            output.println("${clients.find { it == clientSocket }?.inetAddress}: $request")
        }
    }
}
