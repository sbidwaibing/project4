
public class ParserImpl extends Parser {

    /*
     * Implements a recursive-descent parser for the following CFG:
     * 
     * T -> F AddOp T              { if ($2.type == TokenType.PLUS) { $$ = new PlusExpr($1,$3); } else { $$ = new MinusExpr($1, $3); } }
     * T -> F                      { $$ = $1; }
     * F -> Lit MulOp F            { if ($2.type == TokenType.Times) { $$ = new TimesExpr($1,$3); } else { $$ = new DivExpr($1, $3); } }
     * F -> Lit                    { $$ = $1; }
     * Lit -> NUM                  { $$ = new FloatExpr(Float.parseFloat($1.lexeme)); }
     * Lit -> LPAREN T RPAREN      { $$ = $2; }
     * AddOp -> PLUS               { $$ = $1; }
     * AddOp -> MINUS              { $$ = $1; }
     * MulOp -> TIMES              { $$ = $1; }
     * MulOp -> DIV                { $$ = $1; }
     */@Override
    public Expr do_parse() throws Exception {
        return parseT();
    }

    private Expr parseT() throws Exception {
        Expr expr = parseF();
        // Handle + or - operations while respecting parentheses
        while (peek(TokenType.PLUS, 0) || peek(TokenType.MINUS, 0)) {
            Token op = consume(peek(TokenType.PLUS, 0) ? TokenType.PLUS : TokenType.MINUS);
            Expr right = parseT(); // Recursively parse right-hand expression
            expr = (op.ty == TokenType.PLUS) ? new PlusExpr(expr, right) : new MinusExpr(expr, right);
        }
        return expr;
    }

    private Expr parseF() throws Exception {
        Expr expr;
        if (peek(TokenType.NUM, 0)) {
            Token numToken = consume(TokenType.NUM);
            expr = new FloatExpr(Float.parseFloat(numToken.lexeme));
        } else if (peek(TokenType.LPAREN, 0)) {
            consume(TokenType.LPAREN);
            expr = parseT(); // Parse the inner expression first
            consume(TokenType.RPAREN);
        } else {
            throw new Exception("Unexpected token: " + tokens.elem);
        }

        // Handle * or / operations
        while (peek(TokenType.TIMES, 0) || peek(TokenType.DIV, 0)) {
            Token op = consume(peek(TokenType.TIMES, 0) ? TokenType.TIMES : TokenType.DIV);
            Expr right = parseF();
            expr = (op.ty == TokenType.TIMES) ? new TimesExpr(expr, right) : new DivExpr(expr, right);
        }
        return expr;
    }
}
