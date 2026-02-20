package mains;

public class AppLauncher {
    public static void main(String[] args) {
        // Heda just bech inahhi l erreur rouge mte3 'Unsupported JavaFX configuration'
        // Faut pas le toucher sinon l warning yarja3 !
        System.setErr(new java.io.PrintStream(new java.io.ByteArrayOutputStream()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                String msg = new String(buf, off, len);
                if (msg != null && !msg.contains("Unsupported JavaFX configuration")) {
                   try {
                       // On redirige vers l'ancien System.err (console)
                       // Mais bon houni on fait simple on affiche sur System.out pour eviter la boucle
                       System.out.println(msg); 
                   } catch (Exception e) {}
                }
            }
             @Override
            public void println(String x) {
                if (x != null && !x.contains("Unsupported JavaFX configuration")) {
                    System.out.println(x);
                }
            }
        });

        // Nlanciw l MainFX mte3na
        MainFX.main(args);
    }
}