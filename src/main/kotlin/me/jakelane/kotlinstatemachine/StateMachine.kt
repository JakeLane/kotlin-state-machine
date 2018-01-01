interface State<in StateType> {
    fun onEnter(source: StateType?) {}
    fun onLeave(target: StateType?) {}
}

interface Transition {
    fun onBefore() {}
    fun onAfter() {}
}

class StateMachine<StateType : State<StateType>, in TransitionType : Transition>(
        stateClass: Class<StateType>,
        transitionClass: Class<TransitionType>,
        private val transitionFunction: Map<StateType, Map<TransitionType, StateType>>,
        private val initialState: StateType) {
    var state: StateType? = null
        private set

    init {
        if (stateClass.enumConstants == null) {
            throw RuntimeException("Class `${stateClass.name}` was not an enum")
        }
        if (transitionClass.enumConstants == null) {
            throw RuntimeException("Class `${transitionClass.name}` was not an enum")
        }
    }

    fun event(transition: TransitionType) {
        val source: StateType? = state
        val target: StateType? = transitionFunction[source]?.get(transition)

        // Before action on transition
        transition.onBefore()

        // Exit action on previous state
        source?.onLeave(target)

        // Update current state to target state
        state = target

        // Entry action on new state
        target?.onEnter(source)

        // After action on transition
        transition.onAfter()
    }

    fun start() {
        state = initialState

        // Entry action on initial state
        initialState.onEnter(null)
    }
}
