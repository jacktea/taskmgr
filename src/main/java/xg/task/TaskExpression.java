package xg.task;

import java.util.ArrayList;
import java.util.List;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.parser.FelNode;
import com.greenpineyu.fel.parser.VarAstNode;

public class TaskExpression {

	private String expression;

	private FelEngine engine;

	private FelContext context;

	private Expression exp;

	public TaskExpression(String expression) {
		super();
		this.expression = expression;
		engine = FelEngine.instance;
		context = engine.getContext();
		for(String name : varNames()){
			context.set(name, false);
		}
		exp = engine.compile(expression, context);
	}

	public void addVar(String name, Object value) {
		context.set(name, value);
	}

	public Boolean eval() {
		return (Boolean) exp.eval(context);
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public List<String> varNames() {
		FelNode node = engine.parse(expression);
		List<String> out = new ArrayList<String>();
		visit(node,out);
		return out;
	}

	void visit(FelNode node,List<String> out) {
		if (node instanceof VarAstNode) {
			out.add(node.getText());
		}
		if (null != node.getChildren()){
			for (FelNode fn : node.getChildren()) {
				visit(fn,out);
			}
		}
	}

}
