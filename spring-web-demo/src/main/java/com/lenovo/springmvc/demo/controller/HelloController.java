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
package com.lenovo.springmvc.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableList;
import com.lenovo.springmvc.vo.MessageInfoVo;
import com.lenovo.springmvc.vo.SubMessageInfoVo;

/**
 * @author shijy2
 *
 *         Created At 2016年8月28日 下午11:44:00
 */
@Controller
@RequestMapping(value = "/app")
public class HelloController {

    @RequestMapping(value = "/hello", method = { RequestMethod.POST })
    @ResponseBody
    public String hello() {
        return "hello web";
    }

    @RequestMapping(value = "/hello1", method = { RequestMethod.POST })
    @ResponseBody
    public MessageInfoVo hello1( MessageInfoVo message) {
    	MessageInfoVo info = new MessageInfoVo();
    	info.setId(1);
    	info.setName("test");
        return info;
    }

    @RequestMapping(value = "/hello2", method = { RequestMethod.POST })
    @ResponseBody
    public MessageInfoVo hello2(@RequestBody MessageInfoVo message) {
        return message;
    }

    @RequestMapping(value = "/hello3/{path}", method = { RequestMethod.POST })
    @ResponseBody
    public MessageInfoVo hello3(MessageInfoVo message,@PathVariable String path) {
    	MessageInfoVo info = new MessageInfoVo();
    	info.setId(1);
    	info.setName("test");
    	SubMessageInfoVo subinfo = new SubMessageInfoVo();
    	subinfo.setId(2);
    	subinfo.setTitle("ttt11");
    	subinfo.setName("中文测试下 看是否有乱码");
    	info.setSubMessage(ImmutableList.<SubMessageInfoVo>of(subinfo));
        return info;
    }


    @RequestMapping(value = "/hello6", method = { RequestMethod.POST })
    @ResponseBody
    public MessageInfoVo hello6(@RequestBody MessageInfoVo message) {
        return message;
    }

    @RequestMapping(value = "/hello4", method = { RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> hello4(@RequestBody Map<String, Object> message) {
        return message;
    }

    @RequestMapping(value = "/hello5", method = { RequestMethod.POST })
    @ResponseBody
    public List<Object> hello5(@RequestBody List<Object> message) {
        return message;
    }

   
}
