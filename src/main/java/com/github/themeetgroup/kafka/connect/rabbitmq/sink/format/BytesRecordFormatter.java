/**
 * Copyright © 2017 Kyumars Sheykh Esmaili (kyumarss@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import org.apache.kafka.connect.converters.ByteArrayConverter;
import org.apache.kafka.connect.sink.SinkRecord;

public class BytesRecordFormatter implements RecordFormatter {

  private final ByteArrayConverter converter;

  public BytesRecordFormatter() {
    converter = new ByteArrayConverter();
  }

  @Override
  public byte[] format(SinkRecord record) {
    return converter.fromConnectData(
          record.topic(),
          record.valueSchema(),
          record.value());
  }
}
