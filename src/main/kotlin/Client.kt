import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.util.*
import javax.swing.JFrame
import javax.swing.SwingUtilities


fun main() {
    Client.start()
}

object Client {
    val socket: Socket = Socket("localhost", 8080)
    private val output: ObjectOutputStream = ObjectOutputStream(socket.getOutputStream())
    private var connected = false

    private val scanner = Scanner(System.`in`)

    val frame: Window = Window()
    val panel: GamePanel = GamePanel()

    init {
        println("Connected to server: ${socket.inetAddress.hostAddress}")
    }

    fun start() {

        SwingUtilities.invokeLater {
            frame.init(panel)
        }

        // Socket input thread
        Thread {
            val input = ObjectInputStream(socket.getInputStream())
            while (true) {
                handleRequest(input.readObject() as Request)
            }
        }.start()

        // console reader thread
        Thread {
            while (true) {
                val message = scanner.nextLine() ?: break
                sendMessage(message)
            }
        }.start()

        sendToServer("connection", panel.player)

        // Client thread
        while (true) {
            if (connected)
                //sendToServer("getPositions", "")
                Thread.sleep(16)
        }
    }

    fun handleRequest(request: Request) {
        when (request.command) {
            "connection" -> {
                println("Player id is ${request.data}")
                panel.player.id = request.data as Int
                frame.title = "client ${panel.player.id}"
                connected = true
            }
            "players" -> {
                synchronized(panel.players) {
                    panel.players.addAll(request.data as List<Player>)
                }
            }
            "message" -> {
                println("${request.data}")
                panel.addMessage(request.data as String)
            }
        }
    }

    fun sendToServer(command: String, data: Any) {
        val req = Request(command, data)
        output.reset()
        output.writeObject(req)
    }

    fun sendMessage(message: String) {
        sendToServer("message", message)
    }

}