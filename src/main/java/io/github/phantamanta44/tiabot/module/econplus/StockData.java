package io.github.phantamanta44.tiabot.module.econplus;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.github.phantamanta44.tiabot.util.MessageUtils;
import io.github.phantamanta44.tiabot.util.http.HttpException;
import io.github.phantamanta44.tiabot.util.http.HttpUtils;

public class StockData {

	private static final String QUOTE_EPT = "http://dev.markitondemand.com/MODApis/Api/v2/Quote?symbol=";
	
	public static StockData getQuote(String ticker) throws ClientProtocolException, HttpException, IOException {
		String resp = HttpUtils.requestXml(QUOTE_EPT + ticker);
		if (resp.startsWith("<Error>"))
			return null;
		return new StockData(resp);
	}
	
	private String name, symbol;
	private float open, lastPrice, high, low, change;
	private String fieldName;
	
	private StockData(String xml) throws HttpException {
		try {
			SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(new StringReader(xml)), new DefaultHandler() {

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					fieldName = MessageUtils.decapitalize(qName);
				}

				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					try {
						Field f = StockData.class.getDeclaredField(fieldName);
						String tCont = String.valueOf(Arrays.copyOfRange(ch, start, start + length));
						f.setAccessible(true);
						if (f.getType() == String.class)
							f.set(StockData.this, tCont);
						else if (f.getType() == Float.TYPE)
							f.setFloat(StockData.this, Float.parseFloat(tCont));
					} catch (Exception ex) {
						if (!(ex instanceof NoSuchFieldException))
							ex.printStackTrace();
					}
				}
					
			});
		} catch (Exception ex) {
			throw new HttpException(502);
		}
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public float getOpenPrice() {
		return open;
	}

	public float getPrice() {
		return lastPrice;
	}

	public float getHighPrice() {
		return high;
	}

	public float getLowPrice() {
		return low;
	}

	public float getChange() {
		return change;
	}
	
}
