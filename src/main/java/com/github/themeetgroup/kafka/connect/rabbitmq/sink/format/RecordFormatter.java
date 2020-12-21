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

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;

public interface RecordFormatter {

  byte[] format(SinkRecord sinkRecord);

  static RecordFormatter getInstance(String type) {
    if ("bytes".equals(type)) {
      return new BytesRecordFormatter();
    } else if ("json".equals(type)) {
      return new JsonRecordFormatter();
    } else if ("avro".equals(type)) {
      return new AvroFormatter();
    }
    throw new ConnectException("The provided format type is not one of 'bytes', 'json' or 'avro', but: " + type);
  }
}
