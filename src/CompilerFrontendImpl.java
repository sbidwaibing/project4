import java.util.HashSet;
import java.util.EnumMap;

public class CompilerFrontendImpl extends CompilerFrontend {

    public CompilerFrontendImpl() {
        super();
    }

    public CompilerFrontendImpl(boolean enableDebug) {
        super(enableDebug);
    }

    @Override
    protected void init_lexer() {
        Lexer lexer = new LexerImpl();

        // Initialize all token automata
        lexer.add_automaton(TokenType.NUM, createNumberAutomaton());
        lexer.add_automaton(TokenType.PLUS, createSimpleAutomaton('+'));
        lexer.add_automaton(TokenType.MINUS, createSimpleAutomaton('-'));
        lexer.add_automaton(TokenType.TIMES, createSimpleAutomaton('*'));
        lexer.add_automaton(TokenType.DIV, createSimpleAutomaton('/'));
        lexer.add_automaton(TokenType.LPAREN, createSimpleAutomaton('('));
        lexer.add_automaton(TokenType.RPAREN, createSimpleAutomaton(')'));
        lexer.add_automaton(TokenType.WHITE_SPACE, createWhitespaceAutomaton());

        // Assign the configured lexer
        this.lex = lexer;
    }

    /**
     * Creates an automaton for recognizing numeric literals (e.g., integers and floats).
     */
    private Automaton createNumberAutomaton() {
        Automaton numberAutomaton = new AutomatonImpl();

        addStates(numberAutomaton, 0, true, false);
        addStates(numberAutomaton, 1, false, false);
        addStates(numberAutomaton, 2, false, false);
        addStates(numberAutomaton, 3, false, true);

        // Digit transitions
        addDigitTransitions(numberAutomaton, 0, 1);
        addDigitTransitions(numberAutomaton, 1, 1);

        // Decimal point transitions
        numberAutomaton.addTransition(0, '.', 2);
        numberAutomaton.addTransition(1, '.', 2);

        // Fractional part transitions
        addDigitTransitions(numberAutomaton, 2, 3);
        addDigitTransitions(numberAutomaton, 3, 3);

        return numberAutomaton;
    }

    /**
     * Creates a simple automaton for single-character tokens (e.g., '+', '-', '*', '/').
     */
    private Automaton createSimpleAutomaton(char tokenChar) {
        Automaton simpleAutomaton = new AutomatonImpl();
        addStates(simpleAutomaton, 0, true, false);
        addStates(simpleAutomaton, 1, false, true);
        simpleAutomaton.addTransition(0, tokenChar, 1);
        return simpleAutomaton;
    }

    /**
     * Creates an automaton for recognizing whitespace characters (e.g., spaces, tabs, newlines).
     */
    private Automaton createWhitespaceAutomaton() {
        Automaton whitespaceAutomaton = new AutomatonImpl();
        addStates(whitespaceAutomaton, 0, true, true);
        addStates(whitespaceAutomaton, 1, false, true);

        // Whitespace transitions
        char[] whitespaceChars = {' ', '\n', '\r', '\t'};
        for (char ws : whitespaceChars) {
            whitespaceAutomaton.addTransition(0, ws, 1);
            whitespaceAutomaton.addTransition(1, ws, 1);
        }

        return whitespaceAutomaton;
    }

    /**
     * Helper method to add a state to an automaton.
     */
    private void addStates(Automaton automaton, int stateId, boolean isStart, boolean isAccepting) {
        automaton.addState(stateId, isStart, isAccepting);
    }

    /**
     * Helper method to add digit transitions for a given state.
     */
    private void addDigitTransitions(Automaton automaton, int fromState, int toState) {
        for (char digit = '0'; digit <= '9'; digit++) {
            automaton.addTransition(fromState, digit, toState);
        }
    }
}
