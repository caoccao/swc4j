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
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstBinaryOp {
    @Test
    public void testGetOppositeOperator() {
        assertThat(Swc4jAstBinaryOp.Sub.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.Add);
        assertThat(Swc4jAstBinaryOp.BitOr.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.BitAnd);
        assertThat(Swc4jAstBinaryOp.BitAnd.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.BitOr);
        assertThat(Swc4jAstBinaryOp.Mul.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.Div);
        assertThat(Swc4jAstBinaryOp.NotEq.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.EqEq);
        assertThat(Swc4jAstBinaryOp.NotEqEq.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.EqEqEq);
        assertThat(Swc4jAstBinaryOp.LtEq.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.Gt);
        assertThat(Swc4jAstBinaryOp.Lt.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.GtEq);
        assertThat(Swc4jAstBinaryOp.RShift.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.LShift);
        assertThat(Swc4jAstBinaryOp.LogicalOr.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.LogicalAnd);
        assertThat(Swc4jAstBinaryOp.LogicalAnd.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.LogicalOr);
        assertThat(Swc4jAstBinaryOp.GtEq.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.Lt);
        assertThat(Swc4jAstBinaryOp.Gt.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.LtEq);
        assertThat(Swc4jAstBinaryOp.Div.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.Mul);
        assertThat(Swc4jAstBinaryOp.EqEq.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.NotEq);
        assertThat(Swc4jAstBinaryOp.EqEqEq.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.NotEqEq);
        assertThat(Swc4jAstBinaryOp.LShift.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.RShift);
        assertThat(Swc4jAstBinaryOp.Add.getOppositeOperator()).isEqualTo(Swc4jAstBinaryOp.Sub);
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
            assertThat(binaryOp.isLogicalCompareOperator())
                    .as(binaryOp.name())
                    .isEqualTo(logicalCompareOperatorSet.contains(binaryOp));
            assertThat(binaryOp.isLogicalConditionOperator())
                    .as(binaryOp.name())
                    .isEqualTo(logicalConditionOperatorSet.contains(binaryOp));
            assertThat(binaryOp.isLogicalOperator())
                    .as(binaryOp.name())
                    .isEqualTo(logicalCompareOperatorSet.contains(binaryOp)
                            || logicalConditionOperatorSet.contains(binaryOp));
        });
    }
}
