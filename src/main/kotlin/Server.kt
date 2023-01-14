import java.io.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

fun main(args: Array<String>) {
    Server(8080).start()
}

class Server {
    private val serverSocket: ServerSocket
    val clients = mutableListOf<Client>()
    val players = mutableListOf<Player>()

    constructor(port: Int) {
        serverSocket = ServerSocket(port)
        println("Server started on port $port")
    }

    fun start() {
        while (true) {
            val clientSocket = serverSocket.accept()
            println("New client connected: ${clientSocket.inetAddress.hostAddress}")
            val hc = Client(clientSocket)
            clients.add(hc)

            Thread {
                hc.start()
            }.start()
        }
    }

    inner class Client(val clientSocket: Socket) {
        val output = ObjectOutputStream(clientSocket.getOutputStream())
        val name = Random.nextBytes(8).toString()
        var playerId = 0

        fun start() {

            // Socket input thread
            Thread {
                try {
                    val input = ObjectInputStream(clientSocket.getInputStream())
                    while (true) {
                        handleRequest(input.readObject() as Request)
                    }
                } catch (e: Exception) {
                    clients.remove(this)
                    players.removeIf { it.id == playerId }
                }

            }.start()

            // Server thread
            while (true) {
                Thread.sleep(100)
            }
        }

        fun handleRequest(request: Request) {
            when (request.command) {
                "connection" -> {
                    val player = request.data as Player
                    val id = id.getAndIncrement()
                    playerId = id
                    player.id = id
                    synchronized(players) {
                        players.add(player)
                    }
                    sendToClient(this, "connection", id)
                }
                "players" -> {
                    println(players)
                    synchronized(players) {
                        sendToClient(this, "players", players)
                    }
                }
                "message" -> {
                    sendMessage(request.data as String)
                }
            }
        }

        fun sendToClient(client: Client, command: String, data: Any) {
            val req = Request(command, data)
            client.output.writeUnshared(req)
            client.output.reset()
        }

        fun sendToClients(command: String, data: Any) {
            val req = Request(command, data)
            clients.forEach {
                it.output.writeObject(req)
            }
        }

        fun sendMessage(message: String) {
            val req = Request("message", "$this : $message")
            clients.filter { it != this }.forEach {
                it.output.writeObject(req)
            }
        }
    }

    companion object {
        var id = AtomicInteger(1)
    }
}
