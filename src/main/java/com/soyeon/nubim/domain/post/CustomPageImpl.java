package com.soyeon.nubim.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.Getter;
import lombok.Setter;

/**
 * 랜덤 시드값 전송을 위한 커스텀 클래스
 */
@Setter
@Getter
public class CustomPageImpl<T> extends PageImpl<T> {
	private Float randomSeed;

	public CustomPageImpl(Page<T> page) {
		super(page.getContent(), page.getPageable(), page.getTotalElements());
	}
}
