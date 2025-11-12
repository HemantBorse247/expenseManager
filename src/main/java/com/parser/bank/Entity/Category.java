package com.parser.bank.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "category")

public class Category {
	@Id
	private String id;
	private String cat_name; // narration
	private String cat_value;

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Category() {
		super();
		// TODO Auto-generated constructor stub
	}

}
