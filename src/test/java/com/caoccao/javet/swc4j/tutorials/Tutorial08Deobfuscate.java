/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.tutorials;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.plugins.jsfuck.Swc4jPluginHostJsFuckDecoder;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class Tutorial08Deobfuscate {
    private static void deobfuscate(
            Swc4j swc4j,
            Swc4jTransformOptions options,
            ISwc4jPluginHost pluginHost,
            String code) throws Swc4jCoreException, JavetException {
        options.setPluginHost(pluginHost);
        System.out.println("/*********************************************");
        System.out.println("       The transformed code is as follows.");
        System.out.println("*********************************************/");
        // Transform the code.
        Swc4jTransformOutput output;
        while (true) {
            output = swc4j.transform(code, options);
            if (code.equals(output.getCode())) {
                break;
            }
            code = output.getCode();
            System.out.println(code);
        }
        System.out.println("/*********************************************");
        System.out.println("       The evaluated result in V8.");
        System.out.println("*********************************************/");
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            System.out.println(v8Runtime.getExecutor(output.getCode()).executeString());
        }
    }

    public static void main(String[] args) throws Swc4jCoreException, MalformedURLException, JavetException {
        // Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
        // Prepare a JavaScript code snippet.
        String code = "[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]"; // 1+1
        // Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
        // Prepare a transform options.
        Swc4jTransformOptions options = new Swc4jTransformOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.JavaScript)
                .setOmitLastSemi(true)
                .setInlineSources(false)
                .setSourceMap(Swc4jSourceMapOption.None);
        deobfuscate(swc4j, options, new Swc4jPluginHostDeobfuscator(new DeobfuscatorVisitorV1()), code);
        deobfuscate(swc4j, options, new Swc4jPluginHostDeobfuscator(new DeobfuscatorVisitorV2()), code);
        deobfuscate(swc4j, options, new Swc4jPluginHostDeobfuscator(new DeobfuscatorVisitorV3()), code);
        deobfuscate(swc4j, options, new Swc4jPluginHostJsFuckDecoder(), code);
    }

    public static class DeobfuscatorVisitorV1 extends Swc4jAstVisitor {
        @Override
        public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
            ISwc4jAstExpr arg = node.getArg().unParenExpr();
            Optional<ISwc4jAst> newNode = Optional.empty();
            switch (node.getOp()) {
                case Bang:
                    switch (arg.getType()) {
                        case ArrayLit:
                        case ObjectLit:
                            // ![] => false, !{} => false
                            newNode = Optional.of(Swc4jAstBool.create(false));
                            break;
                        case Bool:
                        case Number:
                        case Str:
                            // !true => false, !false => true, !0 => true, !0.1 => false, !'' => true, !'false' => false
                            newNode = Optional.of(Swc4jAstBool.create(!arg.as(ISwc4jAstCoercionPrimitive.class).asBoolean()));
                            break;
                        default:
                            break;
                    }
                    break;
                case Plus:
                    switch (arg.getType()) {
                        case ArrayLit:
                            // +[] => 0, +[1] => 1, +[1,2] => NaN
                            newNode = Optional.of(Swc4jAstNumber.create(arg.as(Swc4jAstArrayLit.class).asDouble()));
                            break;
                        case Bool:
                            // +true => 1, +false => 0
                            newNode = Optional.of(Swc4jAstNumber.create(arg.as(ISwc4jAstCoercionPrimitive.class).asInt()));
                            break;
                        case Ident:
                            if (arg.isNaN() || arg.isUndefined()) {
                                // +NaN => NaN, +undefined => NaN
                                newNode = Optional.of(Swc4jAstNumber.createNaN());
                            } else if (arg.isInfinity()) {
                                // +Infinity => Infinity
                                newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                            }
                            break;
                        case Number:
                            // +1 => 1, +1e22 => 1e+22
                            Swc4jAstNumber number = arg.as(Swc4jAstNumber.class);
                            newNode = Optional.of(Swc4jAstNumber.create(number.getValue(), number.getRaw().orElse(null)));
                            break;
                        case ObjectLit:
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                            break;
                        case Str:
                            // +'  ' => 0, +' 0.1 ' => 0.1, +'a' => NaN
                            String stringValue = arg.as(Swc4jAstStr.class).getValue().trim();
                            double doubleValue;
                            if (StringUtils.isEmpty(stringValue)) {
                                doubleValue = 0;
                            } else {
                                try {
                                    doubleValue = Double.parseDouble(stringValue);
                                } catch (Throwable t) {
                                    doubleValue = Double.NaN;
                                }
                            }
                            newNode = Optional.of(Swc4jAstNumber.create(doubleValue));
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            newNode.ifPresent(n -> node.getParent().replaceNode(node, n));
            return super.visitUnaryExpr(node);
        }
    }

    public static class DeobfuscatorVisitorV2 extends DeobfuscatorVisitorV1 {
        @Override
        public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
            Optional<ISwc4jAst> newNode = Optional.empty();
            ISwc4jAstExpr left = node.getLeft().unParenExpr();
            ISwc4jAstExpr right = node.getRight().unParenExpr();
            Swc4jAstType leftType = left.getType();
            Swc4jAstType rightType = right.getType();
            switch (node.getOp()) {
                case Add:
                    if (leftType.isArrayLit() && !left.as(Swc4jAstArrayLit.class).isAllPrimitive()) {
                        break;
                    }
                    if (rightType.isArrayLit() && !right.as(Swc4jAstArrayLit.class).isAllPrimitive()) {
                        break;
                    }
                    if ((leftType.isBool() && rightType.isNumber()) ||
                            (leftType.isBool() && rightType.isBool()) ||
                            (leftType.isNumber() && rightType.isBool()) ||
                            (leftType.isNumber() && rightType.isNumber())) {
                        // true+0 => 1, 0+false => 0, true+true => 2, 1+1 => 2
                        double value = left.as(ISwc4jAstCoercionPrimitive.class).asDouble()
                                + right.as(ISwc4jAstCoercionPrimitive.class).asDouble();
                        newNode = Optional.of(Swc4jAstNumber.create(value));
                    } else if ((leftType.isBool() && rightType.isArrayLit()) ||
                            (leftType.isArrayLit() && rightType.isBool()) ||
                            (leftType.isArrayLit() && rightType.isArrayLit()) ||
                            (leftType.isStr() && rightType.isArrayLit()) ||
                            (leftType.isArrayLit() && rightType.isStr()) ||
                            (leftType.isStr() && rightType.isStr()) ||
                            (leftType.isRegex() && rightType.isArrayLit()) ||
                            (leftType.isArrayLit() && rightType.isRegex()) ||
                            (leftType.isRegex() && rightType.isStr()) ||
                            (leftType.isStr() && rightType.isRegex()) ||
                            (leftType.isRegex() && rightType.isBool()) ||
                            (leftType.isBool() && rightType.isRegex()) ||
                            (leftType.isRegex() && rightType.isNumber()) ||
                            (leftType.isNumber() && rightType.isRegex()) ||
                            (leftType.isRegex() && rightType.isRegex()) ||
                            (leftType.isStr() && rightType.isNumber()) ||
                            (leftType.isStr() && rightType.isBool()) ||
                            (leftType.isBool() && rightType.isStr()) ||
                            (leftType.isArrayLit() && rightType.isNumber()) ||
                            (leftType.isNumber() && rightType.isStr()) ||
                            (leftType.isNumber() && rightType.isArrayLit())) {
                        // true+[] => 'true', true+'' => 'true', [0]+/a/i => '0/a/i'
                        String value = left.as(ISwc4jAstCoercionPrimitive.class).asString()
                                + right.as(ISwc4jAstCoercionPrimitive.class).asString();
                        newNode = Optional.of(Swc4jAstStr.create(value));
                    } else if (left.isNaN()) {
                        if (rightType.isNumber() || rightType.isBool()) {
                            // NaN+0 => NaN, NaN+true => NaN
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        } else if (rightType.isStr() || rightType.isArrayLit()) {
                            // NaN+'a' => 'NaNa', NaN+['a','b'] => 'NaNa,b'
                            newNode = Optional.of(Swc4jAstStr.create(ISwc4jConstants.NAN + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                        } else if (rightType.isMemberExpr()) {
                            newNode = right.as(Swc4jAstMemberExpr.class).evalAsString()
                                    .map(rightString -> Swc4jAstStr.create(ISwc4jConstants.NAN + rightString));
                        } else if (right.isNaN() || right.isUndefined() || right.isInfinity()) {
                            // NaN+NaN => NaN, NaN+undefined => NaN, NaN+Infinity => NaN
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        }
                    } else if (left.isInfinity()) {
                        if (rightType.isNumber() || rightType.isBool()) {
                            // Infinity+0 => Infinity, Infinity+true => Infinity
                            newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                        } else if (rightType.isStr() || rightType.isArrayLit()) {
                            // Infinity+'a' => 'Infinitya', Infinity+[0] => 'Infinity0'
                            newNode = Optional.of(Swc4jAstStr.create(ISwc4jConstants.INFINITY + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                        } else if (rightType.isMemberExpr()) {
                            newNode = right.as(Swc4jAstMemberExpr.class).evalAsString()
                                    .map(rightString -> Swc4jAstStr.create(ISwc4jConstants.INFINITY + rightString));
                        } else if (right.isInfinity()) {
                            // Infinity+Infinity => Infinity
                            newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                        } else if (right.isNaN() || right.isUndefined()) {
                            // Infinity+NaN => NaN, Infinity+undefined => NaN
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        }
                    } else if (left.isUndefined()) {
                        if (rightType.isNumber() || rightType.isBool()) {
                            // undefined+0 => NaN, undefined+true => NaN
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        } else if (rightType.isStr() || rightType.isArrayLit()) {
                            // undefined+'a' = 'undefineda', undefined+[0] => 'undefined0'
                            newNode = Optional.of(Swc4jAstStr.create(ISwc4jConstants.UNDEFINED + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                        } else if (right.isNaN() || right.isUndefined() || right.isInfinity()) {
                            // undefined+NaN => NaN, undefined+undefined => NaN, undefined+Infinity => NaN
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        }
                    } else if (right.isNaN()) {
                        if (leftType.isNumber() || leftType.isBool()) {
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        } else if (leftType.isStr() || leftType.isArrayLit()) {
                            newNode = Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.NAN));
                        } else if (leftType.isMemberExpr()) {
                            newNode = left.as(Swc4jAstMemberExpr.class).evalAsString()
                                    .map(leftString -> Swc4jAstStr.create(leftString + ISwc4jConstants.NAN));
                        } else if (left.isNaN() || left.isUndefined() || left.isInfinity()) {
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        }
                    } else if (right.isInfinity()) {
                        if (leftType.isNumber() || leftType.isBool()) {
                            newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                        } else if (leftType.isStr() || leftType.isArrayLit()) {
                            newNode = Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.INFINITY));
                        } else if (leftType.isMemberExpr()) {
                            newNode = left.as(Swc4jAstMemberExpr.class).evalAsString()
                                    .map(leftString -> Swc4jAstStr.create(leftString + ISwc4jConstants.INFINITY));
                        } else if (left.isInfinity()) {
                            newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                        } else if (left.isNaN() || left.isUndefined()) {
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        }
                    } else if (right.isUndefined()) {
                        if (leftType.isNumber() || leftType.isBool()) {
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        } else if (leftType.isStr() || leftType.isArrayLit()) {
                            newNode = Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.UNDEFINED));
                        } else if (left.isNaN() || left.isUndefined() || left.isInfinity()) {
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        }
                    } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isCallExpr()) {
                        newNode = right.as(Swc4jAstCallExpr.class).eval()
                                .map(n -> left.as(ISwc4jAstCoercionPrimitive.class).asString() + n)
                                .map(Swc4jAstStr::create);
                    } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isCallExpr()) {
                        newNode = left.as(Swc4jAstCallExpr.class).eval()
                                .map(n -> n + right.as(ISwc4jAstCoercionPrimitive.class).asString())
                                .map(Swc4jAstStr::create);
                    } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isMemberExpr()) {
                        newNode = right.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(rightString -> left.as(ISwc4jAstCoercionPrimitive.class).asString() + rightString)
                                .map(Swc4jAstStr::create);
                    } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isMemberExpr()) {
                        newNode = left.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(leftString -> leftString + right.as(ISwc4jAstCoercionPrimitive.class).asString())
                                .map(Swc4jAstStr::create);
                    }
                    break;
                default:
                    break;
            }
            newNode.ifPresent(n -> node.getParent().replaceNode(node, n));
            return super.visitBinExpr(node);
        }
    }

    public static class DeobfuscatorVisitorV3 extends DeobfuscatorVisitorV2 {
        @Override
        public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
            Optional<ISwc4jAst> newNode = Optional.empty();
            ISwc4jAstExpr obj = node.getObj().unParenExpr();
            ISwc4jAstMemberProp prop = node.getProp();
            switch (obj.getType()) {
                case Str:
                    if (prop instanceof Swc4jAstComputedPropName) {
                        Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                        ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                        String value = obj.as(Swc4jAstStr.class).getValue();
                        switch (expr.getType()) {
                            case Number: {
                                // 'string'[0] => 's'
                                int index = expr.as(Swc4jAstNumber.class).asInt();
                                if (index >= 0 && index < value.length()) {
                                    value = value.substring(index, index + 1);
                                    newNode = Optional.of(Swc4jAstStr.create(value));
                                }
                                break;
                            }
                            case Str: {
                                // 'string'['0'] => 's'
                                Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                if (StringUtils.isDigit(str.getValue())) {
                                    int index = str.asInt();
                                    if (index >= 0 && index < value.length()) {
                                        value = value.substring(index, index + 1);
                                        newNode = Optional.of(Swc4jAstStr.create(value));
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            newNode.ifPresent(n -> node.getParent().replaceNode(node, n));
            return super.visitMemberExpr(node);
        }
    }

    public static class Swc4jPluginHostDeobfuscator implements ISwc4jPluginHost {
        private final ISwc4jAstVisitor visitor;

        public Swc4jPluginHostDeobfuscator(ISwc4jAstVisitor visitor) {
            this.visitor = visitor;
        }

        @Override
        public boolean process(ISwc4jAstProgram<?> program) {
            program.visit(visitor);
            return true;
        }
    }
}
