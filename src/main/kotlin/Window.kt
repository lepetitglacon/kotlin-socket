import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.lang.Exception
import javax.swing.JFrame

class Window : JFrame() {
    var up = false
    var left = false
    var down = false
    var right = false
    var keyboardMovementVector = Vec2()

    val sync = Object()

    fun init(gamePanel: GamePanel) {
        title = "Bac+4 survival game - Esteban GAGNEUR"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        isVisible = true
        preferredSize = Dimension(WIDTH, HEIGHT)
        isAlwaysOnTop = true
        add(gamePanel, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)

        // input event
        addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode)
                {
                    KeyEvent.VK_ENTER -> {}

                    KeyEvent.VK_Z, KeyEvent.VK_UP -> {
                        up = true
                    }
                    KeyEvent.VK_Q, KeyEvent.VK_LEFT -> {
                        left = true
                    }
                    KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                        down = true
                    }
                    KeyEvent.VK_D, KeyEvent.VK_RIGHT -> {
                        right = true
                    }
                }
            }

            override fun keyReleased(e: KeyEvent) {
                when (e.keyCode)
                {
                    KeyEvent.VK_Z, KeyEvent.VK_UP -> {
                        up = false
                    }
                    KeyEvent.VK_Q, KeyEvent.VK_LEFT -> {
                        left = false
                    }
                    KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                        down = false
                    }
                    KeyEvent.VK_D, KeyEvent.VK_RIGHT -> {
                        right = false
                    }
                }
            }
        })

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                println("stopping window")
                //connected = true

                try {
                    Client.socket.close()
                } catch (e: Exception) { e.printStackTrace() }

                dispose()
                Thread.currentThread().join(1)
            }
        })
    }

    fun getKeyboardMovementInput()
    {
        if (up && left) {

            keyboardMovementVector.x = -8.0
            keyboardMovementVector.y = -8.0
        } else if (up && right) {

            keyboardMovementVector.x = 8.0
            keyboardMovementVector.y = -8.0
        } else if (down && left) {

            keyboardMovementVector.x = -8.0
            keyboardMovementVector.y = 8.0
        } else if (down && right) {

            keyboardMovementVector.x = 8.0
            keyboardMovementVector.y = 8.0
        } else if (up) {

            keyboardMovementVector.x = 0.0
            keyboardMovementVector.y = -8.0
        } else if (down) {

            keyboardMovementVector.x = 0.0
            keyboardMovementVector.y = 8.0
        } else if (left) {

            keyboardMovementVector.x = -8.0
            keyboardMovementVector.y = 0.0
        } else if (right) {

            keyboardMovementVector.x = 8.0
            keyboardMovementVector.y = 0.0
        } else {

            keyboardMovementVector.x = 0.0
            keyboardMovementVector.y = 0.0
        }
        keyboardMovementVector.normalize()

    }

    companion object {
        var WIDTH: Int = 480
        var HEIGHT: Int = 720
    }
}