package com.example.scodd.model

import com.example.scodd.R

data class Mode(
    val id: String = "",
    val workflowChores: List<String> = emptyList(),
    val selectedWorkflows: List<String> = emptyList(),
    val chores: List<String> = emptyList(),
    val rooms: List<String> = emptyList()
)

object ScoddModes{
    val TIME_MODE = "time_mode"
    val SPIN_MODE = "spin_mode"
    val BANK_MODE = "bank_mode"
    val QUEST_MODE = "quest_mode"
    val SAND_MODE = "sand_mode"
}

sealed class ScoddMode(val modeId: String, val title: Int, val description: Int){
companion object {
    val allModes: List<ScoddMode> = listOf(BankMode, TimeMode, QuestMode, SandMode, SpinMode)
}
object BankMode : ScoddMode("bank_mode", R.string.bank_mode_title, R.string.bank_mode_desc)
object TimeMode : ScoddMode("time_mode", R.string.time_mode_title, R.string.time_mode_desc)
object QuestMode : ScoddMode("quest_mode", R.string.quest_mode_title, R.string.quest_mode_desc)
object SandMode : ScoddMode("sand_mode", R.string.sand_mode_title, R.string.sand_mode_desc)
object SpinMode : ScoddMode("spin_mode", R.string.spin_mode_title, R.string.spin_mode_desc)


}

