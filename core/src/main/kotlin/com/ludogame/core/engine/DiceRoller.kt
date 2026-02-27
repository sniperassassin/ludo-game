package com.ludogame.core.engine

import kotlin.random.Random

class DiceRoller(private val random: Random = Random) {
    fun roll(): Int = random.nextInt(1, 7)
}
