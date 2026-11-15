package com.campus.canteen.bean;

import com.campus.canteen.dao.UserDAO;
import com.campus.canteen.dao.VendorDAO;
import com.campus.canteen.model.User;
import com.campus.canteen.model.Vendor;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "authBean")
@SessionScoped
public class AuthBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;
    private VendorDAO vendorDAO;

    private String loginUsername;
    private String loginPassword;

    // Registration fields
    private String regUsername;
    private String regPassword;
    private String regConfirmPassword;
    private String regFullName;
    private String regEmail;
    private String regPhone;

    private User currentUser;
    private Vendor currentVendor; // Populated if role is VENDOR

    @PostConstruct
    public void init() {
        userDAO = new UserDAO();
        vendorDAO = new VendorDAO();
    }

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (loginUsername == null || loginUsername.trim().isEmpty() ||
            loginPassword == null || loginPassword.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Required", "Please enter both username and password."));
            return null;
        }

        User user = userDAO.authenticate(loginUsername.trim(), loginPassword.trim());
        if (user != null) {
            this.currentUser = user;
            if (user.isVendor()) {
                this.currentVendor = vendorDAO.findByUserId(user.getId());
                return "/vendor/orders.xhtml?faces-redirect=true";
            } else if (user.isAdmin()) {
                return "/admin/dashboard.xhtml?faces-redirect=true";
            } else {
                return "/menu.xhtml?faces-redirect=true";
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password."));
            return null;
        }
    }

    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (regUsername == null || regUsername.trim().isEmpty() ||
            regPassword == null || regPassword.trim().isEmpty() ||
            regFullName == null || regFullName.trim().isEmpty() ||
            regEmail == null || regEmail.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please fill in all required fields."));
            return null;
        }

        if (!regPassword.equals(regConfirmPassword)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mismatch", "Passwords do not match."));
            return null;
        }

        if (userDAO.isUsernameTaken(regUsername.trim())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Taken", "Username is already registered."));
            return null;
        }

        if (userDAO.isEmailTaken(regEmail.trim())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Taken", "Email is already registered."));
            return null;
        }

        User newUser = new User();
        newUser.setUsername(regUsername.trim());
        newUser.setPassword(regPassword.trim());
        newUser.setFullName(regFullName.trim());
        newUser.setEmail(regEmail.trim());
        newUser.setPhone(regPhone != null ? regPhone.trim() : "");
        newUser.setRole("STUDENT");

        boolean success = userDAO.register(newUser);
        if (success) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Registration successful! Please log in."));
            // Clear registration fields
            regUsername = "";
            regPassword = "";
            regConfirmPassword = "";
            regFullName = "";
            regEmail = "";
            regPhone = "";
            return "/login.xhtml?faces-redirect=true";
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Registration failed. Please try again."));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.currentUser = null;
        this.currentVendor = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.isStudent();
    }

    public boolean isVendor() {
        return currentUser != null && currentUser.isVendor();
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    // Getters and Setters
    public String getLoginUsername() { return loginUsername; }
    public void setLoginUsername(String loginUsername) { this.loginUsername = loginUsername; }

    public String getLoginPassword() { return loginPassword; }
    public void setLoginPassword(String loginPassword) { this.loginPassword = loginPassword; }

    public String getRegUsername() { return regUsername; }
    public void setRegUsername(String regUsername) { this.regUsername = regUsername; }

    public String getRegPassword() { return regPassword; }
    public void setRegPassword(String regPassword) { this.regPassword = regPassword; }

    public String getRegConfirmPassword() { return regConfirmPassword; }
    public void setRegConfirmPassword(String regConfirmPassword) { this.regConfirmPassword = regConfirmPassword; }

    public String getRegFullName() { return regFullName; }
    public void setRegFullName(String regFullName) { this.regFullName = regFullName; }

    public String getRegEmail() { return regEmail; }
    public void setRegEmail(String regEmail) { this.regEmail = regEmail; }

    public String getRegPhone() { return regPhone; }
    public void setRegPhone(String regPhone) { this.regPhone = regPhone; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public Vendor getCurrentVendor() { return currentVendor; }
    public void setCurrentVendor(Vendor currentVendor) { this.currentVendor = currentVendor; }
}
