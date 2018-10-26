package org.ramonaranda.marsrover.domain.navigation

import org.ramonaranda.marsrover.domain.Coordinate

interface Localizator {
    fun isCoordinateOccupied(coordinate: Coordinate): Boolean
    fun isCoordinateOutOfBounds(coordinate: Coordinate): Boolean
    fun wrap(coordinate: Coordinate): Coordinate
}