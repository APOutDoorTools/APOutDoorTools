package ml.p_seminar.apoutdoortools;

import android.graphics.Color;
import android.graphics.Rect;

public class NV21Image {

	private byte[] bild;
	private int breite;
	private int hoehe;
	
	public NV21Image(byte[] bild, int breite, int hoehe) {
		super();
		this.bild = bild;
		this.breite = breite;
		this.hoehe = hoehe;
	}

	/**
	 * @param x
	 * @param y
	 * @return rgb-codierte Farbe des Pixels an der Stelle x/y im YUV/NV21-codierten Bild
	 */
	public int[] holePixel(int x, int y) {
		if(x>=breite || x<0 || y>=hoehe || y<0)  return new int[]{-1};
		int frameSize = breite * hoehe;
		int uvp = frameSize + (y >> 1) * breite;
		int Y = (0xff & ((int)bild[breite*y + x])) - 16;
		if (Y < 0) Y = 0;
		int v = (0xff & bild[uvp+2*(x/2)]) - 128;
		int u = (0xff & bild[uvp+2*(x/2)+1]) - 128;	
		int y1192 = 1192 * Y;
		int r = (y1192 + 1634 * v);
		int g = (y1192 - 833 * v - 400 * u);
		int b = (y1192 + 2066 * u);
		if (r < 0) r = 0; else if (r > 262143) r = 262143;
		if (g < 0) g = 0; else if (g > 262143) g = 262143;
		if (b < 0) b = 0; else if (b > 262143) b = 262143;
		return new int[]{Y,0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff)};
	}
	/**
	 Created by lukas thyroff, felix kühn
	 */
	public int zaehleBlauePixel(Rect ausschnitt) {
		int anzahl = 0;
		for(int x=ausschnitt.left; x<ausschnitt.right; x++) {
			for(int y=ausschnitt.top; y<ausschnitt.bottom; y++) {
				if(istPixelFarbig(x,y)==Farben.BLAU) {
					anzahl++;
				}
			}
		}
		return anzahl;
	}
	/**
	 Created by lukas thyroff, felix kühn
	 */
	public Farben istPixelFarbig(int x, int y) {
		int[] farbe= holePixel(x, y);
		int Y=farbe[0];

		if (farbe[0]==-1){
			return Farben.NULL;
		}

		int f=farbe[1];

		final int s= 40;
		final int w=100;

		int durchschnitt=(Color.red(f)+Color.green(f)+Color.blue(f))/3;
		if((Color.blue(f) > 170 && Color.blue(f)- durchschnitt > 10) || (Color.blue(f)- durchschnitt > 50)) {
			return Farben.BLAU;
		}else if(Y<s){
			return Farben.SCHWARZ;
		}else if(Y>w){
			return Farben.WEIß;
		}
		return Farben.NULL;
	}

	public int getBreite() {
		return breite;
	}

	public int getHoehe() {
		return hoehe;
	}
}
