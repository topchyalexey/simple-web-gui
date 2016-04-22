package ru.simpleweb.gui.payments.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "payment_types.forms")
// http://javaee.support/sample/jpa-native-sql-resultset-mapping/
//@SqlResultSetMapping(name = "fromSqlRs", entities = {@EntityResult(entityClass = Form.class, fields = {@FieldResult(name = "identifier", column = "id_form"), @FieldResult(name = "funcCode", column = "func_code")})})
@XmlRootElement
public class Form implements Serializable {
    private static final long serialVersionUID = 1L;
 
    @Id
    @Column(name = "id_form", nullable = false)
    private Integer id;
 
    @Column(name="id_service")
    private Integer service;

    @Column(name="func_code")
    private String funcCode;

    @Column
    private String description;
 
    //@OneToMany(mappedBy="form", cascade={CascadeType.ALL})
    //private Set<Field> fields;
 
    public void setId(Integer id) {
		this.id = id;
	}
    
    public Integer getId() {
		return id;
	}
    
    public void setService(Integer service) {
		this.service = service;
	}
    
    public Integer getService() {
		return service;
	}
    
    public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}
    
    public String getFuncCode() {
		return funcCode;
	}
    
    public void setDescription(String description) {
		this.description = description;
	}
    
    public String getDescription() {
		return description;
	}
      
}
