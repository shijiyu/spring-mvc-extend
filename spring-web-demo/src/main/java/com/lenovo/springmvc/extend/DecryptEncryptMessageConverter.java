package com.lenovo.springmvc.extend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.lenovo.springmvc.demo.utils.Bash64Utils;

public class DecryptEncryptMessageConverter extends FastJsonHttpMessageConverter4 implements InitializingBean {

    private RequestDecryptBodyProcessor requestDecryptResponseEncryptBodyProcessor;

    public void setRequestDecryptResponseEncryptBodyProcessor(RequestDecryptBodyProcessor requestDecryptResponseEncryptBodyProcessor) {
        this.requestDecryptResponseEncryptBodyProcessor = requestDecryptResponseEncryptBodyProcessor;
    }

    public RequestDecryptBodyProcessor getRequestDecryptResponseEncryptBodyProcessor() {
        return requestDecryptResponseEncryptBodyProcessor;
    }

    public DecryptEncryptMessageConverter() {
        super();
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        InputStream in = inputMessage.getBody();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        if (requestDecryptResponseEncryptBodyProcessor != null) {
            String input = requestDecryptResponseEncryptBodyProcessor.decryptRequestBody(inputMessage, fastJsonConfig.getCharset());

            byte[] bytes = input.getBytes(fastJsonConfig.getCharset());
            return JSON.parseObject(bytes, 0, bytes.length, fastJsonConfig.getCharset(), type, fastJsonConfig.getFeatures());
        }
        return JSON.parseObject(in, fastJsonConfig.getCharset(), type, fastJsonConfig.getFeatures());
    }

	@Override
    protected void writeInternal(Object obj, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        //ByteArrayOutputStream outnew = new ByteArrayOutputStream();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        JSONObject json = (JSONObject) JSON.toJSON(obj);
        //修改主键为ID的值 设置成base64编码值
        json = changeIdValue(json);
        String jsonString = json.toJSONString();;
        if (requestDecryptResponseEncryptBodyProcessor != null) {
            jsonString = requestDecryptResponseEncryptBodyProcessor.encryptResponseBody(jsonString, headers, fastJsonConfig.getCharset());
        }
       
        byte[] bytes = jsonString.getBytes(fastJsonConfig.getCharset());
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setContentLength(bytes.length);
        OutputStream out = outputMessage.getBody();
        out.write(bytes);
        //outnew.writeTo(out);
        //outnew.close();
        //out.close();
    }
	private JSONObject changeIdValue(JSONObject json){
		Set<String> keys = json.keySet();
		for(String key:keys){
			if("id".equals(key)){
				json.put(key, Bash64Utils.enBase64(json.getInteger(key).toString()));
				continue;
			}
			Object obj = json.get(key);
			if(obj instanceof JSONArray){
				JSONArray array = (JSONArray)obj;
				int size =  array.size();
				for(int i=0;i<size;i++){
					JSONObject jsonObject = array.getJSONObject(i);
					changeIdValue(jsonObject);
				}
			}
		}
		 // 得到类对象
		return json;
	}

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        InputStream in = inputMessage.getBody();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        if (requestDecryptResponseEncryptBodyProcessor != null) {
            String input = requestDecryptResponseEncryptBodyProcessor.decryptRequestBody(inputMessage, fastJsonConfig.getCharset());
            return JSON.parseObject(input.getBytes(fastJsonConfig.getCharset()), 0, input.length(), fastJsonConfig.getCharset(), clazz, fastJsonConfig.getFeatures());
        }
        return JSON.parseObject(in, fastJsonConfig.getCharset(), clazz, fastJsonConfig.getFeatures());

    }

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 重新排列处理方法，不然map会先处理，从而加密不了数据
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>(requestMappingHandlerAdapter.getReturnValueHandlers());

        int requestResponseBodyMethodProcessorIndex = 0;
        int requestDecryptResponseEncryptBodyMethodProcessorIndex = 0;
        RequestEncryptBodyMethodProcessor requestDecryptResponseEncryptBodyMethodProcessor = null;
        for (int i = 0, length = handlers.size(); i < length; i++) {
            HandlerMethodReturnValueHandler handler = handlers.get(i);
            if (handler instanceof RequestEncryptBodyMethodProcessor) {
                requestDecryptResponseEncryptBodyMethodProcessor = (RequestEncryptBodyMethodProcessor) handler;
                requestDecryptResponseEncryptBodyMethodProcessorIndex = i;
            } else if (handler instanceof RequestResponseBodyMethodProcessor) {
                requestResponseBodyMethodProcessorIndex = i;
            }

        }

        if (requestDecryptResponseEncryptBodyMethodProcessor != null) {
            handlers.remove(requestDecryptResponseEncryptBodyMethodProcessorIndex);
            handlers.add(requestResponseBodyMethodProcessorIndex + 1, requestDecryptResponseEncryptBodyMethodProcessor);
        }

        requestMappingHandlerAdapter.setReturnValueHandlers(handlers);

        //
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>(requestMappingHandlerAdapter.getArgumentResolvers());
        RequestEncryptBodyMethodProcessor requestDecryptResponseEncryptBodyMethodProcessor2 = null;
        for (int i = 0, length = argumentResolvers.size(); i < length; i++) {
            HandlerMethodArgumentResolver argumentResolver = argumentResolvers.get(i);
            if (argumentResolver instanceof RequestEncryptBodyMethodProcessor) {
                requestDecryptResponseEncryptBodyMethodProcessor2 = (RequestEncryptBodyMethodProcessor) argumentResolver;
                requestDecryptResponseEncryptBodyMethodProcessorIndex = i;
            } else if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                requestResponseBodyMethodProcessorIndex = i;
            }
        }

        if (requestDecryptResponseEncryptBodyMethodProcessor2 != null) {
            argumentResolvers.remove(requestDecryptResponseEncryptBodyMethodProcessorIndex);
            argumentResolvers.add(requestResponseBodyMethodProcessorIndex + 1, requestDecryptResponseEncryptBodyMethodProcessor2);
        }

        requestMappingHandlerAdapter.setArgumentResolvers(argumentResolvers);

    }

}
