package services;

public class VerificationCodeEmailService {

    private final MailerSendEmailService emailService;

    public VerificationCodeEmailService() {
        this(new MailerSendEmailService());
    }

    public VerificationCodeEmailService(MailerSendEmailService emailService) {
        this.emailService = emailService;
    }

    public void sendSignupCode(String toEmail, String code) throws Exception {
        String subject = "AgriFlow - Code d'inscription";
        String text = "Votre code d'inscription AgriFlow est : " + code + "\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";
        String html = "<p>Votre code d'inscription <b>AgriFlow</b> est :</p>" +
                "<h2>" + escapeHtml(code) + "</h2>" +
                "<p>Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>";
        emailService.sendEmail(toEmail, subject, text, html);
    }

    public void sendPasswordResetCode(String toEmail, String code) throws Exception {
        String subject = "AgriFlow - Code de réinitialisation";
        String text = "Votre code de réinitialisation de mot de passe AgriFlow est : " + code + "\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";
        String html = "<p>Votre code de réinitialisation de mot de passe <b>AgriFlow</b> est :</p>" +
                "<h2>" + escapeHtml(code) + "</h2>" +
                "<p>Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>";
        emailService.sendEmail(toEmail, subject, text, html);
    }

    public void sendProfileUpdateCode(String toEmail, String code) throws Exception {
        String subject = "AgriFlow - Confirmation de modification";
        String text = "Votre code de confirmation AgriFlow est : " + code + "\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";
        String html = "<p>Votre code de confirmation <b>AgriFlow</b> est :</p>" +
                "<h2>" + escapeHtml(code) + "</h2>" +
                "<p>Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>";
        emailService.sendEmail(toEmail, subject, text, html);
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
