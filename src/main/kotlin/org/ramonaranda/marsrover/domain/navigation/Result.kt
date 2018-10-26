package org.ramonaranda.marsrover.domain.navigation

import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.Position

sealed class Result {
    sealed class Success : Result() {
        data class Rotation(val direction: Direction): Success()
        data class Translation(val coordinate: Coordinate): Success()
    }
    data class Failure(val reason: FailureReason): Result()
}

sealed class FailureReason {

    abstract fun errorMessage(): String

    data class ObstacleFound(private val coordinate: Coordinate, private val position: Position): FailureReason() {
        override fun errorMessage(): String {
            return "Obstacle found at ${coordinate.asString()} when traveling from ${position.coordinate.asString()} and ${position.direction.asString()}"
        }
    }

}

private fun Coordinate.asString() = "<X:$x; Y:$y>"
private fun Direction.asString() = "direction: ${this.name}"