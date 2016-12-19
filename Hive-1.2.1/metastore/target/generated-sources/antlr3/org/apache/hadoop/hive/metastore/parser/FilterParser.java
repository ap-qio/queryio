// $ANTLR 3.4 org/apache/hadoop/hive/metastore/parser/Filter.g 2016-12-15 16:48:36

package org.apache.hadoop.hive.metastore.parser;

import org.apache.hadoop.hive.metastore.parser.ExpressionTree;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree.LeafNode;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree.Operator;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree.LogicalOperator;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FilterParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "BETWEEN", "DateLiteral", "DateString", "Digit", "EQUAL", "GREATERTHAN", "GREATERTHANOREQUALTO", "Identifier", "IntegralLiteral", "KW_AND", "KW_DATE", "KW_LIKE", "KW_NOT", "KW_OR", "LESSTHAN", "LESSTHANOREQUALTO", "LPAREN", "Letter", "NOTEQUAL", "RPAREN", "StringLiteral", "WS"
    };

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

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public FilterParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public FilterParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return FilterParser.tokenNames; }
    public String getGrammarFileName() { return "org/apache/hadoop/hive/metastore/parser/Filter.g"; }


      public ExpressionTree tree = new ExpressionTree();

      public static String TrimQuotes (String input) {
        if (input.length () > 1) {
          if ((input.charAt (0) == '"' && input.charAt (input.length () - 1) == '"')
            || (input.charAt (0) == '\'' && input.charAt (input.length () - 1) == '\'')) {
            return input.substring (1, input.length () - 1);
          }
        }
        return input;
      }



    // $ANTLR start "filter"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:99:1: filter : orExpression ;
    public final void filter() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:100:5: ( orExpression )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:101:5: orExpression
            {
            pushFollow(FOLLOW_orExpression_in_filter84);
            orExpression();

            state._fsp--;


            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "filter"



    // $ANTLR start "orExpression"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:104:1: orExpression : andExpression ( KW_OR andExpression )* ;
    public final void orExpression() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:105:5: ( andExpression ( KW_OR andExpression )* )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:106:5: andExpression ( KW_OR andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_orExpression106);
            andExpression();

            state._fsp--;


            // org/apache/hadoop/hive/metastore/parser/Filter.g:106:19: ( KW_OR andExpression )*
            loop1:
            do {
                int alt1=2;
                switch ( input.LA(1) ) {
                case KW_OR:
                    {
                    alt1=1;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // org/apache/hadoop/hive/metastore/parser/Filter.g:106:20: KW_OR andExpression
            	    {
            	    match(input,KW_OR,FOLLOW_KW_OR_in_orExpression109); 

            	    pushFollow(FOLLOW_andExpression_in_orExpression111);
            	    andExpression();

            	    state._fsp--;


            	     tree.addIntermediateNode(LogicalOperator.OR); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "orExpression"



    // $ANTLR start "andExpression"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:109:1: andExpression : expression ( KW_AND expression )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:110:5: ( expression ( KW_AND expression )* )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:111:5: expression ( KW_AND expression )*
            {
            pushFollow(FOLLOW_expression_in_andExpression137);
            expression();

            state._fsp--;


            // org/apache/hadoop/hive/metastore/parser/Filter.g:111:16: ( KW_AND expression )*
            loop2:
            do {
                int alt2=2;
                switch ( input.LA(1) ) {
                case KW_AND:
                    {
                    alt2=1;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org/apache/hadoop/hive/metastore/parser/Filter.g:111:17: KW_AND expression
            	    {
            	    match(input,KW_AND,FOLLOW_KW_AND_in_andExpression140); 

            	    pushFollow(FOLLOW_expression_in_andExpression142);
            	    expression();

            	    state._fsp--;


            	     tree.addIntermediateNode(LogicalOperator.AND); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "andExpression"



    // $ANTLR start "expression"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:114:1: expression : ( LPAREN orExpression RPAREN | operatorExpression );
    public final void expression() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:115:5: ( LPAREN orExpression RPAREN | operatorExpression )
            int alt3=2;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt3=1;
                }
                break;
            case DateLiteral:
            case Identifier:
            case IntegralLiteral:
            case StringLiteral:
                {
                alt3=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }

            switch (alt3) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:116:5: LPAREN orExpression RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_expression169); 

                    pushFollow(FOLLOW_orExpression_in_expression171);
                    orExpression();

                    state._fsp--;


                    match(input,RPAREN,FOLLOW_RPAREN_in_expression173); 

                    }
                    break;
                case 2 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:118:5: operatorExpression
                    {
                    pushFollow(FOLLOW_operatorExpression_in_expression185);
                    operatorExpression();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "expression"



    // $ANTLR start "operatorExpression"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:121:1: operatorExpression : ( betweenExpression | binOpExpression );
    public final void operatorExpression() throws RecognitionException {
        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:122:5: ( betweenExpression | binOpExpression )
            int alt4=2;
            switch ( input.LA(1) ) {
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case BETWEEN:
                case KW_NOT:
                    {
                    alt4=1;
                    }
                    break;
                case EQUAL:
                case GREATERTHAN:
                case GREATERTHANOREQUALTO:
                case KW_LIKE:
                case LESSTHAN:
                case LESSTHANOREQUALTO:
                case NOTEQUAL:
                    {
                    alt4=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;

                }

                }
                break;
            case DateLiteral:
            case IntegralLiteral:
            case StringLiteral:
                {
                alt4=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }

            switch (alt4) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:123:5: betweenExpression
                    {
                    pushFollow(FOLLOW_betweenExpression_in_operatorExpression206);
                    betweenExpression();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:125:5: binOpExpression
                    {
                    pushFollow(FOLLOW_binOpExpression_in_operatorExpression218);
                    binOpExpression();

                    state._fsp--;


                    }
                    break;

            }
        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "operatorExpression"



    // $ANTLR start "binOpExpression"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:128:1: binOpExpression : ( ( (key= Identifier op= operator value= DateLiteral ) | (value= DateLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntegralLiteral ) | (value= IntegralLiteral op= operator key= Identifier ) ) ) ;
    public final void binOpExpression() throws RecognitionException {
        Token key=null;
        Token value=null;
        Operator op =null;



            boolean isReverseOrder = false;
            Object val = null;

        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:133:5: ( ( ( (key= Identifier op= operator value= DateLiteral ) | (value= DateLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntegralLiteral ) | (value= IntegralLiteral op= operator key= Identifier ) ) ) )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:134:5: ( ( (key= Identifier op= operator value= DateLiteral ) | (value= DateLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntegralLiteral ) | (value= IntegralLiteral op= operator key= Identifier ) ) )
            {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:134:5: ( ( (key= Identifier op= operator value= DateLiteral ) | (value= DateLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) ) | ( (key= Identifier op= operator value= IntegralLiteral ) | (value= IntegralLiteral op= operator key= Identifier ) ) )
            int alt8=3;
            switch ( input.LA(1) ) {
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case EQUAL:
                case GREATERTHAN:
                case GREATERTHANOREQUALTO:
                case KW_LIKE:
                case LESSTHAN:
                case LESSTHANOREQUALTO:
                case NOTEQUAL:
                    {
                    switch ( input.LA(3) ) {
                    case DateLiteral:
                        {
                        alt8=1;
                        }
                        break;
                    case StringLiteral:
                        {
                        alt8=2;
                        }
                        break;
                    case IntegralLiteral:
                        {
                        alt8=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 5, input);

                        throw nvae;

                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;

                }

                }
                break;
            case DateLiteral:
                {
                alt8=1;
                }
                break;
            case StringLiteral:
                {
                alt8=2;
                }
                break;
            case IntegralLiteral:
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:135:8: ( (key= Identifier op= operator value= DateLiteral ) | (value= DateLiteral op= operator key= Identifier ) )
                    {
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:135:8: ( (key= Identifier op= operator value= DateLiteral ) | (value= DateLiteral op= operator key= Identifier ) )
                    int alt5=2;
                    switch ( input.LA(1) ) {
                    case Identifier:
                        {
                        alt5=1;
                        }
                        break;
                    case DateLiteral:
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
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:136:10: (key= Identifier op= operator value= DateLiteral )
                            {
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:136:10: (key= Identifier op= operator value= DateLiteral )
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:136:11: key= Identifier op= operator value= DateLiteral
                            {
                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_binOpExpression269); 

                            pushFollow(FOLLOW_operator_in_binOpExpression275);
                            op=operator();

                            state._fsp--;


                            value=(Token)match(input,DateLiteral,FOLLOW_DateLiteral_in_binOpExpression282); 

                            }


                            }
                            break;
                        case 2 :
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:138:10: (value= DateLiteral op= operator key= Identifier )
                            {
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:138:10: (value= DateLiteral op= operator key= Identifier )
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:138:11: value= DateLiteral op= operator key= Identifier
                            {
                            value=(Token)match(input,DateLiteral,FOLLOW_DateLiteral_in_binOpExpression310); 

                            pushFollow(FOLLOW_operator_in_binOpExpression317);
                            op=operator();

                            state._fsp--;


                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_binOpExpression323); 

                            }


                             isReverseOrder = true; 

                            }
                            break;

                    }


                     val = FilterLexer.ExtractDate(value.getText()); 

                    }
                    break;
                case 2 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:141:8: ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) )
                    {
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:141:8: ( (key= Identifier op= operator value= StringLiteral ) | (value= StringLiteral op= operator key= Identifier ) )
                    int alt6=2;
                    switch ( input.LA(1) ) {
                    case Identifier:
                        {
                        alt6=1;
                        }
                        break;
                    case StringLiteral:
                        {
                        alt6=2;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 0, input);

                        throw nvae;

                    }

                    switch (alt6) {
                        case 1 :
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:142:10: (key= Identifier op= operator value= StringLiteral )
                            {
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:142:10: (key= Identifier op= operator value= StringLiteral )
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:142:11: key= Identifier op= operator value= StringLiteral
                            {
                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_binOpExpression371); 

                            pushFollow(FOLLOW_operator_in_binOpExpression377);
                            op=operator();

                            state._fsp--;


                            value=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_binOpExpression384); 

                            }


                            }
                            break;
                        case 2 :
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:144:10: (value= StringLiteral op= operator key= Identifier )
                            {
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:144:10: (value= StringLiteral op= operator key= Identifier )
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:144:11: value= StringLiteral op= operator key= Identifier
                            {
                            value=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_binOpExpression412); 

                            pushFollow(FOLLOW_operator_in_binOpExpression419);
                            op=operator();

                            state._fsp--;


                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_binOpExpression425); 

                            }


                             isReverseOrder = true; 

                            }
                            break;

                    }


                     val = TrimQuotes(value.getText()); 

                    }
                    break;
                case 3 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:147:8: ( (key= Identifier op= operator value= IntegralLiteral ) | (value= IntegralLiteral op= operator key= Identifier ) )
                    {
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:147:8: ( (key= Identifier op= operator value= IntegralLiteral ) | (value= IntegralLiteral op= operator key= Identifier ) )
                    int alt7=2;
                    switch ( input.LA(1) ) {
                    case Identifier:
                        {
                        alt7=1;
                        }
                        break;
                    case IntegralLiteral:
                        {
                        alt7=2;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 0, input);

                        throw nvae;

                    }

                    switch (alt7) {
                        case 1 :
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:148:10: (key= Identifier op= operator value= IntegralLiteral )
                            {
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:148:10: (key= Identifier op= operator value= IntegralLiteral )
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:148:11: key= Identifier op= operator value= IntegralLiteral
                            {
                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_binOpExpression473); 

                            pushFollow(FOLLOW_operator_in_binOpExpression479);
                            op=operator();

                            state._fsp--;


                            value=(Token)match(input,IntegralLiteral,FOLLOW_IntegralLiteral_in_binOpExpression485); 

                            }


                            }
                            break;
                        case 2 :
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:150:10: (value= IntegralLiteral op= operator key= Identifier )
                            {
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:150:10: (value= IntegralLiteral op= operator key= Identifier )
                            // org/apache/hadoop/hive/metastore/parser/Filter.g:150:11: value= IntegralLiteral op= operator key= Identifier
                            {
                            value=(Token)match(input,IntegralLiteral,FOLLOW_IntegralLiteral_in_binOpExpression513); 

                            pushFollow(FOLLOW_operator_in_binOpExpression519);
                            op=operator();

                            state._fsp--;


                            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_binOpExpression525); 

                            }


                             isReverseOrder = true; 

                            }
                            break;

                    }


                     val = Long.parseLong(value.getText()); 

                    }
                    break;

            }



                    LeafNode node = new LeafNode();
                    node.keyName = key.getText();
                    node.value = val;
                    node.operator = op;
                    node.isReverseOrder = isReverseOrder;

                    tree.addLeafNode(node);
                

            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "binOpExpression"



    // $ANTLR start "operator"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:163:1: operator returns [Operator op] : t= ( LESSTHAN | LESSTHANOREQUALTO | GREATERTHAN | GREATERTHANOREQUALTO | KW_LIKE | EQUAL | NOTEQUAL ) ;
    public final Operator operator() throws RecognitionException {
        Operator op = null;


        Token t=null;

        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:164:4: (t= ( LESSTHAN | LESSTHANOREQUALTO | GREATERTHAN | GREATERTHANOREQUALTO | KW_LIKE | EQUAL | NOTEQUAL ) )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:165:4: t= ( LESSTHAN | LESSTHANOREQUALTO | GREATERTHAN | GREATERTHANOREQUALTO | KW_LIKE | EQUAL | NOTEQUAL )
            {
            t=(Token)input.LT(1);

            if ( (input.LA(1) >= EQUAL && input.LA(1) <= GREATERTHANOREQUALTO)||input.LA(1)==KW_LIKE||(input.LA(1) >= LESSTHAN && input.LA(1) <= LESSTHANOREQUALTO)||input.LA(1)==NOTEQUAL ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }



                  op = Operator.fromString(t.getText().toUpperCase());
               

            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return op;
    }
    // $ANTLR end "operator"



    // $ANTLR start "betweenExpression"
    // org/apache/hadoop/hive/metastore/parser/Filter.g:170:1: betweenExpression : (key= Identifier ( KW_NOT )? BETWEEN ( (left= DateLiteral KW_AND right= DateLiteral ) | (left= StringLiteral KW_AND right= StringLiteral ) | (left= IntegralLiteral KW_AND right= IntegralLiteral ) ) ) ;
    public final void betweenExpression() throws RecognitionException {
        Token key=null;
        Token left=null;
        Token right=null;


            Object leftV = null;
            Object rightV = null;
            boolean isPositive = true;

        try {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:176:5: ( (key= Identifier ( KW_NOT )? BETWEEN ( (left= DateLiteral KW_AND right= DateLiteral ) | (left= StringLiteral KW_AND right= StringLiteral ) | (left= IntegralLiteral KW_AND right= IntegralLiteral ) ) ) )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:177:5: (key= Identifier ( KW_NOT )? BETWEEN ( (left= DateLiteral KW_AND right= DateLiteral ) | (left= StringLiteral KW_AND right= StringLiteral ) | (left= IntegralLiteral KW_AND right= IntegralLiteral ) ) )
            {
            // org/apache/hadoop/hive/metastore/parser/Filter.g:177:5: (key= Identifier ( KW_NOT )? BETWEEN ( (left= DateLiteral KW_AND right= DateLiteral ) | (left= StringLiteral KW_AND right= StringLiteral ) | (left= IntegralLiteral KW_AND right= IntegralLiteral ) ) )
            // org/apache/hadoop/hive/metastore/parser/Filter.g:178:8: key= Identifier ( KW_NOT )? BETWEEN ( (left= DateLiteral KW_AND right= DateLiteral ) | (left= StringLiteral KW_AND right= StringLiteral ) | (left= IntegralLiteral KW_AND right= IntegralLiteral ) )
            {
            key=(Token)match(input,Identifier,FOLLOW_Identifier_in_betweenExpression638); 

            // org/apache/hadoop/hive/metastore/parser/Filter.g:178:25: ( KW_NOT )?
            int alt9=2;
            switch ( input.LA(1) ) {
                case KW_NOT:
                    {
                    alt9=1;
                    }
                    break;
            }

            switch (alt9) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:178:26: KW_NOT
                    {
                    match(input,KW_NOT,FOLLOW_KW_NOT_in_betweenExpression641); 

                     isPositive = false; 

                    }
                    break;

            }


            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression648); 

            // org/apache/hadoop/hive/metastore/parser/Filter.g:179:8: ( (left= DateLiteral KW_AND right= DateLiteral ) | (left= StringLiteral KW_AND right= StringLiteral ) | (left= IntegralLiteral KW_AND right= IntegralLiteral ) )
            int alt10=3;
            switch ( input.LA(1) ) {
            case DateLiteral:
                {
                alt10=1;
                }
                break;
            case StringLiteral:
                {
                alt10=2;
                }
                break;
            case IntegralLiteral:
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:180:10: (left= DateLiteral KW_AND right= DateLiteral )
                    {
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:180:10: (left= DateLiteral KW_AND right= DateLiteral )
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:180:11: left= DateLiteral KW_AND right= DateLiteral
                    {
                    left=(Token)match(input,DateLiteral,FOLLOW_DateLiteral_in_betweenExpression673); 

                    match(input,KW_AND,FOLLOW_KW_AND_in_betweenExpression675); 

                    right=(Token)match(input,DateLiteral,FOLLOW_DateLiteral_in_betweenExpression681); 

                    }



                                leftV = FilterLexer.ExtractDate(left.getText());
                                rightV = FilterLexer.ExtractDate(right.getText());
                             

                    }
                    break;
                case 2 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:185:10: (left= StringLiteral KW_AND right= StringLiteral )
                    {
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:185:10: (left= StringLiteral KW_AND right= StringLiteral )
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:185:11: left= StringLiteral KW_AND right= StringLiteral
                    {
                    left=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_betweenExpression711); 

                    match(input,KW_AND,FOLLOW_KW_AND_in_betweenExpression713); 

                    right=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_betweenExpression719); 

                    }


                     leftV = TrimQuotes(left.getText());
                                rightV = TrimQuotes(right.getText());
                             

                    }
                    break;
                case 3 :
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:189:10: (left= IntegralLiteral KW_AND right= IntegralLiteral )
                    {
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:189:10: (left= IntegralLiteral KW_AND right= IntegralLiteral )
                    // org/apache/hadoop/hive/metastore/parser/Filter.g:189:11: left= IntegralLiteral KW_AND right= IntegralLiteral
                    {
                    left=(Token)match(input,IntegralLiteral,FOLLOW_IntegralLiteral_in_betweenExpression749); 

                    match(input,KW_AND,FOLLOW_KW_AND_in_betweenExpression751); 

                    right=(Token)match(input,IntegralLiteral,FOLLOW_IntegralLiteral_in_betweenExpression757); 

                    }


                     leftV = Long.parseLong(left.getText());
                                rightV = Long.parseLong(right.getText());
                             

                    }
                    break;

            }


            }



                    LeafNode leftNode = new LeafNode(), rightNode = new LeafNode();
                    leftNode.keyName = rightNode.keyName = key.getText();
                    leftNode.value = leftV;
                    rightNode.value = rightV;
                    leftNode.operator = isPositive ? Operator.GREATERTHANOREQUALTO : Operator.LESSTHAN;
                    rightNode.operator = isPositive ? Operator.LESSTHANOREQUALTO : Operator.GREATERTHAN;
                    tree.addLeafNode(leftNode);
                    tree.addLeafNode(rightNode);
                    tree.addIntermediateNode(isPositive ? LogicalOperator.AND : LogicalOperator.OR);
                

            }

        }

          catch (RecognitionException e){
            throw e;
          }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "betweenExpression"

    // Delegated rules


 

    public static final BitSet FOLLOW_orExpression_in_filter84 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andExpression_in_orExpression106 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_KW_OR_in_orExpression109 = new BitSet(new long[]{0x0000000001101820L});
    public static final BitSet FOLLOW_andExpression_in_orExpression111 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_expression_in_andExpression137 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_KW_AND_in_andExpression140 = new BitSet(new long[]{0x0000000001101820L});
    public static final BitSet FOLLOW_expression_in_andExpression142 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_LPAREN_in_expression169 = new BitSet(new long[]{0x0000000001101820L});
    public static final BitSet FOLLOW_orExpression_in_expression171 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RPAREN_in_expression173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorExpression_in_expression185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_operatorExpression206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binOpExpression_in_operatorExpression218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_binOpExpression269 = new BitSet(new long[]{0x00000000004C8700L});
    public static final BitSet FOLLOW_operator_in_binOpExpression275 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DateLiteral_in_binOpExpression282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DateLiteral_in_binOpExpression310 = new BitSet(new long[]{0x00000000004C8700L});
    public static final BitSet FOLLOW_operator_in_binOpExpression317 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_Identifier_in_binOpExpression323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_binOpExpression371 = new BitSet(new long[]{0x00000000004C8700L});
    public static final BitSet FOLLOW_operator_in_binOpExpression377 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_StringLiteral_in_binOpExpression384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_binOpExpression412 = new BitSet(new long[]{0x00000000004C8700L});
    public static final BitSet FOLLOW_operator_in_binOpExpression419 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_Identifier_in_binOpExpression425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_binOpExpression473 = new BitSet(new long[]{0x00000000004C8700L});
    public static final BitSet FOLLOW_operator_in_binOpExpression479 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IntegralLiteral_in_binOpExpression485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IntegralLiteral_in_binOpExpression513 = new BitSet(new long[]{0x00000000004C8700L});
    public static final BitSet FOLLOW_operator_in_binOpExpression519 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_Identifier_in_binOpExpression525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_operator573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_betweenExpression638 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_KW_NOT_in_betweenExpression641 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression648 = new BitSet(new long[]{0x0000000001001020L});
    public static final BitSet FOLLOW_DateLiteral_in_betweenExpression673 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_KW_AND_in_betweenExpression675 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DateLiteral_in_betweenExpression681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_betweenExpression711 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_KW_AND_in_betweenExpression713 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_StringLiteral_in_betweenExpression719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IntegralLiteral_in_betweenExpression749 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_KW_AND_in_betweenExpression751 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IntegralLiteral_in_betweenExpression757 = new BitSet(new long[]{0x0000000000000002L});

}