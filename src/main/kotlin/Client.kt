import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.util.*
import javax.swing.JFrame


fun main() {
    Client.start()
}

object Client {
    private val socket: Socket = Socket("localhost", 8080)
    private val output: ObjectOutputStream = ObjectOutputStream(socket.getOutputStream())
    private var connected = false

    private val scanner = Scanner(System.`in`)

    private val frame: JFrame = JFrame()
    private val panel: GamePanel = GamePanel()

    init {
        println("Connected to server: ${socket.inetAddress.hostAddress}")
    }

    fun start() {
        frame.setSize(400, 400)
        frame.add(panel)
        frame.isVisible = true
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                println("stopping window")
                //connected = true

                try {
                    socket.close()
                } catch (e: Exception) { e.printStackTrace() }

                frame.dispose()
                Thread.currentThread().join(1)
            }
        })

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
            sendToServer("players", "")
            Thread.sleep(100)
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
                println("received players :\n${request.data}")
                panel.players.addAll(request.data as List<Player>)
            }
            "message" -> {
                println("${request.data}")
                panel.addMessage(request.data as String)
            }
        }
    }

    fun sendToServer(command: String, data: Any) {
        val req = Request(command, data)
        output.writeObject(req)
    }

    fun sendMessage(message: String) {
        sendToServer("message", message)
    }

}