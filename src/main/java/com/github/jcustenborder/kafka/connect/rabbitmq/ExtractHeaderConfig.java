/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
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
package com.github.jcustenborder.kafka.connect.rabbitmq;

import com.github.jcustenborder.kafka.connect.utils.config.ConfigKeyBuilder;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class ExtractHeaderConfig extends AbstractConfig {

  public final String headerName;

  public ExtractHeaderConfig(Map<?, ?> originals) {
    super(config(), originals);
    this.headerName = getString(HEADER_NAME_CONF);
  }

  public static final String HEADER_NAME_CONF = "header.name";
  public static final String HEADER_NAME_DOC = "Header name.";

  public static ConfigDef config() {
    return new ConfigDef()
        .define(
            ConfigKeyBuilder.of(HEADER_NAME_CONF, ConfigDef.Type.STRING)
                .importance(ConfigDef.Importance.HIGH)
                .documentation(HEADER_NAME_DOC)
                .build()
        );
  }

}
