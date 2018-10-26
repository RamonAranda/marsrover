package org.ramonaranda.marsrover.stub

import org.ramonaranda.marsrover.domain.Direction
import java.util.Random

object DirectionStub {
    fun random() = Direction.values().let { it[Random().nextInt(it.size - 1)] }
}