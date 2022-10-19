package com.scai.entities.model;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "personnel", schema = "public")
public class Personnel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "personnel_iD")
	private long personnelID;

	@Column(name = "sku_code")
	private String skuCode;

	@Column(name = "sku_name")
	private String skuName;

	@Column(name = "location_code")
	private String locationCode;

	@Column(name = "location_name")
	private String locationName;

	@Column(name = "tenant_id")
	private String tenantID;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "personnel", cascade = CascadeType.ALL)
	private Set<SalesmanHierarchy> salesmanHierarchy;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "personnel", cascade = CascadeType.ALL)
	private Set<DemandPlannerHierarchy> demandPlannerHierarchy;

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getTenantID() {
		return tenantID;
	}

	public void setTenantID(String tenantID) {
		this.tenantID = tenantID;
	}

	public Set<SalesmanHierarchy> getSalesmanHierarchy() {
		return salesmanHierarchy;
	}

	public void setSalesmanHierarchy(Set<SalesmanHierarchy> salesmanHierarchy) {
		this.salesmanHierarchy = salesmanHierarchy;
	}

	public Set<DemandPlannerHierarchy> getDemandPlannerHierarchy() {
		return demandPlannerHierarchy;
	}

	public void setDemandPlannerHierarchy(Set<DemandPlannerHierarchy> demandPlannerHierarchy) {
		this.demandPlannerHierarchy = demandPlannerHierarchy;
	}

	public long getPersonnelID() {
		return personnelID;
	}

}
