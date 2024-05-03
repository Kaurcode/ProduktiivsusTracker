package produktiivsustracker.produktiivsustrackerserver;

public class AndmebaasiVorm {
    private String URL;
    private int port;
    private String kasutajaNimi;
    private String parool;
    private String andmebaasiNimi;
    private boolean teeKontroll;

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getKasutajaNimi() {
        return kasutajaNimi;
    }

    public void setKasutajaNimi(String kasutajaNimi) {
        this.kasutajaNimi = kasutajaNimi;
    }

    public String getParool() {
        return parool;
    }

    public void setParool(String parool) {
        this.parool = parool;
    }

    public String getAndmebaasiNimi() {
        return andmebaasiNimi;
    }

    public void setAndmebaasiNimi(String andmebaasiNimi) {
        this.andmebaasiNimi = andmebaasiNimi;
    }

    public boolean isTeeKontroll() {
        return teeKontroll;
    }

    public void setTeeKontroll(boolean teeKontroll) {
        this.teeKontroll = teeKontroll;
    }
}
