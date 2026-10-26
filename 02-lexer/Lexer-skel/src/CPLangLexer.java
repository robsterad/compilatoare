// Generated from C:/Users/robst/Documents/Facultate/Anul4/Sem1/CPL/laboratoare/02-lexer/Lexer-skel/CPLangLexer.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CPLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IF=1, INT=2, REAL=3, STRING=4, BLOCK_COMMENT=5, WS=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"IF", "DIGIT", "INT", "DIGITS", "FRACTION", "EXPONENT", "REAL", "STRING", 
			"BLOCK_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'if'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IF", "INT", "REAL", "STRING", "BLOCK_COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CPLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CPLangLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 7:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 System.out.println("there are no strings in CPLang, but shhh.."); 
			break;
		}
	}

	public static final String _serializedATN =
		"\u0004\u0000\u0006W\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0004\u0002\u001c\b\u0002\u000b"+
		"\u0002\f\u0002\u001d\u0001\u0003\u0004\u0003!\b\u0003\u000b\u0003\f\u0003"+
		"\"\u0001\u0004\u0001\u0004\u0003\u0004\'\b\u0004\u0003\u0004)\b\u0004"+
		"\u0001\u0005\u0001\u0005\u0003\u0005-\b\u0005\u0001\u0005\u0003\u0005"+
		"0\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007:\b\u0007\n\u0007\f\u0007"+
		"=\t\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0005\bG\b\b\n\b\f\bJ\t\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\t\u0004\tR\b\t\u000b\t\f\tS\u0001\t\u0001\t\u0002;H\u0000"+
		"\n\u0001\u0001\u0003\u0000\u0005\u0002\u0007\u0000\t\u0000\u000b\u0000"+
		"\r\u0003\u000f\u0004\u0011\u0005\u0013\u0006\u0001\u0000\u0003\u0001\u0000"+
		"09\u0002\u0000++--\u0003\u0000\t\n\r\r  ]\u0000\u0001\u0001\u0000\u0000"+
		"\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000"+
		"\u0000\u0013\u0001\u0000\u0000\u0000\u0001\u0015\u0001\u0000\u0000\u0000"+
		"\u0003\u0018\u0001\u0000\u0000\u0000\u0005\u001b\u0001\u0000\u0000\u0000"+
		"\u0007 \u0001\u0000\u0000\u0000\t(\u0001\u0000\u0000\u0000\u000b/\u0001"+
		"\u0000\u0000\u0000\r1\u0001\u0000\u0000\u0000\u000f5\u0001\u0000\u0000"+
		"\u0000\u0011A\u0001\u0000\u0000\u0000\u0013Q\u0001\u0000\u0000\u0000\u0015"+
		"\u0016\u0005i\u0000\u0000\u0016\u0017\u0005f\u0000\u0000\u0017\u0002\u0001"+
		"\u0000\u0000\u0000\u0018\u0019\u0007\u0000\u0000\u0000\u0019\u0004\u0001"+
		"\u0000\u0000\u0000\u001a\u001c\u0003\u0003\u0001\u0000\u001b\u001a\u0001"+
		"\u0000\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001b\u0001"+
		"\u0000\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e\u0006\u0001"+
		"\u0000\u0000\u0000\u001f!\u0003\u0003\u0001\u0000 \u001f\u0001\u0000\u0000"+
		"\u0000!\"\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000\u0000\"#\u0001"+
		"\u0000\u0000\u0000#\b\u0001\u0000\u0000\u0000$&\u0005.\u0000\u0000%\'"+
		"\u0003\u0007\u0003\u0000&%\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000"+
		"\u0000\')\u0001\u0000\u0000\u0000($\u0001\u0000\u0000\u0000()\u0001\u0000"+
		"\u0000\u0000)\n\u0001\u0000\u0000\u0000*,\u0005e\u0000\u0000+-\u0007\u0001"+
		"\u0000\u0000,+\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-.\u0001"+
		"\u0000\u0000\u0000.0\u0003\u0007\u0003\u0000/*\u0001\u0000\u0000\u0000"+
		"/0\u0001\u0000\u0000\u00000\f\u0001\u0000\u0000\u000012\u0003\u0007\u0003"+
		"\u000023\u0003\t\u0004\u000034\u0003\u000b\u0005\u00004\u000e\u0001\u0000"+
		"\u0000\u00005;\u0005\"\u0000\u000067\u0005\\\u0000\u00007:\u0005\"\u0000"+
		"\u00008:\t\u0000\u0000\u000096\u0001\u0000\u0000\u000098\u0001\u0000\u0000"+
		"\u0000:=\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000;9\u0001\u0000"+
		"\u0000\u0000<>\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000>?\u0005"+
		"\"\u0000\u0000?@\u0006\u0007\u0000\u0000@\u0010\u0001\u0000\u0000\u0000"+
		"AB\u0005/\u0000\u0000BC\u0005*\u0000\u0000CH\u0001\u0000\u0000\u0000D"+
		"G\u0003\u0011\b\u0000EG\t\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000"+
		"FE\u0001\u0000\u0000\u0000GJ\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000"+
		"\u0000HF\u0001\u0000\u0000\u0000IK\u0001\u0000\u0000\u0000JH\u0001\u0000"+
		"\u0000\u0000KL\u0005*\u0000\u0000LM\u0005/\u0000\u0000MN\u0001\u0000\u0000"+
		"\u0000NO\u0006\b\u0001\u0000O\u0012\u0001\u0000\u0000\u0000PR\u0007\u0002"+
		"\u0000\u0000QP\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SQ\u0001"+
		"\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000"+
		"UV\u0006\t\u0001\u0000V\u0014\u0001\u0000\u0000\u0000\f\u0000\u001d\""+
		"&(,/9;FHS\u0002\u0001\u0007\u0000\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}