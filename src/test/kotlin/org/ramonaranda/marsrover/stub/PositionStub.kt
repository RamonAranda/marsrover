package org.ramonaranda.marsrover.stub

import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.Position

object PositionStub {
    fun random(direction: Direction = DirectionStub.random(),
               coordinate: Coordinate = CoordinateStub.random()) =
            Position(direction, coordinate)
}