package semantic;

import parser.ASTNode;
import parser.ASTVisitor;

public class DefinitionPassVisitor implements ASTVisitor<Void> {

    Scope currentScope = null;

    @Override
    public Void visit(ASTNode.Id id) {
        var symbol = currentScope.lookup(id.getToken().getText());

        id.setScope(currentScope);

        // Semnalăm eroare dacă nu există.
        if (symbol == null) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " undefined");
            return null;
        }

        // Atașăm simbolul nodului din arbore.
        id.setSymbol((IdSymbol) symbol);

        return null;
    }

    @Override
    public Void visit(ASTNode.IntLiteral intLiteral) {
        return null;
    }

    @Override
    public Void visit(ASTNode.If anIf) {
        anIf.cond.accept(this);
        anIf.thenBranch.accept(this);
        anIf.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visit(ASTNode.For aFor) {
        aFor.init.accept(this);
        aFor.cond.accept(this);
        aFor.step.accept(this);
        aFor.body.accept(this);

        return null;
    }

    @Override
    public Void visit(ASTNode.FloatLiteral floatLiteral) {
        return null;
    }

    @Override
    public Void visit(ASTNode.BoolLiteral boolLiteral) {
        return null;
    }

    @Override
    public Void visit(ASTNode.Assign assign) {
        assign.id.accept(this);
        assign.expr.accept(this);
        assign.id.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTNode.Relational relational) {
        return null;
    }

    @Override
    public Void visit(ASTNode.Arithmetic arithmetic) {
        arithmetic.left.accept(this);
        arithmetic.right.accept(this);
        return null;
    }

    @Override
    public Void visit(ASTNode.UnaryMinus unaryMinus) {
        unaryMinus.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(ASTNode.Call call) {
        var id = call.id;
        for (var arg : call.args) {
            arg.accept(this);
        }
        id.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTNode.Type type) {
        return null;
    }

    @Override
    public Void visit(ASTNode.FormalDef formalDef) {
        // TODO 2: La definirea unei funcții, parametrii formali pot fi tratați ca o definiție
        // de variabilă. Se va crea un nou simbol.
        // Adăugăm simbolul în domeniul de vizibilitate curent(practic domeniul de vizibilitate dat
        // de funcția al cărei parametru este parametrul formal curent).
        var id = formalDef.id;
        var symbol = new IdSymbol(id.getToken().getText());

        // TODO 3: Verificăm dacă parametrul deja există în scope-ul
        // curent.
        if (!currentScope.add(symbol)) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " redefined");
            return null;
        }

        id.setSymbol(symbol);
        id.setScope(currentScope);

        return null;
    }

    @Override
    public Void visit(ASTNode.LocalVarDef localVarDef) {

        return processVarStructure(localVarDef.id, localVarDef.initValue, false);
    }

    @Override
    public Void visit(ASTNode.GlobalVarDef globalVarDef) {

        return processVarStructure(globalVarDef.id, globalVarDef.initValue, true);
    }

    @Override
    public Void visit(ASTNode.FuncDef funcDef) {
        // TODO 2: Asemeni variabilelor globale, vom defini un nou simbol
        // pentru funcții. Acest nou FunctionSymbol va avea că părinte scope-ul
        // curent currentScope și va avea numele funcției.
        //
        // Nu uitați să updatati scope-ul curent înainte să fie parcurs corpul funcției,
        // și să îl restaurati la loc după ce acesta a fost parcurs.
        var id = funcDef.id;
        var functionSymbol = new FunctionSymbol(currentScope, id.getToken().getText());

        // TODO 3: Verificăm faptul că o funcție cu același nume nu a mai fost
        // definită până acum.
        if (!currentScope.add(functionSymbol)) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " function redefined");
            return null;
        }

        currentScope = functionSymbol;

        id.setSymbol(functionSymbol);
        id.setScope(currentScope);

        for (var formal : funcDef.formalDefs) {
            formal.accept(this);
        }

        funcDef.body.accept(this);

        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public Void visit(ASTNode.Block block) {
        // TODO 2: Nu uitați să updatati scope-ul curent înainte să fie parcurs corpul blocului,
        // și să îl restaurati la loc după ce acesta a fost parcurs.
        currentScope = new DefaultScope(currentScope);

        for (var stmt : block.stmts)
            stmt.accept(this);

        currentScope = currentScope.getParent();

        return null;

    }

    @Override
    public Void visit(ASTNode.Program program) {
        currentScope = new DefaultScope(null);
        for (var stmt : program.stmts) {
            stmt.accept(this);
        }
        return null;
    }

    Void processVarStructure(ASTNode.Id id, ASTNode.Expression initValue, boolean globalFlag) {
        // TODO 2: La definirea unei variabile, creăm un nou simbol.
        // Adăugăm simbolul în domeniul de vizibilitate curent.
        // Atașăm simbolul nodului din arbore.

        var symbol = new IdSymbol(id.getToken().getText());
        symbol.isGlobal = globalFlag;

        // TODO 3: Semnalăm eroare dacă există deja variabilă în scope-ul
        // curent.
        if (!currentScope.add(symbol)) {
            ASTVisitor.error(id.getToken(), id.getToken().getText() + " redefined");
            return null;
        }

        id.setSymbol(symbol);

        if (initValue != null)
            initValue.accept(this);

        return null;
    }

}