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
 * TestColumnColumnOperationVectorExpressionEvaluation.
 *
 */
public class TestColumnColumnOperationVectorExpressionEvaluation{

  private static final int BATCH_SIZE = 100;
  private static final long SEED = 0xfa57;

  
  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddIntervalYearMonthColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColSubtractIntervalYearMonthColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColSubtractIntervalYearMonthColumn vectorExpression =
      new IntervalYearMonthColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddIntervalDayTimeColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColSubtractIntervalDayTimeColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColSubtractIntervalDayTimeColumn vectorExpression =
      new IntervalDayTimeColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddTimestampColumn vectorExpression =
      new IntervalDayTimeColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddTimestampColumn vectorExpression =
      new IntervalDayTimeColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddTimestampColumn vectorExpression =
      new IntervalDayTimeColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddTimestampColumn vectorExpression =
      new IntervalDayTimeColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddTimestampColumn vectorExpression =
      new IntervalDayTimeColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddTimestampColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddTimestampColumn vectorExpression =
      new IntervalDayTimeColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalDayTimeColumn vectorExpression =
      new TimestampColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalDayTimeColumn vectorExpression =
      new TimestampColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalDayTimeColumn vectorExpression =
      new TimestampColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalDayTimeColumn vectorExpression =
      new TimestampColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalDayTimeColumn vectorExpression =
      new TimestampColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalDayTimeColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalDayTimeColumn vectorExpression =
      new TimestampColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalDayTimeColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalDayTimeColumn vectorExpression =
      new TimestampColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractTimestampColumn vectorExpression =
      new TimestampColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractTimestampColumn vectorExpression =
      new TimestampColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractTimestampColumn vectorExpression =
      new TimestampColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractTimestampColumn vectorExpression =
      new TimestampColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractTimestampColumn vectorExpression =
      new TimestampColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractTimestampColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractTimestampColumn vectorExpression =
      new TimestampColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractDateColumn vectorExpression =
      new DateColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractDateColumn vectorExpression =
      new DateColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractDateColumn vectorExpression =
      new DateColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractDateColumn vectorExpression =
      new DateColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractDateColumn vectorExpression =
      new DateColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractDateColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractDateColumn vectorExpression =
      new DateColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractTimestampColumn vectorExpression =
      new DateColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractTimestampColumn vectorExpression =
      new DateColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractTimestampColumn vectorExpression =
      new DateColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractTimestampColumn vectorExpression =
      new DateColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractTimestampColumn vectorExpression =
      new DateColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractTimestampColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractTimestampColumn vectorExpression =
      new DateColSubtractTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractDateColumn vectorExpression =
      new TimestampColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractDateColumn vectorExpression =
      new TimestampColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractDateColumn vectorExpression =
      new TimestampColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractDateColumn vectorExpression =
      new TimestampColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractDateColumn vectorExpression =
      new TimestampColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractDateColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractDateColumn vectorExpression =
      new TimestampColSubtractDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalDayTimeColumn vectorExpression =
      new DateColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalDayTimeColumn vectorExpression =
      new DateColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalDayTimeColumn vectorExpression =
      new DateColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalDayTimeColumn vectorExpression =
      new DateColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalDayTimeColumn vectorExpression =
      new DateColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalDayTimeColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalDayTimeColumn vectorExpression =
      new DateColAddIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalDayTimeColumn vectorExpression =
      new DateColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalDayTimeColumn vectorExpression =
      new DateColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalDayTimeColumn vectorExpression =
      new DateColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalDayTimeColumn vectorExpression =
      new DateColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalDayTimeColumn vectorExpression =
      new DateColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalDayTimeColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalDayTimeColumn vectorExpression =
      new DateColSubtractIntervalDayTimeColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddDateColumn vectorExpression =
      new IntervalDayTimeColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddDateColumn vectorExpression =
      new IntervalDayTimeColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddDateColumn vectorExpression =
      new IntervalDayTimeColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddDateColumn vectorExpression =
      new IntervalDayTimeColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddDateColumn vectorExpression =
      new IntervalDayTimeColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalDayTimeColAddDateColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalDayTimeColAddDateColumn vectorExpression =
      new IntervalDayTimeColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalYearMonthColumn vectorExpression =
      new DateColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalYearMonthColumn vectorExpression =
      new DateColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalYearMonthColumn vectorExpression =
      new DateColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalYearMonthColumn vectorExpression =
      new DateColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalYearMonthColumn vectorExpression =
      new DateColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColAddIntervalYearMonthColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColAddIntervalYearMonthColumn vectorExpression =
      new DateColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalYearMonthColumn vectorExpression =
      new DateColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalYearMonthColumn vectorExpression =
      new DateColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalYearMonthColumn vectorExpression =
      new DateColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalYearMonthColumn vectorExpression =
      new DateColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalYearMonthColumn vectorExpression =
      new DateColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDateColSubtractIntervalYearMonthColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DateColSubtractIntervalYearMonthColumn vectorExpression =
      new DateColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalYearMonthColumn vectorExpression =
      new TimestampColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalYearMonthColumn vectorExpression =
      new TimestampColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalYearMonthColumn vectorExpression =
      new TimestampColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalYearMonthColumn vectorExpression =
      new TimestampColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalYearMonthColumn vectorExpression =
      new TimestampColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColAddIntervalYearMonthColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColAddIntervalYearMonthColumn vectorExpression =
      new TimestampColAddIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testTimestampColSubtractIntervalYearMonthColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    TimestampColSubtractIntervalYearMonthColumn vectorExpression =
      new TimestampColSubtractIntervalYearMonthColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddDateColumn vectorExpression =
      new IntervalYearMonthColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddDateColumn vectorExpression =
      new IntervalYearMonthColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddDateColumn vectorExpression =
      new IntervalYearMonthColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddDateColumn vectorExpression =
      new IntervalYearMonthColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddDateColumn vectorExpression =
      new IntervalYearMonthColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddDateColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddDateColumn vectorExpression =
      new IntervalYearMonthColAddDateColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddTimestampColumn vectorExpression =
      new IntervalYearMonthColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddTimestampColumn vectorExpression =
      new IntervalYearMonthColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddTimestampColumn vectorExpression =
      new IntervalYearMonthColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddTimestampColumn vectorExpression =
      new IntervalYearMonthColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddTimestampColumn vectorExpression =
      new IntervalYearMonthColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testIntervalYearMonthColAddTimestampColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    IntervalYearMonthColAddTimestampColumn vectorExpression =
      new IntervalYearMonthColAddTimestampColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddLongColumn vectorExpression =
      new LongColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddLongColumn vectorExpression =
      new LongColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddLongColumn vectorExpression =
      new LongColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddLongColumn vectorExpression =
      new LongColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddLongColumn vectorExpression =
      new LongColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddLongColumn vectorExpression =
      new LongColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractLongColumn vectorExpression =
      new LongColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractLongColumn vectorExpression =
      new LongColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractLongColumn vectorExpression =
      new LongColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractLongColumn vectorExpression =
      new LongColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractLongColumn vectorExpression =
      new LongColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractLongColumn vectorExpression =
      new LongColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyLongColumn vectorExpression =
      new LongColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyLongColumn vectorExpression =
      new LongColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyLongColumn vectorExpression =
      new LongColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyLongColumn vectorExpression =
      new LongColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyLongColumn vectorExpression =
      new LongColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyLongColumn vectorExpression =
      new LongColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddDoubleColumn vectorExpression =
      new LongColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddDoubleColumn vectorExpression =
      new LongColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddDoubleColumn vectorExpression =
      new LongColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddDoubleColumn vectorExpression =
      new LongColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddDoubleColumn vectorExpression =
      new LongColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColAddDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColAddDoubleColumn vectorExpression =
      new LongColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractDoubleColumn vectorExpression =
      new LongColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractDoubleColumn vectorExpression =
      new LongColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractDoubleColumn vectorExpression =
      new LongColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractDoubleColumn vectorExpression =
      new LongColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractDoubleColumn vectorExpression =
      new LongColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColSubtractDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColSubtractDoubleColumn vectorExpression =
      new LongColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyDoubleColumn vectorExpression =
      new LongColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyDoubleColumn vectorExpression =
      new LongColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyDoubleColumn vectorExpression =
      new LongColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyDoubleColumn vectorExpression =
      new LongColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyDoubleColumn vectorExpression =
      new LongColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColMultiplyDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColMultiplyDoubleColumn vectorExpression =
      new LongColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddLongColumn vectorExpression =
      new DoubleColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddLongColumn vectorExpression =
      new DoubleColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddLongColumn vectorExpression =
      new DoubleColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddLongColumn vectorExpression =
      new DoubleColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddLongColumn vectorExpression =
      new DoubleColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddLongColumn vectorExpression =
      new DoubleColAddLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractLongColumn vectorExpression =
      new DoubleColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractLongColumn vectorExpression =
      new DoubleColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractLongColumn vectorExpression =
      new DoubleColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractLongColumn vectorExpression =
      new DoubleColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractLongColumn vectorExpression =
      new DoubleColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractLongColumn vectorExpression =
      new DoubleColSubtractLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyLongColumn vectorExpression =
      new DoubleColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyLongColumn vectorExpression =
      new DoubleColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyLongColumn vectorExpression =
      new DoubleColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyLongColumn vectorExpression =
      new DoubleColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyLongColumn vectorExpression =
      new DoubleColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyLongColumn vectorExpression =
      new DoubleColMultiplyLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddDoubleColumn vectorExpression =
      new DoubleColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddDoubleColumn vectorExpression =
      new DoubleColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddDoubleColumn vectorExpression =
      new DoubleColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddDoubleColumn vectorExpression =
      new DoubleColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddDoubleColumn vectorExpression =
      new DoubleColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColAddDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColAddDoubleColumn vectorExpression =
      new DoubleColAddDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractDoubleColumn vectorExpression =
      new DoubleColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractDoubleColumn vectorExpression =
      new DoubleColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractDoubleColumn vectorExpression =
      new DoubleColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractDoubleColumn vectorExpression =
      new DoubleColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractDoubleColumn vectorExpression =
      new DoubleColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColSubtractDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColSubtractDoubleColumn vectorExpression =
      new DoubleColSubtractDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyDoubleColumn vectorExpression =
      new DoubleColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyDoubleColumn vectorExpression =
      new DoubleColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyDoubleColumn vectorExpression =
      new DoubleColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyDoubleColumn vectorExpression =
      new DoubleColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyDoubleColumn vectorExpression =
      new DoubleColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColMultiplyDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColMultiplyDoubleColumn vectorExpression =
      new DoubleColMultiplyDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColDivideDoubleColumn vectorExpression =
      new LongColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColDivideDoubleColumn vectorExpression =
      new LongColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColDivideDoubleColumn vectorExpression =
      new LongColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColDivideDoubleColumn vectorExpression =
      new LongColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColDivideDoubleColumn vectorExpression =
      new LongColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColDivideDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColDivideDoubleColumn vectorExpression =
      new LongColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideLongColumn vectorExpression =
      new DoubleColDivideLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideLongColumn vectorExpression =
      new DoubleColDivideLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideLongColumn vectorExpression =
      new DoubleColDivideLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideLongColumn vectorExpression =
      new DoubleColDivideLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideLongColumn vectorExpression =
      new DoubleColDivideLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideLongColumn vectorExpression =
      new DoubleColDivideLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideDoubleColumn vectorExpression =
      new DoubleColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideDoubleColumn vectorExpression =
      new DoubleColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideDoubleColumn vectorExpression =
      new DoubleColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideDoubleColumn vectorExpression =
      new DoubleColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideDoubleColumn vectorExpression =
      new DoubleColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColDivideDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColDivideDoubleColumn vectorExpression =
      new DoubleColDivideDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloLongColumn vectorExpression =
      new LongColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloLongColumn vectorExpression =
      new LongColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloLongColumn vectorExpression =
      new LongColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloLongColumn vectorExpression =
      new LongColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloLongColumn vectorExpression =
      new LongColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloLongColumn vectorExpression =
      new LongColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloDoubleColumn vectorExpression =
      new LongColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloDoubleColumn vectorExpression =
      new LongColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloDoubleColumn vectorExpression =
      new LongColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloDoubleColumn vectorExpression =
      new LongColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloDoubleColumn vectorExpression =
      new LongColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColModuloDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColModuloDoubleColumn vectorExpression =
      new LongColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloLongColumn vectorExpression =
      new DoubleColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloLongColumn vectorExpression =
      new DoubleColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloLongColumn vectorExpression =
      new DoubleColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloLongColumn vectorExpression =
      new DoubleColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloLongColumn vectorExpression =
      new DoubleColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloLongColumn vectorExpression =
      new DoubleColModuloLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloDoubleColumn vectorExpression =
      new DoubleColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloDoubleColumn vectorExpression =
      new DoubleColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloDoubleColumn vectorExpression =
      new DoubleColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloDoubleColumn vectorExpression =
      new DoubleColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloDoubleColumn vectorExpression =
      new DoubleColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColModuloDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    DoubleColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColModuloDoubleColumn vectorExpression =
      new DoubleColModuloDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualDoubleColumn vectorExpression =
      new LongColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualDoubleColumn vectorExpression =
      new LongColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualDoubleColumn vectorExpression =
      new LongColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualDoubleColumn vectorExpression =
      new LongColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualDoubleColumn vectorExpression =
      new LongColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualDoubleColumn vectorExpression =
      new LongColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualDoubleColumn vectorExpression =
      new DoubleColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualDoubleColumn vectorExpression =
      new DoubleColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualDoubleColumn vectorExpression =
      new DoubleColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualDoubleColumn vectorExpression =
      new DoubleColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualDoubleColumn vectorExpression =
      new DoubleColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualDoubleColumn vectorExpression =
      new DoubleColEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualDoubleColumn vectorExpression =
      new LongColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualDoubleColumn vectorExpression =
      new LongColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualDoubleColumn vectorExpression =
      new LongColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualDoubleColumn vectorExpression =
      new LongColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualDoubleColumn vectorExpression =
      new LongColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualDoubleColumn vectorExpression =
      new LongColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualDoubleColumn vectorExpression =
      new DoubleColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualDoubleColumn vectorExpression =
      new DoubleColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualDoubleColumn vectorExpression =
      new DoubleColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualDoubleColumn vectorExpression =
      new DoubleColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualDoubleColumn vectorExpression =
      new DoubleColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualDoubleColumn vectorExpression =
      new DoubleColNotEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessDoubleColumn vectorExpression =
      new LongColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessDoubleColumn vectorExpression =
      new LongColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessDoubleColumn vectorExpression =
      new LongColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessDoubleColumn vectorExpression =
      new LongColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessDoubleColumn vectorExpression =
      new LongColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessDoubleColumn vectorExpression =
      new LongColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessDoubleColumn vectorExpression =
      new DoubleColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessDoubleColumn vectorExpression =
      new DoubleColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessDoubleColumn vectorExpression =
      new DoubleColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessDoubleColumn vectorExpression =
      new DoubleColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessDoubleColumn vectorExpression =
      new DoubleColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessDoubleColumn vectorExpression =
      new DoubleColLessDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualDoubleColumn vectorExpression =
      new LongColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualDoubleColumn vectorExpression =
      new LongColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualDoubleColumn vectorExpression =
      new LongColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualDoubleColumn vectorExpression =
      new LongColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualDoubleColumn vectorExpression =
      new LongColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualDoubleColumn vectorExpression =
      new LongColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualDoubleColumn vectorExpression =
      new DoubleColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualDoubleColumn vectorExpression =
      new DoubleColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualDoubleColumn vectorExpression =
      new DoubleColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualDoubleColumn vectorExpression =
      new DoubleColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualDoubleColumn vectorExpression =
      new DoubleColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualDoubleColumn vectorExpression =
      new DoubleColLessEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterDoubleColumn vectorExpression =
      new LongColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterDoubleColumn vectorExpression =
      new LongColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterDoubleColumn vectorExpression =
      new LongColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterDoubleColumn vectorExpression =
      new LongColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterDoubleColumn vectorExpression =
      new LongColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterDoubleColumn vectorExpression =
      new LongColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterDoubleColumn vectorExpression =
      new DoubleColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterDoubleColumn vectorExpression =
      new DoubleColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterDoubleColumn vectorExpression =
      new DoubleColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterDoubleColumn vectorExpression =
      new DoubleColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterDoubleColumn vectorExpression =
      new DoubleColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterDoubleColumn vectorExpression =
      new DoubleColGreaterDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualDoubleColumn vectorExpression =
      new LongColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualDoubleColumn vectorExpression =
      new LongColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualDoubleColumn vectorExpression =
      new LongColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualDoubleColumn vectorExpression =
      new LongColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualDoubleColumn vectorExpression =
      new LongColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualDoubleColumn vectorExpression =
      new LongColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualDoubleColumn vectorExpression =
      new DoubleColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualDoubleColumn vectorExpression =
      new DoubleColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualDoubleColumn vectorExpression =
      new DoubleColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualDoubleColumn vectorExpression =
      new DoubleColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualDoubleColumn vectorExpression =
      new DoubleColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualDoubleColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualDoubleColumn vectorExpression =
      new DoubleColGreaterEqualDoubleColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualLongColumn vectorExpression =
      new LongColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualLongColumn vectorExpression =
      new LongColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualLongColumn vectorExpression =
      new LongColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualLongColumn vectorExpression =
      new LongColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualLongColumn vectorExpression =
      new LongColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColEqualLongColumn vectorExpression =
      new LongColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualLongColumn vectorExpression =
      new DoubleColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualLongColumn vectorExpression =
      new DoubleColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualLongColumn vectorExpression =
      new DoubleColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualLongColumn vectorExpression =
      new DoubleColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualLongColumn vectorExpression =
      new DoubleColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColEqualLongColumn vectorExpression =
      new DoubleColEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualLongColumn vectorExpression =
      new LongColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualLongColumn vectorExpression =
      new LongColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualLongColumn vectorExpression =
      new LongColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualLongColumn vectorExpression =
      new LongColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualLongColumn vectorExpression =
      new LongColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColNotEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColNotEqualLongColumn vectorExpression =
      new LongColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualLongColumn vectorExpression =
      new DoubleColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualLongColumn vectorExpression =
      new DoubleColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualLongColumn vectorExpression =
      new DoubleColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualLongColumn vectorExpression =
      new DoubleColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualLongColumn vectorExpression =
      new DoubleColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColNotEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColNotEqualLongColumn vectorExpression =
      new DoubleColNotEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessLongColumn vectorExpression =
      new LongColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessLongColumn vectorExpression =
      new LongColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessLongColumn vectorExpression =
      new LongColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessLongColumn vectorExpression =
      new LongColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessLongColumn vectorExpression =
      new LongColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessLongColumn vectorExpression =
      new LongColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessLongColumn vectorExpression =
      new DoubleColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessLongColumn vectorExpression =
      new DoubleColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessLongColumn vectorExpression =
      new DoubleColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessLongColumn vectorExpression =
      new DoubleColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessLongColumn vectorExpression =
      new DoubleColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessLongColumn vectorExpression =
      new DoubleColLessLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualLongColumn vectorExpression =
      new LongColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualLongColumn vectorExpression =
      new LongColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualLongColumn vectorExpression =
      new LongColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualLongColumn vectorExpression =
      new LongColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualLongColumn vectorExpression =
      new LongColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColLessEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColLessEqualLongColumn vectorExpression =
      new LongColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualLongColumn vectorExpression =
      new DoubleColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualLongColumn vectorExpression =
      new DoubleColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualLongColumn vectorExpression =
      new DoubleColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualLongColumn vectorExpression =
      new DoubleColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualLongColumn vectorExpression =
      new DoubleColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColLessEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColLessEqualLongColumn vectorExpression =
      new DoubleColLessEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterLongColumn vectorExpression =
      new LongColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterLongColumn vectorExpression =
      new LongColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterLongColumn vectorExpression =
      new LongColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterLongColumn vectorExpression =
      new LongColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterLongColumn vectorExpression =
      new LongColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterLongColumn vectorExpression =
      new LongColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterLongColumn vectorExpression =
      new DoubleColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterLongColumn vectorExpression =
      new DoubleColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterLongColumn vectorExpression =
      new DoubleColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterLongColumn vectorExpression =
      new DoubleColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterLongColumn vectorExpression =
      new DoubleColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterLongColumn vectorExpression =
      new DoubleColGreaterLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualLongColumn vectorExpression =
      new LongColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualLongColumn vectorExpression =
      new LongColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualLongColumn vectorExpression =
      new LongColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualLongColumn vectorExpression =
      new LongColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualLongColumn vectorExpression =
      new LongColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testLongColGreaterEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    LongColGreaterEqualLongColumn vectorExpression =
      new LongColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongColumnOutNullsRepeatsC1RepeatsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualLongColumn vectorExpression =
      new DoubleColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongColumnC1Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualLongColumn vectorExpression =
      new DoubleColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongColumnOutNullsC1NullsC2NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualLongColumn vectorExpression =
      new DoubleColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongColumnOutNullsRepeatsC1NullsRepeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(true,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualLongColumn vectorExpression =
      new DoubleColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongColumnC1RepeatsC2Nulls() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      false, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      true, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(true,
      false, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualLongColumn vectorExpression =
      new DoubleColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }

  @Test
  public void testDoubleColGreaterEqualLongColumnOutRepeatsC2Repeats() {

    Random rand = new Random(SEED);

    LongColumnVector outputColumnVector =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    DoubleColumnVector inputColumnVector1 =
      VectorizedRowGroupGenUtil.generateDoubleColumnVector(false,
      false, BATCH_SIZE, rand);

    LongColumnVector inputColumnVector2 =
      VectorizedRowGroupGenUtil.generateLongColumnVector(false,
      true, BATCH_SIZE, rand);

    VectorizedRowBatch rowBatch = new VectorizedRowBatch(3, BATCH_SIZE);
    rowBatch.cols[0] = inputColumnVector1;
    rowBatch.cols[1] = inputColumnVector2;
    rowBatch.cols[2] = outputColumnVector;

    DoubleColGreaterEqualLongColumn vectorExpression =
      new DoubleColGreaterEqualLongColumn(0, 1, 2);

    vectorExpression.evaluate(rowBatch);

    assertEquals(
        "Output column vector repeating state does not match operand columns",
        (!inputColumnVector1.noNulls && inputColumnVector1.isRepeating)
        || (!inputColumnVector2.noNulls && inputColumnVector2.isRepeating)
        || inputColumnVector1.isRepeating && inputColumnVector2.isRepeating,
        outputColumnVector.isRepeating);

    assertEquals(
        "Output column vector no nulls state does not match operand columns",
        inputColumnVector1.noNulls && inputColumnVector2.noNulls, outputColumnVector.noNulls);

    //if repeating, only the first value matters
    if(!outputColumnVector.noNulls && !outputColumnVector.isRepeating) {
      for(int i = 0; i < BATCH_SIZE; i++) {
        //null vectors are safe to check, as they are always initialized to match the data vector
        assertEquals("Output vector doesn't match input vectors' is null state for index",
          inputColumnVector1.isNull[i] || inputColumnVector2.isNull[i],
          outputColumnVector.isNull[i]);
      }
    }
  }


}


