package ru.gur;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ComtradeHandler {

	/**
	 * args:
	 * 1. transform all analog values from primary to secondary and write it to the same path:
	 * comtrade #path_to_comtrade# transform #primary_value# #secondary_value#
	 * 2. transform all analog values from primary to secondary and write it to a specific path:
	 * comtrade #path_to_comtrade# transform #primary_value# #secondary_value# write #path_to_write_to#
	 * 3. transform specific analog values from primary to secondary:
	 * comtrade #path_to_comtrade# transform #array of analog value to transform# #primary_value# #secondary_value#
	 * comtrade #path_to_comtrade# transform #array of analog value to transform# #primary_value# #secondary_value# write #path_to_write_to#
	 **/

	public static void main(String[] args) {
		ActionsWithComtrade actionsWithComtrade = new ActionsWithComtrade();
		int i = 0;
		if (args[i].equals("comtrade")) {
			Path pathToFile = Path.of(args[i + 1]);

			Comtrade comtrade = new Comtrade();
			try {
				comtrade.setCfg(Files.readString(Paths.get(pathToFile + ".cfg"), StandardCharsets.US_ASCII));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				comtrade.setDat(new StringBuilder(Files.readString(Paths.get(pathToFile + ".dat"), StandardCharsets.US_ASCII)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			i = i + 2;
			Comtrade comtradeUpd;
			if (args[i].equals("transform")) {
				int countOfSignalsToTransform = (int) IntStream.range(i + 1, args.length)
						.takeWhile(j -> !args[j].equals("write")).count() - 2;
				if (countOfSignalsToTransform < 0) {
					return;
				}
				Set<Integer> arrayOfAnalogSignals = (countOfSignalsToTransform == 0) ? null :
						IntStream.range(i + 1, args.length)
								.mapToObj(j -> Integer.parseInt(args[j]))
								.collect(Collectors.toSet());
				comtradeUpd = actionsWithComtrade.transformPrimaryValuesToSecondary(
						comtrade,
						arrayOfAnalogSignals,
						Float.parseFloat(args[i + 1]),
						Float.parseFloat(args[i + 2]));
				i = i + 2 + countOfSignalsToTransform;

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