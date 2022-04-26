package ru.mpei;

import java.util.*;
import java.util.stream.Collectors;

public class ComtradeHandler {

    private String buildCfgSignalRow(String[] signalArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signalArray.length; i++) {
            sb.append(signalArray[i]);
            if (i != signalArray.length - 1) sb.append(",");
        }
        return String.valueOf(sb);
    }

    public Map<String, Comtrade> getCurrentSum(String newName,
                                               Map<String, Comtrade> elementPositionToComtradeReportMap,
                                               String... measTransList
    ) {
        Map<String, Comtrade> newElementPositionToComtradeReportMap = new HashMap<>();
        List<Comtrade> listToSum = new LinkedList<>();
        elementPositionToComtradeReportMap.forEach((key, value) -> {
            if (Arrays.asList(measTransList).contains(key)) {
                listToSum.add(value);
            } else {
                newElementPositionToComtradeReportMap.put(key, value);
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
        for (int i=0; i<datRow1.length; i++) {
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
        newElementPositionToComtradeReportMap.put(newName, newComtrade);
        return newElementPositionToComtradeReportMap;
    }

    private void transformPrimaryValuesToSecondary(Comtrade comtrade) {
        String[] datRows = comtrade.getDat().toString().split("\n");
        float transformFactor;
//        if (element.isItCurrentTransformer()) {
//            transformFactor = (float) directoryData.getRatedCurrentValue(element)[0] / directoryData.getRatedCurrentValue(element)[1];
//        } else {
//            transformFactor = (float) (directoryData.getVoltageDirectoryById(element.getVoltageLevel()).get().getVoltageValue() * 10.0 / 1000.0);
//        }
        transformFactor = 1f;
        for (int i = 0; i < datRows.length; i++) {
            String[] fieldsOfRow = datRows[i].split(",");
            fieldsOfRow[2] = String.valueOf(Double.parseDouble(fieldsOfRow[2]) / transformFactor);
            fieldsOfRow[3] = String.valueOf(Double.parseDouble(fieldsOfRow[3]) / transformFactor);
            fieldsOfRow[4] = String.valueOf(Double.parseDouble(fieldsOfRow[4]) / transformFactor);

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
        comtrade.setDat(sb);
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
        for (int j = 0; j < column.length; j++) {
            sb.append(column[j]);
            if (j != column.length - 1) sb.append(",");
            else sb.append("\n");
        }
        return sb;
    }

    public Comtrade unifyComtrades(Map<String, Comtrade> comtradeReportMap, String firstEl, String secondEl) {

        Comtrade comtrade = new Comtrade();
        String[] cfg = comtradeReportMap.get(firstEl).getCfg().split("\n");
        String[] cfgNew = new String[cfg.length + 3];
        cfgNew[0] = cfg[0];
        String[] numberOfSignals = cfg[1].split(",");
        numberOfSignals[0] = String.valueOf(Integer.parseInt(numberOfSignals[0]) + 3);
        numberOfSignals[1] = String.valueOf(Integer.parseInt(numberOfSignals[1].replaceAll("[^0-9]", "")) + 3);
        cfgNew[1] = numberOfSignals[0] + "," + numberOfSignals[1] + "A," + numberOfSignals[2];
        StringBuilder sb;

        String[][] signals = addDigitToNameOfSignal(cfg[2], cfg[3], cfg[4], "1", "1", "2", "3");
        cfgNew[2] = buildCfgSignalRow(signals[0]);
        cfgNew[3] = buildCfgSignalRow(signals[1]);
        cfgNew[4] = buildCfgSignalRow(signals[2]);

        cfg = comtradeReportMap.get(secondEl).getCfg().split("\n");
        signals = addDigitToNameOfSignal(cfg[2], cfg[3], cfg[4], "2", "4", "5", "6");
        cfgNew[5] = buildCfgSignalRow(signals[0]);
        cfgNew[6] = buildCfgSignalRow(signals[1]);
        cfgNew[7] = buildCfgSignalRow(signals[2]);

        System.arraycopy(cfg, 5, cfgNew, 8, 8);
        comtrade.setCfg(fillCfgFile(cfgNew));

        sb = new StringBuilder();
        String[] datOriginal1 = String.valueOf(comtradeReportMap.get(firstEl).getDat()).split("\n");
        String[] datOriginal2 = String.valueOf(comtradeReportMap.get(secondEl).getDat()).split("\n");
        for (int i = 0; i < datOriginal1.length; i++) {
            String[] rowOriginal1 = datOriginal1[i].split(",");
            String[] rowOriginal2 = datOriginal2[i].split(",");
            String[] column = new String[rowOriginal1.length + 3];
            column[0] = rowOriginal1[0];
            column[1] = rowOriginal1[1];
            column[2] = rowOriginal1[2];
            column[3] = rowOriginal1[3];
            column[4] = rowOriginal1[4];
            column[5] = rowOriginal2[2];
            column[6] = rowOriginal2[3];
            column[7] = rowOriginal2[4];
            column[8] = rowOriginal1[5];

            sb.append(fillRowOfDatFile(column));
        }
        comtrade.setDat(sb);
        return comtrade;
    }

    private String fillCfgFile(String[] cfgArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cfgArray.length; i++) {
            sb.append(cfgArray[i]);
            if (i != cfgArray.length - 1) sb.append("\n");
        }
        return String.valueOf(sb);
    }

    public Comtrade unifyComtrades(
            Map<String, Comtrade> comtradeReportMap, String firstEl, String secondEl, String thirdEl
    ) {
        Comtrade comtrade = new Comtrade();
        String[] cfg = comtradeReportMap.get(firstEl).getCfg().split("\n");
        String[] cfgNew = new String[cfg.length + 6];
        cfgNew[0] = cfg[0];
        String[] numberOfSignals = cfg[1].split(",");
        numberOfSignals[0] = String.valueOf(Integer.parseInt(numberOfSignals[0]) + 6);
        numberOfSignals[1] = String.valueOf(Integer.parseInt(numberOfSignals[1].replaceAll("[^0-9]", "")) + 6);
        cfgNew[1] = numberOfSignals[0] + "," + numberOfSignals[1] + "A," + numberOfSignals[2];

        String[][] signals = addDigitToNameOfSignal(cfg[2], cfg[3], cfg[4], "1", "1", "2", "3");
        cfgNew[2] = buildCfgSignalRow(signals[0]);
        cfgNew[3] = buildCfgSignalRow(signals[1]);
        cfgNew[4] = buildCfgSignalRow(signals[2]);

        cfg = comtradeReportMap.get(secondEl).getCfg().split("\n");
        signals = addDigitToNameOfSignal(cfg[2], cfg[3], cfg[4], "2", "4", "5", "6");
        cfgNew[5] = buildCfgSignalRow(signals[0]);
        cfgNew[6] = buildCfgSignalRow(signals[1]);
        cfgNew[7] = buildCfgSignalRow(signals[2]);

        cfg = comtradeReportMap.get(thirdEl).getCfg().split("\n");
        signals = addDigitToNameOfSignal(cfg[2], cfg[3], cfg[4], "3", "7", "8", "9");
        cfgNew[8] = buildCfgSignalRow(signals[0]);
        cfgNew[9] = buildCfgSignalRow(signals[1]);
        cfgNew[10] = buildCfgSignalRow(signals[2]);

        System.arraycopy(cfg, 5, cfgNew, 11, 8);
        comtrade.setCfg(fillCfgFile(cfgNew));

        StringBuilder sb = new StringBuilder();
        String[] datOriginal1 = String.valueOf(comtradeReportMap.get(firstEl).getDat()).split("\n");
        String[] datOriginal2 = String.valueOf(comtradeReportMap.get(secondEl).getDat()).split("\n");
        String[] datOriginal3 = String.valueOf(comtradeReportMap.get(thirdEl).getDat()).split("\n");
        for (int i = 0; i < datOriginal1.length; i++) {
            String[] rowOriginal1 = datOriginal1[i].split(",");
            String[] rowOriginal2 = datOriginal2[i].split(",");
            String[] rowOriginal3 = datOriginal3[i].split(",");
            String[] column = new String[rowOriginal1.length + 6];
            column[0] = rowOriginal1[0];
            column[1] = rowOriginal1[1];
            column[2] = rowOriginal1[2];
            column[3] = rowOriginal1[3];
            column[4] = rowOriginal1[4];
            column[5] = rowOriginal2[2].charAt(0) == '-' ? rowOriginal2[2].substring(1) : "-" + rowOriginal2[2];
            column[6] = rowOriginal2[3].charAt(0) == '-' ? rowOriginal2[3].substring(1) : "-" + rowOriginal2[3];
            column[7] = rowOriginal2[4].charAt(0) == '-' ? rowOriginal2[4].substring(1) : "-" + rowOriginal2[4];
            column[8] = rowOriginal3[2].charAt(0) == '-' ? rowOriginal3[2].substring(1) : "-" + rowOriginal3[2];
            column[9] = rowOriginal3[3].charAt(0) == '-' ? rowOriginal3[3].substring(1) : "-" + rowOriginal3[3];
            column[10] = rowOriginal3[4].charAt(0) == '-' ? rowOriginal3[4].substring(1) : "-" + rowOriginal3[4];
            column[11] = rowOriginal1[5];

            sb.append(fillRowOfDatFile(column));
        }

        comtrade.setDat(sb);
        return comtrade;
    }

}
