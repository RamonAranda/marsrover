package org.ramonaranda.marsrover.domain

sealed class Command {
    sealed class Rotation : Command() {
        object Right : Rotation()
        object Left : Rotation()
    }

    sealed class Translation : Command() {
        object Forward : Translation()
        object Backward : Translation()
    }
}

sealed class CommandResult {
    data class Success(val position: Position): CommandResult()
    data class Failure(val failureReason: String): CommandResult()
}