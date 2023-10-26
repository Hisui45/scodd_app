package com.example.scodd.objects

interface ScoddTime {
    val title : String
    var selected : Boolean
}

object Daily : ScoddTime{
    override val title = "Daily"
    override var selected = true
}

object Weekly : ScoddTime{
    override val title = "Weekly"
    override var selected = false
}

object Monthly : ScoddTime{
    override val title = "Monthly"
    override var selected = false
}

object Yearly : ScoddTime{
    override val title = "Yearly"
    override var selected = false
}

object Custom : ScoddTime{
    override val title = "Custom"
    override var selected = false
}

val scoddTimings = listOf(Daily, Weekly, Monthly, Yearly, Custom)

object Sunday : ScoddTime{
    override val title = "Sun"
    override var selected = false
}

object Monday : ScoddTime{
    override val title = "Mon"
    override var selected = false
}

object Tuesday : ScoddTime{
    override val title = "Tue"
    override var selected = false
}

object Wednesday : ScoddTime{
    override val title = "Wed"
    override var selected = false
}

object Thursday : ScoddTime{
    override val title = "Thu"
    override var selected = false
}

object Friday : ScoddTime{
    override val title = "Fri"
    override var selected = false
}

object Saturday : ScoddTime{
    override val title = "Sat"
    override var selected = false
}

val scoddDaysOfWeek = listOf(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)

object Day : ScoddTime{
    override val title = "day"
    override var selected = false
}

object Week : ScoddTime{
    override val title = "week"
    override var selected = false
}

object Month : ScoddTime{
    override val title = "month"
    override var selected = false
}

object Year : ScoddTime{
    override val title = "year"
    override var selected = false
}

val scoddFrequency = listOf(Day, Week, Month, Year)

