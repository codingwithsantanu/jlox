package tool;

import java.util.List;
import java.util.Arrays;

public class GenerateAst {
    public static void main(String[] args) {
        defineExprAst();
    }

    // Pre-defined methods for defining AST classes.
    private static void defineExprAst() {
        defineAst("Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right", //
            "Variable : Token name",
            "Assign   : Token name, Expr value", //
            "Logical  : Expr left, Token operator, Expr right", //
            "Call     : Expr callee, Token paren, List<Expr> arguments",
            "Get      : Expr object, Token name",
            "Set      : Expr object, Token name, Expr value"
            //"This     : Token keyword", //
            //"Super    : Token keyword, Token method"
        ));
    }
    
    private static void defineStmtAst() {
        defineAst("Stmt", Arrays.asList(
            "Expression : Expr expression",
            "Print      : Expr expression",
            "Var        : Token name, Expr initializer", //
            "Block      : List<Stmt> statements",
            "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
            "While      : Expr condition, Stmt body", //
            "Function   : Token name, List<Token> params, List<Stmt> body",
            "Return     : Token keyword, Expr value", //
            "Class      : Token name, Expr.Variable superclass, List<Stmt.Function> methods"
        ));
    }


    // Main methods for defining the AST classes.
    private static void defineAst(String baseName, List<String> classes) {
        System.out.println("package lox;");
        System.out.println();
        System.out.println("import java.util.List;");
        System.out.println();
        System.out.println("abstract class " + baseName + " {");

        // Visitor interface.
        defineVisitorInterface(baseName, classes);

        // The AST classes.
        for (String branch : classes) {
            String className = branch.split(":")[0].trim();
            String fields = branch.split(":")[1].trim();
            defineBranch(baseName, className, fields);
        }

        // The Base accept() method.
        System.out.println();
        System.out.println("    abstract <R> R accept(Visitor<R> visitor);");

        System.out.println("}");
    }

    private static void defineVisitorInterface(String baseName, List<String> classes) {
        System.out.println("    interface Visitor<R> {");

        for (String branch : classes) {
            String branchName = branch.split(":")[0].trim();
            System.out.println("        R visit" + branchName + baseName +
                "(" + branchName + " " + branchName.toLowerCase() + ");"
            );
        }

        System.out.println("    }");
    }

    private static void defineBranch(String baseName, String className, String fieldList) {
        System.out.println();
        System.out.println("    static class " + className + " extends " + baseName + " {");

        // Fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            System.out.println("        final " + field + ";");
        }
        System.out.println();

        // Constructor.
        System.out.println("        " + className + "(" + fieldList + ") {");
        for (String field : fields) {
            String name = field.split(" ")[1];
            System.out.println("            this." + name + " = " + name + ";");
        }
        System.out.println("        }");

        // Visitor pattern.
        System.out.println();
        System.out.println("        @Override");
        System.out.println("        <R> R accept(Visitor<R> visitor) {");
        System.out.println("            return visitor.visit" + className + baseName + "(this);");
        System.out.println("        }");
        
        System.out.println("    }");
    }
}