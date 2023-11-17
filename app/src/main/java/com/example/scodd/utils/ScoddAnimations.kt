package com.example.scodd.utils

import androidx.compose.animation.core.*
import androidx.compose.ui.unit.IntOffset

    enum class LazyAnimations(
        var animation: FiniteAnimationSpec<IntOffset>){
        ROOM(tween(durationMillis = 500, easing = EaseInOutBack)),
        WORKFLOW(tween(500, 0, EaseInBack)),
        CHORE(tween(500, 0, EaseOutSine)),
        CREATE_WORKFLOW(tween(500, 0, LinearEasing))
    }