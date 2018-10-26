package org.ramonaranda.marsrover.infrastructure.navigation

import org.ramonaranda.marsrover.domain.Coordinate
import org.ramonaranda.marsrover.domain.Obstacle
import org.ramonaranda.marsrover.domain.Plateau
import org.ramonaranda.marsrover.domain.navigation.Localizator

class InMemoryLocalizator(private val plateau: Plateau, private val obstacles: List<Obstacle>) : Localizator {

    override fun isCoordinateOccupied(coordinate: Coordinate) =
            obstacles.asSequence().map { it.coordinate }.contains(coordinate)

    override fun isCoordinateOutOfBounds(coordinate: Coordinate) = !plateau.contains(coordinate)

    override fun wrap(coordinate: Coordinate) =
            when (true) {
                plateau.topRightBound.x < coordinate.x   -> coordinate.copy(x = plateau.bottomLeftBound.x)
                plateau.topRightBound.y < coordinate.y   -> coordinate.copy(y = plateau.bottomLeftBound.y)
                plateau.bottomLeftBound.x > coordinate.x -> coordinate.copy(x = plateau.topRightBound.x)
                plateau.bottomLeftBound.y > coordinate.y -> coordinate.copy(y = plateau.topRightBound.y)
                else                                     -> coordinate
            }


}