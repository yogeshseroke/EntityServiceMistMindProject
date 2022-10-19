package com.scai.entities.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "location_hierarchy", schema = "public")
public class LocationHierarchy {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "location_hierarchy_id")
	private long locationHierarchyID;

	@Column(name = "level")
	private String level;

	@Column(name = "value")
	private String value;

	@ManyToOne
	@JoinColumn(name = "locationId", nullable = false)
	private Location location;

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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public long getLocationHierarchyID() {
		return locationHierarchyID;
	}

}
