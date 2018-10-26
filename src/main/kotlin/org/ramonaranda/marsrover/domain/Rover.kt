package org.ramonaranda.marsrover.domain

import org.ramonaranda.marsrover.domain.navigation.Navigator
import org.ramonaranda.marsrover.domain.navigation.Result

// IMHO Rover has to know where it is and how to handle commands as they are constraints of the kata.
// One possible way to fix this is to move actual position logic from rover to localizator service in order to avoid breaking SRP.
// Another possible way is to be agnostic of current position using recursion and pass actual state into handle function.
class Rover(private val navigator: Navigator, private var position: Position) {

    tailrec fun handle(commands: List<Command>): CommandResult {
        if (commands.isEmpty()) return CommandResult.Success(position)
        val command = commands.first()
        val result = command.execute().also { it.updatePosition() }
        return when (result) {
            is Result.Success.Rotation, is Result.Success.Translation -> handle(commands.drop(1))
            is Result.Failure                                         -> CommandResult.Failure(result.reason.errorMessage())
        }
    }

    private fun Command.execute() =
            when (this) {
                Command.Rotation.Right, Command.Rotation.Left             ->
                    navigator.handleRotation(this as Command.Rotation, position.direction)
                Command.Translation.Forward, Command.Translation.Backward ->
                    navigator.handleTranslation(this as Command.Translation, position)
            }

    private fun Result.updatePosition() {
        when (this) {
            is Result.Success.Rotation    -> position = position.updateDirection(direction)
            is Result.Success.Translation -> position = position.updateCoordinate(coordinate)
        }
    }

}