package com.scai.entities.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "demandplanner_hierarchy", schema = "public")
public class DemandPlannerHierarchy {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "demandplannerhierarchy_id")
	private long demandPlannerHierarchyId;

	@Column(name = "level")
	private String level;

	@Column(name = "value")
	private String value;

	@ManyToOne
	@JoinColumn(name = "personnelId", nullable = false)
	private Personnel personnel;

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

	public Personnel getPersonnel() {
		return personnel;
	}

	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}

	public long getDemandPlannerHierarchyId() {
		return demandPlannerHierarchyId;
	}

}
