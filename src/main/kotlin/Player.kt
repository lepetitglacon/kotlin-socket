import java.awt.Color
import java.io.Serializable
import kotlin.random.Random

class Player : Serializable {
    var id: Int = 0
    val name: String = java.util.UUID.randomUUID().toString()
    var color = Color(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
    var x = Random.nextInt(0, 300)
    var y = Random.nextInt(0, 300)
}