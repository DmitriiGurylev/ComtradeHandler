package ru.gur;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ComtradeHandler {

	/**
	 * args:
	 * 1. transform primary to secondary for all analog values and write it to the same path:
	 * comtrade #path_to_comtrade# transformAll #primary_value# #secondary_value#
	 * 2. transform primary to secondary for all analog values and write it to a specific path:
	 * comtrade #path_to_comtrade# transformAll #primary_value# #secondary_value# write #path_to_write_to#
	 * 3. transform primary to secondary for specific analog values and write it to a specific path:
	 * comtrade #path_to_comtrade# transform #primary_value# #secondary_value# write #path_to_write_to#
	 * **/

	public static void main(String[] args) {
		ActionsWithComtrade actionsWithComtrade = new ActionsWithComtrade();
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
				if (args[i].equals("transformAll")) {
					Comtrade comtradeUpd = actionsWithComtrade.transformPrimaryValuesToSecondary(
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