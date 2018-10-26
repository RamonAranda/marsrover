package org.ramonaranda.marsrover.infrastructure

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.infrastructure.navigation.CompassRoseRotator

class CompassRoseRotatorTest : ShouldSpec(
        {
            val rotator = CompassRoseRotator()
            should("it should rotate right") {
                forall(
                        row(Direction.NORTH, Direction.EAST),
                        row(Direction.EAST, Direction.SOUTH),
                        row(Direction.SOUTH, Direction.WEST),
                        row(Direction.WEST, Direction.NORTH))
                { actual, expected ->
                    rotator.turnRight(actual) shouldBe expected
                }
            }
            should("it should rotate left") {
                forall(
                        row(Direction.NORTH, Direction.WEST),
                        row(Direction.WEST, Direction.SOUTH),
                        row(Direction.SOUTH, Direction.EAST),
                        row(Direction.EAST, Direction.NORTH))
                { actual, expected ->
                    rotator.turnLeft(actual) shouldBe expected
                }
            }
        })