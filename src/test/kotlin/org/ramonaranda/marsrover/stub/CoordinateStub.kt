package org.ramonaranda.marsrover.stub

import org.ramonaranda.marsrover.domain.Coordinate
import java.util.Random


object CoordinateStub {
    fun random(x: Int = Random().nextInt(), y: Int = Random().nextInt()) = Coordinate(x, y)
}