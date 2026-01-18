package dev.mb_labs.travelshare.model;

public class ConfirmResetRequest {
    private String email;
    private String code;
    private String newPassword;

    public ConfirmResetRequest(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }
}