package com.scai.entities.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sku_hierarchy", schema = "public")
public class SkuHierarchy implements Serializable {

	private static final long serialVersionUID = -1293360438506861166L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "sku_hier_id")
	private long skuHierId;

	@Column(name = "level")
	private String level;

	@Column(name = "value")
	private String value;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "sku_id")
	private Sku sku;

	public SkuHierarchy() {

	}

	public SkuHierarchy(String levels, String value, Sku sku) {
		super();
		this.level = levels;
		this.value = value;
		this.sku = sku;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Sku getSku() {
		return sku;
	}

	public void setSku(Sku sku) {
		this.sku = sku;
	}

	public long getSkuHierId() {
		return skuHierId;
	}
}
