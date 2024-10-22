/*
 * reflection-remapper
 *
 * Copyright (c) 2021 Jason Penilla
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
package dependancies.xyz.jpenilla.reflectionremapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.fabricmc.mappingio.tree.MappingTree;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import dependancies.xyz.jpenilla.reflectionremapper.internal.util.StringPool;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static dependancies.xyz.jpenilla.reflectionremapper.internal.util.Util.descriptorString;

@DefaultQualifier(NonNull.class)
final class ReflectionRemapperImpl implements ReflectionRemapper {
  private final Map<String, ClassMapping> mappingsByObf;
  private final Map<String, ClassMapping> mappingsByDeobf;

  private ReflectionRemapperImpl(final Set<ClassMapping> mappings) {
    this.mappingsByObf = Collections.unmodifiableMap(
      mappings.stream().collect(toMap(ClassMapping::obfName, identity()))
    );
    this.mappingsByDeobf = Collections.unmodifiableMap(
      mappings.stream().collect(toMap(ClassMapping::deobfName, identity()))
    );
  }

  @Override
  public String remapClassName(final String className) {
    final @Nullable ClassMapping map = this.mappingsByDeobf.get(className);
    if (map == null) {
      return className;
    }
    return map.obfName();
  }

  @Override
  public String remapFieldName(final Class<?> holdingClass, final String fieldName) {
    final @Nullable ClassMapping clsMap = this.mappingsByObf.get(holdingClass.getName());
    if (clsMap == null) {
      return fieldName;
    }
    return clsMap.fieldsDeobfToObf().getOrDefault(fieldName, fieldName);
  }

  @Override
  public String remapMethodName(final Class<?> holdingClass, final String methodName, final Class<?>... paramTypes) {
    final @Nullable ClassMapping clsMap = this.mappingsByObf.get(holdingClass.getName());
    if (clsMap == null) {
      return methodName;
    }
    return clsMap.methods().getOrDefault(methodKey(methodName, paramTypes), methodName);
  }

  private static String methodKey(final String deobfName, final Class<?>... paramTypes) {
    return deobfName + paramsDescriptor(paramTypes);
  }

  private static String methodKey(final String deobfName, final String obfMethodDesc) {
    return deobfName + paramsDescFromMethodDesc(obfMethodDesc);
  }

  private static String paramsDescriptor(final Class<?>... params) {
    final StringBuilder builder = new StringBuilder();
    for (final Class<?> param : params) {
      builder.append(descriptorString(param));
    }
    return builder.toString();
  }

  private static String paramsDescFromMethodDesc(final String methodDescriptor) {
    String ret = methodDescriptor.substring(1);
    ret = ret.substring(0, ret.indexOf(")"));
    return ret;
  }

  static ReflectionRemapperImpl fromMappingTree(
    final MappingTree tree,
    final String fromNamespace,
    final String toNamespace
  ) {
    final StringPool pool = new StringPool();

    final Set<ClassMapping> mappings = new HashSet<>();

    for (final MappingTree.ClassMapping cls : tree.getClasses()) {
      final Map<String, String> fields = new HashMap<>();
      for (final MappingTree.FieldMapping field : cls.getFields()) {
        fields.put(
          pool.string(field.getName(fromNamespace)),
          pool.string(field.getName(toNamespace))
        );
      }

      final Map<String, String> methods = new HashMap<>();
      for (final MappingTree.MethodMapping method : cls.getMethods()) {
        methods.put(
          pool.string(methodKey(method.getName(fromNamespace), method.getDesc(toNamespace))),
          pool.string(method.getName(toNamespace))
        );
      }

      final ClassMapping map = new ClassMapping(
        cls.getName(toNamespace).replace('/', '.'),
        cls.getName(fromNamespace).replace('/', '.'),
        Collections.unmodifiableMap(fields),
        Collections.unmodifiableMap(methods)
      );

      mappings.add(map);
    }

    return new ReflectionRemapperImpl(mappings);
  }

  /**
   * @param methods deobfMethodName + obfParamsDescriptor -> obfMethodName
   */
  private record ClassMapping(String obfName, String deobfName, Map<String, String> fieldsDeobfToObf,
                              Map<String, String> methods) {

    @Override
      public String toString() {
        return "ClassMapping[" +
                "obfName=" + this.obfName + ", " +
                "deobfName=" + this.deobfName + ", " +
                "fieldsDeobfToObf=" + this.fieldsDeobfToObf + ", " +
                "methods=" + this.methods + ']';
      }
    }
}
