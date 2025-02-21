package lox;

class AstPrinter implements Expr.Visitor<String> {
	public static void main(String[] args) {
		Expr expression = new Expr.Binary(
			new Expr.Unary(
				new Token(TokenType.MINUS, "-", null, 1),
				new Expr.Literal(123)
			),
			new Token(TokenType.STAR, "*", null, 1),
			new Expr.Grouping(
				new Expr.Literal(45.67)
			)
		);
	}

	String print(Expr expr) {
		return expr.accept(this);
	}

	// Main Expression visitors.
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
    	return parenthesize(
    		expr.operator.lexeme, expr.left, expr.right
        );
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
    	return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
    	if (expr.value == null)
    		return "nil";
    	return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
    	return parenthesize(expr.operator.lexeme, expr.right);
    }

    // Additional Expression visitors to prevent errors.
    @Override public String visitVariableExpr(Expr.Variable expr) { return ""; }
    @Override public String visitAssignExpr(Expr.Assign expr) { return ""; }
    @Override public String visitLogicalExpr(Expr.Logical expr) { return ""; }
    @Override public String visitCallExpr(Expr.Call expr) { return ""; }
    @Override public String visitGetExpr(Expr.Get expr) { return ""; }
    @Override public String visitSetExpr(Expr.Set expr) { return ""; }
    //@Override public String visitThisExpr(Expr.This expr) { return ""; }
    //@Override public String visitSuperExpr(Expr.Super expr) { return ""; }
    

    // Helper methods for clarity and modularity.
    private String parenthesize(String name, Expr... exprs) {
    	String body = "(" + name;
    	for (Expr expr : exprs) {
    		body += " " + expr.accept(this);
    	}
    	return body + ")";
    }
}