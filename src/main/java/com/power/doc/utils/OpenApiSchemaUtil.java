/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2022 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.utils;

import com.power.common.util.CollectionUtil;
import com.power.common.util.MD6Util;
import com.power.common.util.StringUtil;
import com.power.doc.model.ApiParam;
import com.thoughtworks.qdox.model.JavaParameter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.ARRAY;

/**
 * @author yu 2020/11/29.
 */
public class OpenApiSchemaUtil {

    public final static String NO_BODY_PARAM = "NO_BODY_PARAM";
    final static  Pattern  p =  Pattern.compile("[A-Z]\\w+.*?|[A-Z]");
    public static Map<String, Object> primaryTypeSchema(String primaryType) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
        return map;
    }

    public static Map<String, Object> mapTypeSchema(String primaryType) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "object");
        Map<String, Object> items = new HashMap<>();
        items.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
        map.put("additionalProperties", items);
        return map;
    }

    public static Map<String, Object> arrayTypeSchema(String primaryType) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "array");
        Map<String, Object> items = new HashMap<>();
        items.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
        map.put("items", items);
        return map;
    }

    public static Map<String, Object> returnSchema(String returnGicName) {
        if (StringUtil.isEmpty(returnGicName)) {
            return null;
        }
        returnGicName = returnGicName.replace(">", "");
        String[] types = returnGicName.split("<");
        StringBuilder builder = new StringBuilder();
        for (String str : types) {
            builder.append(DocClassUtil.getSimpleName(str).replace(",", ""));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("$ref", builder.toString());
        return map;
    }

    public static String getClassNameFromParams(List<ApiParam> apiParams) {
        // if array[Primitive] or Primitive
        if (CollectionUtil.isNotEmpty(apiParams) && apiParams.size() == 1
                && CollectionUtil.isEmpty(apiParams.get(0).getChildren())) {
            return "string";
        }
        for(ApiParam a : apiParams){
            if(StringUtil.isNotEmpty(a.getClassName())){
                 return OpenApiSchemaUtil.delClassName(a.getClassName());
            }
        }
        return NO_BODY_PARAM;
    }
    public static String  delClassName(String className){
        return String.join("", getPatternResult(p, className));
    }

    public static List<String> getPatternResult(Pattern p,String content){
        List<String> matchers = new ArrayList<>();
        Matcher m = p.matcher(content);
        while (m.find()){
            matchers.add(m.group());
        }
        return matchers;
    }
    public static List<String> getPatternResult(String rex,String content){
        Pattern p = Pattern.compile(rex);
        List<String> matchers = new ArrayList<>();
        Matcher m = p.matcher(content);
        while (m.find()){
            matchers.add(m.group());
        }
        return matchers;
    }
}
