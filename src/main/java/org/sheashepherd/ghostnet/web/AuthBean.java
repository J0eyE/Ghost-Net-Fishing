package org.sheashepherd.ghostnet.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;

import org.sheashepherd.ghostnet.model.Account;
import org.sheashepherd.ghostnet.repo.AccountRepository;
import org.sheashepherd.ghostnet.security.PasswordUtil;

@Named("auth")
@SessionScoped
public class AuthBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Account user;

	// modal: "login", "register", "profile"
	private boolean showModal;
	private String modal;

	// ===== Login-Felder
	@NotBlank(message = "Nachname ist Pflicht.")
	private String loginLastName;

	@NotBlank(message = "Passwort ist Pflicht.")
	private String loginPassword;

	// ===== Registrierung-Felder
	@NotBlank(message = "Vorname ist Pflicht.")
	private String regFirstName;

	@NotBlank(message = "Nachname ist Pflicht.")
	private String regLastName;

	@Size(max = 120)
	private String regCompany;

	@NotBlank(message = "Telefon ist Pflicht.")
	@Pattern(regexp = "^[+0-9 /-]{0,30}$", message = "Bitte eine gültige Telefonnummer angeben.")
	private String regPhone;

	@Size(min = 6, max = 100, message = "Passwort: mind. 6 Zeichen.")
	private String regPassword;

	private String regPassword2;

	@Inject
	AccountRepository accounts;

	// ========= Modal-Actions =========
	public String openLogin() {
		this.modal = "login";
		this.showModal = true;
		return null;
	}

	public String openRegister() {
		this.modal = "register";
		this.showModal = true;
		return null;
	}

	public String openProfile() {
		this.modal = (user == null ? "login" : "profile");
		this.showModal = true;
		return null;
	}

	public String closeModal() {
		this.showModal = false;
		this.modal = null;
		return null;
	}

	// ========= Auth-Actions =========
	public String register() {
		FacesContext fc = FacesContext.getCurrentInstance();

		if (accounts.existsLastName(trim(regLastName))) {
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nachname bereits vergeben.", null));
			return null;
		}
		if (regPassword == null || !regPassword.equals(regPassword2)) {
			fc.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwörter stimmen nicht überein.", null));
			return null;
		}

		String salt = PasswordUtil.newSaltBase64();
		String hash = PasswordUtil.hash(regPassword.toCharArray(), salt);

		Account a = new Account();
		a.setFirstName(trim(regFirstName));
		a.setLastName(trim(regLastName));
		a.setCompany(emptyToNull(regCompany));
		a.setPhone(trim(regPhone));
		a.setSalt(salt);
		a.setPasswordHash(hash);

		accounts.save(a);

		this.user = a;
		fc.getExternalContext().getSessionMap().put("user", a);
		resetForms();
		closeModal();
		return null;
	}

	public String login() {
		FacesContext fc = FacesContext.getCurrentInstance();
		Account a = accounts.findByLastName(trim(loginLastName));
		if (a == null || !PasswordUtil.verify((loginPassword == null ? new char[0] : loginPassword.toCharArray()),
				a.getSalt(), a.getPasswordHash())) {
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anmeldung fehlgeschlagen.", null));
			return null;
		}
		this.user = a;
		fc.getExternalContext().getSessionMap().put("user", a);
		resetForms();
		closeModal(); 
		return null;
	}

	public String logout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		this.user = null;
		fc.getExternalContext().invalidateSession();
		closeModal();
		return null;
	}

	private static String trim(String s) {
		return s == null ? null : s.trim();
	}

	private static String emptyToNull(String s) {
		return (s == null || s.isBlank()) ? null : s.trim();
	}

	private void resetForms() {
		loginLastName = loginPassword = null;
		regFirstName = regLastName = regCompany = regPhone = regPassword = regPassword2 = null;
	}

	// ========= Helpers für UI =========
	public boolean isLoggedIn() {
		return user != null;
	}

	public Account getUser() {
		return user;
	}

	public String getUserDisplayName() {
		return user == null ? "" : (user.getFirstName() + " " + user.getLastName());
	}

	/** Für die automatische Befüllung */
	public String getReportDisplayName() {
		if (user == null)
			return "";
		if (user.getCompany() != null && !user.getCompany().isBlank())
			return user.getCompany();
		return user.getFirstName() + " " + user.getLastName();
	}

	public String getReportPhone() {
		return user != null ? user.getPhone() : "";
	}

	// ========= Getter/Setter (EL) =========
	public boolean isShowModal() {
		return showModal;
	}

	public void setShowModal(boolean showModal) {
		this.showModal = showModal;
	}

	public String getModal() {
		return modal;
	}

	public void setModal(String modal) {
		this.modal = modal;
	}

	public String getLoginLastName() {
		return loginLastName;
	}

	public void setLoginLastName(String loginLastName) {
		this.loginLastName = loginLastName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getRegFirstName() {
		return regFirstName;
	}

	public void setRegFirstName(String regFirstName) {
		this.regFirstName = regFirstName;
	}

	public String getRegLastName() {
		return regLastName;
	}

	public void setRegLastName(String regLastName) {
		this.regLastName = regLastName;
	}

	public String getRegCompany() {
		return regCompany;
	}

	public void setRegCompany(String regCompany) {
		this.regCompany = regCompany;
	}

	public String getRegPhone() {
		return regPhone;
	}

	public void setRegPhone(String regPhone) {
		this.regPhone = regPhone;
	}

	public String getRegPassword() {
		return regPassword;
	}

	public void setRegPassword(String regPassword) {
		this.regPassword = regPassword;
	}

	public String getRegPassword2() {
		return regPassword2;
	}

	public void setRegPassword2(String regPassword2) {
		this.regPassword2 = regPassword2;
	}
}
