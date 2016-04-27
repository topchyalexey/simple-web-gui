package ru.simpleweb.gui.payments.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.Gson;

@Entity
@Table(name = "payment_types.form_fields")
@XmlRootElement
public class Field implements Serializable {

	private static final long serialVersionUID = -1753346309579673877L;

    @Id
    @Column(name = "id_form_field", nullable = false)
    private Integer id;
 
    @JoinColumn(name = "id_form", referencedColumnName = "id_form")
    @ManyToOne(fetch = FetchType.EAGER)
    private Form form;
    
    @Column(name="code")
    private String code;

    @Column
    private String type;

    @Column
    private String description;

    @Transient
    private String value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

    
}