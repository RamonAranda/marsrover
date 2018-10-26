package org.ramonaranda.marsrover.infrastructure.navigation

import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.navigation.Rotator

class CompassRoseRotator : Rotator {

    override fun turnRight(actual: Direction) =
            when (actual) {
                Direction.NORTH -> Direction.EAST
                Direction.EAST  -> Direction.SOUTH
                Direction.SOUTH -> Direction.WEST
                Direction.WEST  -> Direction.NORTH
            }

    override fun turnLeft(actual: Direction) =
            when (actual) {
                Direction.NORTH -> Direction.WEST
                Direction.WEST  -> Direction.SOUTH
                Direction.SOUTH -> Direction.EAST
                Direction.EAST  -> Direction.NORTH
            }

}