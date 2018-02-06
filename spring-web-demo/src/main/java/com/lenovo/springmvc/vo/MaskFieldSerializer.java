package com.lenovo.springmvc.vo;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MaskFieldSerializer extends JsonSerializer<Object> {

	@Override
	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString("******");
	}
}