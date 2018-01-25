/*
 * Copyright 2016    https://github.com/sdcuike Inc. 
 * All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lenovo.springmvc.extend;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.util.UriComponentsBuilder;

import com.lenovo.springmvc.demo.utils.Bash64Utils;

/**
 * 自定义注解处理器
 * 
 * @author sdcuike
 *
 *         Created At 2016年10月26日 下午9:44:25
 */
public class PathVariableDecryptMethodProcessor extends PathVariableMethodArgumentResolver {

	private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (!parameter.hasParameterAnnotation(PathVariableDecrypt.class)) {
			return false;
		}
		if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
			String paramName = parameter.getParameterAnnotation(PathVariableDecrypt.class).value();
			return StringUtils.hasText(paramName);
		}
		return true;
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		PathVariableDecrypt annotation = parameter.getParameterAnnotation(PathVariableDecrypt.class);
		return new PathVariableNamedValueInfo(annotation);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
		Map<String, String> uriTemplateVars = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		String value = (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
		if(value!=null){
			  return new String(Bash64Utils.deBase64(value));
		}
		return value;
	}

	@Override
	public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder,
			Map<String, Object> uriVariables, ConversionService conversionService) {

		if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
			return;
		}

		PathVariableDecrypt ann = parameter.getParameterAnnotation(PathVariableDecrypt.class);
		String name = (ann != null && !StringUtils.isEmpty(ann.value()) ? ann.value() : parameter.getParameterName());
		value = formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
		uriVariables.put(name, value);
	}

	protected String formatUriValue(ConversionService cs, TypeDescriptor sourceType, Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return (String) value;
		} else if (cs != null) {
			return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
		} else {
			return value.toString();
		}
	}

	private static class PathVariableNamedValueInfo extends NamedValueInfo {

		public PathVariableNamedValueInfo(PathVariableDecrypt annotation) {
			super(annotation.name(), annotation.required(), ValueConstants.DEFAULT_NONE);
		}
	}

}
