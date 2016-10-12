package com.queryio.core.monitor.common.ext;

import java.util.ArrayList;
import java.util.Map;

import org.ks.cm.parser.Expression;
import org.ks.cm.parser.ExpressionException;
import org.ks.cm.parser.FunctionExpression;
import org.ks.cm.parser.NumberExpression;
import org.ks.cm.parser.OperatorExpression;
import org.ks.cm.parser.ParameterExpression;

import com.queryio.core.monitor.alerts.evaluator.AlertEvaluationManager;
import com.queryio.core.monitor.alerts.evaluator.ExpressionParser;

public class DurationFunctionExpression extends FunctionExpression
{
	private static final long serialVersionUID = 7526000057010002401L;
	private ExpressionParser expressionParser = new ExpressionParser();

	/**
	 * Constructor for TimeFunctionExpression.
	 */
	public DurationFunctionExpression()
	{
		super();
	}

	/**
	 * Constructor for TimeFunctionExpression.
	 * 
	 * @param args
	 */
	public DurationFunctionExpression(final Expression[] args)
	{
		super(args);
	}

	/**
	 * @see org.ks.cm.parser.Expression#calculate()
	 */
	public double calculate() throws ExpressionException
	{
		// duration(ruleId, func, duration, sub-exp)
		final String ruleKey = ((ParameterExpression) this.args[0]).getName();
		final long dataTimeStamp = (long) this.args[1].calculate();
		final String agFunction = ((ParameterExpression) this.args[2]).getName();
		final long durationInMilliSecs = (long) this.args[3].calculate();
		final double value = ((OperatorExpression)this.args[4]).getLeft().calculate();
		
		//System.out.println(ruleKey + " Function: " + agFunction + " time: " + dataTimeStamp + " value: " + value + " Sub-Expression: " + this.args[4].toString());
		Map ruleCache = AlertEvaluationManager.getDurationRulesCache();
		
		DurationCache cachedObject = (DurationCache)ruleCache.get(ruleKey);
		if (cachedObject == null)
		{
			cachedObject = new DurationCache();
			ruleCache.put(ruleKey, cachedObject);
		}
		
		// get the first time
		long startTime = cachedObject.peep();
		
		// add the new data that we received
		cachedObject.add(dataTimeStamp, value);

		if (startTime == -1 || startTime > dataTimeStamp - durationInMilliSecs)
		{
			// We do not have enough data to violate this rule as yet.
			//System.out.println(ruleKey + " does not have enough data"); 
			return 0.0;
		}
		else
		{
			// remove the unwanted data first
			int r = 0;
			while (startTime != -1 && startTime < dataTimeStamp - durationInMilliSecs)
			{
				cachedObject.pop();
				startTime = cachedObject.peep();
				r++;
			}
			
			// we have data for complete duration, so evaluate the rule now.
			String expression = this.args[4].toString();
			final String varName = "calVal";
			expression = varName + expression.substring(expression.indexOf(')') + 1);
			if (expression.charAt(expression.length() - 1) == ']')
			{
				expression = expression.substring(0, expression.length() - 1);
			}
			this.expressionParser.clearAll();
			this.expressionParser.addVariable("calVal", 0.0);
			this.expressionParser.parseExpression(expression);
			
			//System.out.println(ruleKey + " does have enough data, expression: " + expression + " time diff time: " + (((TimeValue)cachedObject.data.get(cachedObject.data.size() - 1)).time - startTime) + " removed: " + r); 
			
			int n = cachedObject.data.size();
			TimeValue tv;
			if (agFunction.equals("none"))
			{
				for (int i = 0; i < n; i ++)
				{
					tv = (TimeValue)cachedObject.data.get(i);
					this.expressionParser.addVariable(varName, tv.value);
					this.expressionParser.calculate();
					if (this.expressionParser.getValue() == 0.0)
					{
						return 0.0;
					}
				}
				if (n > 0)
				{
					return 1.0;
				}
			}
			else if (agFunction.equals("avg"))
			{
				double total = 0.0;
				for (int i = 0; i < n; i ++)
				{
					tv = (TimeValue)cachedObject.data.get(i);
					total += tv.value;
				}
				if (n > 0)
				{
					this.expressionParser.addVariable(varName, total/n);
					this.expressionParser.calculate();
					//System.out.println("Avg: " + (total/n) + " rule value: " + this.expressionParser.getValue());
					return this.expressionParser.getValue();
				}
			}
			else if (agFunction.equals("min"))
			{
				double min = Double.MAX_VALUE;
				for (int i = 0; i < n; i ++)
				{
					tv = (TimeValue)cachedObject.data.get(i);
					min = Math.min(min, tv.value);
				}
				if (n > 0)
				{
					this.expressionParser.addVariable(varName, min);
					this.expressionParser.calculate();
					return this.expressionParser.getValue();
				}
			}
			else if (agFunction.equals("max"))
			{
				double max = Double.MIN_VALUE;
				for (int i = 0; i < n; i ++)
				{
					tv = (TimeValue)cachedObject.data.get(i);
					max = Math.max(max, tv.value);
				}
				if (n > 0)
				{
					this.expressionParser.addVariable(varName, max);
					this.expressionParser.calculate();
					return this.expressionParser.getValue();
				}
			}
		}
		return 0.0;
	}

	/**
	 * Generates the key representing the expression.
	 * 
	 * @param expr
	 * @return
	 */
	public static String getKey(final Expression expr)
	{
		final StringBuffer sBuff = new StringBuffer();
		final boolean isParamExp = expr instanceof ParameterExpression;

		if (isParamExp)
		{
			sBuff.append(((ParameterExpression) expr).getName());
		}
		else if (expr instanceof NumberExpression)
		{
			sBuff.append(expr.toString());
		}
		else
		{
			sBuff.append(expr.getClass().getName());
		}
		if (!isParamExp)
		{
			final Expression[] expressions = expr.getSubexpressions();
			if ((expressions != null) && (expressions.length > 0))
			{
				for (int i = 0; i < expressions.length; i++)
				{
					sBuff.append(getKey(expressions[i]));
				}
			}
		}
		return sBuff.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer("duration(");
		sbToString.append(this.printArgs());
		sbToString.append(')');
		return sbToString.toString();
	}
	
	private class TimeValue
	{
		long time;
		double value;
		
		TimeValue(long ts, double val)
		{
			this.time = ts;
			this.value = val;
		}
		
	}
	
	private class DurationCache
	{
		ArrayList data = new ArrayList();
		
		long peep()
		{
			return data.size() > 0 ? ((TimeValue)data.get(0)).time: -1;
		}
		
		TimeValue pop()
		{
			if (data.size() > 0)
			{
				return (TimeValue)data.remove(0);
			}
			return null;
		}
		
		void add(long timeStamp, double value)
		{
			data.add(new TimeValue(timeStamp, value));
		}
		
		
	}
	
	
}
