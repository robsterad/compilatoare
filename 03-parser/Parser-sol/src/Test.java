import lexer.CPLangLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.CPLangParser;
import parser.CPLangParserBaseVisitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;


public class Test {
    public static CPLangParser readFromFile(String filename) throws IOException {
        var input = CharStreams.fromFileName(filename);
        var lexer = new CPLangLexer(input);
        var tokenStream = new CommonTokenStream(lexer);

        // Decomentează pentru a afișa mulțimea de tokeni generată
        // după analiza lexicală pe fișierul de intrare:
/*
		tokenStream.fill();
		List<Token> tokens = tokenStream.getTokens();
		for (var token : tokens) {
		    var text = token.getText();
		    var type = CPLangLexer.VOCABULARY.getSymbolicName(token.getType());

		    System.out.println(text + " : " + type);
		}
*/

        return new CPLangParser(tokenStream);
    }

    public static void task1() throws IOException {
        // Fișierul manual.txt conține un program CPLang.
        // Fișierul reference1.txt poate fi folosit ca referință.
        var parser = readFromFile("manual.txt");

        /* TODO 1: înlocuiește expr cu regula de start din gramatică. */
        var tree = parser.prog();
        System.out.println(tree.toStringTree(parser));

    }

    public static void task2() throws IOException {
        // Fișierul input2.txt conține un program CPLang restricționat,
        // fără definiții, fără referiri la variabile sau funcții.
        // Conține o serie de instrucțiuni aritmetice, doar pentru literali
        // întregi.
        //
        // Fișierul reference2.txt conține output-ul corespunzător.
        var parser = readFromFile("input2.txt");

        // TODO 2: înlocuiește `expr` cu regula de start.
        var tree = parser.prog();

        Scanner s = new Scanner(new File("reference2.txt"));
        ArrayList<Integer> reference = new ArrayList<>();
        while (s.hasNextLine()) {
            reference.add(Integer.parseInt(s.nextLine()));
        }
        s.close();

        // Interfața CPLangParserVisitor conține câte o metodă pentru
        // fiecare alternativă etichetată. Spre exemplu, pentru eticheta
        // `arithmetic` avem metoda T visitArithmetic(ArithmeticContext).
        // Clasa CPLangParserBaseVisitor<T> oferă implementări implicite
        // ale acestor metode, a.î. noi să putem supradefini doar metodele
        // de interes.
        //
        // De remarcat că, spre deosebire de listeners, metodele de vizitare
        // pot întoarce și o valoare utilă, care poate fi interpretată
        // recursiv. Acest lucru, alături de faptul că putem vizita doar
        // nodurile de interes pentru noi, constituie avantajul vizitatorilor.
        // Dezavantajul constă în faptul că este necesară vizitarea explicită
        // a copiilor, mai ales când trebuie să parcurgem întregul arbore.
        //
        // TODO 2: Pentru acest exercițiu, programele CPLang vor conține doar
        //  expresii aritmetice cu literali numerici, fără definiții sau
        //  referiri la variabile!
        //  Pentru rezolvare trebuie suprascrise metodele visit, doar pentru
        //  etichetele corespunzătoare literalilor, operațiilor aritmetice și
        //  expresiilor parantezate. Fiecare metodă va întoarce un Integer.
        var visitor = new CPLangParserBaseVisitor<Integer>() {
            @Override
            public Integer visitProg(CPLangParser.ProgContext ctx) {
                int index = 0;
                for (var expr : ctx.expr()) {
                    Integer res = visit(expr);
                    System.out.println(expr.getText() + " = " + res);
                    var ref_res = reference.get(index++);
                    if (!Objects.equals(res, ref_res)) {
                        System.err.println("Your result : " + res + " is different from " + ref_res);
                    }
                }

                return 0;
            }

            // TODO 2: Suprascrie visit<etichetă> unde eticheta corespunde
            //  literalilor întregi, operației minus-unar, operațiilor aritmetice
            //  binare și expresiilor parantezate.

            @Override
            public Integer visitInt(CPLangParser.IntContext ctx) {
                return Integer.parseInt(ctx.getText());
            }

            @Override
            public Integer visitUnaryMinus(CPLangParser.UnaryMinusContext ctx) {
                return -visit(ctx.expr());
            }

            @Override
            public Integer visitArithmetic(CPLangParser.ArithmeticContext ctx) {
                Integer left = visit(ctx.left);
                Integer right = visit(ctx.right);
                String op = ctx.op.getText();
                return switch (op) {
                    case "+" -> left + right;
                    case "-" -> left - right;
                    case "*" -> left * right;
                    default -> left / right;
                };
            }

            @Override
            public Integer visitParen(CPLangParser.ParenContext ctx) {
                return visit(ctx.expr());
            }
        };

        visitor.visit(tree);
    }

    public static void main(String[] args) throws IOException {
        // TODO 1: decomentați pentru a testa exercițiul 1.
        task1();

        // TODO 2: decomentați pentru a testa exercițiul 2.
        task2();
    }
}
