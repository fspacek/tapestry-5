// Copyright 2006, 2007, 2010, 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.services;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.services.MasterObjectProvider;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.services.transform.InjectionProvider2;

import java.lang.annotation.Annotation;

/**
 * Worker for the {@link org.apache.tapestry5.ioc.annotations.Inject} annotation that delegates out to the master
 * {@link org.apache.tapestry5.ioc.services.MasterObjectProvider} to access the value. This worker must be scheduled
 * after certain other workers, such as {@link BlockInjectionProvider} (which is keyed off a combination of type and
 * the Inject annotation).
 *
 * @see org.apache.tapestry5.ioc.services.MasterObjectProvider
 */
public class DefaultInjectionProvider implements InjectionProvider2
{
    private final MasterObjectProvider masterObjectProvider;

    private final ObjectLocator locator;

    private final ComponentClassCache classCache;

    public DefaultInjectionProvider(MasterObjectProvider masterObjectProvider, ObjectLocator locator, ComponentClassCache classCache)
    {
        this.masterObjectProvider = masterObjectProvider;
        this.locator = locator;
        this.classCache = classCache;
    }

    public boolean provideInjection(final PlasticField field, ObjectLocator locator, MutableComponentModel componentModel)
    {
        Class fieldType = classCache.forName(field.getTypeName());

        Object injectionValue = masterObjectProvider.provide(fieldType, new AnnotationProvider()
        {
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
            {
                return field.getAnnotation(annotationClass);
            }
        }, this.locator, false);

        // Null means that no ObjectProvider could provide the value. We have set up the chain of
        // command so that InjectResources can give it a try next. Later, we'll try to match against
        // a service.

        if (injectionValue != null)
        {
            field.inject(injectionValue);
            return true;
        }

        return false;
    }
}
