package com.example.quizletappandroidv1.utils

import android.animation.Animator

class AnimationUtils {
    abstract class AnimationEndListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationCancel(animation: Animator) {

        }
    }
}