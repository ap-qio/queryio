/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.exec.vector.expressions.gen;

import static org.junit.Assert.assertEquals;
import java.util.Random;
import org.apache.hadoop.hive.ql.exec.vector.DoubleColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.exec.vector.util.VectorizedRowGroupGenUtil;
import org.junit.Test;


/**
 *
 * TestColumnScalarOperationVectorExpressionEvaluation.
 *
 */
public class TestColumnScalarOperationVectorExpressionEvaluation{

  private static final int BATCH_SIZE = 100;
  private static final long SEED = 0xfa57;

  
  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddIntervalYearMonthColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddIntervalYearMonthColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddIntervalYearMonthColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddIntervalYearMonthColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddIntervalYearMonthColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColSubtractIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColSubtractIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColSubtractIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColSubtractIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColSubtractIntervalYearMonthScalar vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarSubtractIntervalYearMonthColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarSubtractIntervalYearMonthColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarSubtractIntervalYearMonthColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarSubtractIntervalYearMonthColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarSubtractIntervalYearMonthColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddIntervalDayTimeColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddIntervalDayTimeColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddIntervalDayTimeColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddIntervalDayTimeColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddIntervalDayTimeColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColSubtractIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColSubtractIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColSubtractIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColSubtractIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColSubtractIntervalDayTimeScalar vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarSubtractIntervalDayTimeColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarSubtractIntervalDayTimeColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarSubtractIntervalDayTimeColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarSubtractIntervalDayTimeColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarSubtractIntervalDayTimeColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddTimestampScalar vectorExpression =
      new IntervalDayTimeColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddTimestampScalar vectorExpression =
      new IntervalDayTimeColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddTimestampScalar vectorExpression =
      new IntervalDayTimeColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddTimestampScalar vectorExpression =
      new IntervalDayTimeColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddTimestampScalar vectorExpression =
      new IntervalDayTimeColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddTimestampColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddTimestampColumn vectorExpression =
      new IntervalDayTimeScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddTimestampColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddTimestampColumn vectorExpression =
      new IntervalDayTimeScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddTimestampColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddTimestampColumn vectorExpression =
      new IntervalDayTimeScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddTimestampColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddTimestampColumn vectorExpression =
      new IntervalDayTimeScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddTimestampColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddTimestampColumn vectorExpression =
      new IntervalDayTimeScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalDayTimeScalar vectorExpression =
      new TimestampColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalDayTimeScalar vectorExpression =
      new TimestampColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalDayTimeScalar vectorExpression =
      new TimestampColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalDayTimeScalar vectorExpression =
      new TimestampColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalDayTimeScalar vectorExpression =
      new TimestampColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalDayTimeColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalDayTimeColumn vectorExpression =
      new TimestampScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalDayTimeColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalDayTimeColumn vectorExpression =
      new TimestampScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalDayTimeColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalDayTimeColumn vectorExpression =
      new TimestampScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalDayTimeColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalDayTimeColumn vectorExpression =
      new TimestampScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalDayTimeColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalDayTimeColumn vectorExpression =
      new TimestampScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalDayTimeScalar vectorExpression =
      new TimestampColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalDayTimeScalar vectorExpression =
      new TimestampColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalDayTimeScalar vectorExpression =
      new TimestampColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalDayTimeScalar vectorExpression =
      new TimestampColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalDayTimeScalar vectorExpression =
      new TimestampColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalDayTimeColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalDayTimeColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalDayTimeColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalDayTimeColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalDayTimeColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractTimestampScalar vectorExpression =
      new TimestampColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractTimestampScalar vectorExpression =
      new TimestampColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractTimestampScalar vectorExpression =
      new TimestampColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractTimestampScalar vectorExpression =
      new TimestampColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractTimestampScalar vectorExpression =
      new TimestampColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractTimestampColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractTimestampColumn vectorExpression =
      new TimestampScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractTimestampColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractTimestampColumn vectorExpression =
      new TimestampScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractTimestampColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractTimestampColumn vectorExpression =
      new TimestampScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractTimestampColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractTimestampColumn vectorExpression =
      new TimestampScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractTimestampColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractTimestampColumn vectorExpression =
      new TimestampScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractDateColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractDateColumn vectorExpression =
      new DateScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractDateColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractDateColumn vectorExpression =
      new DateScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractDateColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractDateColumn vectorExpression =
      new DateScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractDateColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractDateColumn vectorExpression =
      new DateScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractDateColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractDateColumn vectorExpression =
      new DateScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractDateScalar vectorExpression =
      new DateColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractDateScalar vectorExpression =
      new DateColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractDateScalar vectorExpression =
      new DateColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractDateScalar vectorExpression =
      new DateColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractDateScalar vectorExpression =
      new DateColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractTimestampColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractTimestampColumn vectorExpression =
      new DateScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractTimestampColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractTimestampColumn vectorExpression =
      new DateScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractTimestampColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractTimestampColumn vectorExpression =
      new DateScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractTimestampColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractTimestampColumn vectorExpression =
      new DateScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractTimestampColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractTimestampColumn vectorExpression =
      new DateScalarSubtractTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractTimestampScalar vectorExpression =
      new DateColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractTimestampScalar vectorExpression =
      new DateColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractTimestampScalar vectorExpression =
      new DateColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractTimestampScalar vectorExpression =
      new DateColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractTimestampScalar vectorExpression =
      new DateColSubtractTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractDateColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractDateColumn vectorExpression =
      new TimestampScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractDateColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractDateColumn vectorExpression =
      new TimestampScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractDateColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractDateColumn vectorExpression =
      new TimestampScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractDateColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractDateColumn vectorExpression =
      new TimestampScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractDateColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractDateColumn vectorExpression =
      new TimestampScalarSubtractDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractDateScalar vectorExpression =
      new TimestampColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractDateScalar vectorExpression =
      new TimestampColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractDateScalar vectorExpression =
      new TimestampColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractDateScalar vectorExpression =
      new TimestampColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractDateScalar vectorExpression =
      new TimestampColSubtractDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalDayTimeColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalDayTimeColumn vectorExpression =
      new DateScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalDayTimeColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalDayTimeColumn vectorExpression =
      new DateScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalDayTimeColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalDayTimeColumn vectorExpression =
      new DateScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalDayTimeColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalDayTimeColumn vectorExpression =
      new DateScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalDayTimeColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalDayTimeColumn vectorExpression =
      new DateScalarAddIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalDayTimeScalar vectorExpression =
      new DateColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalDayTimeScalar vectorExpression =
      new DateColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalDayTimeScalar vectorExpression =
      new DateColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalDayTimeScalar vectorExpression =
      new DateColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalDayTimeScalar vectorExpression =
      new DateColAddIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalDayTimeColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalDayTimeColumn vectorExpression =
      new DateScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalDayTimeColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalDayTimeColumn vectorExpression =
      new DateScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalDayTimeColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalDayTimeColumn vectorExpression =
      new DateScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalDayTimeColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalDayTimeColumn vectorExpression =
      new DateScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalDayTimeColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalDayTimeColumn vectorExpression =
      new DateScalarSubtractIntervalDayTimeColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalDayTimeScalar vectorExpression =
      new DateColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalDayTimeScalar vectorExpression =
      new DateColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalDayTimeScalar vectorExpression =
      new DateColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalDayTimeScalar vectorExpression =
      new DateColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalDayTimeScalar vectorExpression =
      new DateColSubtractIntervalDayTimeScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddDateColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddDateColumn vectorExpression =
      new IntervalDayTimeScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddDateColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddDateColumn vectorExpression =
      new IntervalDayTimeScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddDateColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddDateColumn vectorExpression =
      new IntervalDayTimeScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddDateColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddDateColumn vectorExpression =
      new IntervalDayTimeScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeScalarAddDateColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeScalarAddDateColumn vectorExpression =
      new IntervalDayTimeScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddDateScalar vectorExpression =
      new IntervalDayTimeColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddDateScalar vectorExpression =
      new IntervalDayTimeColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddDateScalar vectorExpression =
      new IntervalDayTimeColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddDateScalar vectorExpression =
      new IntervalDayTimeColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalDayTimeColAddDateScalar vectorExpression =
      new IntervalDayTimeColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalYearMonthColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalYearMonthColumn vectorExpression =
      new DateScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalYearMonthColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalYearMonthColumn vectorExpression =
      new DateScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalYearMonthColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalYearMonthColumn vectorExpression =
      new DateScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalYearMonthColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalYearMonthColumn vectorExpression =
      new DateScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarAddIntervalYearMonthColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarAddIntervalYearMonthColumn vectorExpression =
      new DateScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalYearMonthScalar vectorExpression =
      new DateColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalYearMonthScalar vectorExpression =
      new DateColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalYearMonthScalar vectorExpression =
      new DateColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalYearMonthScalar vectorExpression =
      new DateColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColAddIntervalYearMonthScalar vectorExpression =
      new DateColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalYearMonthColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalYearMonthColumn vectorExpression =
      new DateScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalYearMonthColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalYearMonthColumn vectorExpression =
      new DateScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalYearMonthColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalYearMonthColumn vectorExpression =
      new DateScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalYearMonthColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalYearMonthColumn vectorExpression =
      new DateScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateScalarSubtractIntervalYearMonthColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateScalarSubtractIntervalYearMonthColumn vectorExpression =
      new DateScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalYearMonthScalar vectorExpression =
      new DateColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalYearMonthScalar vectorExpression =
      new DateColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalYearMonthScalar vectorExpression =
      new DateColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalYearMonthScalar vectorExpression =
      new DateColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DateColSubtractIntervalYearMonthScalar vectorExpression =
      new DateColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalYearMonthColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalYearMonthColumn vectorExpression =
      new TimestampScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalYearMonthColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalYearMonthColumn vectorExpression =
      new TimestampScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalYearMonthColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalYearMonthColumn vectorExpression =
      new TimestampScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalYearMonthColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalYearMonthColumn vectorExpression =
      new TimestampScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarAddIntervalYearMonthColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarAddIntervalYearMonthColumn vectorExpression =
      new TimestampScalarAddIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalYearMonthScalar vectorExpression =
      new TimestampColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalYearMonthScalar vectorExpression =
      new TimestampColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalYearMonthScalar vectorExpression =
      new TimestampColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalYearMonthScalar vectorExpression =
      new TimestampColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColAddIntervalYearMonthScalar vectorExpression =
      new TimestampColAddIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalYearMonthColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalYearMonthColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalYearMonthColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalYearMonthColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampScalarSubtractIntervalYearMonthColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampScalarSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampScalarSubtractIntervalYearMonthColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalYearMonthScalar vectorExpression =
      new TimestampColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalYearMonthScalar vectorExpression =
      new TimestampColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalYearMonthScalar vectorExpression =
      new TimestampColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalYearMonthScalar vectorExpression =
      new TimestampColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    TimestampColSubtractIntervalYearMonthScalar vectorExpression =
      new TimestampColSubtractIntervalYearMonthScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddDateColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddDateColumn vectorExpression =
      new IntervalYearMonthScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddDateColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddDateColumn vectorExpression =
      new IntervalYearMonthScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddDateColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddDateColumn vectorExpression =
      new IntervalYearMonthScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddDateColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddDateColumn vectorExpression =
      new IntervalYearMonthScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddDateColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddDateColumn vectorExpression =
      new IntervalYearMonthScalarAddDateColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddDateScalar vectorExpression =
      new IntervalYearMonthColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddDateScalar vectorExpression =
      new IntervalYearMonthColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddDateScalar vectorExpression =
      new IntervalYearMonthColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddDateScalar vectorExpression =
      new IntervalYearMonthColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddDateScalar vectorExpression =
      new IntervalYearMonthColAddDateScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddTimestampColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddTimestampColumn vectorExpression =
      new IntervalYearMonthScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddTimestampColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddTimestampColumn vectorExpression =
      new IntervalYearMonthScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddTimestampColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddTimestampColumn vectorExpression =
      new IntervalYearMonthScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddTimestampColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddTimestampColumn vectorExpression =
      new IntervalYearMonthScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthScalarAddTimestampColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthScalarAddTimestampColumn vectorExpression =
      new IntervalYearMonthScalarAddTimestampColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddTimestampScalar vectorExpression =
      new IntervalYearMonthColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddTimestampScalar vectorExpression =
      new IntervalYearMonthColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddTimestampScalar vectorExpression =
      new IntervalYearMonthColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddTimestampScalar vectorExpression =
      new IntervalYearMonthColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    IntervalYearMonthColAddTimestampScalar vectorExpression =
      new IntervalYearMonthColAddTimestampScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColAddLongScalar vectorExpression =
      new LongColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColAddLongScalar vectorExpression =
      new LongColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColAddLongScalar vectorExpression =
      new LongColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColAddLongScalar vectorExpression =
      new LongColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColAddLongScalar vectorExpression =
      new LongColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColSubtractLongScalar vectorExpression =
      new LongColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColSubtractLongScalar vectorExpression =
      new LongColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColSubtractLongScalar vectorExpression =
      new LongColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColSubtractLongScalar vectorExpression =
      new LongColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColSubtractLongScalar vectorExpression =
      new LongColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColMultiplyLongScalar vectorExpression =
      new LongColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColMultiplyLongScalar vectorExpression =
      new LongColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColMultiplyLongScalar vectorExpression =
      new LongColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColMultiplyLongScalar vectorExpression =
      new LongColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColMultiplyLongScalar vectorExpression =
      new LongColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColAddDoubleScalar vectorExpression =
      new LongColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColAddDoubleScalar vectorExpression =
      new LongColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColAddDoubleScalar vectorExpression =
      new LongColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColAddDoubleScalar vectorExpression =
      new LongColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColAddDoubleScalar vectorExpression =
      new LongColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColSubtractDoubleScalar vectorExpression =
      new LongColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColSubtractDoubleScalar vectorExpression =
      new LongColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColSubtractDoubleScalar vectorExpression =
      new LongColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColSubtractDoubleScalar vectorExpression =
      new LongColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColSubtractDoubleScalar vectorExpression =
      new LongColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColMultiplyDoubleScalar vectorExpression =
      new LongColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColMultiplyDoubleScalar vectorExpression =
      new LongColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColMultiplyDoubleScalar vectorExpression =
      new LongColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColMultiplyDoubleScalar vectorExpression =
      new LongColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColMultiplyDoubleScalar vectorExpression =
      new LongColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColAddLongScalar vectorExpression =
      new DoubleColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColAddLongScalar vectorExpression =
      new DoubleColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColAddLongScalar vectorExpression =
      new DoubleColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColAddLongScalar vectorExpression =
      new DoubleColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColAddLongScalar vectorExpression =
      new DoubleColAddLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColSubtractLongScalar vectorExpression =
      new DoubleColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColSubtractLongScalar vectorExpression =
      new DoubleColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColSubtractLongScalar vectorExpression =
      new DoubleColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColSubtractLongScalar vectorExpression =
      new DoubleColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColSubtractLongScalar vectorExpression =
      new DoubleColSubtractLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColMultiplyLongScalar vectorExpression =
      new DoubleColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColMultiplyLongScalar vectorExpression =
      new DoubleColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColMultiplyLongScalar vectorExpression =
      new DoubleColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColMultiplyLongScalar vectorExpression =
      new DoubleColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColMultiplyLongScalar vectorExpression =
      new DoubleColMultiplyLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColAddDoubleScalar vectorExpression =
      new DoubleColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColAddDoubleScalar vectorExpression =
      new DoubleColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColAddDoubleScalar vectorExpression =
      new DoubleColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColAddDoubleScalar vectorExpression =
      new DoubleColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColAddDoubleScalar vectorExpression =
      new DoubleColAddDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColSubtractDoubleScalar vectorExpression =
      new DoubleColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColSubtractDoubleScalar vectorExpression =
      new DoubleColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColSubtractDoubleScalar vectorExpression =
      new DoubleColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColSubtractDoubleScalar vectorExpression =
      new DoubleColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColSubtractDoubleScalar vectorExpression =
      new DoubleColSubtractDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColMultiplyDoubleScalar vectorExpression =
      new DoubleColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColMultiplyDoubleScalar vectorExpression =
      new DoubleColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColMultiplyDoubleScalar vectorExpression =
      new DoubleColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColMultiplyDoubleScalar vectorExpression =
      new DoubleColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColMultiplyDoubleScalar vectorExpression =
      new DoubleColMultiplyDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddLongColumn vectorExpression =
      new LongScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddLongColumn vectorExpression =
      new LongScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddLongColumn vectorExpression =
      new LongScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddLongColumn vectorExpression =
      new LongScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddLongColumn vectorExpression =
      new LongScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractLongColumn vectorExpression =
      new LongScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractLongColumn vectorExpression =
      new LongScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractLongColumn vectorExpression =
      new LongScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractLongColumn vectorExpression =
      new LongScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractLongColumn vectorExpression =
      new LongScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyLongColumn vectorExpression =
      new LongScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyLongColumn vectorExpression =
      new LongScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyLongColumn vectorExpression =
      new LongScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyLongColumn vectorExpression =
      new LongScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyLongColumn vectorExpression =
      new LongScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddDoubleColumn vectorExpression =
      new LongScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddDoubleColumn vectorExpression =
      new LongScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddDoubleColumn vectorExpression =
      new LongScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddDoubleColumn vectorExpression =
      new LongScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarAddDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarAddDoubleColumn vectorExpression =
      new LongScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractDoubleColumn vectorExpression =
      new LongScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractDoubleColumn vectorExpression =
      new LongScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractDoubleColumn vectorExpression =
      new LongScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractDoubleColumn vectorExpression =
      new LongScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarSubtractDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarSubtractDoubleColumn vectorExpression =
      new LongScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyDoubleColumn vectorExpression =
      new LongScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyDoubleColumn vectorExpression =
      new LongScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyDoubleColumn vectorExpression =
      new LongScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyDoubleColumn vectorExpression =
      new LongScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarMultiplyDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarMultiplyDoubleColumn vectorExpression =
      new LongScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddLongColumn vectorExpression =
      new DoubleScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddLongColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddLongColumn vectorExpression =
      new DoubleScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddLongColumn vectorExpression =
      new DoubleScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddLongColumn vectorExpression =
      new DoubleScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddLongColumn vectorExpression =
      new DoubleScalarAddLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractLongColumn vectorExpression =
      new DoubleScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractLongColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractLongColumn vectorExpression =
      new DoubleScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractLongColumn vectorExpression =
      new DoubleScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractLongColumn vectorExpression =
      new DoubleScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractLongColumn vectorExpression =
      new DoubleScalarSubtractLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyLongColumn vectorExpression =
      new DoubleScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyLongColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyLongColumn vectorExpression =
      new DoubleScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyLongColumn vectorExpression =
      new DoubleScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyLongColumn vectorExpression =
      new DoubleScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyLongColumn vectorExpression =
      new DoubleScalarMultiplyLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddDoubleColumn vectorExpression =
      new DoubleScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddDoubleColumn vectorExpression =
      new DoubleScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddDoubleColumn vectorExpression =
      new DoubleScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddDoubleColumn vectorExpression =
      new DoubleScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarAddDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarAddDoubleColumn vectorExpression =
      new DoubleScalarAddDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractDoubleColumn vectorExpression =
      new DoubleScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractDoubleColumn vectorExpression =
      new DoubleScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractDoubleColumn vectorExpression =
      new DoubleScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractDoubleColumn vectorExpression =
      new DoubleScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarSubtractDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarSubtractDoubleColumn vectorExpression =
      new DoubleScalarSubtractDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyDoubleColumn vectorExpression =
      new DoubleScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyDoubleColumn vectorExpression =
      new DoubleScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyDoubleColumn vectorExpression =
      new DoubleScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyDoubleColumn vectorExpression =
      new DoubleScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarMultiplyDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarMultiplyDoubleColumn vectorExpression =
      new DoubleScalarMultiplyDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColDivideDoubleScalar vectorExpression =
      new LongColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColDivideDoubleScalar vectorExpression =
      new LongColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColDivideDoubleScalar vectorExpression =
      new LongColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColDivideDoubleScalar vectorExpression =
      new LongColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColDivideDoubleScalar vectorExpression =
      new LongColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColDivideLongScalar vectorExpression =
      new DoubleColDivideLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColDivideLongScalar vectorExpression =
      new DoubleColDivideLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColDivideLongScalar vectorExpression =
      new DoubleColDivideLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColDivideLongScalar vectorExpression =
      new DoubleColDivideLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColDivideLongScalar vectorExpression =
      new DoubleColDivideLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColDivideDoubleScalar vectorExpression =
      new DoubleColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColDivideDoubleScalar vectorExpression =
      new DoubleColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColDivideDoubleScalar vectorExpression =
      new DoubleColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColDivideDoubleScalar vectorExpression =
      new DoubleColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColDivideDoubleScalar vectorExpression =
      new DoubleColDivideDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarDivideDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarDivideDoubleColumn vectorExpression =
      new LongScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarDivideDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarDivideDoubleColumn vectorExpression =
      new LongScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarDivideDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarDivideDoubleColumn vectorExpression =
      new LongScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarDivideDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarDivideDoubleColumn vectorExpression =
      new LongScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarDivideDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarDivideDoubleColumn vectorExpression =
      new LongScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideLongColumn vectorExpression =
      new DoubleScalarDivideLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideLongColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideLongColumn vectorExpression =
      new DoubleScalarDivideLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideLongColumn vectorExpression =
      new DoubleScalarDivideLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideLongColumn vectorExpression =
      new DoubleScalarDivideLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideLongColumn vectorExpression =
      new DoubleScalarDivideLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideDoubleColumn vectorExpression =
      new DoubleScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideDoubleColumn vectorExpression =
      new DoubleScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideDoubleColumn vectorExpression =
      new DoubleScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideDoubleColumn vectorExpression =
      new DoubleScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarDivideDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarDivideDoubleColumn vectorExpression =
      new DoubleScalarDivideDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColModuloLongScalar vectorExpression =
      new LongColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColModuloLongScalar vectorExpression =
      new LongColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColModuloLongScalar vectorExpression =
      new LongColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColModuloLongScalar vectorExpression =
      new LongColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColModuloLongScalar vectorExpression =
      new LongColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColModuloDoubleScalar vectorExpression =
      new LongColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColModuloDoubleScalar vectorExpression =
      new LongColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColModuloDoubleScalar vectorExpression =
      new LongColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColModuloDoubleScalar vectorExpression =
      new LongColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColModuloDoubleScalar vectorExpression =
      new LongColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColModuloLongScalar vectorExpression =
      new DoubleColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColModuloLongScalar vectorExpression =
      new DoubleColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColModuloLongScalar vectorExpression =
      new DoubleColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColModuloLongScalar vectorExpression =
      new DoubleColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColModuloLongScalar vectorExpression =
      new DoubleColModuloLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColModuloDoubleScalar vectorExpression =
      new DoubleColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleScalar() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColModuloDoubleScalar vectorExpression =
      new DoubleColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColModuloDoubleScalar vectorExpression =
      new DoubleColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColModuloDoubleScalar vectorExpression =
      new DoubleColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColModuloDoubleScalar vectorExpression =
      new DoubleColModuloDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloLongColumn vectorExpression =
      new LongScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloLongColumn vectorExpression =
      new LongScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloLongColumn vectorExpression =
      new LongScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloLongColumn vectorExpression =
      new LongScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloLongColumn vectorExpression =
      new LongScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloDoubleColumn vectorExpression =
      new LongScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloDoubleColumn vectorExpression =
      new LongScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloDoubleColumn vectorExpression =
      new LongScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloDoubleColumn vectorExpression =
      new LongScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarModuloDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarModuloDoubleColumn vectorExpression =
      new LongScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloLongColumn vectorExpression =
      new DoubleScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloLongColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloLongColumn vectorExpression =
      new DoubleScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloLongColumn vectorExpression =
      new DoubleScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloLongColumn vectorExpression =
      new DoubleScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloLongColumn vectorExpression =
      new DoubleScalarModuloLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloDoubleColumn vectorExpression =
      new DoubleScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloDoubleColumn() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloDoubleColumn vectorExpression =
      new DoubleScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloDoubleColumn vectorExpression =
      new DoubleScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloDoubleColumn vectorExpression =
      new DoubleScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarModuloDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarModuloDoubleColumn vectorExpression =
      new DoubleScalarModuloDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColEqualDoubleScalar vectorExpression =
      new LongColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColEqualDoubleScalar vectorExpression =
      new LongColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColEqualDoubleScalar vectorExpression =
      new LongColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColEqualDoubleScalar vectorExpression =
      new LongColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColEqualDoubleScalar vectorExpression =
      new LongColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColEqualDoubleScalar vectorExpression =
      new DoubleColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColEqualDoubleScalar vectorExpression =
      new DoubleColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColEqualDoubleScalar vectorExpression =
      new DoubleColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColEqualDoubleScalar vectorExpression =
      new DoubleColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColEqualDoubleScalar vectorExpression =
      new DoubleColEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColNotEqualDoubleScalar vectorExpression =
      new LongColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColNotEqualDoubleScalar vectorExpression =
      new LongColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColNotEqualDoubleScalar vectorExpression =
      new LongColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColNotEqualDoubleScalar vectorExpression =
      new LongColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColNotEqualDoubleScalar vectorExpression =
      new LongColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColNotEqualDoubleScalar vectorExpression =
      new DoubleColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColNotEqualDoubleScalar vectorExpression =
      new DoubleColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColNotEqualDoubleScalar vectorExpression =
      new DoubleColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColNotEqualDoubleScalar vectorExpression =
      new DoubleColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColNotEqualDoubleScalar vectorExpression =
      new DoubleColNotEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessDoubleScalar vectorExpression =
      new LongColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessDoubleScalar vectorExpression =
      new LongColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessDoubleScalar vectorExpression =
      new LongColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessDoubleScalar vectorExpression =
      new LongColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessDoubleScalar vectorExpression =
      new LongColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessDoubleScalar vectorExpression =
      new DoubleColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessDoubleScalar vectorExpression =
      new DoubleColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessDoubleScalar vectorExpression =
      new DoubleColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessDoubleScalar vectorExpression =
      new DoubleColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessDoubleScalar vectorExpression =
      new DoubleColLessDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessEqualDoubleScalar vectorExpression =
      new LongColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessEqualDoubleScalar vectorExpression =
      new LongColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessEqualDoubleScalar vectorExpression =
      new LongColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessEqualDoubleScalar vectorExpression =
      new LongColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColLessEqualDoubleScalar vectorExpression =
      new LongColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessEqualDoubleScalar vectorExpression =
      new DoubleColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessEqualDoubleScalar vectorExpression =
      new DoubleColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessEqualDoubleScalar vectorExpression =
      new DoubleColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessEqualDoubleScalar vectorExpression =
      new DoubleColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColLessEqualDoubleScalar vectorExpression =
      new DoubleColLessEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterDoubleScalar vectorExpression =
      new LongColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterDoubleScalar vectorExpression =
      new LongColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterDoubleScalar vectorExpression =
      new LongColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterDoubleScalar vectorExpression =
      new LongColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterDoubleScalar vectorExpression =
      new LongColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterDoubleScalar vectorExpression =
      new DoubleColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterDoubleScalar vectorExpression =
      new DoubleColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterDoubleScalar vectorExpression =
      new DoubleColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterDoubleScalar vectorExpression =
      new DoubleColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterDoubleScalar vectorExpression =
      new DoubleColGreaterDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterEqualDoubleScalar vectorExpression =
      new LongColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterEqualDoubleScalar vectorExpression =
      new LongColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterEqualDoubleScalar vectorExpression =
      new LongColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterEqualDoubleScalar vectorExpression =
      new LongColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    LongColGreaterEqualDoubleScalar vectorExpression =
      new LongColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterEqualDoubleScalar vectorExpression =
      new DoubleColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterEqualDoubleScalar vectorExpression =
      new DoubleColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterEqualDoubleScalar vectorExpression =
      new DoubleColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterEqualDoubleScalar vectorExpression =
      new DoubleColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleColGreaterEqualDoubleScalar vectorExpression =
      new DoubleColGreaterEqualDoubleScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColEqualLongScalar vectorExpression =
      new LongColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColEqualLongScalar vectorExpression =
      new LongColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColEqualLongScalar vectorExpression =
      new LongColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColEqualLongScalar vectorExpression =
      new LongColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColEqualLongScalar vectorExpression =
      new LongColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColEqualLongScalar vectorExpression =
      new DoubleColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColEqualLongScalar vectorExpression =
      new DoubleColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColEqualLongScalar vectorExpression =
      new DoubleColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColEqualLongScalar vectorExpression =
      new DoubleColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColEqualLongScalar vectorExpression =
      new DoubleColEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColNotEqualLongScalar vectorExpression =
      new LongColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColNotEqualLongScalar vectorExpression =
      new LongColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColNotEqualLongScalar vectorExpression =
      new LongColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColNotEqualLongScalar vectorExpression =
      new LongColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColNotEqualLongScalar vectorExpression =
      new LongColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColNotEqualLongScalar vectorExpression =
      new DoubleColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColNotEqualLongScalar vectorExpression =
      new DoubleColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColNotEqualLongScalar vectorExpression =
      new DoubleColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColNotEqualLongScalar vectorExpression =
      new DoubleColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColNotEqualLongScalar vectorExpression =
      new DoubleColNotEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessLongScalar vectorExpression =
      new LongColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessLongScalar vectorExpression =
      new LongColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessLongScalar vectorExpression =
      new LongColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessLongScalar vectorExpression =
      new LongColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessLongScalar vectorExpression =
      new LongColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessLongScalar vectorExpression =
      new DoubleColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessLongScalar vectorExpression =
      new DoubleColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessLongScalar vectorExpression =
      new DoubleColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessLongScalar vectorExpression =
      new DoubleColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessLongScalar vectorExpression =
      new DoubleColLessLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessEqualLongScalar vectorExpression =
      new LongColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessEqualLongScalar vectorExpression =
      new LongColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessEqualLongScalar vectorExpression =
      new LongColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessEqualLongScalar vectorExpression =
      new LongColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColLessEqualLongScalar vectorExpression =
      new LongColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessEqualLongScalar vectorExpression =
      new DoubleColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessEqualLongScalar vectorExpression =
      new DoubleColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessEqualLongScalar vectorExpression =
      new DoubleColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessEqualLongScalar vectorExpression =
      new DoubleColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColLessEqualLongScalar vectorExpression =
      new DoubleColLessEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterLongScalar vectorExpression =
      new LongColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterLongScalar vectorExpression =
      new LongColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterLongScalar vectorExpression =
      new LongColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterLongScalar vectorExpression =
      new LongColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterLongScalar vectorExpression =
      new LongColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterLongScalar vectorExpression =
      new DoubleColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterLongScalar vectorExpression =
      new DoubleColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterLongScalar vectorExpression =
      new DoubleColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterLongScalar vectorExpression =
      new DoubleColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterLongScalar vectorExpression =
      new DoubleColGreaterLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterEqualLongScalar vectorExpression =
      new LongColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterEqualLongScalar vectorExpression =
      new LongColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterEqualLongScalar vectorExpression =
      new LongColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterEqualLongScalar vectorExpression =
      new LongColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongColGreaterEqualLongScalar vectorExpression =
      new LongColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongScalarOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterEqualLongScalar vectorExpression =
      new DoubleColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongScalar() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterEqualLongScalar vectorExpression =
      new DoubleColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongScalarOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterEqualLongScalar vectorExpression =
      new DoubleColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongScalarOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterEqualLongScalar vectorExpression =
      new DoubleColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongScalarOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    DoubleColGreaterEqualLongScalar vectorExpression =
      new DoubleColGreaterEqualLongScalar(0, scalarValue, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualDoubleColumn vectorExpression =
      new LongScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualDoubleColumn vectorExpression =
      new LongScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualDoubleColumn vectorExpression =
      new LongScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualDoubleColumn vectorExpression =
      new LongScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualDoubleColumn vectorExpression =
      new LongScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualDoubleColumn vectorExpression =
      new DoubleScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualDoubleColumn vectorExpression =
      new DoubleScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualDoubleColumn vectorExpression =
      new DoubleScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualDoubleColumn vectorExpression =
      new DoubleScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualDoubleColumn vectorExpression =
      new DoubleScalarEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualDoubleColumn vectorExpression =
      new LongScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualDoubleColumn vectorExpression =
      new LongScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualDoubleColumn vectorExpression =
      new LongScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualDoubleColumn vectorExpression =
      new LongScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualDoubleColumn vectorExpression =
      new LongScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualDoubleColumn vectorExpression =
      new DoubleScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualDoubleColumn vectorExpression =
      new DoubleScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualDoubleColumn vectorExpression =
      new DoubleScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualDoubleColumn vectorExpression =
      new DoubleScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualDoubleColumn vectorExpression =
      new DoubleScalarNotEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessDoubleColumn vectorExpression =
      new LongScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessDoubleColumn vectorExpression =
      new LongScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessDoubleColumn vectorExpression =
      new LongScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessDoubleColumn vectorExpression =
      new LongScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessDoubleColumn vectorExpression =
      new LongScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessDoubleColumn vectorExpression =
      new DoubleScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessDoubleColumn vectorExpression =
      new DoubleScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessDoubleColumn vectorExpression =
      new DoubleScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessDoubleColumn vectorExpression =
      new DoubleScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessDoubleColumn vectorExpression =
      new DoubleScalarLessDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualDoubleColumn vectorExpression =
      new LongScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualDoubleColumn vectorExpression =
      new LongScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualDoubleColumn vectorExpression =
      new LongScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualDoubleColumn vectorExpression =
      new LongScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualDoubleColumn vectorExpression =
      new LongScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualDoubleColumn vectorExpression =
      new DoubleScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualDoubleColumn vectorExpression =
      new DoubleScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualDoubleColumn vectorExpression =
      new DoubleScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualDoubleColumn vectorExpression =
      new DoubleScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualDoubleColumn vectorExpression =
      new DoubleScalarLessEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterDoubleColumn vectorExpression =
      new LongScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterDoubleColumn vectorExpression =
      new LongScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterDoubleColumn vectorExpression =
      new LongScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterDoubleColumn vectorExpression =
      new LongScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterDoubleColumn vectorExpression =
      new LongScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterDoubleColumn vectorExpression =
      new DoubleScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterDoubleColumn vectorExpression =
      new DoubleScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterDoubleColumn vectorExpression =
      new DoubleScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterDoubleColumn vectorExpression =
      new DoubleScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterDoubleColumn vectorExpression =
      new DoubleScalarGreaterDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualDoubleColumn vectorExpression =
      new LongScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualDoubleColumn vectorExpression =
      new LongScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualDoubleColumn vectorExpression =
      new LongScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualDoubleColumn vectorExpression =
      new LongScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualDoubleColumn vectorExpression =
      new LongScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualDoubleColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualDoubleColumn vectorExpression =
      new DoubleScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualDoubleColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualDoubleColumn vectorExpression =
      new DoubleScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualDoubleColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualDoubleColumn vectorExpression =
      new DoubleScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualDoubleColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualDoubleColumn vectorExpression =
      new DoubleScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualDoubleColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualDoubleColumn vectorExpression =
      new DoubleScalarGreaterEqualDoubleColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualLongColumn vectorExpression =
      new LongScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualLongColumn vectorExpression =
      new LongScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualLongColumn vectorExpression =
      new LongScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualLongColumn vectorExpression =
      new LongScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarEqualLongColumn vectorExpression =
      new LongScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualLongColumn vectorExpression =
      new DoubleScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualLongColumn vectorExpression =
      new DoubleScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualLongColumn vectorExpression =
      new DoubleScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualLongColumn vectorExpression =
      new DoubleScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarEqualLongColumn vectorExpression =
      new DoubleScalarEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualLongColumn vectorExpression =
      new LongScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualLongColumn vectorExpression =
      new LongScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualLongColumn vectorExpression =
      new LongScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualLongColumn vectorExpression =
      new LongScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarNotEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarNotEqualLongColumn vectorExpression =
      new LongScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualLongColumn vectorExpression =
      new DoubleScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualLongColumn vectorExpression =
      new DoubleScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualLongColumn vectorExpression =
      new DoubleScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualLongColumn vectorExpression =
      new DoubleScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarNotEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarNotEqualLongColumn vectorExpression =
      new DoubleScalarNotEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessLongColumn vectorExpression =
      new LongScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessLongColumn vectorExpression =
      new LongScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessLongColumn vectorExpression =
      new LongScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessLongColumn vectorExpression =
      new LongScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessLongColumn vectorExpression =
      new LongScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessLongColumn vectorExpression =
      new DoubleScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessLongColumn vectorExpression =
      new DoubleScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessLongColumn vectorExpression =
      new DoubleScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessLongColumn vectorExpression =
      new DoubleScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessLongColumn vectorExpression =
      new DoubleScalarLessLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualLongColumn vectorExpression =
      new LongScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualLongColumn vectorExpression =
      new LongScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualLongColumn vectorExpression =
      new LongScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualLongColumn vectorExpression =
      new LongScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarLessEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarLessEqualLongColumn vectorExpression =
      new LongScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualLongColumn vectorExpression =
      new DoubleScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualLongColumn vectorExpression =
      new DoubleScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualLongColumn vectorExpression =
      new DoubleScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualLongColumn vectorExpression =
      new DoubleScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarLessEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarLessEqualLongColumn vectorExpression =
      new DoubleScalarLessEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterLongColumn vectorExpression =
      new LongScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterLongColumn vectorExpression =
      new LongScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterLongColumn vectorExpression =
      new LongScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterLongColumn vectorExpression =
      new LongScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterLongColumn vectorExpression =
      new LongScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterLongColumn vectorExpression =
      new DoubleScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterLongColumn vectorExpression =
      new DoubleScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterLongColumn vectorExpression =
      new DoubleScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterLongColumn vectorExpression =
      new DoubleScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterLongColumn vectorExpression =
      new DoubleScalarGreaterLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualLongColumn vectorExpression =
      new LongScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualLongColumn vectorExpression =
      new LongScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualLongColumn vectorExpression =
      new LongScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualLongColumn vectorExpression =
      new LongScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongScalarGreaterEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    long scalarValue = 0;
    do {
      scalarValue = rand.nextLong();
    } while(scalarValue == 0);

    LongScalarGreaterEqualLongColumn vectorExpression =
      new LongScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualLongColumnOutRepeatsColNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualLongColumn vectorExpression =
      new DoubleScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualLongColumn() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualLongColumn vectorExpression =
      new DoubleScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualLongColumnOutNullsColNulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualLongColumn vectorExpression =
      new DoubleScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualLongColumnOutNullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualLongColumn vectorExpression =
      new DoubleScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleScalarGreaterEqualLongColumnOutNullsColRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(2, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector;
    rowBatch.cols[1] = outputColumnVector;

    double scalarValue = 0;
    do {
      scalarValue = rand.nextDouble();
    } while(scalarValue == 0);

    DoubleScalarGreaterEqualLongColumn vectorExpression =
      new DoubleScalarGreaterEqualLongColumn(scalarValue, 0, 1);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
      "Output column vector is repeating state does not match operand column",
      inputColumnVector.isRepeating, outputColumnVector.isRepeating);

    assertEquals(
      "Output column vector no nulls state does not match operand column",
      inputColumnVector.noNulls, outputColumnVector.noNulls);

    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vector's is null state for index",
          inputColumnVector.isNull[i], outputColumnVector.isNull[i]);
      }
    }
  }


}


