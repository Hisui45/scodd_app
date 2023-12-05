package com.example.scodd.ui.mode

import com.example.scodd.model.ScoddTime

class TimeUtils {

    companion object {
        fun getTimeListInSeconds(timeValues: List<String>): Long {
            // Ensure that the list has at least three elements
            if (timeValues.size < 3) {
                throw IllegalArgumentException("List must have at least three elements: hours, minutes, and seconds")
            }

            // Parse each element and convert to seconds
            val hoursInSeconds = if (timeValues[0].isNotEmpty()) timeValues[0].toLong() * 3600 else 0
            val minutesInSeconds = if (timeValues[1].isNotEmpty()) timeValues[1].toLong() * 60 else 0
            val seconds = if (timeValues[2].isNotEmpty()) timeValues[2].toLong() else 0


            // Sum up the values to get the total time in seconds
            return hoursInSeconds + minutesInSeconds + seconds
        }

        fun getChoreTimerDurationInSeconds(timeModeValue: Long, unit: ScoddTime): Long{

            return when(unit){
                ScoddTime.SECOND -> {
                    timeModeValue
                }

                ScoddTime.MINUTE -> {
                    timeModeValue * 60
                }

                ScoddTime.HOUR -> {
                    timeModeValue * 60 * 60
                }

                else ->{
                    -1
                }
            }
        }

        fun convertToMinutes(value: Int, unit: ScoddTime): Int{
            return when(unit){
                ScoddTime.SECOND->{
                    value / 60
                }

                ScoddTime.MINUTE->{
                    value
                }

                ScoddTime.HOUR->{
                    value * 60
                }

                else -> {
                    0
                }
            }
        }

        fun convertToString(totalTimeInMinutes: Int): String{
            if (totalTimeInMinutes < 0) {
                return "< 1 min"  // Handle negative input as needed
            }

            val hours = totalTimeInMinutes / 60
            val remainingMinutes = totalTimeInMinutes % 60

            val hourString = if (hours > 1) "hours" else "hour"
            val minuteString = if (remainingMinutes > 1) "minutes" else "minute"

            return when {
                hours > 0 && remainingMinutes > 0 -> "$hours $hourString and $remainingMinutes $minuteString"
                hours > 0 -> "$hours $hourString"
                remainingMinutes > 0 -> "$remainingMinutes $minuteString"
                else -> "< 1 min"
            }
        }
    }

}


