package org.ramonaranda.marsrover.domain.navigation

import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Direction

interface Translator {
    fun moveForward(actual: Coordinate, direction: Direction): Coordinate
    fun moveBackward(actual: Coordinate, direction: Direction): Coordinate
}