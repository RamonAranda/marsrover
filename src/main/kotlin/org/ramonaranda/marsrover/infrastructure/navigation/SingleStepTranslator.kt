package org.ramonaranda.marsrover.infrastructure.navigation

import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.navigation.Translator

class SingleStepTranslator: Translator {

    private companion object {
        private const val SPEED = 1
    }

    override fun moveForward(actual: Coordinate, direction: Direction) =
            when (direction) {
                Direction.NORTH -> actual.copy(y = actual.y + SPEED)
                Direction.EAST  -> actual.copy(x = actual.x + SPEED)
                Direction.SOUTH -> actual.copy(y = actual.y - SPEED)
                Direction.WEST  -> actual.copy(x = actual.x - SPEED)
            }


    override fun moveBackward(actual: Coordinate, direction: Direction) =
            when (direction) {
                Direction.NORTH -> actual.copy(y = actual.y - SPEED)
                Direction.EAST  -> actual.copy(x = actual.x - SPEED)
                Direction.SOUTH -> actual.copy(y = actual.y + SPEED)
                Direction.WEST  -> actual.copy(x = actual.x + SPEED)
            }

}