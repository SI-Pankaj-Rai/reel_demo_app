package com.rai.reelsdemo.ui.screens

import android.util.Log
import com.rai.reelsdemo.ui.base.BaseViewModel
import com.rai.reelsdemo.ui.common.UiEffect
import com.rai.reelsdemo.ui.common.UiEvent
import com.rai.reelsdemo.ui.common.UiState
import com.rai.reelsdemo.ui.data.Reel
import com.rai.reelsdemo.ui.data.reelsList

class ReelViewModel :
    BaseViewModel<ReelScreenContract.Event, ReelScreenContract.State, ReelScreenContract.Effect>() {
    override fun createInitialState(): ReelScreenContract.State =ReelScreenContract.State()

    override fun handleEvent(event: ReelScreenContract.Event) {

    }
    init {
        setState {
            copy(
                reels = reelsList
            )
        }
    }
    fun onShareReel(reel: Reel) {
        Log.d(TAG, "onShareReel: {$reel} ")
        setEffect {
            ReelScreenContract.Effect.ShareReelEffect(reel = reel)
        }

    }
    fun onBackPressed(){
        Log.d(TAG, "onBackPressed: Pressed")
    }

    companion object {
        const val TAG="ReelViewModel"
    }
}
class ReelScreenContract{
    data class State(
        val isLoading:Boolean =false,
        val reels: List<Reel> = listOf()
    ):UiState
    sealed class Event:UiEvent
    sealed class Effect : UiEffect{
        data class ShareReelEffect(val reel: Reel): Effect()
    }
}
