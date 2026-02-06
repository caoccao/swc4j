/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.ts;

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstTsKeywordTypeKind;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstTsTypeOperatorOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdentName;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBigInt;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestTypeResolverTsTypeCoverage extends BaseTestCompileSuite {
    private static Swc4jAstTsKeywordType keyword(Swc4jAstTsKeywordTypeKind kind) {
        return Swc4jAstTsKeywordType.create(kind);
    }

    private static Swc4jAstTsTupleType tuple(ISwc4jAstTsType... types) {
        return Swc4jAstTsTupleType.create(List.of(types)
                .stream()
                .map(Swc4jAstTsTupleElement::create)
                .toList());
    }

    private static Swc4jAstTsTypeRef typeRef(String typeName) {
        return Swc4jAstTsTypeRef.create(Swc4jAstIdent.create(typeName));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapTsTypeToDescriptorCoverage(JdkVersion jdkVersion) throws Exception {
        var resolver = getCompiler(jdkVersion).getTypeResolver();

        List<List<String>> cases = List.of(
                List.of("keywordUndefined", resolver.mapTsTypeToDescriptor(keyword(Swc4jAstTsKeywordTypeKind.TsUndefinedKeyword)), "Ljava/lang/Object;"),
                List.of("unionNumericWiden", resolver.mapTsTypeToDescriptor(Swc4jAstTsUnionType.create(List.of(typeRef("int"), keyword(Swc4jAstTsKeywordTypeKind.TsNumberKeyword)))), "D"),
                List.of("unionMixed", resolver.mapTsTypeToDescriptor(Swc4jAstTsUnionType.create(List.of(keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword), typeRef("int")))), "Ljava/lang/Object;"),
                List.of("unionWithVoid", resolver.mapTsTypeToDescriptor(Swc4jAstTsUnionType.create(List.of(typeRef("int"), keyword(Swc4jAstTsKeywordTypeKind.TsVoidKeyword)))), "Ljava/lang/Object;"),
                List.of("intersectionSingle", resolver.mapTsTypeToDescriptor(Swc4jAstTsIntersectionType.create(List.of(typeRef("int")))), "I"),
                List.of("intersectionMixed", resolver.mapTsTypeToDescriptor(Swc4jAstTsIntersectionType.create(List.of(keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword), typeRef("int")))), "Ljava/lang/Object;"),
                List.of("mappedType", resolver.mapTsTypeToDescriptor(Swc4jAstTsMappedType.create(Swc4jAstTsTypeParam.create(Swc4jAstIdent.create("K")))), "Ljava/util/LinkedHashMap;"),
                List.of("indexedArray", resolver.mapTsTypeToDescriptor(Swc4jAstTsIndexedAccessType.create(Swc4jAstTsArrayType.create(typeRef("int")), keyword(Swc4jAstTsKeywordTypeKind.TsNumberKeyword))), "I"),
                List.of("indexedTupleAtOne", resolver.mapTsTypeToDescriptor(Swc4jAstTsIndexedAccessType.create(tuple(keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword), typeRef("int")), Swc4jAstTsLitType.create(Swc4jAstNumber.create(1)))), "I"),
                List.of("indexedTupleOutOfRange", resolver.mapTsTypeToDescriptor(Swc4jAstTsIndexedAccessType.create(tuple(keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword), typeRef("int")), Swc4jAstTsLitType.create(Swc4jAstNumber.create(5)))), "Ljava/lang/Object;"),
                List.of("tupleType", resolver.mapTsTypeToDescriptor(tuple(typeRef("int"), keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword))), "Ljava/util/List;"),
                List.of("optionalType", resolver.mapTsTypeToDescriptor(Swc4jAstTsOptionalType.create(typeRef("int"))), "I"),
                List.of("restTypePrimitive", resolver.mapTsTypeToDescriptor(Swc4jAstTsRestType.create(typeRef("int"))), "[I"),
                List.of("restTypeArray", resolver.mapTsTypeToDescriptor(Swc4jAstTsRestType.create(Swc4jAstTsArrayType.create(typeRef("int")))), "[I"),
                List.of("litInt", resolver.mapTsTypeToDescriptor(Swc4jAstTsLitType.create(Swc4jAstNumber.create(7))), "I"),
                List.of("litDouble", resolver.mapTsTypeToDescriptor(Swc4jAstTsLitType.create(Swc4jAstNumber.create(7.5))), "D"),
                List.of("litBool", resolver.mapTsTypeToDescriptor(Swc4jAstTsLitType.create(Swc4jAstBool.create(true))), "Z"),
                List.of("litString", resolver.mapTsTypeToDescriptor(Swc4jAstTsLitType.create(Swc4jAstStr.create("x"))), "Ljava/lang/String;"),
                List.of("litBigInt", resolver.mapTsTypeToDescriptor(Swc4jAstTsLitType.create(Swc4jAstBigInt.create("123n"))), "J"),
                List.of("litTemplate", resolver.mapTsTypeToDescriptor(Swc4jAstTsLitType.create(Swc4jAstTsTplLitType.create(List.of(keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword))))), "Ljava/lang/String;"),
                List.of("typePredicate", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypePredicate.create(Swc4jAstIdent.create("value"))), "Z"),
                List.of("typeOperatorReadonly", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypeOperator.create(Swc4jAstTsTypeOperatorOp.ReadOnly, typeRef("int"))), "I"),
                List.of("typeOperatorUnique", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypeOperator.create(Swc4jAstTsTypeOperatorOp.Unique, keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword))), "Ljava/lang/String;"),
                List.of("typeOperatorKeyOf", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypeOperator.create(Swc4jAstTsTypeOperatorOp.KeyOf, keyword(Swc4jAstTsKeywordTypeKind.TsObjectKeyword))), "Ljava/lang/String;")
        );

        for (List<String> entry : cases) {
            assertThat(entry.get(1)).as(entry.get(0)).isEqualTo(entry.get(2));
        }
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapTsTypeToDescriptorTypeQueryAndImport(JdkVersion jdkVersion) throws Exception {
        var resolver = getCompiler(jdkVersion).getTypeResolver();

        Swc4jAstTsImportType importStringType = Swc4jAstTsImportType.create(
                Swc4jAstStr.create("java.lang"),
                Swc4jAstIdent.create("String"));
        Swc4jAstTsImportType importWithoutQualifier = Swc4jAstTsImportType.create(Swc4jAstStr.create("java.lang"));
        Swc4jAstTsQualifiedName javaLang = Swc4jAstTsQualifiedName.create(
                Swc4jAstIdent.create("java"),
                Swc4jAstIdentName.create("lang"));
        Swc4jAstTsQualifiedName javaLangString = Swc4jAstTsQualifiedName.create(
                javaLang,
                Swc4jAstIdentName.create("String"));

        List<List<String>> cases = List.of(
                List.of("importTypeSimple", resolver.mapTsTypeToDescriptor(importStringType), "Ljava/lang/String;"),
                List.of("importTypeWithoutQualifier", resolver.mapTsTypeToDescriptor(importWithoutQualifier), "Ljava/lang/Object;"),
                List.of("typeQueryImportType", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypeQuery.create(importStringType)), "Ljava/lang/String;"),
                List.of("typeQueryQualifiedEntity", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypeQuery.create(javaLangString)), "Ljava/lang/String;"),
                List.of("typeQueryUnknownEntity", resolver.mapTsTypeToDescriptor(Swc4jAstTsTypeQuery.create(Swc4jAstIdent.create("localValue"))), "Ljava/lang/Object;")
        );

        for (List<String> entry : cases) {
            assertThat(entry.get(1)).as(entry.get(0)).isEqualTo(entry.get(2));
        }
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapTsTypeToDescriptorUnsupportedTypeLevelFeatures(JdkVersion jdkVersion) {
        var resolver = getCompiler(jdkVersion).getTypeResolver();

        assertThatThrownBy(() -> resolver.mapTsTypeToDescriptor(Swc4jAstTsConditionalType.create(
                keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword),
                keyword(Swc4jAstTsKeywordTypeKind.TsObjectKeyword),
                keyword(Swc4jAstTsKeywordTypeKind.TsStringKeyword),
                typeRef("int"))))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Conditional types are not supported");

        assertThatThrownBy(() -> resolver.mapTsTypeToDescriptor(
                Swc4jAstTsInferType.create(Swc4jAstTsTypeParam.create(Swc4jAstIdent.create("T")))))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Infer types are not supported");
    }
}
