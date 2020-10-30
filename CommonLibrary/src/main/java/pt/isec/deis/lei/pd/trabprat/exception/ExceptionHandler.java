package pt.isec.deis.lei.pd.trabprat.exception;

public final class ExceptionHandler {
    private ExceptionHandler() {}

    public static void ShowException(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
        ex.printStackTrace();
    }
}
