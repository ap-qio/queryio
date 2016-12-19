// $ANTLR 3.4 org/apache/hadoop/hive/metastore/parser/Filter.g 2016-12-15 16:48:36

package org.apache.hadoop.hive.metastore.parser;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FilterLexer extends Lexer {
    public static final int EOF=-1;
    public static final int BETWEEN=4;
    public static final int DateLiteral=5;
    public static final int DateString=6;
    public static final int Digit=7;
    public static final int EQUAL=8;
    public static final int GREATERTHAN=9;
    public static final int GREATERTHANOREQUALTO=10;
    public static final int Identifier=11;
    public static final int IntegralLiteral=12;
    public static final int KW_AND=13;
    public static final int KW_DATE=14;
    public static final int KW_LIKE=15;
    public static final int KW_NOT=16;
    public static final int KW_OR=17;
    public static final int LESSTHAN=18;
    public static final int LESSTHANOREQUALTO=19;
    public static final int LPAREN=20;
    public static final int Letter=21;
    public static final int NOTEQUAL=22;
    public static final int RPAREN=23;
    public static final int StringLiteral=24;
    public static final int WS=25;

      public String errorMsg;

      private static final Pattern datePattern = Pattern.compile(".*(\\d\\d\\d\\d-\\d\\d-\\d\\d).*");
      private static final ThreadLocal<SimpleDateFormat> dateFormat =
           new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
          SimpleDateFormat val = new SimpleDateFormat("yyyy-MM-dd");
          val.setLenient(false); // Without this, 2020-20-20 becomes 2021-08-20.
          return val;
        };
      };

      public static java.sql.Date ExtractDate (String input) {
        Matcher m = datePattern.matcher(input);
        if (!m.matches()) {
          return null;
        }
        try {
          return new java.sql.Date(dateFormat.get().parse(m.group(1)).getTime());
        } catch (ParseException pe) {
          return null;
        }
      }

      @Override
      public void emitErrorMessage(String msg) {
        // save for caller to detect invalid filter
        errorMsg = msg;
      }


    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public FilterLexer() {} 
    public FilterLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FilterLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "org/apache/hadoop/hive/metastore/parser/Filter.g"; }

    // $ANTLR start "KW_NOT"
    public final void mKW_NOT() throws RecognitionException {
        try {
            int _type = KW_NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:207:8: ( 'NOT' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:207:10: 'NOT'
            {
            match("NOT"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_NOT"

    // $ANTLR start "KW_AND"
    public final void mKW_AND() throws RecognitionException {
        try {
            int _type = KW_AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:208:8: ( 'AND' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:208:10: 'AND'
            {
            match("AND"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_AND"

    // $ANTLR start "KW_OR"
    public final void mKW_OR() throws RecognitionException {
        try {
            int _type = KW_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:209:7: ( 'OR' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:209:9: 'OR'
            {
            match("OR"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_OR"

    // $ANTLR start "KW_LIKE"
    public final void mKW_LIKE() throws RecognitionException {
        try {
            int _type = KW_LIKE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:210:9: ( 'LIKE' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:210:11: 'LIKE'
            {
            match("LIKE"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_LIKE"

    // $ANTLR start "KW_DATE"
    public final void mKW_DATE() throws RecognitionException {
        try {
            int _type = KW_DATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:211:9: ( 'date' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:211:11: 'date'
            {
            match("date"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KW_DATE"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:214:8: ( '(' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:214:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:215:8: ( ')' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:215:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "EQUAL"
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:216:7: ( '=' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:216:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUAL"

    // $ANTLR start "NOTEQUAL"
    public final void mNOTEQUAL() throws RecognitionException {
        try {
            int _type = NOTEQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:217:10: ( '<>' | '!=' )
            int alt1=2;
            switch ( input.LA(1) ) {
            case '<':
                {
                alt1=1;
                }
                break;
            case '!':
                {
                alt1=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:217:12: '<>'
                    {
                    match("<>"); 



                    }
                    break;
                case 2 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:217:19: '!='
                    {
                    match("!="); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOTEQUAL"

    // $ANTLR start "LESSTHANOREQUALTO"
    public final void mLESSTHANOREQUALTO() throws RecognitionException {
        try {
            int _type = LESSTHANOREQUALTO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:218:19: ( '<=' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:218:21: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LESSTHANOREQUALTO"

    // $ANTLR start "LESSTHAN"
    public final void mLESSTHAN() throws RecognitionException {
        try {
            int _type = LESSTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:219:10: ( '<' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:219:12: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LESSTHAN"

    // $ANTLR start "GREATERTHANOREQUALTO"
    public final void mGREATERTHANOREQUALTO() throws RecognitionException {
        try {
            int _type = GREATERTHANOREQUALTO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:220:22: ( '>=' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:220:24: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GREATERTHANOREQUALTO"

    // $ANTLR start "GREATERTHAN"
    public final void mGREATERTHAN() throws RecognitionException {
        try {
            int _type = GREATERTHAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:221:13: ( '>' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:221:15: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GREATERTHAN"

    // $ANTLR start "BETWEEN"
    public final void mBETWEEN() throws RecognitionException {
        try {
            int _type = BETWEEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:222:9: ( 'BETWEEN' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:222:11: 'BETWEEN'
            {
            match("BETWEEN"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BETWEEN"

    // $ANTLR start "Letter"
    public final void mLetter() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:228:5: ( 'a' .. 'z' | 'A' .. 'Z' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Letter"

    // $ANTLR start "Digit"
    public final void mDigit() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:233:5: ( '0' .. '9' )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Digit"

    // $ANTLR start "DateString"
    public final void mDateString() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:237:5: ( ( Digit ) ( Digit ) ( Digit ) ( Digit ) '-' ( Digit ) ( Digit ) '-' ( Digit ) ( Digit ) )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:238:5: ( Digit ) ( Digit ) ( Digit ) ( Digit ) '-' ( Digit ) ( Digit ) '-' ( Digit ) ( Digit )
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            match('-'); 

            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            match('-'); 

            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DateString"

    // $ANTLR start "DateLiteral"
    public final void mDateLiteral() throws RecognitionException {
        try {
            int _type = DateLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:245:5: ( ( KW_DATE )? DateString {...}?)
            // org/apache/hadoop/hive/metastore/parser/Filter.g:246:5: ( KW_DATE )? DateString {...}?
            {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:246:5: ( KW_DATE )?
            int alt2=2;
            switch ( input.LA(1) ) {
                case 'd':
                    {
                    alt2=1;
                    }
                    break;
            }

            switch (alt2) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:246:5: KW_DATE
                    {
                    mKW_DATE(); 


                    }
                    break;

            }


            mDateString(); 


            if ( !(( ExtractDate(getText()) != null )) ) {
                throw new FailedPredicateException(input, "DateLiteral", " ExtractDate(getText()) != null ");
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DateLiteral"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:250:5: ( ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' ) )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:251:5: ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )
            {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:251:5: ( '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\'' | '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"' )
            int alt5=2;
            switch ( input.LA(1) ) {
            case '\'':
                {
                alt5=1;
                }
                break;
            case '\"':
                {
                alt5=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:251:7: '\\'' (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )* '\\''
                    {
                    match('\''); 

                    // org/apache/hadoop/hive/metastore/parser/Filter.g:251:12: (~ ( '\\'' | '\\\\' ) | ( '\\\\' . ) )*
                    loop3:
                    do {
                        int alt3=3;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0 >= '\u0000' && LA3_0 <= '&')||(LA3_0 >= '(' && LA3_0 <= '[')||(LA3_0 >= ']' && LA3_0 <= '\uFFFF')) ) {
                            alt3=1;
                        }
                        else if ( (LA3_0=='\\') ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:251:14: ~ ( '\\'' | '\\\\' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:251:29: ( '\\\\' . )
                    	    {
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:251:29: ( '\\\\' . )
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:251:30: '\\\\' .
                    	    {
                    	    match('\\'); 

                    	    matchAny(); 

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    match('\''); 

                    }
                    break;
                case 2 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:252:7: '\\\"' (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )* '\\\"'
                    {
                    match('\"'); 

                    // org/apache/hadoop/hive/metastore/parser/Filter.g:252:12: (~ ( '\\\"' | '\\\\' ) | ( '\\\\' . ) )*
                    loop4:
                    do {
                        int alt4=3;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '\u0000' && LA4_0 <= '!')||(LA4_0 >= '#' && LA4_0 <= '[')||(LA4_0 >= ']' && LA4_0 <= '\uFFFF')) ) {
                            alt4=1;
                        }
                        else if ( (LA4_0=='\\') ) {
                            alt4=2;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:252:14: ~ ( '\\\"' | '\\\\' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:252:29: ( '\\\\' . )
                    	    {
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:252:29: ( '\\\\' . )
                    	    // org/apache/hadoop/hive/metastore/parser/Filter.g:252:30: '\\\\' .
                    	    {
                    	    match('\\'); 

                    	    matchAny(); 

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    match('\"'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "StringLiteral"

    // $ANTLR start "IntegralLiteral"
    public final void mIntegralLiteral() throws RecognitionException {
        try {
            int _type = IntegralLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:257:5: ( ( '-' )? ( Digit )+ )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:258:5: ( '-' )? ( Digit )+
            {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:258:5: ( '-' )?
            int alt6=2;
            switch ( input.LA(1) ) {
                case '-':
                    {
                    alt6=1;
                    }
                    break;
            }

            switch (alt6) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:258:6: '-'
                    {
                    match('-'); 

                    }
                    break;

            }


            // org/apache/hadoop/hive/metastore/parser/Filter.g:258:12: ( Digit )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt7=1;
                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // org/apache/hadoop/hive/metastore/parser/Filter.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IntegralLiteral"

    // $ANTLR start "Identifier"
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:262:5: ( ( Letter | Digit ) ( Letter | Digit | '_' )* )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:263:5: ( Letter | Digit ) ( Letter | Digit | '_' )*
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // org/apache/hadoop/hive/metastore/parser/Filter.g:263:22: ( Letter | Digit | '_' )*
            loop8:
            do {
                int alt8=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt8=1;
                    }
                    break;

                }

                switch (alt8) {
            	case 1 :
            	    // org/apache/hadoop/hive/metastore/parser/Filter.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Identifier"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/apache/hadoop/hive/metastore/parser/Filter.g:266:5: ( ( ' ' | '\\r' | '\\t' | '\\n' )+ )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:266:9: ( ' ' | '\\r' | '\\t' | '\\n' )+
            {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:266:9: ( ' ' | '\\r' | '\\t' | '\\n' )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                switch ( input.LA(1) ) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    {
                    alt9=1;
                    }
                    break;

                }

                switch (alt9) {
            	case 1 :
            	    // org/apache/hadoop/hive/metastore/parser/Filter.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);


             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // org/apache/hadoop/hive/metastore/parser/Filter.g:1:8: ( KW_NOT | KW_AND | KW_OR | KW_LIKE | KW_DATE | LPAREN | RPAREN | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | BETWEEN | DateLiteral | StringLiteral | IntegralLiteral | Identifier | WS )
        int alt10=19;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:10: KW_NOT
                {
                mKW_NOT(); 


                }
                break;
            case 2 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:17: KW_AND
                {
                mKW_AND(); 


                }
                break;
            case 3 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:24: KW_OR
                {
                mKW_OR(); 


                }
                break;
            case 4 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:30: KW_LIKE
                {
                mKW_LIKE(); 


                }
                break;
            case 5 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:38: KW_DATE
                {
                mKW_DATE(); 


                }
                break;
            case 6 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:46: LPAREN
                {
                mLPAREN(); 


                }
                break;
            case 7 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:53: RPAREN
                {
                mRPAREN(); 


                }
                break;
            case 8 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:60: EQUAL
                {
                mEQUAL(); 


                }
                break;
            case 9 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:66: NOTEQUAL
                {
                mNOTEQUAL(); 


                }
                break;
            case 10 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:75: LESSTHANOREQUALTO
                {
                mLESSTHANOREQUALTO(); 


                }
                break;
            case 11 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:93: LESSTHAN
                {
                mLESSTHAN(); 


                }
                break;
            case 12 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:102: GREATERTHANOREQUALTO
                {
                mGREATERTHANOREQUALTO(); 


                }
                break;
            case 13 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:123: GREATERTHAN
                {
                mGREATERTHAN(); 


                }
                break;
            case 14 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:135: BETWEEN
                {
                mBETWEEN(); 


                }
                break;
            case 15 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:143: DateLiteral
                {
                mDateLiteral(); 


                }
                break;
            case 16 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:155: StringLiteral
                {
                mStringLiteral(); 


                }
                break;
            case 17 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:169: IntegralLiteral
                {
                mIntegralLiteral(); 


                }
                break;
            case 18 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:185: Identifier
                {
                mIdentifier(); 


                }
                break;
            case 19 :
                // org/apache/hadoop/hive/metastore/parser/Filter.g:1:196: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\1\uffff\5\20\3\uffff\1\30\1\uffff\1\32\1\20\1\17\4\uffff\2\20\1"+
        "\37\2\20\4\uffff\1\20\1\17\1\44\1\45\1\uffff\3\20\1\17\2\uffff\1"+
        "\52\1\53\1\20\1\17\2\uffff\2\20\1\uffff\1\17\3\20\1\65\1\20\1\uffff";
    static final String DFA10_eofS =
        "\66\uffff";
    static final String DFA10_minS =
        "\1\11\1\117\1\116\1\122\1\111\1\141\3\uffff\1\75\1\uffff\1\75\1"+
        "\105\1\60\4\uffff\1\124\1\104\1\60\1\113\1\164\4\uffff\1\124\3\60"+
        "\1\uffff\1\105\1\145\1\127\1\60\2\uffff\2\60\1\105\1\55\2\uffff"+
        "\1\60\1\105\1\uffff\2\60\1\116\2\60\1\55\1\uffff";
    static final String DFA10_maxS =
        "\1\172\1\117\1\116\1\122\1\111\1\141\3\uffff\1\76\1\uffff\1\75\1"+
        "\105\1\172\4\uffff\1\124\1\104\1\172\1\113\1\164\4\uffff\1\124\3"+
        "\172\1\uffff\1\105\1\145\1\127\1\172\2\uffff\2\172\1\105\1\172\2"+
        "\uffff\1\71\1\105\1\uffff\1\172\1\71\1\116\1\71\1\172\1\55\1\uffff";
    static final String DFA10_acceptS =
        "\6\uffff\1\6\1\7\1\10\1\uffff\1\11\3\uffff\1\20\1\21\1\22\1\23\5"+
        "\uffff\1\12\1\13\1\14\1\15\4\uffff\1\3\4\uffff\1\1\1\2\4\uffff\1"+
        "\4\1\5\2\uffff\1\17\6\uffff\1\16";
    static final String DFA10_specialS =
        "\66\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\21\2\uffff\1\21\22\uffff\1\21\1\12\1\16\4\uffff\1\16\1\6"+
            "\1\7\3\uffff\1\17\2\uffff\12\15\2\uffff\1\11\1\10\1\13\2\uffff"+
            "\1\2\1\14\11\20\1\4\1\20\1\1\1\3\13\20\6\uffff\3\20\1\5\26\20",
            "\1\22",
            "\1\23",
            "\1\24",
            "\1\25",
            "\1\26",
            "",
            "",
            "",
            "\1\27\1\12",
            "",
            "\1\31",
            "\1\33",
            "\12\34\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "",
            "",
            "",
            "\1\35",
            "\1\36",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\40",
            "\1\41",
            "",
            "",
            "",
            "",
            "\1\42",
            "\12\43\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "\1\46",
            "\1\47",
            "\1\50",
            "\12\51\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\12\54\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\55",
            "\1\56\2\uffff\12\57\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "",
            "\12\60",
            "\1\61",
            "",
            "\12\57\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\12\62",
            "\1\63",
            "\12\64",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\56",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( KW_NOT | KW_AND | KW_OR | KW_LIKE | KW_DATE | LPAREN | RPAREN | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | BETWEEN | DateLiteral | StringLiteral | IntegralLiteral | Identifier | WS );";
        }
    }
 

}