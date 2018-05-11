package com.intelmas.dto.model;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

/** Entity mapping class for oss_list table in cassandra BD.
 * @author Intelma
 *
 */
@Table("node_type_list")
public class NodesTypeEntity {

	@PrimaryKeyColumn(value="type_name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String type_name;
	
	@PrimaryKeyColumn(value="type_tech", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String type_tech;
	
	@PrimaryKeyColumn(value="type_organisation", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String type_organisation;
	
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getType_organisation() {
		return type_organisation;
	}
	public void setType_organisation(String type_organisation) {
		this.type_organisation = type_organisation;
	}
	public String getType_tech() {
		return type_tech;
	}
	public void setType_tech(String type_tech) {
		this.type_tech = type_tech;
	}
	@Override
	public String toString() {
		return "NodesTypeEntity [type_name=" + type_name + ", type_organisation=" + type_organisation + ", type_tech="
				+ type_tech + "]";
	}
}
