package org.ramonaranda.marsrover.domain.navigation

import org.ramonaranda.marsrover.domain.Direction

interface Rotator {
    fun turnRight(actual: Direction): Direction
    fun turnLeft(actual: Direction): Direction
}