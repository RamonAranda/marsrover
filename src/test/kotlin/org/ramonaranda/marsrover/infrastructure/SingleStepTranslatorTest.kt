package org.ramonaranda.marsrover.infrastructure

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.tables.row
import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction
import org.ramonaranda.marsrover.infrastructure.navigation.SingleStepTranslator
import org.ramonaranda.marsrover.stub.CoordinateStub

class SingleStepTranslatorTest : ShouldSpec(
        {
            val translator = SingleStepTranslator()
            val coordinate = CoordinateStub.random()
            should("it should move forward") {
                forall(
                        row(Direction.NORTH, coordinate.copy(y = coordinate.y + 1)),
                        row(Direction.WEST, coordinate.copy(x = coordinate.x - 1)),
                        row(Direction.SOUTH, coordinate.copy(y = coordinate.y - 1)),
                        row(Direction.EAST, coordinate.copy(x = coordinate.x + 1))
                      )
                { direction: Direction, expected: Coordinate ->
                    translator.moveForward(coordinate, direction) shouldBe expected
                }
            }

            should("it should move backwards") {
                forall(
                        row(Direction.NORTH, coordinate.copy(y = coordinate.y - 1)),
                        row(Direction.WEST, coordinate.copy(x = coordinate.x + 1)),
                        row(Direction.SOUTH, coordinate.copy(y = coordinate.y + 1)),
                        row(Direction.EAST, coordinate.copy(x = coordinate.x - 1))
                      )
                { direction: Direction, expected: Coordinate ->
                    translator.moveBackward(coordinate, direction) shouldBe expected
                }
            }
        })