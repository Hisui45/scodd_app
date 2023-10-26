package com.example.scodd.objects

interface ScoddWorkflow {
    val title : String
}

object Workflow1 : ScoddWorkflow {
    override val title = "Clean Room"
}

object Workflow2 : ScoddWorkflow {
    override val title = "Clean Bathroom"
}

object Workflow3 : ScoddWorkflow {
    override val title = "Clean Kitchen"
}

object Workflow4 : ScoddWorkflow {
    override val title = "Clean Basement"
}

val scoddFlows = listOf(Workflow1, Workflow2, Workflow3, Workflow4)