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
    val players = mutableMapOf<Int, Player>()

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
                    println("client $playerId disconnected")
                    clients.remove(this)
                    players.remove(playerId)
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
                        players[id] = player
                    }
                    println(players)
                    resetClientsOutput()
                    // send to client his ID
                    sendToClient(this, "connection", id)
                    // send players to every client
                    synchronized(players) {
                        sendToClients("players", players.values.toList())
                    }
                }
                "getPositions" -> {

                }
                "message" -> {
                    sendMessage(request.data as String)
                }
                "newPlayerPosition" -> {
                    val newPlayer = request.data as Player
                    //println("new player pos ${newPlayer.x} ${newPlayer.y}")
                    resetClientsOutput()
                    synchronized(players) {
                        players[newPlayer.id] = newPlayer
                        sendToClients("players", players.values.toList())
                    }
                }
            }
        }

        /**
         * Sends data to a client
         */
        fun sendToClient(client: Client, command: String, data: Any) {
            val req = Request(command, data)
            client.output.writeObject(req)

        }

        /**
         * Sends data to all clients
         */
        fun sendToClients(command: String, data: Any) {
            val req = Request(command, data)
            clients.forEach {
                it.output.writeObject(req)
            }
        }

        /**
         * Sends a text message to all clients
         */
        fun sendMessage(message: String) {
            val req = Request("message", "$this : $message")
            clients.filter { it != this }.forEach {
                it.output.writeObject(req)
            }
        }

        /**
         * resets clients output ObjectOutputStream to reset data in stream
         */
        fun resetClientsOutput() {
            clients.forEach { it.output.reset() }
        }
    }

    companion object {
        var id = AtomicInteger(1)
    }
}
