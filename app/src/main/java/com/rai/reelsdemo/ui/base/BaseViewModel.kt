package com.rai.reelsdemo.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rai.reelsdemo.ui.common.UiEffect
import com.rai.reelsdemo.ui.common.UiEvent
import com.rai.reelsdemo.ui.common.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event: UiEvent,State: UiState,Effect: UiEffect>: ViewModel() {

    //Create initial State of View
    private val initialState : State by lazy { createInitialState()}
    abstract fun createInitialState():State

    //Get current state
    val currentState:State
        get() = uiState.value
    private val _uiState:MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState=_uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> =MutableSharedFlow()
    val event=_event.asSharedFlow()

    private val _effect:Channel<Effect> = Channel()
    val effect=_effect.receiveAsFlow()


    /**
     * Set new Event
     */
    fun setEvent(event : Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }


    /**
     * Set new Ui State
     */
    protected fun setState(reduce: State.() -> State) {
        _uiState.update {
            currentState.reduce()
        }
    }


    /**
     * Set new Effect
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    init {
        subscribeEvents()
    }

    /**
     * Start listening to Event
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Handle each event
     */
    abstract fun handleEvent(event : Event)
}