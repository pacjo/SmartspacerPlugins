package utils

class Temperature {
    var temperature: Int? = null
    private var targetUnit: String = "K"

    // simply convert units
    constructor(temperature: Int, targetUnit: String) {
        this.temperature = kelvinToUnit(temperature, targetUnit)
        this.targetUnit = targetUnit
    }

    // convert units, then calculate difference
    constructor(temperature1: Int, temperature2: Int, targetUnit: String) {
        this.temperature = kelvinToUnit(temperature1, targetUnit) - kelvinToUnit(temperature2, targetUnit)
        this.targetUnit = targetUnit
    }

    override fun toString(): String {
        val separator = when (targetUnit) {
            "K" -> ' '
            "C" -> '°'
            "F" -> '°'

            else -> {
                throw IllegalArgumentException("Unknown unit: $targetUnit")
            }
        }

        return temperature.toString() + separator + targetUnit
    }

    private fun kelvinToUnit(temp: Int, targetUnit: String): Int {
        return when (targetUnit) {
            "K" -> temp
            "C" -> temp - 273
            "F" -> (((temp - 273) * 1.8) + 32).toInt()

            else -> {
                throw IllegalArgumentException("Unknown unit: $targetUnit")
            }
        }
    }
}