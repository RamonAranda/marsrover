package org.ramonaranda.marsrover.infrastructure

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row
import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Obstacle
import org.ramonaranda.marsrover.domain.Plateau
import org.ramonaranda.marsrover.infrastructure.navigation.InMemoryLocalizator

class InMemoryLocalizatorTest : ShouldSpec(
        {
            val bottomLeftCoordinate = Coordinate(0, 0)
            val topRightCoordinate = Coordinate(2, 2)
            val plateau = Plateau(bottomLeftCoordinate, topRightCoordinate)
            val obstacles = listOf(Obstacle(Coordinate(1, 1)))
            val localizator = InMemoryLocalizator(plateau, obstacles)

            should("it should check if coordinate is occupied") {
                val obstacleCoordinate = obstacles.first().coordinate
                forall(
                        row(obstacleCoordinate, true),
                        row(obstacleCoordinate.copy(x = obstacleCoordinate.x + 1), false),
                        row(obstacleCoordinate.copy(y = obstacleCoordinate.y + 1), false)
                      )
                { coordinate: Coordinate, expected: Boolean ->
                    localizator.isCoordinateOccupied(coordinate) shouldBe expected
                }
            }

            should("it should wrap coordinate if coordinate is out of bounds") {
                forall(
                        row(bottomLeftCoordinate.copy(x = bottomLeftCoordinate.x - 1),
                            bottomLeftCoordinate.copy(x = topRightCoordinate.x)),
                        row(bottomLeftCoordinate.copy(y = bottomLeftCoordinate.y - 1),
                            bottomLeftCoordinate.copy(y = topRightCoordinate.y)),
                        row(topRightCoordinate.copy(x = topRightCoordinate.x + 1),
                            topRightCoordinate.copy(x = bottomLeftCoordinate.x)),
                        row(topRightCoordinate.copy(y = topRightCoordinate.y + 1),
                            topRightCoordinate.copy(y = bottomLeftCoordinate.y)),
                        row(bottomLeftCoordinate, bottomLeftCoordinate),
                        row(topRightCoordinate, topRightCoordinate)
                      )
                { coordinate: Coordinate, expected: Coordinate ->
                    localizator.wrap(coordinate) shouldBe expected
                }
            }

            should("it should check if coordinate is out of bounds") {
                forall(
                        row(bottomLeftCoordinate.copy(x = bottomLeftCoordinate.x - 1), true),
                        row(bottomLeftCoordinate.copy(y = bottomLeftCoordinate.y - 1), true),
                        row(topRightCoordinate.copy(x = topRightCoordinate.x + 1), true),
                        row(topRightCoordinate.copy(y = topRightCoordinate.y + 1), true),
                        row(bottomLeftCoordinate, false),
                        row(topRightCoordinate, false)
                      )
                { coordinate: Coordinate, expected: Boolean ->
                    localizator.isCoordinateOutOfBounds(coordinate) shouldBe expected
                }
            }
        })