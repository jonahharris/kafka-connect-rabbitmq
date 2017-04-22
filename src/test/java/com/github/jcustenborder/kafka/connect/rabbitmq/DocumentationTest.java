/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
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
package com.github.jcustenborder.kafka.connect.rabbitmq;

import com.github.jcustenborder.kafka.connect.utils.BaseDocumentationTest;
import org.apache.kafka.connect.data.Schema;

import java.util.Arrays;
import java.util.List;

public class DocumentationTest extends BaseDocumentationTest {
  @Override
  protected String[] packages() {
    return new String[]{this.getClass().getPackage().getName()};
  }

  @Override
  protected List<Schema> schemas() {
    return Arrays.asList(
        MessageConverter.SCHEMA_HEADER_VALUE,
        MessageConverter.SCHEMA_ENVELOPE,
        MessageConverter.SCHEMA_BASIC_PROPERTIES,
        MessageConverter.SCHEMA_VALUE,
        MessageConverter.SCHEMA_KEY
    );
  }
}
