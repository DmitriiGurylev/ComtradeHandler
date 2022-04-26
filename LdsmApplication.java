package ru.mpei;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class LdsmApplication {

	/**
	 * args:
	 * 1. comtrade *path_to_comtrade* transform *primary_value* *secondary_value* write *path_to_write_to*
	 * **/

	public static void main(String[] args) {
		ComtradeHandler comtradeHandler = new ComtradeHandler();

		for (int i=0; i<args.length; ) {
			if (args[i].equals("comtrade")) {
				String path = args[i+1];
				Comtrade comtrade = new Comtrade();
				try {
					comtrade.setCfg(Files.readString(Paths.get(path+".cfg"), StandardCharsets.US_ASCII));
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					comtrade.setCfg(Files.readString(Paths.get(path+".dat"), StandardCharsets.US_ASCII));
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (args[i+2].equals("transform")) {
					i = i+3;
					Comtrade comtradeUpd = comtradeHandler.transformPrimaryValuesToSecondary(
							comtrade,
							Float.parseFloat(args[i+3]),
							Float.parseFloat(args[i+4]));
				}
			}
		}
	}

}