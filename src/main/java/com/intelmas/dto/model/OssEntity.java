package com.intelmas.dto.model;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

/** Entity mapping class for oss table in cassandra BD.
 * @author Intelma
 *
 */
@Table(value="oss_list")
public class OssEntity {

	
	
	@PrimaryKeyColumn(value="oss_organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String oss_organisation;
	
	@PrimaryKeyColumn(value="oss_name", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String oss_name;
	
	@Column(value="oss_tech")
	private String oss_tech;

	public String getOss_organisation() {
		return oss_organisation;
	}

	public void setOss_organisation(String oss_organisation) {
		this.oss_organisation = oss_organisation;
	}

	public String getOss_name() {
		return oss_name;
	}

	public void setOss_name(String oss_name) {
		this.oss_name = oss_name;
	}

	public String getOss_tech() {
		return oss_tech;
	}

	public void setOss_tech(String oss_tech) {
		this.oss_tech = oss_tech;
	}

	@Override
	public String toString() {
		return "OssEntity [oss_organisation=" + oss_organisation + ", oss_name=" + oss_name + ", oss_tech=" + oss_tech
				+ "]";
	}
}
