package org.sheashepherd.ghostnet.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.util.List;

import org.sheashepherd.ghostnet.model.GhostNet;
import org.sheashepherd.ghostnet.model.GhostNetStatus;
import org.sheashepherd.ghostnet.model.Person;
import org.sheashepherd.ghostnet.repo.GhostNetRepository;
import org.sheashepherd.ghostnet.repo.PersonRepository;

@Named("gn")
@SessionScoped
public class GhostNetBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	GhostNetRepository repo;
	@Inject
	PersonRepository persons;
	@Inject
	AuthBean auth;

	// Paging
	private int page = 0;
	private final int pageSize = 10;

	// Modal
	private boolean modalOpen;
	private String modalAction;
	private String modalTitle;
	private Long selectedId;

	// Report-Felder
	private Double lat;
	private Double lon;
	private String size;

	// Eingabe-Felder
	private boolean anonymous = true;
	private String modalName;
	private String modalPhone;

	// ===== Daten / Paging =====
	public List<GhostNet> getPageItems() {
		return repo.page(page, pageSize);
	}

	public long getPageCount() {
		long total = repo.count();
		return (total + pageSize - 1) / pageSize;
	}

	public boolean isHasPrev() {
		return page > 0;
	}

	public boolean isHasNext() {
		return page + 1 < getPageCount();
	}

	public String prevPage() {
		if (isHasPrev())
			page--;
		return null;
	}

	public String nextPage() {
		if (isHasNext())
			page++;
		return null;
	}

	// ===== Modal öffnen =====
	public String openReport() {
		selectedId = null;
		modalAction = "report";
		modalTitle = "Netz melden";
		modalOpen = true;
		size = null;
		anonymous = true;
		modalName = modalPhone = "";
		return null;
	}

	public String openClaim(Long id) {
		selectedId = id;
		modalAction = "claim";
		modalTitle = "Bergung übernehmen";
		prefillFromAuth();
		modalOpen = true;
		return null;
	}

	public String openMissing(Long id) {
		selectedId = id;
		modalAction = "missing";
		modalTitle = "Als vermisst melden";
		prefillFromAuth();
		modalOpen = true;
		return null;
	}

	public String openRecovered(Long id) {
		selectedId = id;
		modalAction = "recovered";
		modalTitle = "Als geborgen markieren";
		prefillFromAuth();
		modalOpen = true;
		return null;
	}

	public String cancelModal() {
		modalOpen = false;
		return null;
	}

	private void prefillFromAuth() {
		if (auth != null && auth.isLoggedIn()) {
			modalName = auth.getReportDisplayName();
			modalPhone = auth.getReportPhone();
		} else {
			modalName = modalPhone = null;
		}
	}

	// ===== Speichern =====
	public String submitModal() {
		try {
			switch (modalAction) {
			case "report": {
				String rName = anonymous ? null : trimOrNull(modalName);
				String rPhone = anonymous ? null : trimOrNull(modalPhone);
				repo.createReport(req(lat), req(lon), trimOrNull(size), rName, rPhone);
				break;
			}
			case "claim": {
				if (!ensureLoggedIn())
					return null;
				Person p = persons.findOrCreate(trimOrNull(modalName), trimOrNull(modalPhone));
				repo.reassign(selectedId, p);
				break;
			}
			case "missing": {
				if (!ensureLoggedIn())
					return null;
				repo.markMissing(selectedId, trimOrNull(modalName), trimOrNull(modalPhone));
				break;
			}
			case "recovered": {
				if (!ensureLoggedIn())
					return null;
				Person p = persons.findOrCreate(trimOrNull(modalName), trimOrNull(modalPhone));
				repo.markRecovered(selectedId, p);
				break;
			}
			default:
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unbekannte Aktion.", null));
			}
			modalOpen = false;
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: " + ex.getMessage(), null));
		}
		return null;
	}

	private boolean ensureLoggedIn() {
		if (auth == null || !auth.isLoggedIn()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Bitte zuerst anmelden.", null));
			return false;
		}
		return true;
	}

	public String getOpenJson() {
		StringBuilder sb = new StringBuilder(256);
		sb.append('[');
		boolean first = true;

		for (GhostNet g : getPageItems()) {
			if (g.getStatus() == GhostNetStatus.RECOVERED)
				continue;

			if (!first)
				sb.append(',');
			first = false;

			sb.append('{').append("\"id\":").append(g.getId()).append(',').append("\"status\":")
					.append(jstr(String.valueOf(g.getStatus()))).append(',').append("\"lat\":")
					.append(g.getLat() == null ? "null" : g.getLat()).append(',').append("\"lon\":")
					.append(g.getLon() == null ? "null" : g.getLon()).append(',').append("\"size\":")
					.append(g.getSize() == null ? "null" : jstr(g.getSize()));

			Person s = g.getSalvor();
			if (s != null) {
				String sName = safeRead(() -> s.getName());
				String sPhone = safeRead(() -> s.getPhone());
				if (sName != null)
					sb.append(",\"salvor\":").append(jstr(sName));
				if (sPhone != null)
					sb.append(",\"phone\":").append(jstr(sPhone));
			}
			sb.append('}');
		}
		sb.append(']');
		return sb.toString();
	}

	private static String jstr(String s) {
		if (s == null)
			return "null";
		StringBuilder b = new StringBuilder(s.length() + 16);
		b.append('"');
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '"':
				b.append("\\\"");
				break;
			case '\\':
				b.append("\\\\");
				break;
			case '\b':
				b.append("\\b");
				break;
			case '\f':
				b.append("\\f");
				break;
			case '\n':
				b.append("\\n");
				break;
			case '\r':
				b.append("\\r");
				break;
			case '\t':
				b.append("\\t");
				break;
			default:
				if (c < 0x20) {
					b.append(String.format("\\u%04x", (int) c));
				} else {
					b.append(c);
				}
			}
		}
		b.append('"');
		return b.toString();
	}

	private static <T> T safeRead(java.util.concurrent.Callable<T> c) {
		try {
			return c.call();
		} catch (Exception e) {
			return null;
		}
	}

	private static String esc(String s) {
		return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static String trimOrNull(String s) {
		return (s == null || s.isBlank()) ? null : s.trim();
	}

	private static double req(Double d) {
		return d == null ? 0.0 : d.doubleValue();
	}

	// ===== Getter/Setter =====
	public boolean isModalOpen() {
		return modalOpen;
	}

	public String getModalTitle() {
		return modalTitle;
	}

	public String getModalAction() {
		return modalAction;
	}

	public void setModalAction(String modalAction) {
		this.modalAction = modalAction;
	}

	public Long getSelectedId() {
		return selectedId;
	}

	public void setSelectedId(Long id) {
		this.selectedId = id;
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

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getModalName() {
		return modalName;
	}

	public void setModalName(String modalName) {
		this.modalName = modalName;
	}

	public String getModalPhone() {
		return modalPhone;
	}

	public void setModalPhone(String modalPhone) {
		this.modalPhone = modalPhone;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
