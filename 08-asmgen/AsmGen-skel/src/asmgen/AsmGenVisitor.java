package asmgen;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import parser.*;

public class AsmGenVisitor implements ASTVisitor<ST> {
	static STGroupFile templates = new STGroupFile("asmgen/asmgen.stg");
	
	ST mainSection;	// filled directly (through visitor returns)
	ST dataSection; // filled collaterally ("global" access)
	ST funcSection; // filled collaterally ("global" access)

	/*
	 * TODO 1: Plain numbers
	 */

    @Override
    public ST visit(ASTNode.IntLiteral val) {
        return templates.getInstanceOf("literal")
                .add("value", val.getToken().getText());
    }
    
    @Override
    public ST visit(ASTNode.FloatLiteral val) {
        return null;
    }

    @Override
    public ST visit(ASTNode.BoolLiteral val) {
        return null;
    }
    
    /* 
     * TODO 1: Unary operations
     */
    
	@Override
	public ST visit(ASTNode.UnaryMinus uMinus) {
		return null;
	}
    
	/* 
	 * TODO 2: Binary operations
	 */
    
    @Override
    public ST visit(ASTNode.Arithmetic expr) {
		// TODO 2: Check actual operator
    	var st = templates.getInstanceOf("plus");
    	st.add("e1", expr.left.accept(this))
    		.add("e2",  expr.right.accept(this))
    		.add("dStr", expr.debugStr);
    	
    	return st;
    }

	@Override
	public ST visit(ASTNode.Relational expr) {
		return null;
	}

    /*
     * TODO 3: Control structures
     */
    
    @Override
	public ST visit(ASTNode.If iff) {
		return null;
	}

	@Override
	public ST visit(ASTNode.Call call) {
		return null;
	}

    /*
     * TODO 4: Variable definitions, assignments and blocks
     */

	@Override
	public ST visit(ASTNode.GlobalVarDef varDef) {
		// TODO 4: generare cod pentru main și etichetă în .data
		return null;
	}

	@Override
	public ST visit(ASTNode.LocalVarDef localVarDef) {
		// TODO 4: generare cod în blocul curent
		return null;
	}

	@Override
	public ST visit(ASTNode.Assign assign) {
		return null;
	}

	/*
	 * TODO 4: Blocks
	 */

	@Override
	public ST visit(ASTNode.Block block) {
		return null;
	}

	/*
	 * TODO 5: Function definitions
	 */

	@Override
	public ST visit(ASTNode.FuncDef funcDef) {
		// TODO 5: generare cod pentru funcSection. Fără cod în main()!
		return null;
	}

	@Override
	public ST visit(ASTNode.FormalDef formal) {
		// TODO 5
		return null;
	}

	/*
	 * TODO 5: Variable references
	 */
	
	@Override
	public ST visit(ASTNode.Id id) {
		// TODO 5
		return null;
	}

	/*
	 * TODO 6: For
	 */

	@Override
	public ST visit(ASTNode.For aFor) {
		return null;
	}

	@Override
	public ST visit(ASTNode.Type type) {
		return null;
	}

	@Override
	public ST visit(ASTNode.Program program) {
		dataSection = templates.getInstanceOf("sequenceSpaced");
		funcSection = templates.getInstanceOf("sequenceSpaced");
		mainSection = templates.getInstanceOf("sequence");
		
		for (ASTNode e : program.stmts)
			mainSection.add("e", e.accept(this));
		
		//assembly-ing it all together. HA! get it?
		var programST = templates.getInstanceOf("program");
		programST.add("data", dataSection);
		programST.add("textFuncs", funcSection);
		programST.add("textMain", mainSection);
		
		return programST;
	}

}
