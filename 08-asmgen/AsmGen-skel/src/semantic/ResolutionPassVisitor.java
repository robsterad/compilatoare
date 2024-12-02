package semantic;

import parser.ASTNode;
import parser.ASTVisitor;

public class ResolutionPassVisitor implements ASTVisitor<TypeSymbol> {
    @Override
    public TypeSymbol visit(ASTNode.Program prog) {
        for (var stmt : prog.stmts) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.Id id) {
        // Verificăm dacă într-adevăr avem de-a face cu o variabilă
        // sau cu o funcție, al doilea caz constituind eroare.
        // Puteți folosi instanceof.
        var symbol = id.getScope().lookup(id.getToken().getText());

        if (symbol instanceof FunctionSymbol) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " is not a variable");
            return null;
        }

        // TODO 2: Întoarcem informația de tip salvată deja în simbol încă de la
        // definirea variabilei.
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.GlobalVarDef varDef) {

        return processVarStructure(varDef.id, varDef.initValue);
    }


    @Override
    public TypeSymbol visit(ASTNode.LocalVarDef varDef) {

        return processVarStructure(varDef.id, varDef.initValue);
    }

    @Override
    public TypeSymbol visit(ASTNode.FuncDef funcDef) {
        var returnType = funcDef.id.getSymbol().getType();
        var bodyType = funcDef.body.accept(this);

        // TODO 6: Verificăm dacă tipul de retur declarat este compatibil
        // cu cel al corpului.

        // Tipul unei definiții ca instrucțiune în sine nu este relevant.
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.Block block) {
        TypeSymbol lastType = null;
        for (var stmt : block.stmts) {
            lastType = stmt.accept(this);
        }
        // tipul unui bloc este tipul ultimei expresii din acesta
        return lastType;
    }

    @Override
    public TypeSymbol visit(ASTNode.Call call) {
        // Verificăm dacă funcția există în scope. Nu am putut face
        // asta în prima trecere din cauza a forward references.
        //
        // De asemenea, verificăm că Id-ul pe care se face apelul de funcție
        // este, într-adevăr, o funcție și nu o variabilă.
        //
        // Hint: pentru a obține scope-ul, putem folosi call.id.getScope(),
        // setat la trecerea anterioară.
        var id = call.id;
        var symbol = id.getScope().lookup(id.getToken().getText());

        if (symbol == null) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " function undefined");
            return null;
        }

        if (!(symbol instanceof FunctionSymbol)) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " is not a function");
            return null;
        }

        var functionSymbol = (FunctionSymbol) symbol;
        id.setSymbol(functionSymbol);

        // TODO 7: Verificați dacă numărul parametrilor actuali coincide
        // cu cel al parametrilor formali, și că tipurile sunt compatibile.

        var formals = functionSymbol.getFormals();

        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.Assign assign) {
        var idType = assign.id.accept(this);
        var exprType = assign.expr.accept(this);

        // TODO 6: Verificăm dacă expresia cu care se realizează atribuirea
        // are tipul potrivit cu cel declarat pentru variabilă.

        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.If iff) {
        var condType = iff.cond.accept(this);
        var thenType = iff.thenBranch.accept(this);
        var elseType = iff.elseBranch.accept(this);

        // TODO 4: Verificați tipurile celor 3 componente, afișați eroare
        // dacă este cazul, și precizați tipul expresiei.

        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.For aFor) {
        aFor.init.accept(this);

        var condType = aFor.cond.accept(this);
        // TODO 5: Verificați tipul conditiei si afișați eroare dacă este cazul


        aFor.step.accept(this);
        aFor.body.accept(this);

        // Prin convenție, valoarea buclei for este 0.
        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(ASTNode.Type type) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.FormalDef formal) {
        return formal.id.getSymbol().getType();
    }

    // Operații aritmetice si relationale.
    @Override
    public TypeSymbol visit(ASTNode.UnaryMinus uMinus) {
        var exprType = uMinus.expr.accept(this);

        // TODO 3: Verificăm tipul operandului, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.Arithmetic div) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        // Puteți afla felul operației din relational.getToken().getType(),
        // pe care îl puteți compara cu CPLangLexer.EQUAL etc.
        div.left.accept(this);
        div.right.accept(this);


        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.Relational op) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        // Puteți afla felul operației din relational.getToken().getType(),
        // pe care îl puteți compara cu CPLangLexer.EQUAL etc.
        op.left.accept(this);
        op.right.accept(this);


        return null;
    }

    // Tipurile de bază
    @Override
    public TypeSymbol visit(ASTNode.IntLiteral intt) {
        // TODO 2: Întoarcem tipul corect.
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.BoolLiteral bool) {
        // TODO 2: Întoarcem tipul corect.
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode.FloatLiteral floatNum) {
        // TODO 2: Întoarcem tipul corect.
        return null;
    }

    TypeSymbol processVarStructure(ASTNode.Id id, ASTNode.Expression initValue) {

        TypeSymbol declaredType = id.getSymbol().getType();
        if (initValue != null) {

            TypeSymbol initType = initValue.accept(this);

            // TODO 6: Verificăm dacă expresia de inițializare are tipul potrivit
            // cu cel declarat pentru variabilă.

        }


        // Tipul unei definiții ca instrucțiune în sine nu este relevant.
        return null;
    }

}
