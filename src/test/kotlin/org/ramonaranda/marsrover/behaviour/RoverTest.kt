package org.ramonaranda.marsrover.behaviour

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row
import io.mockk.every
import io.mockk.mockk
import org.ramonaranda.marsrover.domain.Command
import org.ramonaranda.marsrover.domain.CommandResult
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.Position
import org.ramonaranda.marsrover.domain.Rover
import org.ramonaranda.marsrover.domain.navigation.FailureReason
import org.ramonaranda.marsrover.domain.navigation.Navigator
import org.ramonaranda.marsrover.domain.navigation.Result
import org.ramonaranda.marsrover.stub.CommandStub
import org.ramonaranda.marsrover.stub.CoordinateStub
import org.ramonaranda.marsrover.stub.DirectionStub
import org.ramonaranda.marsrover.stub.PositionStub

class RoverTest : ShouldSpec(
        {
            val navigator: Navigator = mockk()

            should("it should handle no command") {
                val position = PositionStub.random()
                val rover = Rover(navigator, position)

                rover.handle(emptyList()) shouldBe CommandResult.Success(position)
            }

            should("it should handle rotation command") {
                val position = PositionStub.random()
                val expectedDirection = DirectionStub.random()
                forall(row(Command.Rotation.Left, Result.Success.Rotation(expectedDirection)),
                       row(Command.Rotation.Right, Result.Success.Rotation(expectedDirection)))
                { command: Command.Rotation, rotation: Result.Success.Rotation ->
                    val rover = Rover(navigator, position)
                    navigator.shouldHandleRotation(command, position.direction, rotation)

                    rover.handle(listOf(command)) shouldBe
                            CommandResult.Success(position.updateDirection(expectedDirection))
                }
            }

            should("it should handle multiple rotation commands") {
                val position = PositionStub.random()
                forall(row(CommandStub.RotationStub.random(), CommandStub.RotationStub.random()),
                       row(CommandStub.RotationStub.random(), CommandStub.RotationStub.random()))
                { firstCommand, secondCommand ->
                    val rover = Rover(navigator, position)
                    val commands = listOf(firstCommand, secondCommand)
                    val firstDirection = DirectionStub.random()
                    val secondDirection = DirectionStub.random()
                    val expected = position.updateDirection(secondDirection)

                    navigator.shouldHandleRotation(firstCommand,
                                                   position.direction,
                                                   Result.Success.Rotation(firstDirection))
                    navigator.shouldHandleRotation(secondCommand,
                                                   firstDirection,
                                                   Result.Success.Rotation(secondDirection))

                    rover.handle(commands) shouldBe CommandResult.Success(expected)
                }
            }


            should("it should handle translation command successfully") {
                val position = PositionStub.random()
                val rover = Rover(navigator, position)
                val command = CommandStub.TranslationStub.random()
                val newCoordinate = CoordinateStub.random()
                val expected = position.updateCoordinate(newCoordinate)

                navigator.shouldHandleTranslation(command, position, Result.Success.Translation(newCoordinate))

                rover.handle(listOf(command)) shouldBe CommandResult.Success(expected)
            }

            should("it should handle multiple translation command successfully") {
                val position = PositionStub.random()
                val rover = Rover(navigator, position)
                val firstCommand = CommandStub.TranslationStub.random()
                val secondCommand = CommandStub.TranslationStub.random()
                val commands = listOf(firstCommand, secondCommand)
                val secondCoordinate = CoordinateStub.random()
                val secondPosition = position.updateCoordinate(secondCoordinate)
                val thirdCoordinate = CoordinateStub.random()
                val expected = position.updateCoordinate(thirdCoordinate)

                navigator.shouldHandleTranslation(firstCommand, position, Result.Success.Translation(secondCoordinate))
                navigator.shouldHandleTranslation(secondCommand,
                                                  secondPosition,
                                                  Result.Success.Translation(thirdCoordinate))

                rover.handle(commands) shouldBe CommandResult.Success(expected)
            }

            should("it should handle translation command unsuccessfully") {
                val position = PositionStub.random()
                val rover = Rover(navigator, position)
                val command = CommandStub.TranslationStub.random()
                val coordinate = CoordinateStub.random()
                val expectedErrorCode = "Obstacle found at <X:${coordinate.x}; Y:${coordinate.y}> when traveling from <X:${position.coordinate.x}; Y:${position.coordinate.y}> and direction: ${position.direction.name}"

                navigator.shouldHandleTranslation(command,
                                                  position,
                                                  Result.Failure(FailureReason.ObstacleFound(coordinate, position)))

                rover.handle(listOf(command)) shouldBe CommandResult.Failure(expectedErrorCode)
            }

            should("it should handle translation first command successfully and second one unsuccessfully") {
                val position = PositionStub.random()
                val rover = Rover(navigator, position)
                val newCoordinate = CoordinateStub.random()
                val newPosition = position.updateCoordinate(newCoordinate)
                val firstCommand = CommandStub.TranslationStub.random()
                val secondCommand = CommandStub.TranslationStub.random()
                val nextCoordinate = CoordinateStub.random()
                val expectedErrorCode = "Obstacle found at <X:${nextCoordinate.x}; Y:${nextCoordinate.y}> when traveling from <X:${newPosition.coordinate.x}; Y:${newPosition.coordinate.y}> and direction: ${newPosition.direction.name}"

                navigator.shouldHandleTranslation(firstCommand, position, Result.Success.Translation(newCoordinate))
                navigator.shouldHandleTranslation(
                        secondCommand,
                        newPosition,
                        Result.Failure(FailureReason.ObstacleFound(nextCoordinate, newPosition)))

                rover.handle(listOf(firstCommand, secondCommand)) shouldBe CommandResult.Failure(expectedErrorCode)
            }

        })

private fun Navigator.shouldHandleRotation(command: Command.Rotation, direction: Direction, result: Result) {
    every { handleRotation(command, direction) } returns result
}

private fun Navigator.shouldHandleTranslation(command: Command.Translation, position: Position, result: Result) {
    every { handleTranslation(command, position) } returns result
}