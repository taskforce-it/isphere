/*****************************************************************************
      SQLJEP - Java SQL Expression Parser 0.2
      November 1 2006
         (c) Copyright 2006, Alexey Gaidukov
      SQLJEP Author: Alexey Gaidukov

      SQLJEP is based on JEP 2.24 (http://www.singularsys.com/jep/)
           (c) Copyright 2002, Nathan Funk
 
      See LICENSE.txt for license information.
*****************************************************************************/

package org.medfoster.sqljep;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormatSymbols;

import org.medfoster.sqljep.function.*;

/**
 * Base class for different SQLJEP classes. This class doesn't  know how to get source data.
 * <p/>
 * There are two types of variables in an expression. Columns of a abstarct table data and
 * external variables. Example of external variables are <CODE>TIMESTAMP,DATE,TIME,SYSDATE</CODE> variables.
 * <p/>
 * User should override methods {@link #findColumn(String name)} and {@link #getColumnObject(int column)} for defining
 * access to abstarct table data and the method {@link #getVariable(String name)} to access to external variables.
 * <p/>
 * To get a variable value at first {@link #getVariable(String name)} and after that {@link #findColumn(String name)} method is used.
 * <p/>
 * In expressions 'part_1.part2' names of variables are supported.
 * Parser is case sensitive.
 * <p/>
 * Visit <a href="http://sourceforge.net/projects/sqljep">http://sourceforge.net/projects/sqljep</a>
 * for the newest version of SQLJEP, and complete documentation.
 * @author Alexey Gaidukov
 */
public abstract class BaseJEP implements ParserVisitor {
	/** Debug flag for extra command line output */
	public static final boolean debug = false;
	
	/** Function Table */
	final public static HashMap<String, PostfixCommandI> funTab = new HashMap<String,PostfixCommandI>();
	
	static {
		//add functions to Function Table
		funTab.put("abs", new Abs());
		funTab.put("power", new Power());
		funTab.put("mod", new Modulus());
		funTab.put("substr", new Substring());
		funTab.put("sign", new Sign());
		funTab.put("ceil", new Ceil());
		funTab.put("floor", new Floor());
		funTab.put("trunc", new Trunc());
		funTab.put("round", new Round());
		funTab.put("length", new Length());
		funTab.put("concat", new Concat());
		funTab.put("instr", new Instr());
		funTab.put("trim", new Trim());
		funTab.put("rtrim", new Rtrim());
		funTab.put("ltrim", new Ltrim());
		funTab.put("rpad", new Rpad());
		funTab.put("lpad", new Lpad());
		funTab.put("lower", new Lower());
		funTab.put("upper", new Upper());
		funTab.put("translate", new Translate());
		funTab.put("replace", new Replace());
		funTab.put("initcap", new Initcap());
		funTab.put("value", new Nvl());
		funTab.put("decode", new Decode());
		funTab.put("to_char", new ToChar());
		funTab.put("to_number", new ToNumber());
		funTab.put("imatch", new IndistinctMatching());		// replacement for of Oracle's SOUNDEX
		funTab.put("months_between", new MonthsBetween());
		funTab.put("add_months", new AddMonths());
		funTab.put("last_day", new LastDay());
		funTab.put("next_day", new NextDay());
		funTab.put("to_date", new ToDate());
		funTab.put("case", new Case());			// replacement for CASE WHEN digit = 0 THEN ...;WHEN digit = 1 THEN ...;ELSE ... END CASE
		funTab.put("index", new Instr());						// maxdb
		funTab.put("num", new ToNumber());				// maxdb
		funTab.put("chr", new ToChar());				// maxdb
		funTab.put("dayname", new DayName());			// maxdb
		funTab.put("adddate", new AddDate());				// maxdb
		funTab.put("subdate", new SubDate());				// maxdb
		funTab.put("addtime", new AddTime());			// maxdb
		funTab.put("subtime", new SubTime());				// maxdb
		funTab.put("year", new Year());						// maxdb
		funTab.put("month", new Month());					// maxdb
		funTab.put("day", new Day());							// maxdb
		funTab.put("dayofmonth", new Day());				// maxdb
		funTab.put("hour", new Hour());						// maxdb
		funTab.put("minute", new Minute());					// maxdb
		funTab.put("second", new Second());				// maxdb
		funTab.put("microsecond", new Microsecond());	// maxdb
		funTab.put("datediff", new Datediff());				// maxdb
		funTab.put("dayofweek", new DayOfWeek());	// maxdb
		funTab.put("weekofyear", new WeekOfYear());	// maxdb
		funTab.put("dayofyear", new DayOfYear());		// maxdb
		funTab.put("dayname", new DayName());			// maxdb
		funTab.put("monthname", new MonthName());	// maxdb
		funTab.put("makedate", new MakeDate());		// maxdb
		funTab.put("maketime", new MakeTime());		// maxdb
	}
	
	/** Parse time error List */
	final protected ArrayList<String> errorList = new ArrayList<String>();

	/** Node at the top of the parse tree */
	protected Node topNode;

	/** Evaluator's data */
	final public JepRuntime runtime = new JepRuntime(this);
	
	protected String expression;
	
	/**
	 * Creates a new SQLJEP instance.
	 * Store String representation of the expression. <br/>
	 * To compile it use {@link #parseExpression} method.<br/>
	 * To evaluate use {@link #getValue} method.
	 */
	public BaseJEP(String exp) {
		if (exp == null) {
			throw new IllegalArgumentException("expression can be null");
		}
		expression = exp;
		topNode = null;
		runtime.calendar = JepRuntime.threadCalendar.get();
		if (runtime.calendar == null) {
			runtime.calendar = Calendar.getInstance();
			JepRuntime.threadCalendar.set(runtime.calendar);
		}
		runtime.dateSymbols = JepRuntime.threadDateFormatSymbols.get();
		if (runtime.dateSymbols == null) {
			runtime.dateSymbols = new DateFormatSymbols();
			JepRuntime.threadDateFormatSymbols.set(runtime.dateSymbols);
		}
	}

	/**
	* Change SQLJEP's state to initial state.
	* 
	*/
	public void clear() {
		topNode = null;
		errorList.clear();
	}
	
	/**
	* @return true if expresson is in compiled state otherwise false
	*/
	public boolean isValid() {
		return (topNode != null);
	}

	/**
	 * Returns the top node of the expression tree. Because all nodes are
	 * pointed to either directly or indirectly, the entire expression tree
	 * can be accessed through this node. It may be used to manipulate the
	 * expression, and subsequently evaluate it manually.
	 * <p/>
	 * 	Sometimes it is necessary to change value of a SQLJEP expression
	 * 	by some external reasons. Then following code can be applied
	 * <blockquote><pre>
	 * 		Node topNode = sqljep.getTopNode();
	 * 		if (topNode instanceof ASTVarNode) {
	 * 			((ASTVarNode)topNode).variable.setValue(value);
	 * 		}
	 * 		else {
	 * 			throw new org.medfoster.sqljep.ParseException("Expressions are not permitted");
	 * 		}
	 * </pre></blockquote>
	 * @return The top node of the expression tree
	 */
	public Node getTopNode() {
		return topNode;
	}
	
	/**
	 * method is used to get the value of the column in the current row of
	 * abstract table data.
	 * @return the column value
	 * @param column Number in the abstract table data
	 * @throws org.medfoster.sqljep.ParseException 
	 */
	public abstract Comparable getColumnObject(int column) throws ParseException;
	
	/**
	 * method is used to bind expression variables to columns of abstract table data.
	 * @return number of column in a row. Columns are numbered from 0.
	 * Negative number means column not found.
	 * @param name of the column in abstract table data
	 */
	public abstract int findColumn(String name);
	
	/**
	 * Defines the access to external variables
	 * <p/>
	 * Example of external variables are <CODE>TIMESTAMP,DATE,TIME,SYSDATE</CODE> variables.
	 * Values of them should be fixed before expression evaluation.
	 * <p/>
	 * Expression can be parsed once and executed many times.
	 * So it is necessary to change values of variables. 
	 * This method is used to get variables which cab be changed after parse.
	 * @param name Name of the variable to return
	 * @return Map.Entry variable with name and value. Null value means variable not found.
	 * @throws org.medfoster.sqljep.ParseException 
	 */
	public abstract Map.Entry getVariable(String name) throws ParseException;

	/**
	 * Adds a new function to the parser. This must be done before parsing
	 * an expression so the parser is aware that the new function may be
	 * contained in the expression.
	 * @param functionName The name of the function
	 * @param function The function object that is used for evaluating the
	 * function
	 */
	public static void addFunction(String functionName,
							PostfixCommandI function) {
		funTab.put(functionName, function);
	}

	/**
	 * Removes a function from the parser.
	 *
	 * @return If the function was added earlier, the function class instance
	 * is returned. If the function was not present, <code>null</code>
	 * is returned.
	 */
	public static Object removeFunction(String name) {
		return funTab.remove(name);
	}

	/**
	
	/**
	 * Returns true if an error occured during the most recent
	 * action (parsing or evaluation).
	 * @return Returns <code>true</code> if an error occured during the most
	 * recent action (parsing or evaluation).
	 */
	boolean hasError() {
		return !errorList.isEmpty();
	}

	/**
	 * Reports information on the errors that occured during the most recent
	 * action.
	 * @return A string containing information on the errors, each separated
	 * by a newline character; null if no error has occured
	 */
	String getErrorInfo() {
		if (hasError()) {
			String str = "";
			for (String err : errorList) {
				str += err + "\n";
			}
			return str;
		} else {
			return null;
		}
	}

	/**
	 * Parses the expression. 
	 * The root of the expression tree is returned by {@link #getTopNode()} method
	 * @throws org.medfoster.sqljep.ParseException If there are errors in the expression then it fires the exception
	 */
	final protected void parseExpression() throws ParseException {
		Reader reader = new StringReader(expression);
		Parser parser = new Parser(reader);
		try {
			// try parsing
			errorList.clear();
			topNode = parser.parseStream(reader, this);
		} catch (ParseException e) {
			// an exception was thrown, so there is no parse tree
			topNode = null;
			errorList.add(e.getMessage());
		} catch (Throwable e) {
			throw new org.medfoster.sqljep.ParseException(toString(), e);
		}
		
		if (hasError()) {
			throw new org.medfoster.sqljep.ParseException(getErrorInfo());
		}
		
		// If debug is enabled, print a dump of the tree to
		// standard output
		if (debug) {
			ParserVisitor v = new ParserDumpVisitor();
			try {
				topNode.jjtAccept(v, null);
			} catch (ParseException e) {
				errorList.add(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the value of the expression as an object. The expression
	 * tree is specified with its top node. The algorithm uses a stack 
	 * {@link org.medfoster.sqljep.JepRuntime#stack} for evaluation.
	 * <p>
	 * An exception is thrown, if an error occurs during evaluation.
	 * @return The value of the expression as an object.
	 */
	public Comparable getValue() throws ParseException {
		if (!isValid()) {
			throw new ParseException("Parser is not prepared");
		}
		if (!hasError()) {
			runtime.stack.setSize(0);
			// evaluate by letting the top node accept the visitor
			topNode.jjtAccept(this, null);
			
			// something is wrong if not exactly one item remains on the stack
			// or if the error flag has been set
			if (runtime.stack.size() != 1) {
				throw new ParseException("Wrong stack state. Stack size: "+runtime.stack.size());
			}
	
			// return the value of the expression
			return runtime.stack.pop();
		} else {
			throw new ParseException(getErrorInfo());
		}
	}

	/**
	 * Compares two objects for equality.
	 * Two SQLJEPs are equals only when their String representations are equals.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof BaseJEP) {
			BaseJEP f = (BaseJEP)obj;
			return expression.equals(f.expression);
		}
		return false;
	}

    /**
	 * Returns the expression string representation {@link #BaseJEP(String exp)}
	 */
	public String toString() {
		return expression;
	}

	/* ParserVisitor interface */
	
	/**
	 * This method should never be called when evaluation a normal
	 * expression.
	 */
	final public Object visit(SimpleNode node, Object data) throws ParseException {
		return null;
	}
	
	/**
	 * This method should never be called when evaluating a normal
	 * expression.
	 */
	final public Object visit(ASTStart node, Object data) throws ParseException {
		return null;
	}
	
	/**
	 * Visit a function node. The values of the child nodes
	 * are first pushed onto the stack. Then the function class associated
	 * with the node is used to evaluate the function.
	 */
	final public Object visit(ASTFunNode node, Object data) throws ParseException {
		PostfixCommandI pfmc = node.getPFMC();

		// check if the function class is set
		if (pfmc == null) {
			throw new ParseException("No function class associated with " + node.getName());
		}

		// evaluate all children (each leaves their result on the stack)

		if (debug) {
			System.out.println("Stack size after childrenAccept: " + runtime.stack.size());
		}
		pfmc.evaluate(node, runtime);
		if (debug) {
			System.out.println("Stack size after run: " + runtime.stack.size());
		}
		return null;
	}

	/**
	 * Visit a variable node. The value of the variable is obtained from the
	 * model and pushed onto the stack.
	 */
	final public Object visit(ASTVarNode node, Object data) throws ParseException {
		if (node.index >= 0) {
			runtime.stack.push(getColumnObject(node.index));
		} else {
			runtime.stack.push((Comparable)node.variable.getValue());
		}
		return null;
	}

	/**
	 * Visit a constant node. The value of the constant is pushed onto the
	 * stack.
	 */
	final public Object visit(ASTConstant node, Object data) throws ParseException {
		runtime.stack.push((Comparable)node.value);
		return null;
	}

	final public Object visit(ASTArray node, Object data) throws ParseException {
		node.childrenAccept(this, null);
		return null;
	}
}
