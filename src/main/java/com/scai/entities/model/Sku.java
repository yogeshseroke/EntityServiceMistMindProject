package com.scai.entities.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "sku", schema = "public", uniqueConstraints = {
		@UniqueConstraint(name = "UniqueSkuLocationAndTenantId", columnNames = { "sku_code", "location_code",
				"tenant_id" }) })
public class Sku implements Serializable {

	private static final long serialVersionUID = -3773576976627567572L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "sku_id")
	private long skuId;

	@Column(name = "sku_name")
	private String skuName;

	@Column(name = "location_name")
	private String locationName;

	@Column(name = "sku_code")
	private String skuCode;

	@Column(name = "location_code")
	private String locationCode;

	@Column(name = "sale_price_unit")
	private double salePriceUnit;

	@Column(name = "tenant_id")
	private String tenantId;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "sku", cascade = CascadeType.ALL)
	private Set<SkuHierarchy> skuHierarchy;

	public Sku() {
	}

	public Sku(String skuName, String locationName, String skuCode, String locationCode, double salePriceUnit,
			String tenantId) {
		super();
		// this.skuEmbedded = new SkuEmbedded(skuName, locationName, skuCode);
		this.skuName = skuName;
		this.locationName = locationName;
		this.skuCode = skuCode;
		this.locationCode = locationCode;
		this.salePriceUnit = salePriceUnit;
		this.tenantId = tenantId;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public double getSalePriceUnit() {
		return salePriceUnit;
	}

	public void setSalePriceUnit(double salePriceUnit) {
		this.salePriceUnit = salePriceUnit;
	}

	public long getSkuId() {
		return skuId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Set<SkuHierarchy> getSkuHierarchy() {
		return skuHierarchy;
	}

	public void setSkuHierarchy(Set<SkuHierarchy> skuHierarchy) {
		this.skuHierarchy = skuHierarchy;
	}

}
