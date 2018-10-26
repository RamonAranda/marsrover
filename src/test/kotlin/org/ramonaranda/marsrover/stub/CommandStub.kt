package org.ramonaranda.marsrover.stub

import org.ramonaranda.marsrover.domain.Command
import java.util.Random

object CommandStub{

    object RotationStub {
        fun random() = VALUES.let { it[Random().nextInt(it.size)] }
        private val VALUES = listOf(Command.Rotation.Right, Command.Rotation.Left)
    }

    object TranslationStub {
        fun random() = VALUES.let { it[Random().nextInt(it.size)] }
        private val VALUES = listOf(Command.Translation.Forward, Command.Translation.Backward)
    }

}