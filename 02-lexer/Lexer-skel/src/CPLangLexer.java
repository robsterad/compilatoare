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
		IF=1, THEN=2, ELSE=3, FI=4, TRUE=5, FALSE=6, FOR=7, DO=8, EQ=9, LE=10, 
		LT=11, ASSIGN=12, PLUS=13, MINUS=14, MUL=15, DIV=16, SEMI=17, COMMA=18, 
		LPAREN=19, RPAREN=20, LBRACE=21, RBRACE=22, FLOAT=23, INT=24, TYPE=25, 
		ID=26, STRING=27, UNCLOSED_STRING=28, LINE_COMMENT=29, UNMATCHED_COMMENT_END=30, 
		WS=31, ERROR_CHAR=32, COMMENT_CLOSE=33, COMMENT_EOF=34;
	public static final int
		COMMENT_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "COMMENT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"IF", "THEN", "ELSE", "FI", "TRUE", "FALSE", "FOR", "DO", "EQ", "LE", 
			"LT", "ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "SEMI", "COMMA", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "FLOAT", "INT", "TYPE", "ID", "STRING", 
			"UNCLOSED_STRING", "LINE_COMMENT", "BLOCK_COMMENT_START", "UNMATCHED_COMMENT_END", 
			"WS", "ERROR_CHAR", "COMMENT_OPEN", "COMMENT_CLOSE", "COMMENT_EOF", "COMMENT_CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'if'", "'then'", "'else'", "'fi'", "'true'", "'false'", "'for'", 
			"'do'", "'=='", "'<='", "'<'", "'='", "'+'", "'-'", "'*'", "'/'", "';'", 
			"','", "'('", "')'", "'{'", "'}'", null, null, null, null, null, null, 
			null, null, null, null, "'*/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IF", "THEN", "ELSE", "FI", "TRUE", "FALSE", "FOR", "DO", "EQ", 
			"LE", "LT", "ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "SEMI", "COMMA", 
			"LPAREN", "RPAREN", "LBRACE", "RBRACE", "FLOAT", "INT", "TYPE", "ID", 
			"STRING", "UNCLOSED_STRING", "LINE_COMMENT", "UNMATCHED_COMMENT_END", 
			"WS", "ERROR_CHAR", "COMMENT_CLOSE", "COMMENT_EOF"
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


	    private int commentDepth = 0;
	    private int commentStartLine = 0;


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
		case 27:
			UNCLOSED_STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 29:
			BLOCK_COMMENT_START_action((RuleContext)_localctx, actionIndex);
			break;
		case 30:
			UNMATCHED_COMMENT_END_action((RuleContext)_localctx, actionIndex);
			break;
		case 32:
			ERROR_CHAR_action((RuleContext)_localctx, actionIndex);
			break;
		case 33:
			COMMENT_OPEN_action((RuleContext)_localctx, actionIndex);
			break;
		case 34:
			COMMENT_CLOSE_action((RuleContext)_localctx, actionIndex);
			break;
		case 35:
			COMMENT_EOF_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void UNCLOSED_STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 System.err.println("Eroare lexicala: string neincheiat la linia " + getLine()); 
			break;
		}
	}
	private void BLOCK_COMMENT_START_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:

			        commentDepth = 1;
			        commentStartLine = getLine();
			      
			break;
		}
	}
	private void UNMATCHED_COMMENT_END_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:

			        System.err.println("Eroare lexicala: */ fara /* la linia " + getLine());
			      
			break;
		}
	}
	private void ERROR_CHAR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:

			        System.err.println("Eroare lexicala: caracter necunoscut '" + getText() + "' la linia " + getLine());
			      
			break;
		}
	}
	private void COMMENT_OPEN_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			 commentDepth++; 
			break;
		}
	}
	private void COMMENT_CLOSE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:

			        commentDepth--;
			        if (commentDepth == 0) {
			            popMode();
			            skip();
			        } else {
			            more();
			        }
			      
			break;
		}
	}
	private void COMMENT_EOF_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:

			        System.err.println("Eroare lexicala: comentariu bloc neincheiat, inceput la linia " + commentStartLine);
			      
			break;
		}
	}

	public static final String _serializedATN =
		"\u0004\u0000\"\u010d\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002"+
		"\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002"+
		"\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002"+
		"\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002"+
		"\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002"+
		"\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002"+
		"\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002"+
		" \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001"+
		"\u0016\u0004\u0016\u008e\b\u0016\u000b\u0016\f\u0016\u008f\u0001\u0016"+
		"\u0001\u0016\u0005\u0016\u0094\b\u0016\n\u0016\f\u0016\u0097\t\u0016\u0001"+
		"\u0016\u0001\u0016\u0004\u0016\u009b\b\u0016\u000b\u0016\f\u0016\u009c"+
		"\u0003\u0016\u009f\b\u0016\u0001\u0017\u0004\u0017\u00a2\b\u0017\u000b"+
		"\u0017\f\u0017\u00a3\u0001\u0018\u0001\u0018\u0005\u0018\u00a8\b\u0018"+
		"\n\u0018\f\u0018\u00ab\t\u0018\u0001\u0019\u0001\u0019\u0005\u0019\u00af"+
		"\b\u0019\n\u0019\f\u0019\u00b2\t\u0019\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0005\u001a\u00b8\b\u001a\n\u001a\f\u001a\u00bb\t\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0005"+
		"\u001b\u00c3\b\u001b\n\u001b\f\u001b\u00c6\t\u001b\u0001\u001b\u0003\u001b"+
		"\u00c9\b\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u00cd\b\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0005\u001c\u00d7\b\u001c\n\u001c\f\u001c\u00da\t\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f"+
		"\u0004\u001f\u00ee\b\u001f\u000b\u001f\f\u001f\u00ef\u0001\u001f\u0001"+
		"\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!"+
		"\u0001!\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001"+
		"#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0000\u0000%\u0002"+
		"\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010"+
		"\b\u0012\t\u0014\n\u0016\u000b\u0018\f\u001a\r\u001c\u000e\u001e\u000f"+
		" \u0010\"\u0011$\u0012&\u0013(\u0014*\u0015,\u0016.\u00170\u00182\u0019"+
		"4\u001a6\u001b8\u001c:\u001d<\u0000>\u001e@\u001fB D\u0000F!H\"J\u0000"+
		"\u0002\u0000\u0001\u0007\u0001\u000009\u0001\u0000AZ\u0003\u0000AZ__a"+
		"z\u0001\u0000az\u0004\u0000\n\n\r\r\"\"\\\\\u0002\u0000\n\n\r\r\u0003"+
		"\u0000\t\n\r\r  \u011a\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004"+
		"\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001"+
		"\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000"+
		"\u0000\u0000\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000"+
		"\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000"+
		"\u0000\u0000\u0000\u0016\u0001\u0000\u0000\u0000\u0000\u0018\u0001\u0000"+
		"\u0000\u0000\u0000\u001a\u0001\u0000\u0000\u0000\u0000\u001c\u0001\u0000"+
		"\u0000\u0000\u0000\u001e\u0001\u0000\u0000\u0000\u0000 \u0001\u0000\u0000"+
		"\u0000\u0000\"\u0001\u0000\u0000\u0000\u0000$\u0001\u0000\u0000\u0000"+
		"\u0000&\u0001\u0000\u0000\u0000\u0000(\u0001\u0000\u0000\u0000\u0000*"+
		"\u0001\u0000\u0000\u0000\u0000,\u0001\u0000\u0000\u0000\u0000.\u0001\u0000"+
		"\u0000\u0000\u00000\u0001\u0000\u0000\u0000\u00002\u0001\u0000\u0000\u0000"+
		"\u00004\u0001\u0000\u0000\u0000\u00006\u0001\u0000\u0000\u0000\u00008"+
		"\u0001\u0000\u0000\u0000\u0000:\u0001\u0000\u0000\u0000\u0000<\u0001\u0000"+
		"\u0000\u0000\u0000>\u0001\u0000\u0000\u0000\u0000@\u0001\u0000\u0000\u0000"+
		"\u0000B\u0001\u0000\u0000\u0000\u0001D\u0001\u0000\u0000\u0000\u0001F"+
		"\u0001\u0000\u0000\u0000\u0001H\u0001\u0000\u0000\u0000\u0001J\u0001\u0000"+
		"\u0000\u0000\u0002L\u0001\u0000\u0000\u0000\u0004O\u0001\u0000\u0000\u0000"+
		"\u0006T\u0001\u0000\u0000\u0000\bY\u0001\u0000\u0000\u0000\n\\\u0001\u0000"+
		"\u0000\u0000\fa\u0001\u0000\u0000\u0000\u000eg\u0001\u0000\u0000\u0000"+
		"\u0010k\u0001\u0000\u0000\u0000\u0012n\u0001\u0000\u0000\u0000\u0014q"+
		"\u0001\u0000\u0000\u0000\u0016t\u0001\u0000\u0000\u0000\u0018v\u0001\u0000"+
		"\u0000\u0000\u001ax\u0001\u0000\u0000\u0000\u001cz\u0001\u0000\u0000\u0000"+
		"\u001e|\u0001\u0000\u0000\u0000 ~\u0001\u0000\u0000\u0000\"\u0080\u0001"+
		"\u0000\u0000\u0000$\u0082\u0001\u0000\u0000\u0000&\u0084\u0001\u0000\u0000"+
		"\u0000(\u0086\u0001\u0000\u0000\u0000*\u0088\u0001\u0000\u0000\u0000,"+
		"\u008a\u0001\u0000\u0000\u0000.\u009e\u0001\u0000\u0000\u00000\u00a1\u0001"+
		"\u0000\u0000\u00002\u00a5\u0001\u0000\u0000\u00004\u00ac\u0001\u0000\u0000"+
		"\u00006\u00b3\u0001\u0000\u0000\u00008\u00be\u0001\u0000\u0000\u0000:"+
		"\u00d2\u0001\u0000\u0000\u0000<\u00dd\u0001\u0000\u0000\u0000>\u00e5\u0001"+
		"\u0000\u0000\u0000@\u00ed\u0001\u0000\u0000\u0000B\u00f3\u0001\u0000\u0000"+
		"\u0000D\u00f8\u0001\u0000\u0000\u0000F\u00ff\u0001\u0000\u0000\u0000H"+
		"\u0104\u0001\u0000\u0000\u0000J\u0109\u0001\u0000\u0000\u0000LM\u0005"+
		"i\u0000\u0000MN\u0005f\u0000\u0000N\u0003\u0001\u0000\u0000\u0000OP\u0005"+
		"t\u0000\u0000PQ\u0005h\u0000\u0000QR\u0005e\u0000\u0000RS\u0005n\u0000"+
		"\u0000S\u0005\u0001\u0000\u0000\u0000TU\u0005e\u0000\u0000UV\u0005l\u0000"+
		"\u0000VW\u0005s\u0000\u0000WX\u0005e\u0000\u0000X\u0007\u0001\u0000\u0000"+
		"\u0000YZ\u0005f\u0000\u0000Z[\u0005i\u0000\u0000[\t\u0001\u0000\u0000"+
		"\u0000\\]\u0005t\u0000\u0000]^\u0005r\u0000\u0000^_\u0005u\u0000\u0000"+
		"_`\u0005e\u0000\u0000`\u000b\u0001\u0000\u0000\u0000ab\u0005f\u0000\u0000"+
		"bc\u0005a\u0000\u0000cd\u0005l\u0000\u0000de\u0005s\u0000\u0000ef\u0005"+
		"e\u0000\u0000f\r\u0001\u0000\u0000\u0000gh\u0005f\u0000\u0000hi\u0005"+
		"o\u0000\u0000ij\u0005r\u0000\u0000j\u000f\u0001\u0000\u0000\u0000kl\u0005"+
		"d\u0000\u0000lm\u0005o\u0000\u0000m\u0011\u0001\u0000\u0000\u0000no\u0005"+
		"=\u0000\u0000op\u0005=\u0000\u0000p\u0013\u0001\u0000\u0000\u0000qr\u0005"+
		"<\u0000\u0000rs\u0005=\u0000\u0000s\u0015\u0001\u0000\u0000\u0000tu\u0005"+
		"<\u0000\u0000u\u0017\u0001\u0000\u0000\u0000vw\u0005=\u0000\u0000w\u0019"+
		"\u0001\u0000\u0000\u0000xy\u0005+\u0000\u0000y\u001b\u0001\u0000\u0000"+
		"\u0000z{\u0005-\u0000\u0000{\u001d\u0001\u0000\u0000\u0000|}\u0005*\u0000"+
		"\u0000}\u001f\u0001\u0000\u0000\u0000~\u007f\u0005/\u0000\u0000\u007f"+
		"!\u0001\u0000\u0000\u0000\u0080\u0081\u0005;\u0000\u0000\u0081#\u0001"+
		"\u0000\u0000\u0000\u0082\u0083\u0005,\u0000\u0000\u0083%\u0001\u0000\u0000"+
		"\u0000\u0084\u0085\u0005(\u0000\u0000\u0085\'\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0005)\u0000\u0000\u0087)\u0001\u0000\u0000\u0000\u0088\u0089"+
		"\u0005{\u0000\u0000\u0089+\u0001\u0000\u0000\u0000\u008a\u008b\u0005}"+
		"\u0000\u0000\u008b-\u0001\u0000\u0000\u0000\u008c\u008e\u0007\u0000\u0000"+
		"\u0000\u008d\u008c\u0001\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000"+
		"\u0000\u008f\u008d\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000"+
		"\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091\u0095\u0005.\u0000\u0000"+
		"\u0092\u0094\u0007\u0000\u0000\u0000\u0093\u0092\u0001\u0000\u0000\u0000"+
		"\u0094\u0097\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000"+
		"\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u009f\u0001\u0000\u0000\u0000"+
		"\u0097\u0095\u0001\u0000\u0000\u0000\u0098\u009a\u0005.\u0000\u0000\u0099"+
		"\u009b\u0007\u0000\u0000\u0000\u009a\u0099\u0001\u0000\u0000\u0000\u009b"+
		"\u009c\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000\u0000\u0000\u009c"+
		"\u009d\u0001\u0000\u0000\u0000\u009d\u009f\u0001\u0000\u0000\u0000\u009e"+
		"\u008d\u0001\u0000\u0000\u0000\u009e\u0098\u0001\u0000\u0000\u0000\u009f"+
		"/\u0001\u0000\u0000\u0000\u00a0\u00a2\u0007\u0000\u0000\u0000\u00a1\u00a0"+
		"\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u00a1"+
		"\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a41\u0001"+
		"\u0000\u0000\u0000\u00a5\u00a9\u0007\u0001\u0000\u0000\u00a6\u00a8\u0007"+
		"\u0002\u0000\u0000\u00a7\u00a6\u0001\u0000\u0000\u0000\u00a8\u00ab\u0001"+
		"\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001"+
		"\u0000\u0000\u0000\u00aa3\u0001\u0000\u0000\u0000\u00ab\u00a9\u0001\u0000"+
		"\u0000\u0000\u00ac\u00b0\u0007\u0003\u0000\u0000\u00ad\u00af\u0007\u0002"+
		"\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000\u00af\u00b2\u0001\u0000"+
		"\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000"+
		"\u0000\u0000\u00b15\u0001\u0000\u0000\u0000\u00b2\u00b0\u0001\u0000\u0000"+
		"\u0000\u00b3\u00b9\u0005\"\u0000\u0000\u00b4\u00b5\u0005\\\u0000\u0000"+
		"\u00b5\u00b8\t\u0000\u0000\u0000\u00b6\u00b8\b\u0004\u0000\u0000\u00b7"+
		"\u00b4\u0001\u0000\u0000\u0000\u00b7\u00b6\u0001\u0000\u0000\u0000\u00b8"+
		"\u00bb\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000\u0000\u00b9"+
		"\u00ba\u0001\u0000\u0000\u0000\u00ba\u00bc\u0001\u0000\u0000\u0000\u00bb"+
		"\u00b9\u0001\u0000\u0000\u0000\u00bc\u00bd\u0005\"\u0000\u0000\u00bd7"+
		"\u0001\u0000\u0000\u0000\u00be\u00c4\u0005\"\u0000\u0000\u00bf\u00c0\u0005"+
		"\\\u0000\u0000\u00c0\u00c3\t\u0000\u0000\u0000\u00c1\u00c3\b\u0004\u0000"+
		"\u0000\u00c2\u00bf\u0001\u0000\u0000\u0000\u00c2\u00c1\u0001\u0000\u0000"+
		"\u0000\u00c3\u00c6\u0001\u0000\u0000\u0000\u00c4\u00c2\u0001\u0000\u0000"+
		"\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00cc\u0001\u0000\u0000"+
		"\u0000\u00c6\u00c4\u0001\u0000\u0000\u0000\u00c7\u00c9\u0005\r\u0000\u0000"+
		"\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000"+
		"\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00cd\u0005\n\u0000\u0000\u00cb"+
		"\u00cd\u0005\u0000\u0000\u0001\u00cc\u00c8\u0001\u0000\u0000\u0000\u00cc"+
		"\u00cb\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce"+
		"\u00cf\u0006\u001b\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0"+
		"\u00d1\u0006\u001b\u0001\u0000\u00d19\u0001\u0000\u0000\u0000\u00d2\u00d3"+
		"\u0005/\u0000\u0000\u00d3\u00d4\u0005/\u0000\u0000\u00d4\u00d8\u0001\u0000"+
		"\u0000\u0000\u00d5\u00d7\b\u0005\u0000\u0000\u00d6\u00d5\u0001\u0000\u0000"+
		"\u0000\u00d7\u00da\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000"+
		"\u0000\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9\u00db\u0001\u0000\u0000"+
		"\u0000\u00da\u00d8\u0001\u0000\u0000\u0000\u00db\u00dc\u0006\u001c\u0001"+
		"\u0000\u00dc;\u0001\u0000\u0000\u0000\u00dd\u00de\u0005/\u0000\u0000\u00de"+
		"\u00df\u0005*\u0000\u0000\u00df\u00e0\u0001\u0000\u0000\u0000\u00e0\u00e1"+
		"\u0006\u001d\u0002\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2\u00e3"+
		"\u0006\u001d\u0003\u0000\u00e3\u00e4\u0006\u001d\u0004\u0000\u00e4=\u0001"+
		"\u0000\u0000\u0000\u00e5\u00e6\u0005*\u0000\u0000\u00e6\u00e7\u0005/\u0000"+
		"\u0000\u00e7\u00e8\u0001\u0000\u0000\u0000\u00e8\u00e9\u0006\u001e\u0005"+
		"\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea\u00eb\u0006\u001e\u0001"+
		"\u0000\u00eb?\u0001\u0000\u0000\u0000\u00ec\u00ee\u0007\u0006\u0000\u0000"+
		"\u00ed\u00ec\u0001\u0000\u0000\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000"+
		"\u00ef\u00ed\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000"+
		"\u00f0\u00f1\u0001\u0000\u0000\u0000\u00f1\u00f2\u0006\u001f\u0001\u0000"+
		"\u00f2A\u0001\u0000\u0000\u0000\u00f3\u00f4\t\u0000\u0000\u0000\u00f4"+
		"\u00f5\u0006 \u0006\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f7"+
		"\u0006 \u0001\u0000\u00f7C\u0001\u0000\u0000\u0000\u00f8\u00f9\u0005/"+
		"\u0000\u0000\u00f9\u00fa\u0005*\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000"+
		"\u0000\u00fb\u00fc\u0006!\u0007\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000"+
		"\u00fd\u00fe\u0006!\u0004\u0000\u00feE\u0001\u0000\u0000\u0000\u00ff\u0100"+
		"\u0005*\u0000\u0000\u0100\u0101\u0005/\u0000\u0000\u0101\u0102\u0001\u0000"+
		"\u0000\u0000\u0102\u0103\u0006\"\b\u0000\u0103G\u0001\u0000\u0000\u0000"+
		"\u0104\u0105\u0005\u0000\u0000\u0001\u0105\u0106\u0006#\t\u0000\u0106"+
		"\u0107\u0001\u0000\u0000\u0000\u0107\u0108\u0006#\u0001\u0000\u0108I\u0001"+
		"\u0000\u0000\u0000\u0109\u010a\t\u0000\u0000\u0000\u010a\u010b\u0001\u0000"+
		"\u0000\u0000\u010b\u010c\u0006$\u0004\u0000\u010cK\u0001\u0000\u0000\u0000"+
		"\u0011\u0000\u0001\u008f\u0095\u009c\u009e\u00a3\u00a9\u00b0\u00b7\u00b9"+
		"\u00c2\u00c4\u00c8\u00cc\u00d8\u00ef\n\u0001\u001b\u0000\u0006\u0000\u0000"+
		"\u0001\u001d\u0001\u0005\u0001\u0000\u0003\u0000\u0000\u0001\u001e\u0002"+
		"\u0001 \u0003\u0001!\u0004\u0001\"\u0005\u0001#\u0006";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}