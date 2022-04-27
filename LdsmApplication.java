package ru.mpei;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class LdsmApplication {

	/**
	 * args:
	 * 1. comtrade *path_to_comtrade* transform *primary_value* *secondary_value* write *path_to_write_to*
	 * **/

	public static void main(String[] args) {
		args = new String[]{"comtrade", "/home/gur/Desktop/1", "transform", "2", "1", "write", "/home/gur/Desktop/gur"};
		ComtradeHandler comtradeHandler = new ComtradeHandler();
		for (int i=0; i<args.length; ) {
			if (args[i].equals("comtrade")) {
				Path pathToFile = Path.of(args[i + 1]);

				Comtrade comtrade = new Comtrade();
				try {
					comtrade.setCfg(Files.readString(Paths.get(pathToFile+".cfg"), StandardCharsets.US_ASCII));
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					comtrade.setDat(new StringBuilder(Files.readString(Paths.get(pathToFile + ".dat"), StandardCharsets.US_ASCII)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				i = i + 2;
				if (args[i].equals("transform")) {
					Comtrade comtradeUpd = comtradeHandler.transformPrimaryValuesToSecondary(
							comtrade,
							Float.parseFloat(args[i + 1]),
							Float.parseFloat(args[i + 2]));
					i = i + 3;
					Path pathOfUpdatedComtrades = i < args.length && args[i].equals("write") ?
							Path.of(args[i + 1] + "/UpdatedComtrades") :
							Path.of(pathToFile.getParent() + "/UpdatedComtrades");
					try {
						Files.createDirectories(pathOfUpdatedComtrades);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String fileName = pathOfUpdatedComtrades + "/" + pathToFile.getFileName();
					try {
						Files.write(Paths.get(fileName + ".cfg"), comtradeUpd.getCfg().getBytes());
						Files.write(Paths.get(fileName + ".dat"), String.valueOf(comtradeUpd.getDat()).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}