package org.ramonaranda.marsrover.domain

data class Position(val direction: Direction, val coordinate: Coordinate) {

    fun updateDirection(direction: Direction) = copy(direction = direction)
    fun updateCoordinate(coordinate: Coordinate) = copy(coordinate = coordinate)

}