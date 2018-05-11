package com.intelmas.dto.model;

import java.io.Serializable;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

/** Entity mapping class for nodes_by_organisation key table in cassandra BD.
 * @author Intelma
 *
 */
@PrimaryKeyClass
public class NodesEntityKey implements Serializable {
	
	private static final long serialVersionUID = 5482294210946894868L;

	@PrimaryKeyColumn(name = "node_organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String node_organisation;
	
	@PrimaryKeyColumn(name = "node_type", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String node_type;
	
	@PrimaryKeyColumn(name = "node_name", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String node_name;

	public String getNode_organisation() {
		return node_organisation;
	}

	public void setNode_organisation(String node_organisation) {
		this.node_organisation = node_organisation;
	}

	public String getNode_type() {
		return node_type;
	}

	public void setNode_type(String node_type) {
		this.node_type = node_type;
	}

	public String getNode_name() {
		return node_name;
	}

	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}

	@Override
	public String toString() {
		return "NodesEntityKey [node_organisation=" + node_organisation + ", node_type=" + node_type + ", node_name="
				+ node_name + "]";
	}
}
