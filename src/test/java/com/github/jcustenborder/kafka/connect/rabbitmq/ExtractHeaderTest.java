package com.github.jcustenborder.kafka.connect.rabbitmq;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jcustenborder.kafka.connect.utils.data.NamedTest;
import com.github.jcustenborder.kafka.connect.utils.data.TestDataUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertConnectRecord.assertRecord;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ExtractHeaderTest {
  @TestFactory
  public Stream<DynamicTest> apply() throws IOException {
    return TestDataUtils.loadJsonResourceFiles(this.getClass().getPackage().getName() + ".extractheader", TestCase.class)
        .stream()
        .map(t -> dynamicTest(t.testName(), () -> {
          final boolean isKey = t.testName().startsWith("key");
          final Transformation<SourceRecord> transformation = isKey ? new ExtractHeader.Key() : new ExtractHeader.Value();
          transformation.configure(
              ImmutableMap.of(ExtractHeaderConfig.HEADER_NAME_CONF, "foo")
          );
          final SourceRecord actual = transformation.apply(t.input);
          assertRecord(t.expected, actual);
        }));
  }

  public static class TestCase implements NamedTest {
    @JsonIgnore
    private String testName;
    public SourceRecord input;
    public SourceRecord expected;

    @Override
    public String testName() {
      return this.testName;
    }

    @Override
    public void testName(String s) {
      this.testName = s;
    }
  }
}
