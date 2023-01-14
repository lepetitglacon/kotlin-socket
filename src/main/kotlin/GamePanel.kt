import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.Timer

class GamePanel : JPanel() {
    val player: Player = Player()
    val players: MutableSet<Player> = mutableSetOf()
    val messages = mutableListOf<String>()

    val timer = Timer(1) { run() }.start()

    fun run() {
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        val startY = 100
        messages.forEachIndexed { i, it ->
            g.drawString(it, 100, startY + 25 * i)
        }

        players.filter { player != it }.forEach {
            g.color = it.color
            g.fillOval(it.x, it.y, 16, 16)
        }
    }

    fun addMessage(message: String) {
        messages.add(message)
    }
}