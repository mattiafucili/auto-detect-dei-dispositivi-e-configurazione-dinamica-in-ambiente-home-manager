package it.unibo.homemanager.meteo;

public class Meteo {

	private String nomeSito;
	private String citta;
	private int oraAlba;
	private int oraTramonto;
	private int minutoAlba;
	private int minutoTramonto;
	private float temperatura;
	private float temperaturaMax;
	private float temperaturaMin;
	private int umidita;
	private float pressione;
	private float vento;
	private float precipitazioni;
	private int meteoCode;
	private String meteo;
	
	//costruttore che inizializza tutti i campi
	public Meteo()
	{
		this.nomeSito="null";   //con null semplice da problemi
		this.citta="null";		
		this.oraAlba=0;
		this.oraTramonto=0;
		this.minutoAlba=0;
		this.minutoTramonto=0;
		this.temperatura=0;
		this.temperaturaMax=0;
		this.temperaturaMin=0;
		this.umidita=0;
		this.pressione=0;
		this.vento=0;
		this.precipitazioni=0;
		this.meteoCode=0;
		this.meteo="null";
		
	}
	
	
	//TUTTI GET E SET
	public String getNomeSito() {
		return nomeSito;
	}
	public void setNomeSito(String nomeSito) {
		this.nomeSito = nomeSito.trim();
	}
	public String getCitta() {
		return citta;
	}
	public void setCitta(String citta) {
		this.citta = citta.trim();
	}
	
	public float getTemperatura() {
		return temperatura;
	}
	public void setTemperatura(float temperatura) {
		this.temperatura = temperatura;
	}
	public float getTemperaturaMax() {
		return temperaturaMax;
	}
	public void setTemperaturaMax(float temperaturaMax) {
		this.temperaturaMax = temperaturaMax;
	}
	public float getTemperaturaMin() {
		return temperaturaMin;
	}
	public void setTemperaturaMin(float temperaturaMin) {
		this.temperaturaMin = temperaturaMin;
	}
	public int getUmidita() {
		return umidita;
	}
	public void setUmidita(int umidita) {
		this.umidita = umidita;
	}
	public float getPressione() {
		return pressione;
	}
	public void setPressione(float pressione) {
		this.pressione = pressione;
	}
	public float getVento() {
		return vento;
	}
	public void setVento(float vento) {
		this.vento = vento;
	}
	public float getPrecipitazioni() {
		return precipitazioni;
	}
	public void setPrecipitazioni(float precipitazioni) {
		this.precipitazioni = precipitazioni;
	}
	public int getMeteoCode() {
		return meteoCode;
	}
	public void setMeteoCode(int meteoCode) {
		this.meteoCode = meteoCode;
	}
	public String getMeteo() {
		return meteo;
	}
	public void setMeteo(String meteo) {
		this.meteo = meteo.trim();
	}


	public int getOraAlba() {
		return oraAlba;
	}

	public void setOraAlba(int oraAlba) {
		this.oraAlba = oraAlba;
	}

	public int getOraTramonto() {
		return oraTramonto;
	}

	public void setOraTramonto(int oraTramonto) {
		this.oraTramonto = oraTramonto;
	}

	public int getMinutoTramonto() {
		return minutoTramonto;
	}

	public void setMinutoTramonto(int minutoTramonto) {
		this.minutoTramonto = minutoTramonto;
	}

	public int getMinutoAlba() {
		return minutoAlba;
	}

	public void setMinutoAlba(int minutoAlba) {
		this.minutoAlba = minutoAlba;
	}
	
	
	
	
}
