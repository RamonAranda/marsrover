package org.ramonaranda.marsrover.acceptance

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row
import org.ramonaranda.marsrover.domain.Command
import org.ramonaranda.marsrover.domain.CommandResult
import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.domain.Obstacle
import org.ramonaranda.marsrover.domain.Plateau
import org.ramonaranda.marsrover.domain.Position
import org.ramonaranda.marsrover.domain.Rover
import org.ramonaranda.marsrover.domain.navigation.Navigator
import org.ramonaranda.marsrover.infrastructure.navigation.CompassRoseRotator
import org.ramonaranda.marsrover.infrastructure.navigation.InMemoryLocalizator
import org.ramonaranda.marsrover.infrastructure.navigation.SingleStepTranslator

class RoverNavigationTest : ShouldSpec(
        {
            val bottomLeftCoordinates = Coordinate(0, 0)
            val topRightCoordinates = Coordinate(5, 5)
            val plateau = Plateau(bottomLeftCoordinates, topRightCoordinates)
            val obstacles = listOf(
                    Obstacle(Coordinate(0, 0)),
                    Obstacle(Coordinate(5, 5)))
            val localizator = InMemoryLocalizator(plateau, obstacles)
            val rotator = CompassRoseRotator()
            val translator = SingleStepTranslator()

            should("it should move forward") {
                val actualPosition = Position(Direction.NORTH, Coordinate(0, 2))
                val expectedCoordinate = Coordinate(0, 3)
                val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                rover.handle(listOf(Command.Translation.Forward)) shouldBe
                        CommandResult.Success(actualPosition.updateCoordinate(expectedCoordinate))
            }

            should("it should move forward and wrap") {
                val actualPosition = Position(Direction.NORTH, Coordinate(2, 5))
                val expectedCoordinate = Coordinate(2, 0)
                val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                rover.handle(listOf(Command.Translation.Forward)) shouldBe
                        CommandResult.Success(actualPosition.updateCoordinate(expectedCoordinate))
            }

            should("it should move forward, wrap and find an obstacle") {
                val obstacleCoordinate = obstacles.first().coordinate
                val actualPosition = Position(Direction.NORTH, Coordinate(0, 5))
                val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                val errorString = "Obstacle found at <X:${obstacleCoordinate.x}; Y:${obstacleCoordinate.y}> when traveling from <X:${actualPosition.coordinate.x}; Y:${actualPosition.coordinate.y}> and direction: ${actualPosition.direction.name}"
                rover.handle(listOf(Command.Translation.Forward)) shouldBe CommandResult.Failure(errorString)
            }

            should("it should move backward") {
                val actualPosition = Position(Direction.NORTH, Coordinate(0, 2))
                val expectedCoordinate = Coordinate(0, 1)
                val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                rover.handle(listOf(Command.Translation.Backward)) shouldBe
                        CommandResult.Success(actualPosition.updateCoordinate(expectedCoordinate))
            }

            should("it should move backward and wrap") {
                val actualPosition = Position(Direction.NORTH, Coordinate(2, 0))
                val expectedCoordinate = Coordinate(2, 5)
                val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                rover.handle(listOf(Command.Translation.Backward)) shouldBe
                        CommandResult.Success(actualPosition.updateCoordinate(expectedCoordinate))
            }

            should("it should move backward, wrap and find an obstacle") {
                val obstacleCoordinate = obstacles.first().coordinate
                val actualPosition = Position(Direction.NORTH, Coordinate(0, 1))
                val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                val errorString = "Obstacle found at <X:${obstacleCoordinate.x}; Y:${obstacleCoordinate.y}> when traveling from <X:${actualPosition.coordinate.x}; Y:${actualPosition.coordinate.y}> and direction: ${actualPosition.direction.name}"
                rover.handle(listOf(Command.Translation.Backward)) shouldBe CommandResult.Failure(errorString)
            }

            should("it should do a 360 degree rotation") {
                forall(
                        row(Command.Rotation.Right, Direction.NORTH),
                        row(Command.Rotation.Right, Direction.EAST),
                        row(Command.Rotation.Right, Direction.WEST),
                        row(Command.Rotation.Right, Direction.SOUTH),
                        row(Command.Rotation.Left, Direction.NORTH),
                        row(Command.Rotation.Left, Direction.EAST),
                        row(Command.Rotation.Left, Direction.WEST),
                        row(Command.Rotation.Left, Direction.SOUTH)
                      ) { command: Command, expected: Direction ->
                    val actualPosition = Position(expected, Coordinate(0, 1))
                    val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                    val commands = listOf(command, command, command, command)
                    rover.handle(commands) shouldBe CommandResult.Success(actualPosition)
                }
            }

            should("it should rotate and move until find and obstacle") {
                forall(
                        row(Command.Translation.Backward,
                            Command.Rotation.Left,
                            Coordinate(0, 5),
                            Coordinate(0, 1),
                            Direction.NORTH,
                            Direction.EAST),
                        row(Command.Translation.Backward,
                            Command.Rotation.Right,
                            Coordinate(0, 5),
                            Coordinate(0, 1),
                            Direction.NORTH,
                            Direction.WEST),
                        row(Command.Translation.Forward,
                            Command.Rotation.Left,
                            Coordinate(0, 1),
                            Coordinate(0, 5),
                            Direction.NORTH,
                            Direction.EAST),
                        row(Command.Translation.Forward,
                            Command.Rotation.Right,
                            Coordinate(0, 1),
                            Coordinate(0, 5),
                            Direction.NORTH,
                            Direction.WEST),
                        row(Command.Translation.Backward,
                            Command.Rotation.Left,
                            Coordinate(1, 0),
                            Coordinate(5, 0),
                            Direction.WEST,
                            Direction.NORTH),
                        row(Command.Translation.Backward,
                            Command.Rotation.Right,
                            Coordinate(5, 0),
                            Coordinate(1, 0),
                            Direction.EAST,
                            Direction.NORTH),
                        row(Command.Translation.Forward,
                            Command.Rotation.Left,
                            Coordinate(5, 0),
                            Coordinate(1, 0),
                            Direction.WEST,
                            Direction.NORTH),
                        row(Command.Translation.Forward,
                            Command.Rotation.Right,
                            Coordinate(5, 0),
                            Coordinate(1, 0),
                            Direction.WEST,
                            Direction.SOUTH)
                      ) { translationCommand, rotationCommand, startingCoordinate, endingCoordinate, direction, startingDirection ->
                    val actualPosition = Position(startingDirection, startingCoordinate)
                    val obstacleCoordinate = obstacles.first().coordinate
                    val rover = Rover(Navigator(rotator, translator, localizator), actualPosition)
                    val commands = listOf(rotationCommand,
                                          translationCommand,
                                          translationCommand,
                                          translationCommand,
                                          translationCommand,
                                          translationCommand)
                    val errorString = "Obstacle found at <X:${obstacleCoordinate.x}; Y:${obstacleCoordinate.y}> when traveling from <X:${endingCoordinate.x}; Y:${endingCoordinate.y}> and direction: $direction"
                    rover.handle(commands) shouldBe CommandResult.Failure(errorString)
                }

            }

        })