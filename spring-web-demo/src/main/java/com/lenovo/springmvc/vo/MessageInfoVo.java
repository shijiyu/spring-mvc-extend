package com.lenovo.springmvc.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MessageInfoVo {
	private String name;
	@MaskField
    private Integer id;
	
    private List<SubMessageInfoVo> subMessage;
}
