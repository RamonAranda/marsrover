package org.ramonaranda.marsrover.domain.navigation

import org.ramonaranda.marsrover.domain.Command
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.Position

class Navigator(private val rotator: Rotator,
                private val translator: Translator,
                private val localizator: Localizator) {

    fun handleRotation(command: Command.Rotation, direction: Direction): Result =
            when (command) {
                Command.Rotation.Right -> direction.rotateRight()
                Command.Rotation.Left  -> direction.rotateLeft()
            }.let(Result.Success::Rotation)

    fun handleTranslation(command: Command.Translation, position: Position): Result {
        val coordinate = when (command) {
            Command.Translation.Forward  -> translator.moveForward(position.coordinate, position.direction)
            Command.Translation.Backward -> translator.moveBackward(position.coordinate, position.direction)
        }.let {
            if (localizator.isCoordinateOutOfBounds(it)) localizator.wrap(it) else it
        }
        return when (localizator.isCoordinateOccupied(coordinate)) {
            true  -> Result.Failure(FailureReason.ObstacleFound(coordinate, position))
            false -> Result.Success.Translation(coordinate)
        }
    }

    private fun Direction.rotateRight() = rotator.turnRight(this)
    private fun Direction.rotateLeft() = rotator.turnLeft(this)

}