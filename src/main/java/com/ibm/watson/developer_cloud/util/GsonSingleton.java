/**
 * Copyright 2015 IBM Corp. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.watson.developer_cloud.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson singleton to be use when transforming from JSON to Java Objects and vise versa. It handles
 * date formatting and pretty print the result
 */
public class GsonSingleton {

  private static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  private static Gson gson;

  /**
   * Creates a {@link com.google.gson.Gson} object that can be use to serialize and deserialize Java
   * objects}
   * 
   * @return the Gson
   */
  private static Gson createGson() {
    return new GsonBuilder().setPrettyPrinting().setDateFormat(DATE_FORMAT_UTC).create();
  }

  /**
   * Gets the Gson instance
   * 
   * @return the Gson
   */
  public static Gson getGson() {
    if (gson == null) {
      gson = createGson();
    }
    return gson;
  }
}
