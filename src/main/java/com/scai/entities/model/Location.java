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
@Table(name = "location", schema = "public")
public class Location {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "location_iD")
	private long locationID;
	
	@Column(name = "location_code")
	private String locationCode;
	
	@Column(name = "location_type")
	private String locationType;
	
	@Column(name = "location_address")
	private String locationAddress;
	
	@Column(name = "location_name")
	private String locationName;
	
	@Column(name = "tenant_id")
	private String tenantID;
	
	@Column(name = "latitude")
	private String latitude;
	
	@Column(name = "langitude")
	private String longitude;
	
	@Column(name = "pin_code")
	private String pinCode;
	
	@Column(name = "location_capacity")
	private String locationCapacity;

	@Column(name = "location_cluster")
	private String locationCluster;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "location", cascade = CascadeType.ALL)
	private Set<LocationHierarchy> locaytionHierarchy;

	public long getLocationID() {
		return locationID;
	}

	public void setLocationID(long locationID) {
		this.locationID = locationID;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getLocationCapacity() {
		return locationCapacity;
	}

	public void setLocationCapacity(String locationCapacity) {
		this.locationCapacity = locationCapacity;
	}

	public String getLocationCluster() {
		return locationCluster;
	}

	public void setLocationCluster(String locationCluster) {
		this.locationCluster = locationCluster;
	}

	public Set<LocationHierarchy> getLocaytionHierarchy() {
		return locaytionHierarchy;
	}

	public void setLocaytionHierarchy(Set<LocationHierarchy> locaytionHierarchy) {
		this.locaytionHierarchy = locaytionHierarchy;
	}

}
