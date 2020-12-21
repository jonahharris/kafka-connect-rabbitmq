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

import io.confluent.connect.avro.AvroData;
import io.confluent.kafka.serializers.NonRecordContainer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AvroFormatter implements RecordFormatter {

  private final AvroData avroData;
  private final EncoderFactory encoderFactory;

  public AvroFormatter() {
    avroData = new AvroData(10);
    encoderFactory = EncoderFactory.get();
  }

  @Override
  public byte[] format(SinkRecord sinkRecord) {
    Schema avroSchema = avroData.fromConnectSchema(sinkRecord.valueSchema());
    Object o = avroData.fromConnectData(sinkRecord.valueSchema(), sinkRecord.value());
    if (o == null) {
      return null;
    }
    return serialize(o, avroSchema);
  }

  private byte[] serialize(Object object, Schema schema) {
    Object value = object instanceof NonRecordContainer ? ((NonRecordContainer) object).getValue() : object;
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      if (schema.getType() == Schema.Type.BYTES) {
        if (value instanceof byte[]) {
          out.write(((byte[]) value));
        } else {
          if (!(value instanceof ByteBuffer)) {
            throw new DataException("Error serializing message to format Avro. Unrecognized bytes object of type: " + value.getClass().getName());
          }
          out.write(((ByteBuffer) value).array());
        }
      } else {
        BinaryEncoder encoder = encoderFactory.directBinaryEncoder(out, null);
        DatumWriter<Object> writer;
        if (value instanceof SpecificRecord) {
          writer = new SpecificDatumWriter<>(schema);
        } else {
          writer = new GenericDatumWriter<>(schema);
        }
        writer.write(value, encoder);
        encoder.flush();
      }

      return out.toByteArray();
    } catch (IOException e) {
      throw new DataException("Error serializing message to format Avro", e);
    }
  }
}
