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

package org.apache.tapestry5.validator;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.internal.test.InternalBaseTestCase;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.testng.annotations.Test;

public class MaxTest extends InternalBaseTestCase
{

    @Test
    public void small_enough() throws Exception
    {
        Field field = mockField();
        MessageFormatter formatter = mockMessageFormatter();
        Long constraint = 50L;

        Max validator = new Max(null, mockHtml5Support());

        replay();

        for (int value = 48; value <= 50; value++)
            validator.validate(field, constraint, formatter, value);

        verify();
    }

    @Test
    public void value_too_large() throws Exception
    {
        String label = "My Field";
        Field field = mockFieldWithLabel(label);
        MessageFormatter formatter = mockMessageFormatter();
        String message = "{message}";
        Long constraint = 100l;
        Number value = 101;

        train_format(formatter, message, constraint, label);

        Max validator = new Max(null, mockHtml5Support());

        replay();

        try
        {
            validator.validate(field, constraint, formatter, value);
            unreachable();
        } catch (ValidationException ex)
        {
            assertEquals(ex.getMessage(), message);
        }

        verify();
    }
}
