// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.ioc.internal.services;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.annotations.Value;
import org.apache.tapestry5.ioc.services.Builtin;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.TypeCoercer;

/**
 * Provides an object when the {@link Value} annotation is present. The string value has symbols expanded, and then is
 * {@link TypeCoercer coerced} to the associated type.   The value may first be coerced to an intermediate type if the
 * {@link IntermediateType} annotation is present.
 */
public class ValueObjectProvider implements ObjectProvider
{
    private final SymbolSource symbolSource;

    private final TypeCoercer typeCoercer;

    public ValueObjectProvider(@Builtin SymbolSource symbolSource,

                               @Builtin TypeCoercer typeCoercer)
    {
        this.symbolSource = symbolSource;
        this.typeCoercer = typeCoercer;
    }

    @Override
    public <T> T provide(Class<T> objectType, AnnotationProvider annotationProvider, ObjectLocator locator)
    {
        Value annotation = annotationProvider.getAnnotation(Value.class);

        if (annotation == null) return null;

        String value = annotation.value();
        Object expanded = symbolSource.expandSymbols(value);

        IntermediateType intermediate = annotationProvider.getAnnotation(IntermediateType.class);

        if (intermediate != null) expanded = typeCoercer.coerce(expanded, intermediate.value());

        return typeCoercer.coerce(expanded, objectType);
    }
}
