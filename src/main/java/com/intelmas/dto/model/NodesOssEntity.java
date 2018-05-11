package com.intelmas.dto.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;


/** Entity mapping class for nodes_by_organisation table in cassandra BD.
 * @author Intelma
 *
 */
@Table(value="nodes_by_organisation")
public class NodesOssEntity  implements Serializable{
	
	private static final long serialVersionUID = -2901402526422899900L;

	@PrimaryKey
	private NodesEntityKey key;
	
//	@Column(value="node_name")
//	private String node_name;
	
	@Column(value="node_datetime")
	private LocalDateTime node_datetime;
	
	@Column(value="node_parent")
	private String node_parent;
	
//	@Column(value="node_type")
//	private String node_type;
	
	@Column(value="node_fqdn_name")
	private String node_fqdn_name;
	
	@Column(value="node_region")
	private String node_region;
	
	@Column(value="node_lat")
	private String node_lat;
	
	@Column(value="node_long")
	private String node_long;
	
	@Column(value="node_ip_address")
	private String node_ip_address;
	
	@Column(value="node_tech")
	private String node_tech;
	
	@Column(value="node_version")
	private String node_version;

	

	public NodesEntityKey getKey() {
		return key;
	}

	public void setKey(NodesEntityKey key) {
		this.key = key;
	}

//	public String getNode_name() {
//		return node_name;
//	}
//
//	public void setNode_name(String node_name) {
//		this.node_name = node_name;
//	}

	public LocalDateTime getNode_datetime() {
		return node_datetime;
	}

	public void setNode_datetime(LocalDateTime node_datetime) {
		this.node_datetime = node_datetime;
	}

	public String getNode_parent() {
		return node_parent;
	}

	public void setNode_parent(String node_parent) {
		this.node_parent = node_parent;
	}

//	public String getNode_type() {
//		return node_type;
//	}
//
//	public void setNode_type(String node_type) {
//		this.node_type = node_type;
//	}

	public String getNode_fqdn_name() {
		return node_fqdn_name;
	}

	public void setNode_fqdn_name(String node_fqdn_name) {
		this.node_fqdn_name = node_fqdn_name;
	}

	public String getNode_region() {
		return node_region;
	}

	public void setNode_region(String node_region) {
		this.node_region = node_region;
	}

	public String getNode_lat() {
		return node_lat;
	}

	public void setNode_lat(String node_lat) {
		this.node_lat = node_lat;
	}

	public String getNode_long() {
		return node_long;
	}

	public void setNode_long(String node_long) {
		this.node_long = node_long;
	}

	public String getNode_ip_address() {
		return node_ip_address;
	}

	public void setNode_ip_address(String node_ip_address) {
		this.node_ip_address = node_ip_address;
	}

	public String getNode_tech() {
		return node_tech;
	}

	public void setNode_tech(String node_tech) {
		this.node_tech = node_tech;
	}

	public String getNode_version() {
		return node_version;
	}

	public void setNode_version(String node_version) {
		this.node_version = node_version;
	}

	@Override
	public String toString() {
		return "NodesOssEntity [key=" + key + ", node_datetime=" + node_datetime
				+ ", node_parent=" + node_parent + ", node_fqdn_name=" + node_fqdn_name + ", node_lat=" + node_lat
				+ ", node_long=" + node_long + ", node_ip_address=" + node_ip_address + ", node_tech=" + node_tech
				+ ", node_version=" + node_version + "]";
	}
	
	public NodeTopologyEntity buildNode(){
		NodeTopologyEntity node = new NodeTopologyEntity();
		
		node.setNode_fqdn_name(node_fqdn_name);
//		node.setNodeDatetime(node_datetime);
		node.setNode_ip_address(node_ip_address);
		node.setNode_lat(node_lat);
		node.setNode_long(node_long);
		node.setNode_region(node_region);
		node.setNode_version(node_version);
		node.setNode_parent(node_parent);
		node.setNode_name(key.getNode_name());
		node.setNode_tech(node_tech);
		node.setNode_type(key.getNode_type());
		node.setNode_organisation(key.getNode_organisation());
		
		
		return node;
	}
}
