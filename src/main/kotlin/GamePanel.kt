import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.Timer

class GamePanel : JPanel() {
    val player: Player = Player()
    val players: MutableSet<Player> = mutableSetOf()
    val messages = mutableListOf<String>()

    val timer = Timer(1) { run() }.start()

    fun run() {
        Client.frame.getKeyboardMovementInput()

        // send movement to server
        if (Client.frame.keyboardMovementVector != Vec2()) {
            Client.panel.player.x += (5 * Client.frame.keyboardMovementVector.x).toInt()
            Client.panel.player.y += (5 * Client.frame.keyboardMovementVector.y).toInt()
            Client.sendToServer("newPlayerPosition", Client.panel.player)
        }

        repaint()
    }

    override fun paintComponent(g: Graphics) {

        val startY = 100
        messages.forEachIndexed { i, it ->
            g.drawString(it, 100, startY + 25 * i)
        }

        synchronized(players) {
            players.filter { player.id != it.id }.forEach {
                g.clearRect(0, 0, Window.WIDTH, Window.HEIGHT)
                g.color = it.color
                g.fillOval(it.xFromPlayer(player), it.yFromPlayer(player), 16, 16)
                g.drawString("${it.x} ${it.y}", it.xFromPlayer(player), it.yFromPlayer(player))
            }
        }

        g.color = player.color
        g.fillOval(Window.WIDTH/2 - 16/2, Window.HEIGHT/2 - 16/2, 16, 16)
        g.drawString("${player.x} ${player.y}", Window.WIDTH/2, Window.HEIGHT/2)
    }

    fun addMessage(message: String) {
        messages.add(message)
    }
}