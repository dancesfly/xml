package com.test.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class User {

	int id;
	String name;
	
	@XStreamAlias("cmmdtyCode")  
	List<String> listA;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List getListA() {
		return listA;
	}

	public void setListA(List listA) {
		this.listA = listA;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", listA=" + listA + "]";
	}
}
