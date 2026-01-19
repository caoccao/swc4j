/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.utils.SimpleSet;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstBinaryOp {
    @Test
    public void testGetOppositeOperator() {
        assertEquals(Swc4jAstBinaryOp.Add, Swc4jAstBinaryOp.Sub.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.BitAnd, Swc4jAstBinaryOp.BitOr.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.BitOr, Swc4jAstBinaryOp.BitAnd.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.Div, Swc4jAstBinaryOp.Mul.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.EqEq, Swc4jAstBinaryOp.NotEq.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.EqEqEq, Swc4jAstBinaryOp.NotEqEq.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.Gt, Swc4jAstBinaryOp.LtEq.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.GtEq, Swc4jAstBinaryOp.Lt.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.LShift, Swc4jAstBinaryOp.RShift.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.LogicalAnd, Swc4jAstBinaryOp.LogicalOr.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.LogicalOr, Swc4jAstBinaryOp.LogicalAnd.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.Lt, Swc4jAstBinaryOp.GtEq.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.LtEq, Swc4jAstBinaryOp.Gt.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.Mul, Swc4jAstBinaryOp.Div.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.NotEq, Swc4jAstBinaryOp.EqEq.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.NotEqEq, Swc4jAstBinaryOp.EqEqEq.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.RShift, Swc4jAstBinaryOp.LShift.getOppositeOperator());
        assertEquals(Swc4jAstBinaryOp.Sub, Swc4jAstBinaryOp.Add.getOppositeOperator());
    }

    @Test
    public void testIsLogicalOperator() {
        Set<Swc4jAstBinaryOp> logicalConditionOperatorSet = SimpleSet.of(
                Swc4jAstBinaryOp.LogicalAnd,
                Swc4jAstBinaryOp.LogicalOr);
        Set<Swc4jAstBinaryOp> logicalCompareOperatorSet = SimpleSet.of(
                Swc4jAstBinaryOp.EqEq,
                Swc4jAstBinaryOp.EqEqEq,
                Swc4jAstBinaryOp.Gt,
                Swc4jAstBinaryOp.GtEq,
                Swc4jAstBinaryOp.Lt,
                Swc4jAstBinaryOp.LtEq,
                Swc4jAstBinaryOp.NotEq,
                Swc4jAstBinaryOp.NotEqEq);
        Stream.of(Swc4jAstBinaryOp.values()).forEach(binaryOp -> {
            assertEquals(
                    logicalCompareOperatorSet.contains(binaryOp),
                    binaryOp.isLogicalCompareOperator(),
                    binaryOp.name());
            assertEquals(
                    logicalConditionOperatorSet.contains(binaryOp),
                    binaryOp.isLogicalConditionOperator(),
                    binaryOp.name());
            assertEquals(
                    logicalCompareOperatorSet.contains(binaryOp) || logicalConditionOperatorSet.contains(binaryOp),
                    binaryOp.isLogicalOperator(),
                    binaryOp.name());
        });
    }
}
