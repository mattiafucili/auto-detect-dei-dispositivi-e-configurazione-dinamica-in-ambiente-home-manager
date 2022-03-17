package it.unibo.homemanager.meteo;

import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParserMeteo {

	private static DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
	private static DocumentBuilder domParser;
	private static String city;   	//città dove fare rilevazioni meteo inizializzata durante il metodo init
	private static String state; 	//serve per google
	private static String woeid;  	//woeid di yahoo
	private static String lat;
	private static String lon;
	
	
	
	public static void init() throws Exception
	{
		//inizializzazione del domParser
		dbf.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace",false);  
		domParser=dbf.newDocumentBuilder();
		//ErrorChecker err=new ErrorChecker();
		//domParser.setErrorHandler(err);
		
		//
		//inizializzazione city
		URL url=new URL("http://ip-api.com/xml");
		InputStream stream=url.openStream();
		Document doc=domParser.parse(stream);  
		doc.normalizeDocument();
		city=doc.getElementsByTagName("city").item(0).getChildNodes().item(0).getNodeValue().trim();
		state=doc.getElementsByTagName("countryCode").item(0).getChildNodes().item(0).getNodeValue().trim();
		lat=doc.getElementsByTagName("lat").item(0).getChildNodes().item(0).getNodeValue().trim();
		lon=doc.getElementsByTagName("lon").item(0).getChildNodes().item(0).getNodeValue().trim();
		System.out.println("lat "+lat+" lon "+lon);
		//
		//inizializzazione woeid di yahoo   DA MODIFICARE CON UNA ALTRA KEY DELL UNI
		url=new URL("https://api.flickr.com/services/rest/?method=flickr.places.find&api_key=e32bc8926257d72948f7f956556bd1c7&query="+city+"&format=rest");
		stream=url.openStream();
		doc=domParser.parse(stream);  
		doc.normalizeDocument();
		woeid=doc.getElementsByTagName("place").item(0).getAttributes().getNamedItem("woeid").getNodeValue().trim();//suppongo che il primo sia quello giusto in quanto al 99% è cosi
		
		//
		//Thread.sleep(1000);
	}
	public static Meteo previsioniM(String urlBase) throws Exception
	{
		//DA MODIFICARE CON UNA ALTRA KEY DELL UNI
		URL url=new URL(urlBase+city+"&mode=xml&APPID=941ba071203d6de5eea40c02c4aac22a");
		InputStream stream=url.openStream();
		Document doc=domParser.parse(stream);  
		doc.normalizeDocument();
		return openweathermapw(doc);
	}
	public static Meteo previsioniY(String urlBase) throws Exception
	{
		//qui non c è bisogno di modifica con una nuova key ma c è bisogno per flickr sopra
		URL url3=new URL(urlBase+woeid+"&u=c");
		InputStream stream3=url3.openStream();
		Document doc3=domParser.parse(stream3);
		doc3.normalizeDocument();
		return yahoow(doc3);
	}
	public static Meteo previsioniG(String urlBase) throws Exception
	{
		//DA MODIFICARE CON UNA ALTRA KEY DELL UNI
		//questa non funziona per nomi di città diversi nella lingua inglese quindi meglio usare quello con lat e lon
		//URL url2=new URL("http://api.wunderground.com/api/2cae48e983982ec4/conditions/lang:it/q/"+state+"/"+city+".xml");
		URL url2=new URL(urlBase+lat+","+lon+".xml");
		InputStream stream2=url2.openStream();
		Document doc2=domParser.parse(stream2);
		doc2.normalizeDocument();
		return googlew(doc2);
	}

	
	
	//Funzioni per creare gli oggetti Previsioni
	
	private static Meteo yahoow(Document doc3) 
	{
		int oraA=0,oraT=0,minA=0,minT=0;
		Meteo p=new Meteo();
		p.setNomeSito("yahoo");
		p.setCitta(estrai(doc3, "yweather:location", "city"));
		String alba=estrai(doc3, "yweather:astronomy", "sunrise");
		String tramonto=estrai(doc3, "yweather:astronomy", "sunset");
		{	//elabora stringhe
			StringTokenizer t1=new StringTokenizer(alba,":");
			StringTokenizer t2=new StringTokenizer(tramonto,":");
			oraA=Integer.parseInt(t1.nextToken());
			oraT=Integer.parseInt(t2.nextToken());

			StringTokenizer t3=new StringTokenizer(t1.nextToken());
			StringTokenizer t4=new StringTokenizer(t2.nextToken());
			minA=Integer.parseInt(t3.nextToken());
			minT=Integer.parseInt(t4.nextToken());
		}
		p.setOraAlba(oraA); //es 5:59 am
		p.setMinutoAlba(minA);
		p.setOraTramonto(oraT);
		p.setMinutoTramonto(minT);  
		p.setTemperatura(Float.parseFloat(estrai(doc3, "yweather:condition", "temp")));  //es 21 cioe in gradi
		p.setTemperaturaMax(Float.parseFloat(estrai(doc3, "yweather:forecast", "high")));
		p.setTemperaturaMin(Float.parseFloat(estrai(doc3, "yweather:forecast", "low")));  
		p.setUmidita(Integer.parseInt(estrai(doc3, "yweather:atmosphere", "humidity")));  //es 73 (si intende in %)
		p.setPressione(Float.parseFloat(estrai(doc3, "yweather:atmosphere", "pressure"))); //es 982 (si intende in mb)
		p.setVento(Float.parseFloat(estrai(doc3, "yweather:wind", "speed")));  //es 9.43 (si intende in km/h)
		//mancano le precipitazioni
		p.setMeteoCode(Integer.parseInt(estrai(doc3, "yweather:condition", "code")));  //es 30 (codice numerico per il meteo deciso da yahoo)
		p.setMeteo(estrai(doc3, "yweather:condition", "text"));		//es Partly Cloudy (testo simbolico del tempo)
		
		return p;
	}

	private static Meteo openweathermapw(Document doc)
	{
		int oraA=0,oraT=0,minA=0,minT=0;
		Meteo p=new Meteo();
		p.setNomeSito("openweathermap");
		p.setCitta(estrai(doc, "city", "name"));
		String alba=estrai(doc, "sun", "rise");  
		String tramonto=estrai(doc, "sun", "set"); 
		{	//elabora stringhe
			StringTokenizer t1=new StringTokenizer(alba,"T");
			StringTokenizer t2=new StringTokenizer(tramonto,"T");
			t1.nextToken();
			t2.nextToken();
			StringTokenizer t3=new StringTokenizer(t1.nextToken(),":");
			StringTokenizer t4=new StringTokenizer(t2.nextToken(),":");
			oraA=Integer.parseInt(t3.nextToken());
			oraT=Integer.parseInt(t4.nextToken());
			minA=Integer.parseInt(t3.nextToken());
			minT=Integer.parseInt(t4.nextToken());
		}
		p.setOraAlba(oraA); //es 2015-05-05T03:59:00
		p.setMinutoAlba(minA);
		p.setOraTramonto(oraT);
		p.setMinutoTramonto(minT);  
		p.setTemperatura((float) (Float.parseFloat(estrai(doc, "temperature", "value"))-272.15));  //es 21 cioe in gradi
		p.setTemperaturaMax((float) (Float.parseFloat(estrai(doc, "temperature", "max"))-272.15));
		p.setTemperaturaMin((float) (Float.parseFloat(estrai(doc, "temperature", "min"))-272.15));
		p.setUmidita(Integer.parseInt(estrai(doc, "humidity", "value")));  //es 73 (si intende in %)
		p.setPressione(Float.parseFloat(estrai(doc, "pressure", "value"))); //es 982 (si intende in hpa che equivale al mb)
		p.setVento((float) (Float.parseFloat(estrai(doc, "speed", "value"))*3.6));  //es 9.43 (si intende in km/h)
		if(!estrai(doc, "precipitation", "mode").trim().equals("no"))  //può valere no se non piove o rain se piove per esempio
			p.setPrecipitazioni(Float.parseFloat(estrai(doc, "precipitation", "value")));// es 0.82 metto il volume (in mm) delle precipitazioni nel caso piova
		else
			p.setPrecipitazioni(0); //senno default a 0
		p.setMeteoCode(Integer.parseInt(estrai(doc, "weather", "number")));  //es 500 (codice numerico per il meteo deciso da yahoo)
		p.setMeteo(estrai(doc, "weather", "value"));		//es light rain (testo simbolico del tempo)
		
		return p;
	}
		
	
	private static Meteo googlew(Document doc) 
	{
		Meteo p=new Meteo();
		p.setNomeSito("google");
		p.setCitta(estrai2(doc,"city"));
		//manca l alba
		//manca il tramonto
		p.setTemperatura(Float.parseFloat(estrai2(doc,"temp_c")));  //es 21 cioe in gradi
		//manca la tempmax
		//manca la tempmin
		p.setUmidita(Integer.parseInt(estrai2(doc,"relative_humidity").substring(0,estrai2(doc,"relative_humidity").length()-1))); //es 73 (si intende in %)
		p.setPressione(Float.parseFloat(estrai2(doc,"pressure_mb")));   //es 982 (si intende in hpa che equivale al mb)
		p.setVento(Float.parseFloat(estrai2(doc,"wind_kph")));  //es 9.43 (si intende in km/h)
		p.setPrecipitazioni(Float.parseFloat(estrai2(doc,"precip_1hr_metric")));    
		p.setMeteo(estrai2(doc,"weather"));
		//manca codice meteo
		
		return p;
	}
	
	
	
	//Funzioni ausiliarie per estrazione dei dati dai file XML
	private static String estrai(Document doc,String tag,String attr)
	{
		NodeList nomi=doc.getElementsByTagName(tag);
		Node n=nomi.item(0);
		NamedNodeMap mappa=n.getAttributes();
		Node nome=mappa.getNamedItem(attr);
		return nome.getNodeValue();
	}
	
	private static String estrai2(Document doc,String tag)
	{
		NodeList nomi=doc.getElementsByTagName(tag);
		Node n=nomi.item(0);
		return n.getTextContent();
	}
	
	
}
