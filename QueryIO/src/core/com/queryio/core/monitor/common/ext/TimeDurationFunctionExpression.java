package com.queryio.core.monitor.common.ext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.ks.cm.parser.Expression;
import org.ks.cm.parser.ExpressionException;
import org.ks.cm.parser.FunctionExpression;
import org.ks.cm.parser.InvalidArgumentsException;
import org.ks.cm.parser.NullExpression;
import org.ks.cm.parser.NumberExpression;
import org.ks.cm.parser.ParameterExpression;

public class TimeDurationFunctionExpression extends FunctionExpression
{
	private static final long serialVersionUID = 7526000057010002401L;
	/* constant representing default date format used */
	private static final String DATE_FORMAT = "dd_MM_yyyyHH_mm_ss";

	/* Hashtable storing the expressions which evaluated to true */
	private static Hashtable htDurations = new Hashtable();

	/**
	 * Constructor for TimeFunctionExpression.
	 */
	public TimeDurationFunctionExpression()
	{
		super();
	}

	/**
	 * Constructor for TimeFunctionExpression.
	 * 
	 * @param args
	 */
	public TimeDurationFunctionExpression(final Expression[] args)
	{
		super(args);
	}

	/**
	 * @see org.ks.cm.parser.Expression#calculate()
	 */
	public double calculate() throws ExpressionException
	{
		final int argLength = this.args.length;
		boolean invalid = true;

		// UtilityFunctions.getLogger().info("expression: " + toString());

		switch (this.args.length)
		{
			case 5:
			{
				if ((this.args[0] instanceof NumberExpression /* timeStamp */)
						&& ((this.args[1] instanceof NullExpression) || (this.args[1] instanceof ParameterExpression)) /* startTime */
						&& ((this.args[2] instanceof NullExpression) || (this.args[2] instanceof ParameterExpression)) /* endTime */
						&& (this.args[3] instanceof NumberExpression)) /* duration */
				{
					invalid = false;
				}
				break;
			}
			case 6:
			{
				if ((this.args[0] instanceof NumberExpression /* timeStamp */)
						&& (this.args[1] instanceof ParameterExpression /* date format */)
						&& ((this.args[2] instanceof NullExpression) || (this.args[2] instanceof ParameterExpression)) /* startTime */
						&& ((this.args[3] instanceof NullExpression) || (this.args[3] instanceof ParameterExpression)) /* endTime */
						&& (this.args[4] instanceof NumberExpression)) /* duration */
				{
					invalid = false;
				}
				break;
			}
			default:
		}

		if (invalid)
		{
			// UtilityFunctions.getLogger().info("expression is invalid");
			throw new InvalidArgumentsException();
		}

		String startTime = null;
		String endTime = null;
		long durationInMilliSecs = -1;

		/* first parameter is the current time */
		final long currTime = (long) this.args[0].calculate();

		/*
		 * if arg length == 5, default format is used, else the second parameter
		 * is date format
		 */
		final String format = (argLength == 5) ? DATE_FORMAT : ((ParameterExpression) this.args[1]).getName();

		if (argLength == 5)
		{
			/* default format is used, so start time is second parameter */
			startTime = (this.args[1] instanceof ParameterExpression ? ((ParameterExpression) this.args[1]).getName()
					: null);

			/* default format is used, so end time is third parameter */
			endTime = (this.args[2] instanceof ParameterExpression ? ((ParameterExpression) this.args[2]).getName()
					: null);

			/* default format is used, so duration is fourth parameter */
			durationInMilliSecs = (long) this.args[3].calculate();
		}
		else if (argLength == 6)
		{
			/* format is specified, so start time is third parameter */
			startTime = (this.args[2] instanceof ParameterExpression ? ((ParameterExpression) this.args[2]).getName()
					: null);

			/* format is specified, so end time is fourth parameter */
			endTime = (this.args[3] instanceof ParameterExpression ? ((ParameterExpression) this.args[3]).getName()
					: null);

			/* format is specified, so duration is fifth parameter */
			durationInMilliSecs = (long) this.args[4].calculate();
		}

		final SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date startDt = null;
		Date endDt = null;
		Date currDt = null;

		try
		{
			if ((startTime != null) && (startTime.trim().length() > 0))
			{
				startDt = ("_0_".equals(startTime) ? new Date(0) : sdf.parse(startTime));
			}

			if ((endTime != null) && (endTime.trim().length() > 0))
			{
				endDt = ("_0_".equals(endTime) ? new Date(0) : sdf.parse(endTime));
			}

			currDt = sdf.parse(sdf.format(new Date(currTime)));
		}
		catch (final ParseException e)
		{
			throw new InvalidArgumentsException();
		}

		if ((startDt != null) && (endDt != null))
		{
			// UtilityFunctions.getLogger().info("Start: " + startDt.getTime() +
			// " Current: " + currDt.getTime() + " End: " + endDt.getTime());

			// time has been specified hence we check if the current time is not
			// in between specified time return 0.0
			if ((startDt.getTime() > currDt.getTime() /* no need to check if startDt is > 0 */) || ((endDt.getTime() > 0) && (endDt.getTime() < currDt.getTime())))
			{
				return 0.0;
			}
		}

		/*
		 * either time is not specified or it is between the specified time
		 * interval calculate the expression if arg length == 5, default format
		 * is used, so expression is fifth parameter, else it is sixth parameter
		 */
		final double value = (argLength == 5) ? this.args[4].calculate() : this.args[5].calculate();

		// UtilityFunctions.getLogger().info("durationInMilliSecs: " +
		// durationInMilliSecs + " value: " + value);
		// check if duration is specified
		if (durationInMilliSecs <= 0)
		{
			return value;
		}

		// if the expression evaluates to false then remove from the static
		// Hashtable
		if (value == 0.0)
		{
			if (htDurations != null)
			{
				htDurations.remove(getKey((argLength == 5) ? this.args[4] : this.args[5]));
				// UtilityFunctions.getLogger().info("removing from hashtable: "
				// + htDurations);
			}
		}
		else
		{
			final String key = getKey((argLength == 5) ? this.args[4] : this.args[5]);
			// try to fetch whether this is an active failed rule last time this
			// fuction was evaluated
			Long ll = (Long) htDurations.get(key);
			if (ll == null)
			{
				// if this is the first time the expression has been evaluated
				// to true then put in the Hashtable
				ll = new Long(currTime);
				htDurations.put(key, ll);
				// UtilityFunctions.getLogger().info("adding to hashtable: " +
				// htDurations);
			}
			else
			{
				// if this is not the first time then get the long value when
				// this expression had evaluated to be true
				// for the first time.
				final long lastEvalToTrue = ll.longValue();

				// check whether the expression has evaluated to true for the
				// specified duration is yes then
				// set the evaluation of this function to true
				if ((currTime - lastEvalToTrue) > durationInMilliSecs)
				{
					return 1.0;
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
		final StringBuffer sbToString = new StringBuffer("timeduration(");
		sbToString.append(this.printArgs());
		sbToString.append(')');

		return sbToString.toString();
		// return "timeduration(" + printArgs() + ")";
	}
}
