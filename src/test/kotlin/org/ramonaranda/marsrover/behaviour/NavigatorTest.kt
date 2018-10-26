package org.ramonaranda.marsrover.behaviour

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import org.ramonaranda.marsrover.domain.Command
import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.navigation.FailureReason
import org.ramonaranda.marsrover.domain.navigation.Localizator
import org.ramonaranda.marsrover.domain.navigation.Navigator
import org.ramonaranda.marsrover.domain.navigation.Result
import org.ramonaranda.marsrover.domain.navigation.Rotator
import org.ramonaranda.marsrover.domain.navigation.Translator
import org.ramonaranda.marsrover.stub.CoordinateStub
import org.ramonaranda.marsrover.stub.DirectionStub
import org.ramonaranda.marsrover.stub.PositionStub

class NavigatorTest : ShouldSpec(
        {
            val localizator: Localizator = mockk()
            val rotator: Rotator = mockk()
            val translator: Translator = mockk()
            val navigator = Navigator(rotator, translator, localizator)

            should("it should rotate right") {
                val command = Command.Rotation.Right
                val actual = DirectionStub.random()
                val mockResponse = DirectionStub.random()
                val expected = Result.Success.Rotation(mockResponse)
                rotator.shouldRotateRight(actual, mockResponse)

                navigator.handleRotation(command, actual) shouldBe expected
            }

            should("it should rotate left") {
                val command = Command.Rotation.Left
                val actual = DirectionStub.random()
                val mockResponse = DirectionStub.random()
                val expected = Result.Success.Rotation(mockResponse)
                rotator.shouldRotateLeft(actual, mockResponse)

                navigator.handleRotation(command, actual) shouldBe expected
            }

            should("it should move forward and do not wrap ") {
                val command = Command.Translation.Forward
                val actualPosition = PositionStub.random()
                val expectedCoordinate = CoordinateStub.random()
                val expected = Result.Success.Translation(expectedCoordinate)
                translator.shouldMoveForward(actualPosition.coordinate, actualPosition.direction, expectedCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(expectedCoordinate, false)
                localizator.shouldCheckIfCoordinateIsOccupied(expectedCoordinate, false)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move forward and wrap") {
                val command = Command.Translation.Forward
                val actualPosition = PositionStub.random()
                val newCoordinate = CoordinateStub.random()
                val wrappedCoordinate = CoordinateStub.random()
                val expected = Result.Success.Translation(wrappedCoordinate)
                translator.shouldMoveForward(actualPosition.coordinate, actualPosition.direction, newCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(newCoordinate, true)
                localizator.shouldWrap(newCoordinate, wrappedCoordinate)
                localizator.shouldCheckIfCoordinateIsOccupied(wrappedCoordinate, false)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move forward and find an obstacle") {
                val command = Command.Translation.Forward
                val actualPosition = PositionStub.random()
                val expectedCoordinate = CoordinateStub.random()
                val expected = Result.Failure(FailureReason.ObstacleFound(expectedCoordinate, actualPosition))
                translator.shouldMoveForward(actualPosition.coordinate, actualPosition.direction, expectedCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(expectedCoordinate, false)
                localizator.shouldCheckIfCoordinateIsOccupied(expectedCoordinate, true)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move forward, wrap and find an obstacle") {
                val command = Command.Translation.Forward
                val actualPosition = PositionStub.random()
                val newCoordinate = CoordinateStub.random()
                val wrappedCoordinate = CoordinateStub.random()
                val expected = Result.Failure(FailureReason.ObstacleFound(wrappedCoordinate, actualPosition))
                translator.shouldMoveForward(actualPosition.coordinate, actualPosition.direction, newCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(newCoordinate, true)
                localizator.shouldWrap(newCoordinate, wrappedCoordinate)
                localizator.shouldCheckIfCoordinateIsOccupied(wrappedCoordinate, true)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move backward and do not wrap ") {
                val command = Command.Translation.Backward
                val actualPosition = PositionStub.random()
                val expectedCoordinate = CoordinateStub.random()
                val expected = Result.Success.Translation(expectedCoordinate)
                translator.shouldMoveBackward(actualPosition.coordinate, actualPosition.direction, expectedCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(expectedCoordinate, false)
                localizator.shouldCheckIfCoordinateIsOccupied(expectedCoordinate, false)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move backward and wrap") {
                val command = Command.Translation.Backward
                val actualPosition = PositionStub.random()
                val newCoordinate = CoordinateStub.random()
                val wrappedCoordinate = CoordinateStub.random()
                val expected = Result.Success.Translation(wrappedCoordinate)
                translator.shouldMoveBackward(actualPosition.coordinate, actualPosition.direction, newCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(newCoordinate, true)
                localizator.shouldWrap(newCoordinate, wrappedCoordinate)
                localizator.shouldCheckIfCoordinateIsOccupied(wrappedCoordinate, false)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move backward and find an obstacle") {
                val command = Command.Translation.Backward
                val actualPosition = PositionStub.random()
                val expectedCoordinate = CoordinateStub.random()
                val expected = Result.Failure(FailureReason.ObstacleFound(expectedCoordinate, actualPosition))
                translator.shouldMoveBackward(actualPosition.coordinate, actualPosition.direction, expectedCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(expectedCoordinate, false)
                localizator.shouldCheckIfCoordinateIsOccupied(expectedCoordinate, true)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }

            should("it should move backward, wrap and find an obstacle") {
                val command = Command.Translation.Backward
                val actualPosition = PositionStub.random()
                val newCoordinate = CoordinateStub.random()
                val wrappedCoordinate = CoordinateStub.random()
                val expected = Result.Failure(FailureReason.ObstacleFound(wrappedCoordinate, actualPosition))
                translator.shouldMoveBackward(actualPosition.coordinate, actualPosition.direction, newCoordinate)
                localizator.shouldCheckIfCoordinateIsOutOfBounds(newCoordinate, true)
                localizator.shouldWrap(newCoordinate, wrappedCoordinate)
                localizator.shouldCheckIfCoordinateIsOccupied(wrappedCoordinate, true)

                navigator.handleTranslation(command, actualPosition) shouldBe expected
            }
        })

private fun Rotator.shouldRotateRight(actual: Direction, result: Direction) {
    every { turnRight(actual) } returns result
}

private fun Rotator.shouldRotateLeft(actual: Direction, result: Direction) {
    every { turnLeft(actual) } returns result
}

private fun Translator.shouldMoveForward(coordinate: Coordinate, direction: Direction, result: Coordinate) {
    every { moveForward(coordinate, direction) } returns result
}

private fun Translator.shouldMoveBackward(coordinate: Coordinate, direction: Direction, result: Coordinate) {
    every { moveBackward(coordinate, direction) } returns result
}

private fun Localizator.shouldCheckIfCoordinateIsOutOfBounds(coordinate: Coordinate, result: Boolean) {
    every { isCoordinateOutOfBounds(coordinate) } returns result
}

private fun Localizator.shouldWrap(coordinate: Coordinate, result: Coordinate) {
    every { wrap(coordinate) } returns result
}

private fun Localizator.shouldCheckIfCoordinateIsOccupied(coordinate: Coordinate, result: Boolean) {
    every { isCoordinateOccupied(coordinate) } returns result
}