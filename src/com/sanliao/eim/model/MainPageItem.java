package com.sanliao.eim.model;

/**
 * 
 * 主页面菜单项.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class MainPageItem {

	private String name;
	private Integer image;

	public MainPageItem(String name, Integer image) {
		super();
		this.name = name;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getImage() {
		return image;
	}

	public void setImage(Integer image) {
		this.image = image;
	}

}
