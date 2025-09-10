package org.sheashepherd.ghostnet.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ghost_net")
public class GhostNet implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double lat;
	private Double lon;

	@Column(length = 100)
	private String size;

	@Enumerated(EnumType.STRING)
	@Column(length = 16, nullable = false)
	private GhostNetStatus status = GhostNetStatus.REPORTED;

	@Column(length = 100)
	private String reporterName;
	@Column(length = 30)
	private String reporterPhone;

	@Column(length = 100)
	private String missingReporterName;
	@Column(length = 30)
	private String missingReporterPhone;

	/** Person, die die Bergung (Claim) übernommen hat */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "salvor_id")
	private Person salvor;

	/** Person, die das Netz tatsächlich geborgen hat */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recovered_by_id")
	private Person recoveredBy;

	public GhostNet() {
	}

	public GhostNet(Double lat, Double lon, String size, String reporterName, String reporterPhone) {
		this.lat = lat;
		this.lon = lon;
		this.size = size;
		this.reporterName = reporterName;
		this.reporterPhone = reporterPhone;
		this.status = GhostNetStatus.REPORTED;
	}

	public Long getId() {
		return id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public GhostNetStatus getStatus() {
		return status;
	}

	public void setStatus(GhostNetStatus status) {
		this.status = status;
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public String getReporterPhone() {
		return reporterPhone;
	}

	public void setReporterPhone(String reporterPhone) {
		this.reporterPhone = reporterPhone;
	}

	public String getMissingReporterName() {
		return missingReporterName;
	}

	public void setMissingReporterName(String v) {
		this.missingReporterName = v;
	}

	public String getMissingReporterPhone() {
		return missingReporterPhone;
	}

	public void setMissingReporterPhone(String v) {
		this.missingReporterPhone = v;
	}

	public Person getSalvor() {
		return salvor;
	}

	public void setSalvor(Person salvor) {
		this.salvor = salvor;
	}

	public Person getRecoveredBy() {
		return recoveredBy;
	}

	public void setRecoveredBy(Person recoveredBy) {
		this.recoveredBy = recoveredBy;
	}
}

