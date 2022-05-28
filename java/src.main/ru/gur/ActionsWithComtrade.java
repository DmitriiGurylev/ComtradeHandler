package ru.gur.ru.gur;

import java.util.*;
import java.util.stream.Collectors;

public class ActionsWithComtrade {

    private String buildCfgSignalRow(String[] signalArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signalArray.length; i++) {
            sb.append(signalArray[i]);
            if (i != signalArray.length - 1) sb.append(",");
        }
        return String.valueOf(sb);
    }

    public Map<String, Comtrade> getCurrentSum(String newName,
                                               Map<String, Comtrade> elementPositionToComtradeMap,
                                               String... measTransList
    ) {
        Map<String, Comtrade> newElementPositionToComtradeMap = new HashMap<>();
        List<Comtrade> listToSum = new LinkedList<>();
        elementPositionToComtradeMap.forEach((key, value) -> {
            if (Arrays.asList(measTransList).contains(key)) {
                listToSum.add(value);
            } else {
                newElementPositionToComtradeMap.put(key, value);
            }
        });
        List<Comtrade> comtradesToSum = listToSum.stream()
                .collect(Collectors.toList());
        Comtrade newComtrade = new Comtrade();
        newComtrade.setCfg(comtradesToSum.get(0).getCfg());
        StringBuilder newDat = new StringBuilder();
        //TODO
        String[] datRow1 = comtradesToSum.get(0).getDat().toString().split("\n");
        String[] datRow2 = comtradesToSum.get(1).getDat().toString().split("\n");
        for (int i = 0; i < datRow1.length; i++) {
            String[] datRowSplit1 = datRow1[i].split(",");
            String[] datRowSplit2 = datRow2[i].split(",");
            newDat.append(datRowSplit1[0]).append(",");
            newDat.append(datRowSplit1[1]).append(",");
            newDat.append(Double.parseDouble(datRowSplit1[2]) + Double.parseDouble(datRowSplit2[2])).append(",");
            newDat.append(Double.parseDouble(datRowSplit1[3]) + Double.parseDouble(datRowSplit2[3])).append(",");
            newDat.append(Double.parseDouble(datRowSplit1[4]) + Double.parseDouble(datRowSplit2[4])).append(",");
            newDat.append(datRowSplit1[5]).append("\n");
        }
        newComtrade.setDat(newDat);
        newElementPositionToComtradeMap.put(newName, newComtrade);
        return newElementPositionToComtradeMap;
    }

    public Comtrade transformPrimaryValuesToSecondary(Comtrade comtrade, Set<Integer> arrayOfAnalogSignals, float primaryNominal, float secondaryNominal) {
        Comtrade comtradeNew = new Comtrade();

        String[] cfg = comtrade.getCfg().split("\n");
        String[] numberOfSignals = cfg[1].split(",");
        int analogSignals = Integer.parseInt(numberOfSignals[0].replaceAll("[^0-9]", ""));

        String[] datRows = comtrade.getDat().toString().split("\n");
        float transformFactor = primaryNominal / secondaryNominal;
        for (int i = 0; i < datRows.length; i++) {
            String[] fieldsOfRow = datRows[i].split(",");
            for (int j = 0; j < analogSignals; j++) {
                if (arrayOfAnalogSignals == null || arrayOfAnalogSignals.contains(j + 1)) {
                    fieldsOfRow[2 + j] = String.valueOf(Double.parseDouble(fieldsOfRow[2 + j]) / transformFactor);
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < fieldsOfRow.length; j++) {
                sb.append(fieldsOfRow[j]);
                if (j != fieldsOfRow.length - 1) {
                    sb.append(",");
                }
            }
            datRows[i] = sb.toString();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < datRows.length; i++) {
            sb.append(datRows[i]);
            if (i != datRows.length - 1) {
                sb.append("\n");
            }
        }
        comtradeNew.setCfg(comtrade.getCfg());
        comtradeNew.setDat(sb);
        return comtradeNew;
    }

    private String[][] addDigitToNameOfSignal(String phaseA, String phaseB, String phaseC,
                                              String comtradeNumber,
                                              String signalNumber1, String signalNumber2, String signalNumber3) {
        String[] signalA = phaseA.split(",");
        String[] signalB = phaseB.split(",");
        String[] signalC = phaseC.split(",");
        if (!comtradeNumber.equals("1")) {
            signalA[0] = signalNumber1;
            signalB[0] = signalNumber2;
            signalC[0] = signalNumber3;
        }
        signalA[1] = signalA[1] + comtradeNumber;
        signalB[1] = signalB[1] + comtradeNumber;
        signalC[1] = signalC[1] + comtradeNumber;
        return new String[][]{signalA, signalB, signalC};
    }

    private StringBuilder fillRowOfDatFile(String[] column) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < column.length; i++) {
            sb.append(column[i]);
            if (i != column.length - 1) sb.append(",");
            else sb.append("\n");
        }
        return sb;
    }

    public Comtrade unifyComtrades(Comtrade... comtrades) {
        Comtrade comtrade = new Comtrade();
        comtrade.setCfg(buildCfg(comtrades));
        comtrade.setDat(buildDat(comtrades));
        return comtrade;
    }

    private String buildCfg(Comtrade... comtrades) {
        String[] cfg = comtrades[0].getCfg().split("\n");
        int numberOfAdditionalRowsInCfg = (comtrades.length - 1) * 3;
        String[] cfgNew = new String[cfg.length + numberOfAdditionalRowsInCfg];
        cfgNew[0] = cfg[0];
        String[] numberOfSignals = cfg[1].split(",");
        numberOfSignals[0] = String.valueOf(Integer.parseInt(numberOfSignals[0]) + numberOfAdditionalRowsInCfg);
        numberOfSignals[1] = String.valueOf(Integer.parseInt(numberOfSignals[1].replaceAll("[^0-9]", "")) + numberOfAdditionalRowsInCfg);
        cfgNew[1] = numberOfSignals[0] + "," + numberOfSignals[1] + "A," + numberOfSignals[2];

        int columnPosition = 2;
        for (int i = 0; i < comtrades.length; i++) {
            cfg = comtrades[i].getCfg().split("\n");
            String[][] signals = addDigitToNameOfSignal(cfg[2], cfg[3], cfg[4], String.valueOf(i + 1),
                    String.valueOf(i * 3 + 1), String.valueOf(i * 3 + 2), String.valueOf(i * 3 + 2));
            cfgNew[columnPosition++] = buildCfgSignalRow(signals[0]);
            cfgNew[columnPosition++] = buildCfgSignalRow(signals[1]);
            cfgNew[columnPosition++] = buildCfgSignalRow(signals[2]);
        }
        System.arraycopy(cfg, 5, cfgNew, (comtrades.length - 2) * 3 + 8, 8);
        StringBuilder sb = new StringBuilder();
        Arrays.stream(cfgNew).forEach(s -> sb.append(s).append("\n"));
        return String.valueOf(sb);
    }

    private StringBuilder buildDat(Comtrade... comtrades) {
        StringBuilder sb = new StringBuilder();
        String[][] datOriginals = Arrays.stream(comtrades)
                .map(c -> String.valueOf(c.getDat()).split("\n"))
                .toArray(String[][]::new);

        for (int i = 0; i < datOriginals[0].length; i++) {
            String[][] rowOriginalArray = new String[datOriginals.length][];
            for (int j = 0; j < comtrades.length; j++) {
                rowOriginalArray[j] = datOriginals[j][i].split(",");
            }
            String[] column = new String[rowOriginalArray[0].length + (comtrades.length - 1) * 3];
            column[0] = rowOriginalArray[0][0];
            column[1] = rowOriginalArray[0][1];
            int columnPosition = 2;
            for (int j = 0; j < comtrades.length; j++) {
                column[columnPosition++] = rowOriginalArray[j][2];
                column[columnPosition++] = rowOriginalArray[j][3];
                column[columnPosition++] = rowOriginalArray[j][4];
            }
            column[columnPosition] = rowOriginalArray[0][5];
            sb.append(fillRowOfDatFile(column));
        }
        return sb;
    }

}
