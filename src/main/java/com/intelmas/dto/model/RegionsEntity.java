package com.intelmas.dto.model;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

/** Entity mapping class for region_list table in cassandra BD.
 * @author Intelma
 *
 */
@Table(value="region_list")
public class RegionsEntity {

	
	
	@PrimaryKeyColumn(value="region_organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String region_organisation;
	
	@PrimaryKeyColumn(value="region_name", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String region_name;

	public String getRegion_organisation() {
		return region_organisation;
	}

	public void setRegion_organisation(String region_organisation) {
		this.region_organisation = region_organisation;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	@Override
	public String toString() {
		return "RegionsEntity [region_organisation=" + region_organisation + ", region_name=" + region_name + "]";
	}
}
