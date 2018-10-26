package org.ramonaranda.marsrover.domain

data class Plateau(val bottomLeftBound: Coordinate, val topRightBound: Coordinate) {

    fun contains(coordinate: Coordinate): Boolean =
            (bottomLeftBound.x..topRightBound.x).contains(coordinate.x) &&
            (bottomLeftBound.y..topRightBound.y).contains(coordinate.y)

}